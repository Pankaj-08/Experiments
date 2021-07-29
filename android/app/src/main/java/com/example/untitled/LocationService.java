package com.example.untitled;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.util.Log.e;

public class LocationService extends Service {
    public static boolean running = false;
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;
//    public static String START_COMMAND = "ACTION_START_SERVICE";
    public static int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static int MIN_DISTANCE = 10;
    public static int INTERVAL = 1000;
    public static int FASTEST_INTERVAL = 1000;
    public static int MAX_WAIT_TIME = 5000;

    @Override
    public void onCreate() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
        locationRequest.setPriority(PRIORITY);
        locationRequest.setSmallestDisplacement(MIN_DISTANCE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTracking();
        return START_STICKY;
    }
    public void startTracking() {
        running = true;
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        }
        else
        {
            e("109", "ls -> startTracking  ->  : Not granted permission");
        }
        createNotification("Click to open map");
    }
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationReceiver.class);
        intent.setAction(LocationReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 9, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void createNotification(String text) {
        String NOTIFICATION_CHANNEL_ID = "com.codedevtech.emplitrack";
        String channelName = "Background Service";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(chan);
        }
//        SharedPrefUtils prefUtils = new SharedPrefUtils(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.drawable.only_icon_logo_white_scaled)
                .setContentTitle("Emplitrack is Tracking")
                .setContentText(text)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(4444, notificationBuilder.build());
        else
            manager.notify(4444, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}