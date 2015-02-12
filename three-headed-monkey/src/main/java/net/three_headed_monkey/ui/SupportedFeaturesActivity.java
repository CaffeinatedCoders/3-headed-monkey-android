package net.three_headed_monkey.ui;

import android.app.ActionBar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.utils.GcmUtils;
import net.three_headed_monkey.utils.RootUtils;
import net.three_headed_monkey.utils.SecureSettingsUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;


import eu.chainfire.libsuperuser.Shell;


@SuppressLint("Registered")
@EActivity(R.layout.activity_supported_features)
public class SupportedFeaturesActivity extends Activity {
    @ViewById
    TableLayout table_layout;

    private TableRow rootTableRow;

    @Override
    protected void onResume() {
        super.onResume();
        SecureSettingsUtils secureSettingsUtils = new SecureSettingsUtils(this);
        boolean secure_settings_available = secureSettingsUtils.testAccess();

        addRootTableEntry();
        addTableEntry("Secure Settings Accessable", secure_settings_available);
        addTableEntry("Secure Settings Location Settings", secure_settings_available && secureSettingsUtils.locationModeSettingsAvailable());
        addTableEntry("Google Play Services Available", GcmUtils.isPlayServicesAvailable(this));

        checkRoot();
    }

    private void addTableEntry(String text1, boolean positive) {
        addTableEntry(text1, positive ? "yes" : "no", positive);
    }

    private void addTableEntry(String text1, String text2, boolean text2positive) {
        TableRow row = createRow();
        setRowContent(row, text1, text2, text2positive);
        table_layout.addView(row);
    }

    private void addRootTableEntry() {
        TableRow row = createRow();
        setRowContent(row, "Root Access", "checking...", false);
        rootTableRow = row;
        table_layout.addView(row);
    }

    private TableRow createRow() {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView tv1 = new TextView(this);
        tv1.setPadding(10,0,0,0);
        TextView tv2 = new TextView(this);
        tv2.setPadding(10,0,0,0);
        row.addView(tv1);
        row.addView(tv2);
        return row;
    }

    @UiThread
    protected void setRowContent(TableRow row, String text1, String text2, boolean text2positive) {
        TextView tv1 = (TextView)row.getVirtualChildAt(0);
        TextView tv2 = (TextView)row.getVirtualChildAt(1);
        tv1.setText(text1);
        tv2.setText(text2);
        if(text2positive) {
            tv2.setTextColor(getResources().getColor(R.color.Positive));
        } else {
            tv2.setTextColor(getResources().getColor(R.color.Negative));
        }
    }

    @Background
    protected void checkRoot() {
        boolean root = Shell.SU.available();
        setRowContent(rootTableRow, "Root Access", root ? "yes" : "no", root);
    }

}
