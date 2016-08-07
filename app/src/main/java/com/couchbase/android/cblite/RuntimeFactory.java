package com.couchbase.android.cblite;

import android.content.Context;

import com.couchbase.android.Util;

public class RuntimeFactory {
    public static Runtime create(Context context, String classId) {
        try {
            return Util.instantiate(classId, Runtime.class, context);
        } catch(final IllegalStateException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
