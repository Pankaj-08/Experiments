package com.example.untitled;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.util.Log.e;

public class ls extends Service {
    public static boolean running = false;
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;

    public static String START_COMMAND = "ACTION_START_SERVICE";
    public static String STOP_COMMAND = "ACTION_STOP_SERVICE";
    public static String NOTIFICATION_COMMAND = "ACTION_UPDATE_NOTIFICATION";
    public static String POWER_SAVER_ON_COMMAND = "ACTION_POWER_ON";
    public static String POWER_SAVER_OFF_COMMAND = "ACTION_POWER_OFF";
    public static String INTERNET_CHECK_ON_COMMAND = "ACTION_INTERNET_ON";
    public static String INTERNET_CHECK_OFF_COMMAND = "ACTION_INTERNET_OFF";
    public static int FENCE_INTERVAL = 5000;
    public static int FENCE_FASTEST_INTERVAL = 5000;
    public static int FENCE_MAX_WAIT_TIME = 10000;
    public static int FENCE_TIME_DIFFERENCE_IN_OUT = 60000;
    public static int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static int MIN_DISTANCE = 10;
    public static int maxSpeed = 60;
    public static int INTERVAL = 1000;
    public static int FASTEST_INTERVAL = 1000;
    public static int MAX_WAIT_TIME = 5000;

    @Override
    public void onCreate() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this); 
        locationRequest = new LocationRequest();
//        SharedPrefUtils prefUtils = new SharedPrefUtils(this);
//        if (prefUtils.isFence() == 0) {
            locationRequest.setInterval(INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);
            locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
//        } else {
//            locationRequest.setInterval(FENCE_INTERVAL);
//            locationRequest.setFastestInterval(FENCE_FASTEST_INTERVAL);
//            locationRequest.setMaxWaitTime(FENCE_MAX_WAIT_TIME);
//        }
        locationRequest.setPriority(PRIORITY);
        locationRequest.setSmallestDisplacement(MIN_DISTANCE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && intent.getAction() != null) {
//            if (intent.getAction().equals(START_COMMAND)) {
                startTracking();
//            } else if (intent.getAction().equals(STOP_COMMAND)) {
//                stopTracking();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    stopForeground(true);
//                    stopSelfResult(startId);
//                } else {
//                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    manager.cancel(4444);
//                }
//            } else if (intent.getAction().equals(NOTIFICATION_COMMAND)) {
//                createNotification(intent.getStringExtra("text"));
//            } else if (intent.getAction().equals(POWER_SAVER_ON_COMMAND)) {
//                registerPowerSaverBroadcast(this);
//            } else if (intent.getAction().equals(POWER_SAVER_OFF_COMMAND)) {
//                unRegisterPowerSaverBroadcast(this);
//            } else if (intent.getAction().equals(INTERNET_CHECK_ON_COMMAND)) {
//                checkInternet();
//            } else if (intent.getAction().equals(INTERNET_CHECK_OFF_COMMAND)) {
//                unRegisterNetworkCallback();
//            }

//        }
        return START_STICKY;
    }

    public void startTracking() {
        running = true;
//        SharedPrefUtils prefUtils = new SharedPrefUtils(this);
//        prefUtils.tracking(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        }
        else
        {
            e("109", "ls -> startTracking  ->  : Not granted permission");
        }
        createNotification("Click to open map");
//        registerGPSBroadcast();
    }

//    private void registerGPSBroadcast() {
//        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
//        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
//        registerReceiver(locationSwitchStateReceiver, filter);
//    }

//    public void stopTracking() {
//        running = false;
//        locationProviderClient.removeLocationUpdates(getPendingIntent());
//        SharedPrefUtils prefUtils = new SharedPrefUtils(this);
//        prefUtils.tracking(false);
//        try {
//            unregisterReceiver(locationSwitchStateReceiver);
//        } catch (Exception e) {
//            e("104", "LocationService -> stopTracking: " + e.getMessage());
//        }
//    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), lr.class);
        intent.setAction(lr.ACTION_PROCESS_UPDATES);
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


