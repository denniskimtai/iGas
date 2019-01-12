package com.codegreed_devs.i_gas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String s) {
        saveToken(s);
        super.onNewToken(s);
    }

    //save token to shared preference
    //and to database if user is logged in
    private void saveToken(String token) {

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID + "SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("FCMToken", token);
        editor.apply();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseDatabase
                    .getInstance()
                    .getReference("clients")
                    .child(sharedPref.getString("ClientId", ""))
                    .child("fcm_token")
                    .setValue(token)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful() && task.getException() != null)
                                Log.e("DATABASE ERROR", task.getException().getMessage());
                        }
                    });

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null && remoteMessage.getData() != null)
            showNotification(remoteMessage.getNotification(), remoteMessage.getData());

    }

    //show pop up notification with passed body
    private void showNotification(RemoteMessage.Notification notification, @NonNull Map<String, String> messageData) {

        String vendorId = messageData.get("vendor_id");
        String orderId = messageData.get("order_id");

        Log.e("NOTIFICATION","Notification: " + notification.toString() + ", Data : " + messageData.toString());

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        int id = (int) System.currentTimeMillis();

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(notification.getTitle() + "\n" + notification.getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notificationBuilder.build());

    }
}
