package net.three_headed_monkey.smscommandsender;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    public static final short TARGET_DATASMS_PORT = 4273;

    @ViewById Spinner spinner_commands;
    @ViewById EditText edit_phone_number;
    @ViewById TextView text_shell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
        intentFilter.addDataScheme("sms");
        intentFilter.addDataAuthority("*", "7342");
        DataSmsReceiver dataSmsReceiver = new DataSmsReceiver();
        registerReceiver(dataSmsReceiver, intentFilter);
    }

    @Click(R.id.button_send)
    public void onSendButtonClicked() {
        SmsManager smsManager = SmsManager.getDefault();
        String command = spinner_commands.getSelectedItem().toString();
        String phone_number = edit_phone_number.getText().toString();
        try {
            byte[] data = command.getBytes("UTF-8");
            smsManager.sendDataMessage(phone_number, null, TARGET_DATASMS_PORT, data, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send datasms", e);
            Toast.makeText(this, "Failed to send datasms", Toast.LENGTH_LONG).show();
        }
    }

    public void addSmsCommandResponse(String text) {
        String appendix = "";
        if(text.contains("lat:") && text.contains("long:")) {
            String latitude ="";
            String longitude ="";
            for(String line : text.split("\n")) {
                line = line.replaceAll("\\s", "");
                if(line.contains("lat:")){
                    latitude = line.split(":")[1];
                }
                if(line.contains("long:")) {
                    longitude = line.split(":")[1];
                }
            }
            appendix = "https://maps.google.com/?q="+latitude+"+"+longitude+"\n";
        }
        text += "\n" + appendix;
        text_shell.setText(text_shell.getText() + "\n\n"+text);
    }

    private class DataSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DataSmsReceiver", "DataSmsReceiver called");
            Bundle bundle = intent.getExtras();
            if (bundle == null)
                return;
            Object[] pdus = (Object[]) bundle.get("pdus");
            if(pdus == null || pdus.length == 0)
                return;
            for (int i = 0; i < pdus.length; i++) {
                try {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    byte[] data = smsMessage.getUserData();
                    String message = new String(data, "UTF-8");
                    Log.d("DataSmsReceiver", "Datasms received: "+message);
                    MainActivity.this.addSmsCommandResponse(message);

                } catch (Exception e) {
                    Log.e("DataSmsReceiver", "Unexpected Error", e);
                }
            }
        }
    }

}
