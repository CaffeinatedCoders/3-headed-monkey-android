package net.three_headed_monkey.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import net.three_headed_monkey.R;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.test_utils.ForegroundExecutor;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.SSLUtils;
import net.three_headed_monkey.test_utils.TestBase;

import org.androidannotations.api.BackgroundExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import static net.three_headed_monkey.ui.ServiceAddActivity.State;
import static org.assertj.android.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class ServiceAddActivityTest extends TestBase {

    private SSLUtils sslUtils;
    private ServiceAddActivity_ activity;
    private ShadowActivity shadowActivity;

    RobolectricPackageManager packageManager;

    public ServiceAddActivityTest() {
        sslUtils = new SSLUtils();
    }

    MockWebServer server;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        activity = Robolectric.buildActivity(ServiceAddActivity_.class).create().get();
        shadowActivity = Robolectric.shadowOf(activity);
        application.serviceSettings.getAll().clear();
        BackgroundExecutor.setExecutor(new ForegroundExecutor());
        server = new MockWebServer();

        packageManager = (RobolectricPackageManager) shadowApplication.getPackageManager();

    }

    @After
    public void tearDown() {
        BackgroundExecutor.setExecutor(BackgroundExecutor.DEFAULT_EXECUTOR);
        try {
            server.shutdown();
        } catch (IOException e) {
        }
    }

    @Test
    public void testServiceInfoUpdateOnTextChange() {
        setEditFields("welovetesting.net", "4242", "42is_the_answer");
        ServiceInfo info = activity.current_info;
        assertThat(info.baseUrl, equalTo("welovetesting.net"));
        assertThat(info.baseUrlPort, equalTo(4242));
        assertThat(info.deviceKey, equalTo("42is_the_answer"));
    }

    @Test
    public void testTypicalWorkflow() throws Exception {
        SSLContext sslContext = sslUtils.getSSLContext(sslUtils.getTestCert1());
        server.useHttps(sslContext.getSocketFactory(), false);
        int port = 3333;

        assertThat(activity.current_state, equalTo(State.INITIAL));
        assertThat(activity.details_container).isGone();

        activity.btn_manually_edit_settings.performClick();
        assertThat(activity.details_container).isVisible();
        setEditFields("localhost", String.valueOf(port), "doesn't matter");
        assertThat(activity.current_state, equalTo(State.EDIT_CHANGED));

        server.enqueue(new MockResponse().setBody("doesn't matter"));
        server.play(port);

        activity.btn_check_or_save.performClick();
        assertThat(activity.current_state, equalTo(State.INITIAL_CHECK_OK));
        assertThat(activity.text_status_message).containsText(SSLUtils.TEST_CERT1_HASH);

        activity.btn_check_or_save.performClick();
        assertThat(activity.current_state, equalTo(State.OK_TO_SAVE));

        activity.btn_check_or_save.performClick();
        assertThat(activity.isFinishing(), equalTo(true));
        assertThat(application.serviceSettings.getAll().size(), equalTo(1));
        assertThat(application.serviceSettings.getAll(), hasItem(activity.current_info));
    }

    @Test
    public void testApiCheckFailsIfWrongCertHash() throws Exception {
        SSLContext sslContext = sslUtils.getSSLContext(sslUtils.getTestCert1());
        server.useHttps(sslContext.getSocketFactory(), false);
        int port = 3333;
        server.enqueue(new MockResponse().setBody("doesn't matter"));
        server.play(port);

        setEditFields("localhost", String.valueOf(port), "doesn't matter");
        activity.current_info.certHash = "Something which is not the real hash";
        activity.startApiCheck();
        assertThat(activity.current_state, equalTo(State.ERROR));
    }

    @Test
    public void testScanQRCode() {
        setQrCodeSCannerInstalled();
        String json = "{'base_url':'someurl', 'base_url_port':'1234', 'device_key':'somekey42'}";

        activity.btn_scan_qrcode.performClick();
        Intent intent = shadowActivity.getNextStartedActivityForResult().intent;
        assertThat(intent.getAction(), equalTo("com.google.zxing.client.android.SCAN"));

        activity.onActivityResult(0, Activity.RESULT_OK, new Intent().putExtra("SCAN_RESULT", json));

        assertThat(activity.current_state, equalTo(State.EDIT_CHANGED));
        assertThat(activity.current_info.baseUrl, equalTo("someurl"));
        assertThat(activity.current_info.baseUrlPort, equalTo(1234));
        assertThat(activity.current_info.deviceKey, equalTo("somekey42"));

        assertThat(activity.edit_base_url).hasText("someurl");
        assertThat(activity.edit_base_url_port).hasText("1234");
        assertThat(activity.edit_device_key).hasText("somekey42");
    }

    @Test
    public void testNoQRCodeREaderInstalled() {
        activity.btn_scan_qrcode.performClick();

        ShadowAlertDialog dialog = Robolectric.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(dialog.getTitle().toString(), equalTo(activity.getString(R.string.scanner_needed_dialog_title)));
    }

    private void setEditFields(String base_url, String port, String device_key) {
        activity.edit_base_url.setText(base_url);
        activity.edit_base_url_port.setText(port);
        activity.edit_device_key.setText(device_key);
    }

    private void setQrCodeSCannerInstalled() {
        ResolveInfo resolveInfo = new ResolveInfo();
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = "com.google.zxing.client.android";
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.applicationInfo = applicationInfo;
        resolveInfo.activityInfo.name = "ActivityName";
        packageManager.addResolveInfoForIntent(new Intent("com.google.zxing.client.android.SCAN"), resolveInfo);
    }

}
