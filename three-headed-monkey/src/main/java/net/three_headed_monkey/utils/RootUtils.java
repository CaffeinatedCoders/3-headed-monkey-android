package net.three_headed_monkey.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class RootUtils {
    public static final String TAG="ROOT UTILS";
    public static final String CURRENT_MOUNTS_COMMAND = "mount";
    public static final String REMOUNT_SYSTEM_RW_COMMAND = "mount -o rw,remount /system";
    public static final String REMOUNT_SYSTEM_RO_COMMAND = "mount -o ro,remount /system";
    public static String SETTINGS_BACKUP_FILENAME = "/system/etc/3hm-settings-backup.xml";
    public static String SET_SETTINGS_BACKUP_FILE_PERMISSIONS_COMMAND_PREFIX = "chmod 0664 ";

    private Context context;

    public RootUtils(Context context){
        this.context = context;
    }

    public void backupSettingsFile(){
        boolean wasSystemMountedRO = isSystemMountedReadOnly();

        if(wasSystemMountedRO){
            remountSystemRW();
            if(isSystemMountedReadOnly()){
                //remount seemed to have failed
                Log.e(TAG, "Remount RW failed!");
                //@todo display error?
                return;
            }
        }

        File backup_settings_file = new File(SETTINGS_BACKUP_FILENAME);
        if(!backup_settings_file.exists()){
            Shell.SU.run(new String[] {"touch " + SETTINGS_BACKUP_FILENAME, SET_SETTINGS_BACKUP_FILE_PERMISSIONS_COMMAND_PREFIX+SETTINGS_BACKUP_FILENAME});
        }

        backup_settings_file = new File(SETTINGS_BACKUP_FILENAME);
        if(!backup_settings_file.exists() || !backup_settings_file.canRead()){
            return;
        }

        String original_preference_file_path = PackageUtils.getPreferenceFilePath(context);
        Shell.SU.run("cat " + original_preference_file_path + " > " + backup_settings_file.getAbsolutePath());

        if(wasSystemMountedRO)
            remountSystemRO();

    }

    public void remountSystemRW(){
        Shell.SU.run(REMOUNT_SYSTEM_RW_COMMAND);
    }

    public void remountSystemRO(){
        Shell.SU.run(REMOUNT_SYSTEM_RO_COMMAND);
    }


    public boolean isSystemMountedReadOnly(){
        List<String> mount_output = Shell.SU.run(CURRENT_MOUNTS_COMMAND);
        if(mount_output == null)
            return true;
        for(String line : mount_output){
            if(line.contains(" /system ") && line.contains("ro")){
                return true;
            }
        }
        return false;
    }

}