//    private BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(context);
//            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
//                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//                if (!sharedPrefUtils.isPowerSave()) {
//                    if ((isGpsEnabled || isNetworkEnabled)) {
//                        if (!sharedPrefUtils.isGps()) {
//                            try {
//                                SQLiteHelper sqlHelper = new SQLiteHelper(context);
//                                sharedPrefUtils.preferences.edit()
//                                        .putBoolean(SharedPrefUtils.GPS, true)
//                                        .apply();
//                                sqlHelper.addAction(Action.GeoActivity.GPS, 1, System.currentTimeMillis(), 0);
//                                e("189", "LocationService -> onReceive: " + sharedPrefUtils.getGPSSignalInfo());
//                                if (!sharedPrefUtils.getGPSSignalInfo())
//                                    Utils.cancelGPSOnOffCheck(context);
//                            } catch (Exception e) {
//                                Utils.debug(context, e, e.getMessage());
//                            }
//                        }
//                    } else {
//                        if (sharedPrefUtils.isGps()) {
//                            try {
//                                SQLiteHelper sqlHelper = new SQLiteHelper(context);
//                                sharedPrefUtils.preferences.edit()
//                                        .putBoolean(SharedPrefUtils.GPS, false)
//                                        .apply();
//                                sqlHelper.addAction(Action.GeoActivity.GPS, 0, System.currentTimeMillis(), 0);
//                                if (!sharedPrefUtils.getGPSSignalInfo())
//                                    Utils.gpsOnOffCheck(context);
//                            } catch (Exception e) {
//                                Utils.debug(context, e, e.getMessage());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    };
//
//    BroadcastReceiver powerSaverChangeReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            new SQLiteHelper(context).addAction(
//                    Action.GeoActivity.Power_Saving,
//                    pm.isPowerSaveMode() ? 1 : 0,
//                    System.currentTimeMillis(),
//                    0);
//            new SharedPrefUtils(context).setPowerSave(pm.isPowerSaveMode());
//        }
//    };

//    public void registerPowerSaverBroadcast(Context context) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
//        context.registerReceiver(powerSaverChangeReceiver, filter);
//        e("226", "LocationService -> registerPowerSaverBroadcast: success");
//    }
//
//    public void unRegisterPowerSaverBroadcast(Context context) {
//        try {
//            context.unregisterReceiver(powerSaverChangeReceiver);
//            e("231", "LocationService -> unRegisterPowerSaverBroadcast: success ");
//        } catch (Exception e) {
//            e("235", "LocationService -> unRegisterPowerSaverBroadcast: failed " + e.getMessage());
//        }
//    }

//    public void checkInternet() {
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                connectivityManager.registerDefaultNetworkCallback(networkCallback);
//            } else {
//                NetworkRequest request = new NetworkRequest.Builder()
//                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
//                connectivityManager.registerNetworkCallback(request, networkCallback);
//            }
//        } catch (Exception e) {
//            e("269", "LocationService -> checkInternet  ->  : " + e.getMessage());
//        }
//    }

//    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//        @Override
//        public void onAvailable(@NonNull Network network) {
//            // network available
//            e("271", "LocationService -> onAvailable  ->  : " + network);
//            SharedPrefUtils prefUtils = new SharedPrefUtils(LocationService.this);
//            if (!prefUtils.isInternet()) {
//                new SQLiteHelper(LocationService.this).addAction(
//                        Action.GeoActivity.Internet,
//                        1,
//                        System.currentTimeMillis(),
//                        0);
//                prefUtils.setInternetOnOff(true);
//            }
//        }
//
//        @Override
//        public void onLost(@NonNull Network network) {
//            e("276", "LocationService -> onLost  ->  : " + network);
//            SharedPrefUtils prefUtils = new SharedPrefUtils(LocationService.this);
//            if (prefUtils.isInternet()) {
//                new SQLiteHelper(LocationService.this).addAction(
//                        Action.GeoActivity.Internet,
//                        0,
//                        System.currentTimeMillis(),
//                        0);
//                prefUtils.setInternetOnOff(false);
//            }
//        }
//    };
//
//    private void unRegisterNetworkCallback() {
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            connectivityManager.unregisterNetworkCallback(networkCallback);
//        }
//        catch (Exception e)
//        {
//            e("305", "LocationService -> unRegisterNetworkCallback  ->  : "+e.getMessage());
//        }
//    }

}
