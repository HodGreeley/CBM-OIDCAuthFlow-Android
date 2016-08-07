package com.couchbase.android.cblite;

import android.content.Context;

import com.couchbase.lite.Database;

public interface Runtime {
    Context getContext();

    Database getDb();
    String getSyncUrl();
}
