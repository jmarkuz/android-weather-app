package com.kuziv.android.sunshineapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class  SAuthenticatorService extends Service {

    private SAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new SAuthenticator(this);
    }

    /*
        When the system binds to this Service to make the RPC call
        return the authenticator's IBinder.
    */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
