package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.commands.PullCommandsCommand;
import net.three_headed_monkey.commands.UpdateGcmRegistrationsCommand;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.communication.utils.X509TrustEverythingManager;
import net.three_headed_monkey.communication.utils.X509TrustSingleManager;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.service.CommandExecutorService;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.security.cert.Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

@EActivity(R.layout.activity_service_add)
public class ServiceAddActivity extends Activity {
    public static final String TAG = "ServiceAddActivity";

    @ViewById
    Button btn_scan_qrcode, btn_manually_edit_settings, btn_check_or_save;

    @ViewById
    EditText edit_base_url, edit_base_url_port, edit_device_key;

    @ViewById
    ViewGroup details_container;

    @ViewById
    ProgressBar progress_indicator;

    @ViewById
    TextView text_status_message;

    @App
    ThreeHeadedMonkeyApplication application;


    public ServiceInfo current_info;

    public enum State {
        INITIAL, EDIT_CHANGED, CHECKING, INITIAL_CHECK_OK, ERROR, OK_TO_SAVE
    }

    public State current_state = State.INITIAL;

    public String current_error_message;
    public String current_success_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_info = new ServiceInfo();
    }

    @Click(R.id.btn_scan_qrcode)
    public void scanQrCode() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivityForResult(intent, 0);
        } else {
            createScannerNeededDialog().show();
        }

    }

    @Click(R.id.btn_manually_edit_settings)
    public void openEditSettings() {
        current_state = State.EDIT_CHANGED;
        updateViewsForState();
    }

    @Click(R.id.btn_check_or_save)
    public void checkOrSaveClicked() {
        if (current_state == State.OK_TO_SAVE) {
            application.serviceSettings.add(current_info);
            Intent intent = new Intent(this, CommandExecutorService.class);
            intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, UpdateGcmRegistrationsCommand.COMMAND_STRING);
            intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
            startService(intent);
            finish();
            return;
        }
        if (current_state == State.INITIAL || current_state == State.EDIT_CHANGED || current_state == State.ERROR) {
            current_state = State.CHECKING;
            updateViewsForState();
            startConnectionCheck();
            return;
        }
        if (current_state == State.INITIAL_CHECK_OK) {
            current_state = State.CHECKING;
            updateViewsForState();
            startApiCheck();
            return;
        }
    }

    @Background
    public void startConnectionCheck() {
        try {
            SSLSocketFactory socketFactory = X509TrustEverythingManager.getTrustEverythingFactory();
            URL url = current_info.getBaseUrl();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(socketFactory);
            connection.connect();
//            connection.getInputStream().close();

            Certificate[] certificates = connection.getServerCertificates();

            current_info.certHash = new String(Hex.encodeHex(DigestUtils.sha256(certificates[0].getEncoded())));
            connection.disconnect();

            current_state = State.INITIAL_CHECK_OK;
        } catch (Exception ex) {
            Log.e(TAG, "Can't connect to url: " + current_info.baseUrl, ex);
            current_state = State.ERROR;
            current_error_message = "Can't connect to base url";
        } finally {
            updateViewsForState();
        }

    }

    @Background
    public void startApiCheck() {
        try {
            SSLSocketFactory socketFactory = X509TrustSingleManager.getTrustSingleFactory(current_info.certHash);
            URL url = current_info.getDeviceApiV1Url();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(socketFactory);
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                current_state = State.ERROR;
                current_error_message = "Can't connect to api, please check the device key. Reponse code: " + connection.getResponseCode();
            } else {
                current_state = State.OK_TO_SAVE;
            }
            connection.disconnect();
        } catch (Exception ex) {
            Log.e(TAG, "Can't connect to url: " + current_info.baseUrl, ex);
            current_state = State.ERROR;
            current_error_message = "Can't connect to base url";
        } finally {
            updateViewsForState();
        }
    }

    @AfterTextChange({R.id.edit_base_url, R.id.edit_device_key, R.id.edit_base_url_port})
    public void serviceEditTextsChanged(TextView v, Editable text) {
        switch (v.getId()) {
            case R.id.edit_base_url:
                current_info.baseUrl = text.toString();
                break;
            case R.id.edit_base_url_port:
                current_info.baseUrlPort = Integer.parseInt(text.toString());
                break;
            case R.id.edit_device_key:
                current_info.deviceKey = text.toString();
                break;
        }
        current_state = State.EDIT_CHANGED;
        updateViewsForState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        String content = data.getStringExtra("SCAN_RESULT");
        if (content == null || content.isEmpty())
            return;
        current_info = ServiceInfo.createFromJson(content);
        edit_base_url.setText(current_info.baseUrl);
        edit_base_url_port.setText(String.valueOf(current_info.baseUrlPort));
        edit_device_key.setText(current_info.deviceKey);

        Toast.makeText(this, "base_url=" + current_info.baseUrl + "\ndevice_key=" + current_info.deviceKey, Toast.LENGTH_LONG).show();
        openEditSettings();
    }

    @AfterViews
    @UiThread
    protected void updateViewsForState() {
        if (current_state != State.INITIAL && details_container.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            details_container.startAnimation(animation);
            details_container.setVisibility(View.VISIBLE);
            btn_manually_edit_settings.setVisibility(View.GONE);
        }

        if (current_state == State.INITIAL) {
            details_container.setVisibility(View.GONE);
        }

        if (current_state == State.INITIAL || current_state == State.EDIT_CHANGED) {
            progress_indicator.setVisibility(View.GONE);
            text_status_message.setVisibility(View.GONE);
        }

        if (current_state == State.EDIT_CHANGED) {
            btn_check_or_save.setText(R.string.check);
        }

        if (current_state == State.CHECKING) {
            progress_indicator.setVisibility(View.VISIBLE);
            text_status_message.setVisibility(View.VISIBLE);
            text_status_message.setText("Checking...");
            setEditFieldsEnabled(false);
            btn_check_or_save.setEnabled(false);
            btn_scan_qrcode.setEnabled(false);
        }

        if (current_state == State.INITIAL_CHECK_OK) {
            text_status_message.setText(Html.fromHtml("<font color=green>Connection successful</font>, please check that the following code is equal to what is shown on the webpage before clicking next: " + current_info.certHash));
            btn_check_or_save.setText("Next");
            progress_indicator.setVisibility(View.GONE);
            text_status_message.setVisibility(View.VISIBLE);
            setEditFieldsEnabled(true);
            btn_check_or_save.setEnabled(true);
            btn_scan_qrcode.setEnabled(true);
        }

        if (current_state == State.ERROR || current_state == State.OK_TO_SAVE) {
            progress_indicator.setVisibility(View.GONE);
            text_status_message.setVisibility(View.VISIBLE);
            setEditFieldsEnabled(true);
            btn_check_or_save.setEnabled(true);
            btn_scan_qrcode.setEnabled(true);
        }

        if (current_state == State.ERROR) {
            text_status_message.setText(Html.fromHtml("<font color=red>ERROR</font>: " + current_error_message));
            btn_check_or_save.setText(R.string.check);
        }

        if (current_state == State.OK_TO_SAVE) {
            text_status_message.setText(Html.fromHtml("<font color=green>Everything ok</font>, you can now save this config"));
            btn_check_or_save.setText(R.string.save);
        }


    }

    private void setEditFieldsEnabled(boolean enabled) {
        edit_base_url.setEnabled(enabled);
        edit_base_url_port.setEnabled(enabled);
        edit_device_key.setEnabled(enabled);
    }

    private AlertDialog createScannerNeededDialog() {
        final TextView message = new TextView(this);
        final SpannableString s =
                new SpannableString(getText(R.string.scanner_needed_dialog_message));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        return new AlertDialog.Builder(this)
                .setTitle(R.string.scanner_needed_dialog_title)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.ok, null)
                .setView(message)
                .create();
    }

}
