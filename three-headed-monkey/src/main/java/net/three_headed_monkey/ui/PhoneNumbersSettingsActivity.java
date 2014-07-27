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
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication_;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.ui.adapter.PhoneNumberInfoListAdapter_;

import java.util.ArrayList;
import java.util.Arrays;

public class PhoneNumbersSettingsActivity extends Activity {

    private static final int PICK_CONTACT_REQUEST = 0;

    private ListView phoneNumbersListView;

    private PhoneNumberInfoListAdapter_ phoneNumberInfoListAdapter;

    private ThreeHeadedMonkeyApplication threeHeadedMonkeyApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_numbers_settings_activity);
        phoneNumbersListView = (ListView) findViewById(R.id.phone_numbers_settings_list_view);
        phoneNumberInfoListAdapter = PhoneNumberInfoListAdapter_.getInstance_(this);
        threeHeadedMonkeyApplication = (ThreeHeadedMonkeyApplication) getApplication();

        this.bindAdapter();
    }

    void bindAdapter() {
        phoneNumbersListView.setAdapter(phoneNumberInfoListAdapter);
        phoneNumbersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        phoneNumbersListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        long[] positions = phoneNumbersListView.getCheckedItemIds();
                        Arrays.sort(positions);
                        for (int currentPositionIndex = positions.length - 1; currentPositionIndex >= 0; currentPositionIndex--) {
                            threeHeadedMonkeyApplication.phoneNumberSettings.removePhoneNumber(phoneNumberInfoListAdapter.getItem((int) positions[currentPositionIndex]));
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
                launchAddNewPhoneNumberDialogWizard();
                return true;
            case R.id.action_delete:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    Bundle args = new Bundle();
                    args.putString("contactName", contactName);
                    args.putString("contactId", contactId);
                    SelectNumberFromContactsFragment f = SelectNumberFromContactsFragment.newInstance();
                    f.setArguments(args);
                    f.show(getFragmentManager(), "select");

                }

            }
        }
    }

    private void launchAddNewPhoneNumberDialogWizard() {
        DialogFragment newFragment = SelectNewPhoneNumberAddMethodDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }

    public static class SelectNewPhoneNumberAddMethodDialogFragment extends DialogFragment {

        public static SelectNewPhoneNumberAddMethodDialogFragment newInstance() {
            return new SelectNewPhoneNumberAddMethodDialogFragment();
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
                    } else if (which == 1) {
                        AddNewPhoneNumberDialogFragment.newInstance().show(fragmentTransaction, "add");
                    }
                }
            });

            return selectImportPhoneNumberDialog.create();
        }
    }

    public static class AddNewPhoneNumberDialogFragment extends DialogFragment {

        public static AddNewPhoneNumberDialogFragment newInstance() {
            return new AddNewPhoneNumberDialogFragment();
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
                public void onClick(DialogInterface dialog, int Button) {
                    String possibleNewPhoneNumber = inputNumber.getText().toString();
                    String possibleNewName = inputName.getText().toString();
                    if (possibleNewPhoneNumber == null || possibleNewName == null) {
                        Toast.makeText(getActivity(), R.string.phone_numbers_settings_add_dialog_error_message_not_all_values_set, Toast.LENGTH_SHORT).show();
                    } else if (!PhoneNumberUtils.isGlobalPhoneNumber(possibleNewPhoneNumber)) {
                        Toast.makeText(getActivity(), R.string.phone_numbers_settings_add_dialog_error_message_not_a_valid_phone_number, Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo(PhoneNumberUtils.formatNumber(possibleNewPhoneNumber), possibleNewName);
                        ((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.addPhoneNumber(phoneNumberInfo);
                        getFragmentManager().executePendingTransactions();
                    }
                }

            });

            return addNewPhoneNumberDialog.create();
        }
    }

    public static class SelectNumberFromContactsFragment extends DialogFragment {

        public static SelectNumberFromContactsFragment newInstance() {
            return new SelectNumberFromContactsFragment();
        }

        public ArrayList<Integer> selectedPhoneNumbers;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            selectedPhoneNumbers = new ArrayList<Integer>();

            final String contactName = getArguments().getString("contactName");
            final String contactId = getArguments().getString("contactId");
            final Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            final AlertDialog.Builder selectImportPhoneNumberDialog = new AlertDialog.Builder(getActivity());

            String title = getString(R.string.phone_numbers_settings_add_dialog_import_select_title);
            selectImportPhoneNumberDialog.setTitle(title + " " + contactName);
            selectImportPhoneNumberDialog.setMultiChoiceItems(phones,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            if (isChecked) {
                                String possibleNewPhoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                                if (possibleNewPhoneNumber == null || contactName == null) {
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.phone_numbers_settings_add_dialog_error_message_not_all_values_set, Toast.LENGTH_SHORT).show();
                                    ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                } else if (!PhoneNumberUtils.isGlobalPhoneNumber(possibleNewPhoneNumber)) {
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.phone_numbers_settings_add_dialog_error_message_not_a_valid_phone_number, Toast.LENGTH_SHORT).show();
                                    ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                } else {
                                    selectedPhoneNumbers.add(which);
                                }
                            }
                            else {
                                if(selectedPhoneNumbers.contains(which)) selectedPhoneNumbers.remove(which);
                            }

                        }

                    }
            );
            selectImportPhoneNumberDialog.setPositiveButton(R.string.phone_numbers_settings_add_dialog_button_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    for(int numberId : selectedPhoneNumbers) {
                        phones.moveToPosition(selectedPhoneNumbers.get(numberId));
                        String newPhoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                        int newPhoneNumberTypeId = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        CharSequence possibleNewPhoneNumberType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), newPhoneNumberTypeId, "");
                        PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo(PhoneNumberUtils.formatNumber(newPhoneNumber), contactName + " (" + possibleNewPhoneNumberType + ")");
                        ((ThreeHeadedMonkeyApplication_) getActivity().getApplication()).phoneNumberSettings.addPhoneNumber(phoneNumberInfo);
                        PhoneNumbersSettingsActivity activity = (PhoneNumbersSettingsActivity) getActivity();
                        activity.phoneNumberInfoListAdapter.notifyDataSetChanged();
                        getFragmentManager().executePendingTransactions();
                    }
                }
            });
            selectImportPhoneNumberDialog.setNegativeButton(R.string.phone_numbers_settings_add_dialog_button_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { }

            });


            return selectImportPhoneNumberDialog.create();
        }
    }

}
