package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.three_headed_monkey.R;


public class ApplicationLockActivity extends Activity {

    private static final String TAG = "ApplicationLockActivity";
    private static final String SHARED_PREFERENCES_PASSWORD_KEY = "__PASSWORD__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final Intent intent = new Intent(this, MainActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        LayoutInflater layoutInflater = this.getLayoutInflater();

        String password = getPasswordFromSharedPreferences();

        if (password == null) {
            startActivity(intent);
            this.finish();
        } else {
            alertDialogBuilder.setView(layoutInflater.inflate(R.layout.dialog_application_lock, null))
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ApplicationLockActivity.this.finish();
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ApplicationLockActivity.this.finish();
                        }
                    });

            super.onCreate(savedInstanceState);
            final AlertDialog dialog = alertDialogBuilder.create();

            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) dialog.findViewById(R.id.dialog_application_lock_login_password);
                    if (editText.getText().toString().equals(getPasswordFromSharedPreferences())) {
                        startActivity(intent);
                        dialog.dismiss();
                        finish();

                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.dialog_application_lock_login_error_wrong_password, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    private String getPasswordFromSharedPreferences() {
        Log.d(TAG, "Request Password from Preferences.");
        String password = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            password = sharedPreferences.getString(SHARED_PREFERENCES_PASSWORD_KEY, null);
            return password;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
