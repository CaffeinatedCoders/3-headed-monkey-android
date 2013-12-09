package net.three_headed_monkey.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

public class PackageUtils {

    public static String getPackageName(Context context){
        PackageManager packageManager = context.getPackageManager();
        return context.getPackageName();
    }

    public static String getPreferenceFilePath(Context context){
        String packageName = getPackageName(context);
        if(packageName == null)
            return null;
        // todo: research preference path in different roms
        File f = new File("/data/data/"+packageName+"/shared_prefs/"+packageName+"_preferences.xml");
        if(f.exists())
            return f.getAbsolutePath();
        else
            return null;
    }

}
