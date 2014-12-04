package net.three_headed_monkey;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import net.three_headed_monkey.commands.CommandPrototypeManager;
import net.three_headed_monkey.data.PhoneNumberSettings;
import net.three_headed_monkey.data.ServiceSettings;
import net.three_headed_monkey.data.SimCardSettings;
import net.three_headed_monkey.service.PassiveLocationUpdatesReceiver;
import net.three_headed_monkey.service.PeriodicWorkReceiver;
import net.three_headed_monkey.utils.RootUtils;
import net.three_headed_monkey.utils.SystemSettings;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.SystemService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@EApplication
public class ThreeHeadedMonkeyApplication extends Application {
    public static final String TAG = "ThreeHeadedMonkeyApplication";
    public SimCardSettings simCardSettings;
    public PhoneNumberSettings phoneNumberSettings;
    public CommandPrototypeManager commandPrototypeManager;
    public ServiceSettings serviceSettings;

    @SystemService
    LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        simCardSettings = new SimCardSettings(this);
        phoneNumberSettings = new PhoneNumberSettings(this);
        commandPrototypeManager = new CommandPrototypeManager(this);
        commandPrototypeManager.initPrototypes();
        serviceSettings = new ServiceSettings(this);

        load();

        if ((new File(RootUtils.SETTINGS_BACKUP_FILENAME)).exists() && simCardSettings.getAll().isEmpty() && phoneNumberSettings.getAll().isEmpty() && serviceSettings.getAll().isEmpty()) {
            try {
                restoreSettingsFromBackup();
            } catch (Exception e) {
                Log.e(TAG, "Restoring settings backup failed!!!");
                Log.e(TAG, e.getMessage());
            }
        }

        SystemSettings systemSettings = new SystemSettings(this);
        systemSettings.applyAll();

        registerPassiveLocationUpdates();
        // make sure PeriodicWorkReceiver is registered in case of force close
        PeriodicWorkReceiver.registerPeriodicWorkReceiver(this);

    }

    public void restoreSettingsFromBackup() throws Exception {

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();

        InputStream inputStream = new FileInputStream(new File(RootUtils.SETTINGS_BACKUP_FILENAME));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(inputStream);
        Element root = document.getDocumentElement();

        Node child = root.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;

                String type = element.getNodeName();
                String name = element.getAttribute("name");

                // In my app, all prefs seem to get serialized as either "string" or
                // "boolean" - this will need expanding if yours uses any other types!
                if (type.equals("string")) {
                    String value = element.getTextContent();
                    prefs.putString(name, value);
                } else if (type.equals("boolean")) {
                    String value = element.getAttribute("value");
                    prefs.putBoolean(name, value.equals("true"));
                } else if (type.equals("float")) {
                    float value = Float.valueOf(element.getAttribute("value"));
                    prefs.putFloat(name, value);
                } else if (type.equals("float")) {
                    int value = Integer.valueOf(element.getAttribute("value"));
                    prefs.putInt(name, value);
                }
            }

            child = child.getNextSibling();
        }

        prefs.commit();
        load();
    }

    public void load() {
        simCardSettings.load();
        phoneNumberSettings.loadSettings();
        serviceSettings.load();
    }

    public void registerPassiveLocationUpdates() {
        Intent intent = new Intent(PassiveLocationUpdatesReceiver.INTENT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long minTime = 5000;
        float minDistance = 0;
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTime, minDistance, pendingIntent);

    }

}
