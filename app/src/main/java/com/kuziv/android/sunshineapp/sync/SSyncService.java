package com.kuziv.android.sunshineapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Kuziv on 29.12.2015.
 */
public class SSyncService extends Service {

    private static SSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        if (syncAdapter == null) {
            syncAdapter = new SSyncAdapter(this, false);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
