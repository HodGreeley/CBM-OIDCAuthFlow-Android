package com.couchbase.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

public class Util {
    private static final String TAG = Util.class.getCanonicalName();

    // Retrieve meta-data from Android Manifest
    // e.g. <meta-data android:name="my_api_key" android:value="mykey123" />
    public static String getAppMetadata(Activity activity, String key) {
        String result = null;

        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            result = bundle.getString(key);
        } catch (PackageManager.NameNotFoundException | NullPointerException ex) {
            Log.w(TAG, "Failed to load meta-data: " + ex.getMessage());
        }

        return result;
    }

    public static <T> T instantiate(String className, Class<T> type, Class[] types, Object[] args) {
        try {
            return type.cast(Class.forName(className).getDeclaredConstructor(types).newInstance(args));
        } catch(final InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException ex) {
            Log.e(TAG, "Failed to instantiate class: " + ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }

    public static <T> T instantiate(String className, Class<T> type, Context context) {
        Class[] types = new Class[1];
        Object[] args = new Object[1];

        types[0] = Context.class;
        args[0] = context;

        return instantiate(className, type, types, args);
    }
}
