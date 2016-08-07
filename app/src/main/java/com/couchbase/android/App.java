package com.couchbase.android;

import android.app.Application;

import com.couchbase.android.cblite.Runtime;
import com.couchbase.android.cblite.RuntimeFactory;
import com.couchbase.android.cblite.oidcauthflow.R;

public class App extends Application {
    /*
     * Static data must always be
     * reinitialized in onCreate
     * in case the application is
     * destroyed and recreated.
     * See http://www.developerphil.com/dont-store-data-in-the-application-object/
     */
    private static Runtime runtime;

    @Override
    public void onCreate() {
        super.onCreate();

        runtime = RuntimeFactory.create(getApplicationContext(), getString(R.string.runtime));
    }

    public static Runtime getRuntime() {
        return runtime;
    }
}
