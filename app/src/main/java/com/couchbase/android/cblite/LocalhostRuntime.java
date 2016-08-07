package com.couchbase.android.cblite;

import android.content.Context;

import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

public class LocalhostRuntime implements Runtime {
    private static final String TAG = LocalhostRuntime.class.getCanonicalName();

    public static final String HOST = "localhost";
    public static final String IP = "192.168.56.1";

    private static Context context;
    private static Database db;

    private static final String DB_NAME = "db";
    private static final String SG_URL = "http://192.168.56.1:4984/grocery-sync/";

    LocalhostRuntime(Context context) {
        LocalhostRuntime.context = context;

        try {
            Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            db = manager.getDatabase(DB_NAME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Database getDb() {
        return db;
    }

    @Override
    public String getSyncUrl() {
        return SG_URL;
    }
}
