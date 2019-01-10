package com.codegreed_devs.i_gas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        sharedPref = getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID + "SHARED_PREF", Context.MODE_PRIVATE);
        editor.putString("FCMToken", s);
        editor.commit();
    }
}
