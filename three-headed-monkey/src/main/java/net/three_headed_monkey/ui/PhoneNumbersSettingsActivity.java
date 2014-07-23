package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.telephony.PhoneNumberUtils;

import net.three_headed_monkey.R;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication_;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.ui.adapter.PhoneNumberInfoListAdapter;
import net.three_headed_monkey.ui.adapter.PhoneNumberInfoListAdapter_;

import java.util.*;


public class PhoneNumbersSettingsActivity extends Activity {
    public static final int PICK_CONTACT_REQUEST = 0;

    TextView phoneNumberTextView;

    ListView phoneNumberListView;

    PhoneNumberInfoListAdapter_ adapter;

    ThreeHeadedMonkeyApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonenumbers_settings_activity);
        phoneNumberListView = (ListView) findViewById(R.id.phonenumbers_list);
        phoneNumberTextView = (TextView) findViewById(R.id.text_phonenumber);
        adapter = PhoneNumberInfoListAdapter_.getInstance_(this);
        application = (ThreeHeadedMonkeyApplication) getApplication();

        this.bindAdapter();
    }

    void bindAdapter() {
        phoneNumberListView.setAdapter(adapter);
        phoneNumberListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        phoneNumberListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        long[] positions = phoneNumberListView.getCheckedItemIds();
                        Arrays.sort(positions);
                        for (int currentPositionIndex = positions.length - 1; currentPositionIndex >= 0; currentPositionIndex--) {
                            application.phoneNumberSettings.removePhoneNumber(adapter.getItem((int) positions[currentPositionIndex]));
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.phonenumbers_settings_activity_context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.phonenumbers_settings_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addNewPhoneNumber();
                return true;
            case R.id.action_delete:

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            Log.i("PhoneNumbersSettingsActivity", "1");
            if (resultCode == Activity.RESULT_OK) {
                Log.i("PhoneNumbersSettingsActivity", "1");
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor.moveToFirst()) {
                    Log.i("PhoneNumbersSettingsActivity", "1");
                    final String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    Bundle args = new Bundle();
                    args.putString("contactName", contactName);
                    args.putString("contactId", contactId);
                    SelectNumberFromContactsFragment f = SelectNumberFromContactsFragment.newInstance();
                    f.setArguments(args);
                    getFragmentManager().executePendingTransactions();
                    f.show(getFragmentManager(), "select");

                    Log.i("PhoneNumbersSettingsActivity", "1");
                }

                Log.i("PhoneNumbersSettingsActivity", "1");

            }
    }


    private String addNewPhoneNumber() {
        DialogFragment newFragment = SelectImportMethodDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
        return null;
    }

    public static class SelectImportMethodDialogFragment extends DialogFragment {

        public static SelectImportMethodDialogFragment newInstance() {
            SelectImportMethodDialogFragment selectImportMethodDialogFragment = new SelectImportMethodDialogFragment();
            return selectImportMethodDialogFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder selectImportPhoneNumberDialog = new AlertDialog.Builder(getActivity());

            selectImportPhoneNumberDialog.setItems(R.array.phone_number_settings_select_import_dialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        getActivity().startActivityForResult(intent, PICK_CONTACT_REQUEST);
                        //SelectNumberFromContactsFragment.newInstance().show(fragmentTransaction, "select");
                    } else if (which == 1) {
                        AddNewNumberDialogFragment.newInstance().show(fragmentTransaction, "add");
                    }
                }
            });

            return selectImportPhoneNumberDialog.create();
        }
    }

    public static class AddNewNumberDialogFragment extends DialogFragment {

        public static AddNewNumberDialogFragment newInstance() {
            return new AddNewNumberDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder addNewPhoneNumberDialog = new AlertDialog.Builder(getActivity());
            LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
            final EditText inputName = new EditText(getActivity().getApplicationContext());
            inputName.setHint(R.string.phone_numbers_settings_add_dialog_label_name);
            inputName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(inputName);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText inputNumber = new EditText(getActivity().getApplicationContext());
            inputNumber.setInputType(InputType.TYPE_CLASS_PHONE);
            inputNumber.setHint(R.string.phone_numbers_settings_add_dialog_label_number);
            inputNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(inputNumber);

            addNewPhoneNumberDialog.setView(layout);
            addNewPhoneNumberDialog.setNegativeButton(R.string.phone_numbers_settings_add_dialog_button_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });


            addNewPhoneNumberDialog.setTitle(R.string.phone_numbers_settings_add_dialog_title);
            addNewPhoneNumberDialog.setCancelable(false);
            addNewPhoneNumberDialog.setPositiveButton(R.string.phone_numbers_settings_add_dialog_button_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int Buttton) {
                    String possibleNewPhonenumber = inputNumber.getText().toString();
                    String possibleNewName = inputName.getText().toString();
                    if (possibleNewPhonenumber == null || possibleNewName == null) {
                        Toast.makeText(getActivity(), R.string.phone_numbers_settings_add_dialog_error_message_not_all_values_set, Toast.LENGTH_SHORT).show();
                    } else if (((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.nameExists(possibleNewName)) {
                        Toast.makeText(getActivity(), R.string.phone_numbers_settings_add_dialog_error_message_associated_name_already_exists, Toast.LENGTH_SHORT).show();
                    } else if (!PhoneNumberUtils.isGlobalPhoneNumber(possibleNewPhonenumber)) {
                        Toast.makeText(getActivity(), R.string.phone_numbers_settings_add_dialog_error_message_not_a_valid_phone_number, Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo(PhoneNumberUtils.formatNumber(possibleNewPhonenumber), possibleNewName);
                        ((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.addPhoneNumber(phoneNumberInfo);
                        getFragmentManager().executePendingTransactions();
                    }
                }

            });
            final AlertDialog alertDialog = addNewPhoneNumberDialog.create();

            return alertDialog;
        }
    }

    public static class SelectNumberFromContactsFragment extends DialogFragment {

        public static SelectNumberFromContactsFragment newInstance() {
            SelectNumberFromContactsFragment selectNumberFromContactsFragment = new SelectNumberFromContactsFragment();
            return selectNumberFromContactsFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final String contactName = getArguments().getString("contactName");
            final String contactId = getArguments().getString("contactId");
            final Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            final AlertDialog.Builder selectImportPhoneNumberDialog = new AlertDialog.Builder(getActivity());
            selectImportPhoneNumberDialog.setTitle("Test");
            selectImportPhoneNumberDialog.setSingleChoiceItems(phones,
                    -1,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            phones.moveToPosition(which);
                            String possibleNewPhonenumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                            Log.i("PhoneNumbersSettingsActivity", possibleNewPhonenumber);
                            if (possibleNewPhonenumber == null || contactName == null) {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.phone_numbers_settings_add_dialog_error_message_not_all_values_set, Toast.LENGTH_SHORT).show();
                            } else if (((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.nameExists(contactName)) {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.phone_numbers_settings_add_dialog_error_message_associated_name_already_exists, Toast.LENGTH_SHORT).show();
                            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(possibleNewPhonenumber)) {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.phone_numbers_settings_add_dialog_error_message_not_a_valid_phone_number, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("PhoneNumbersSettingsActivity", "lets add something");
                                PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo(PhoneNumberUtils.formatNumber(possibleNewPhonenumber), contactName);
                                //((ThreeHeadedMonkeyApplication_)((Dialog)dialog).getContext().getApplicationContext()).phoneNumberSettings.addPhoneNumber(phoneNumberInfo);
                                ((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.addPhoneNumber(phoneNumberInfo);
                                getFragmentManager().executePendingTransactions();
                            }
                        }

                    }
            );
            final AlertDialog alertDialog = selectImportPhoneNumberDialog.create();
            return alertDialog;
        }
    }

}
