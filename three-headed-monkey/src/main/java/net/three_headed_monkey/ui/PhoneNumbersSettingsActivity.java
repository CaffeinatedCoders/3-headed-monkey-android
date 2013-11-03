package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.three_headed_monkey.R;
import com.googlecode.androidannotations.annotations.*;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.ui.adapter.PhoneNumberInfoListAdapter;

@EActivity(R.layout.phonenumbers_settings_activity)
public class PhoneNumbersSettingsActivity extends Activity {

    @ViewById
    TextView text_phonenumber;

    @ViewById
    ListView phonenumbers_list;

    @Bean
    PhoneNumberInfoListAdapter adapter;

    @App
    ThreeHeadedMonkeyApplication application;

    @Trace
    @AfterViews
    void bindAdapter(){
        phonenumbers_list.setAdapter(adapter);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.phonenumbers_settings_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                addNewPhoneNumber();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String addNewPhoneNumber() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Add new phonenumber");
        alertDialogBuilder.setCancelable(false);
        final EditText input = new EditText(this);
        input.setHint("hint");
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo(input.getText().toString());

                application.phoneNumberSettings.addPhoneNumber(phoneNumberInfo);

            }
        })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        return null;
    }

}
