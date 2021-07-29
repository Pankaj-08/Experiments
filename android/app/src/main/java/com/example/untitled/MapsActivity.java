//package com.example.untitled;
//
//import android.Manifest;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.preference.PreferenceManager;
//
//import androidx.core.app.ActivityCompat;
//
//import com.google.firebase.installations.Utils;
//
//import org.json.JSONObject;
//
//import static android.content.Context.LOCATION_SERVICE;
//
//public class MapsActivity {
//
//    private LocationDBHelper locationHelper;
//    private VisitDBHelper helper;
//    public static VisitHelper visitHelper;
//    private MapHelper map;
//	public static Activity activity;
//	public MapMeetingAdapter meetingAdapter;
//	Apis apis;
//
//	private TextView distanceText;
//    private TextView timeText, officeTimeText;
//
//    private Button mainButton;
//
//    private SlidingUpPanelLayout slidingUpPanelLayout;
//
//    private LinearLayout panel;
//
//    private AnimatedMenu mainMenu;
//
//    private boolean isFollowing = false;
//	boolean isFirstTime = true;
//	boolean isHead;
//
//	private ScheduledFuture<?> locationReadingTask;
//    SimpleDateFormat clockInDateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.US);
//
//    private Rect bound;
//    NotificationManager manager;
//    NotificationCompat.Builder builder;
//    ScaleAnimation scaleAnimation;
//    TranslateAnimation translateAnimation;
//    ImageView locationIcon;
//
//    public Marker myLocation;
//    private JSONObject locationData;
//
//    public final int RUNNING = 2;
//    public final int STARTED = 3;
//    public final int START = 6;
//    public final int ONBREAK = 4;
//    public final int MEETING = 5;
//    public final int INOFFICE = 7;
//    public int currentState = START;
//	private final double minDistance = 100;
//
//	@SuppressLint("SetTextI18n")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        try {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_maps);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            activity = MapsActivity.this;
//            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.cancel(5562);
//            apis = new Apis(this);
//            Intent in = new Intent(getApplicationContext(), MapsActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, in, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder = new NotificationCompat.Builder(this, "emplitrack")
//                    .setSmallIcon(R.drawable.only_icon_logo_white_scaled)
//                    .setContentTitle("Office Mode")
//                    .setContentText("Your are in Office")
//                    .setAutoCancel(false)
//                    .setOngoing(true)
//                    .setContentIntent(pendingIntent);
//            manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel("emplitrack", "channelName", NotificationManager.IMPORTANCE_LOW);
//                channel.setLightColor(Color.BLUE);
//                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//                manager.createNotificationChannel(channel);
//            }
//        } catch (Exception e) {
//            Utils.debug(this, e, e.getMessage());
//            finish();
//            startActivity(new Intent(MapsActivity.this, MainActivity.class));
//        }
//        try {
//            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode()) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_MaterialComponents_Light_Dialog)
//                        .setTitle("Battery Saver")
//                        .setMessage("Your battery saver is On!, that will decrease accuracy of tracker. It is recommended to turn off battery saver.")
//                        .setCancelable(false)
//                        .setPositiveButton("Ok", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                TextView txt = dialog.findViewById(android.R.id.message);
//                assert txt != null;
//                txt.setTextSize(15);
//                txt.setTextColor(Utils.getColor(activity, R.color.textColor));
//            }
//
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//            assert mapFragment != null;
//            mapFragment.getMapAsync(this);
//            slidingUpPanelLayout = findViewById(R.id.map_layout);
//            panel = findViewById(R.id.map_panel);
//
//            distanceText = findViewById(R.id.map_panel_distance);
//            timeText = findViewById(R.id.map_panel_time);
//            officeTimeText = findViewById(R.id.map_panel_office_time);
//            helper = new VisitDBHelper(this);
//
//
//            SharedPrefUtils preferences = new SharedPrefUtils(this);
//            isHead = preferences.preferences.getBoolean("head", false);
//            if (preferences.isTracking()) {
//                Intent serviceIntent = new Intent(this, LocationService.class);
//                serviceIntent.setAction(LocationService.NOTIFICATION_COMMAND);
//                serviceIntent.putExtra("text", "Emplitrack is tracking");
//                startService(serviceIntent);
//            }
//            ((TextView) findViewById(R.id.text_location)).setText(preferences.preferences.getString(SharedPrefUtils.FIRST_NAME, "") + " (" + preferences.myid().split("\\$")[1] + ")");
//
//            long time = preferences.clockIn();
//            if (time != 0) {
//                String clockInTime = "Clock in : " + clockInDateFormat.format(time);
//                ((TextView) findViewById(R.id.clock_in_time)).setText(clockInTime);
//            }
//            findViewById(R.id.full_path_button).setOnClickListener(view -> {
//                try {
//                    focusAllPaths(view);
//                } catch (Exception e) {
//                    Utils.debug(this, e, e.getMessage());
//                    e.printStackTrace();
//                }
//            });
//            setupButtons();
//            locationHelper = new LocationDBHelper(this);
//        } catch (Exception e) {
//            Utils.debug(this, e, e.getMessage());
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        try {
//            map = new MapHelper(this, googleMap, 0f);
//            googleMap.setPadding(0, 0, 0, (int) com.harsh.hkutils.Utils.dpToPixel(15, this));
//            if (ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Snackbar.make(slidingUpPanelLayout, "Location permission require", Snackbar.LENGTH_SHORT).show();
//                return;
//            }
//
//            googleMap.setOnCameraMoveStartedListener(i -> {
//                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//                    isFollowing = false;
//                }
//            });
//
//            SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
//            visitHelper = new VisitHelper(this, helper.getVisits(), map);
//
//            map.setInfoWindowClick(visitHelper.visits, true);
//            RecyclerView visitList = findViewById(R.id.swipe_up_meeting_recycler_view);
//            visitList.setLayoutManager(new LinearLayoutManager(this));
//            visitList.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
//
//            meetingAdapter = new MapMeetingAdapter(this, visitHelper.visits, Action.fromJSONArray(sqLiteHelper.getActions()), slidingUpPanelLayout, googleMap);
//            visitList.setAdapter(meetingAdapter);
//            if (meetingAdapter.getItemCount() > 0)
//                findViewById(R.id.swipe_up_meeting_recycler_empty_view).setVisibility(View.GONE);
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//            if (preferences.getBoolean("tracking", false))
//                startReadingLocations();
//            else
//                loadPaths();
//
//            FloatingActionButton satellite = findViewById(R.id.satellite_view_option_button);
//            satellite.setOnClickListener(v -> {
//                if (googleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
//                    satellite.setImageResource(R.drawable.normal_view);
//                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                } else {
//                    satellite.setImageResource(R.drawable.satellite_view);
//                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                }
//
//            });
//
//        } catch (Exception e) {
//            Utils.debug(this, e, e.getMessage());
//        }
//    }
//
//    private void updateMyLocation(LatLng latLng) throws Exception {
//        if (myLocation == null) {
//            myLocation = map.addCurrentLocation(latLng);
//        } else {
//            myLocation.setPosition(latLng);
//        }
//    }
//
//    private void startReadingLocations() throws Exception {
//        mainButton.setText("Please wait...");
//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        locationReadingTask = scheduledExecutorService.scheduleAtFixedRate(() -> {
//            try {
//                loadPaths();
//            } catch (Exception e) {
//                Utils.debug(this, e, e.getMessage());
//            }
//        }, 0, 10, TimeUnit.SECONDS);
//    }
//
//    private void setupButtons() throws Exception {
//        mainButton = findViewById(R.id.map_panel_main_btn);
//        locationIcon = findViewById(R.id.map_panel_location_icon);
//        mainMenu = findViewById(R.id.main_menu);
//        mainMenu.addButton(R.drawable.ic_coffie, "Break !", view -> {
//            for (TextView s : mainMenu.labels) {
//                s.setClickable(false);
//            }
//            for (FloatingActionButton s : mainMenu.buttons) {
//                s.setClickable(false);
//            }
//            mainMenu.toggleMenu();
//            try {
//                breakContinueClick();
//            } catch (Exception e) {
//                Utils.debug(this, e, e.getMessage());
//            }
//        })
//                .addButton(R.drawable.ic_baseline_work_24, "In Office", view -> {
//                    mainMenu.setClickable(false);
//                    for (TextView s : mainMenu.labels) {
//                        s.setClickable(false);
//                    }
//                    for (FloatingActionButton s : mainMenu.buttons) {
//                        s.setClickable(false);
//                    }
//                    mainMenu.toggleMenu();
//                    try {
//                        if (Utils.isMarkerAdded)
//                            InOfficeNotification();
//                        else
//                            Snackbar.make(view, "GPS not detected, Please wait!", BaseTransientBottomBar.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        Utils.debug(this, e, e.getMessage());
//                    }
//                })
//                .addButton(R.drawable.ic_home, "Home", v -> {
//                    for (TextView s : mainMenu.labels) {
//                        s.setClickable(false);
//                    }
//                    for (FloatingActionButton s : mainMenu.buttons) {
//                        s.setClickable(false);
//                    }
//                    mainMenu.toggleMenu();
//                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
//                });
//        if (isHead)
//            mainMenu.addButton(R.drawable.ic_group, "Team", v -> {
//                for (TextView s : mainMenu.labels) {
//                    s.setClickable(false);
//                }
//                for (FloatingActionButton s : mainMenu.buttons) {
//                    s.setClickable(false);
//                }
//                mainMenu.toggleMenu();
//                startActivity(new Intent(MapsActivity.this, TeamListActivity.class));
//            });
//
//        for (TextView t : mainMenu.labels) {
//            t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);
//        }
//
//        mainButton.setOnClickListener(view -> {
//            if (currentState == START) {
//                createAlertDialog(currentState, "Are you sure you want to start your day?");
//            } else if (currentState == RUNNING) {
//                createAlertDialog(currentState, "Are you sure you want to mark visit?");
//            } else if (currentState == MEETING) {
//                visitHelper.endVisit();
//            }
//        });
//    }
//
//    private void createAlertDialog(int currentState, String message) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this,
//                R.style.Theme_MaterialComponents_Light_Dialog)
//                .setTitle("Confirm")
//                .setMessage(message)
//                .setPositiveButton("Yes", (dialogInterface, i) -> {
//                    if (currentState == RUNNING) {
//                        try {
//                            if (visitHelper.mark(getLastKnownLocation(), locationData.getDouble("distance"))) {
//                                updateUI(MEETING);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Route Not found", Toast.LENGTH_SHORT).show();
//                            Utils.debug(this, e, e.getMessage());
//                        }
//                    } else if (currentState == START) {
//                        try {
//                            breakContinueClick();
//                        } catch (Exception e) {
//                            Utils.debug(this, e, e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                })
//                .setNegativeButton("No", null);
//        final AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(dialogInterface -> {
//            TextView txt = dialog.findViewById(android.R.id.message);
//            Objects.requireNonNull(txt).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
//            DialogTitle dialogTitle = dialog.findViewById(R.id.alertTitle);
//            Objects.requireNonNull(dialogTitle).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);
//            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//            btn.setBackgroundColor(Color.WHITE);
//            btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
//            btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//            btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
//            btn.setBackgroundColor(Color.WHITE);
//        });
//        dialog.show();
//    }
//
//    ObjectAnimator locationRotateAnimator;
//
//    private void startAnimation() {
//        locationIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                locationIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int width = locationIcon.getWidth();
//                int height = locationIcon.getHeight();
//                if (scaleAnimation == null) {
//                    scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f);
//                    scaleAnimation.setDuration(500);
//                    scaleAnimation.setRepeatCount(Animation.INFINITE);
//                    scaleAnimation.setRepeatMode(Animation.REVERSE);
//                }
//                if (translateAnimation == null) {
//                    translateAnimation = new TranslateAnimation(0, -(width * 0.1f), 0, -(height * 0.1f));
//                    translateAnimation.setDuration(500);
//                    translateAnimation.setRepeatCount(Animation.INFINITE);
//                    translateAnimation.setRepeatMode(Animation.REVERSE);
//                }
//                if (locationRotateAnimator == null) {
//                    locationRotateAnimator = ObjectAnimator.ofFloat(locationIcon, "rotationY", 0.0f, 180);
//                    locationRotateAnimator.setDuration(1000);
//                    locationRotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
//                    locationRotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//                }
//                locationRotateAnimator.start();
//
//                AnimationSet set = new AnimationSet(true);
//                set.addAnimation(translateAnimation);
//                set.addAnimation(scaleAnimation);
//                locationIcon.startAnimation(set);
//            }
//        });
//    }
//
//    private void stopAnimation() {
//        locationIcon.clearAnimation();
//        if (locationRotateAnimator != null)
//            locationRotateAnimator.cancel();
//        locationIcon.setRotationY(0);
//    }
//
//
//    public void breakContinueClick() throws Exception {
//        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tracking", false) || currentState == INOFFICE) {
//            DialogUtils.iconDialog(this,
//                    R.drawable.ic_warning,
//                    "Wanna Break !",
//                    "Are you sure want to break?",
//                    "Yes",
//                    dialog -> {
//                        dialog.dismiss();
//                        manager.cancel(1000);
//                        SharedPrefUtils preferences = new SharedPrefUtils(this);
//                        if (currentState == INOFFICE) {
//                            preferences.setOfficeTime();
//                            SQLiteHelper helper = new SQLiteHelper(this);
//                            helper.addAction(Action.GeoActivity.Office, 1, System.currentTimeMillis(), 0);
//                            meetingAdapter = new MapMeetingAdapter(this, visitHelper.visits, Action.fromJSONArray(helper.getActions()), slidingUpPanelLayout, map.gmap);
//                            RecyclerView visitList = findViewById(R.id.swipe_up_meeting_recycler_view);
//                            visitList.setAdapter(meetingAdapter);
//                        }
//                        try {
//                            stopTracking();
//                        } catch (Exception e) {
//                            Utils.debug(this, e, e.getMessage());
//                            e.printStackTrace();
//                        }
//                        updateUI(ONBREAK);
//                        SQLiteHelper helper = new SQLiteHelper(this);
//                        helper.addAction(Action.GeoActivity.Break, 0, System.currentTimeMillis(), 0);
//                    },
//                    "Cancel", null);
//        } else {
//            if (currentState == START) {
//                apis.startDay(MapsActivity.this, object -> {
//                    if (object.getString("status").equals("success")) {
//                        Utils.isMarkerAdded = false;
//                        SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
//                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
//                        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                        boolean currentlyPowerSaving = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode();
//                        long currentTime = System.currentTimeMillis();
//                        if (currentlyPowerSaving) sqLiteHelper.addAction(
//                                Action.GeoActivity.Power_Saving,
//                                1,
//                                currentTime,
//                                currentTime);
//                        sharedPrefUtils.preferences.edit()
//                                .putBoolean(SharedPrefUtils.POWER_SAVING, currentlyPowerSaving)
//                                .apply();
//                        long clock_in_time = object.getLong("clock_in");
//                        runOnUiThread(() -> {
//                            try {
//                                startTracking();
//                            } catch (Exception e) {
//                                Utils.debug(this, e, e.getMessage());
//                                e.printStackTrace();
//                            }
//                            ((TextView) findViewById(R.id.clock_in_time)).setText("Clock in : " + clockInDateFormat.format(clock_in_time));
//                            updateUI(STARTED);
//                            mainMenu.toggleMenu();
//                        });
//                    }
//                }, null);
//            } else {
//                startTracking();
//                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                SQLiteHelper helper = new SQLiteHelper(this);
//                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
//                if (!sharedPrefUtils.isGps() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    sharedPrefUtils.preferences.edit()
//                            .putBoolean(SharedPrefUtils.GPS, true)
//                            .apply();
//                    helper.addAction(Action.GeoActivity.GPS, 1, System.currentTimeMillis(), 0);
//                }
//                helper.addAction(Action.GeoActivity.Break, 1, System.currentTimeMillis(), 0);
//                findViewById(R.id.swipe_up_meeting_recycler_empty_view).setVisibility(View.GONE);
//                meetingAdapter = new MapMeetingAdapter(this, visitHelper.visits, Action.fromJSONArray(helper.getActions()), slidingUpPanelLayout, map.gmap);
//                RecyclerView visitList = findViewById(R.id.swipe_up_meeting_recycler_view);
//                visitList.setAdapter(meetingAdapter);
//            }
//        }
//    }
//
//    public void updateUI(int state) {
//        currentState = state;
//        int maintext = R.string.start;
//        int background = R.drawable.btn_background;
//        String firstText = mainMenu.labels.get(0).getText().toString();
//        String secondText = mainMenu.labels.get(1).getText().toString();
//        mainMenu.labels.get(1).setEnabled(true);
//        mainMenu.buttons.get(1).setEnabled(true);
//
//        switch (state) {
//            case START:
//                stopAnimation();
//                maintext = R.string.start;
//                background = R.drawable.btn_background;
//                mainMenu.labels.get(1).setEnabled(false);
//                mainMenu.buttons.get(1).setEnabled(false);
//                firstText = "Start";
//                break;
//            case STARTED:
//                startAnimation();
//                maintext = R.string.your_day_started;
//                background = R.drawable.btn_background;
//                firstText = "Stop Driving";
//                break;
//            case RUNNING:
//                startAnimation();
//                maintext = R.string.mark;
//                background = R.drawable.rounded_corner_green_dark;
//                firstText = "Stop Driving";
//                break;
//            case ONBREAK:
//                stopAnimation();
//                maintext = R.string.on_break;
//                background = R.drawable.rounded_corner_orange_dark;
//                firstText = "Continue Driving";
//                secondText = "In Office";
//                mainMenu.labels.get(1).setEnabled(false);
//                break;
//            case MEETING:
//                stopAnimation();
//                maintext = R.string.end_visit;
//                firstText = "Stop Driving";
//                background = R.drawable.rounded_corner_red_dark;
//                break;
//            case INOFFICE:
//                stopAnimation();
//                maintext = R.string.officemode;
//                background = R.drawable.rounded_corner_blue_light;
//                secondText = "Leave Office";
//                firstText = "Break !";
//                break;
//        }
//        mainButton.setText(maintext);
//        mainButton.setBackgroundResource(background);
//        mainMenu.labels.get(0).setText(firstText);
//        mainMenu.labels.get(1).setText(secondText);
//        SharedPrefUtils preferences = new SharedPrefUtils(this);
//        String officeTimeString = "Office Time : " + Utils.timeString(preferences.getOfficeTime(), true, true);
//        if (officeTimeText != null) officeTimeText.setText(officeTimeString);
//    }
//
//    private void loadPaths() throws Exception {
//        JSONObject allData = locationHelper.getTrackData(false);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        runOnUiThread(() -> {
//            boolean tracking = sharedPreferences.getBoolean("tracking", false);
//            locationData = allData;
//            map.drawData(locationData, false);
//
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && tracking) {
//                try {
//                    stopTracking();
//                    onGPS();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Utils.debug(this, e, e.getMessage());
//                }
//            }
//
//            LatLng latLng = getLastKnownLocation();
//            if (latLng != null) {
//                try {
//                    updateMyLocation(latLng);
//                } catch (Exception e) {
//                    Utils.debug(this, e, e.getMessage());
//                    e.printStackTrace();
//                }
//                if (isFollowing)
//                    map.focus(latLng);
//                if (isFirstTime) {
//                    isFirstTime = false;
//                    map.gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//                }
//            }
//            if (timeText != null) {
//                String timeString = null;
//                try {
//                    timeString = "Driving Time : " + Utils.timeString(allData.getLong("time"), true);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                timeText.setText(timeString);
//            }
//
//            slidingUpPanelLayout.setTouchEnabled(true);
//            slidingUpPanelLayout.setDragView(findViewById(R.id.map_panel_head));
//            SharedPrefUtils preferences = new SharedPrefUtils(MapsActivity.this);
//            long clockIn = preferences.clockIn();
//
//            double distance = 0;
//            try {
//                distance = allData.getDouble("distance");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (distanceText != null)
//                distanceText.setText(String.format(Locale.US, "%.2f " + (distance > 1000 ? "Km" : "Meters") + " Driven", (distance > 1000 ? distance / 1000 : distance)));
//            if (preferences.office_start() != 0) {
//                updateUI(INOFFICE);
//                manager.notify(1000, builder.build());
//            } else {
//                if (distance < minDistance) {
//                    updateUI(clockIn == 0 ? START : (tracking ? STARTED : ONBREAK));
//                } else {
//                    if (tracking)
//                        updateUI(visitHelper.pendingVisit == null ? RUNNING : MEETING);
//                    else updateUI(clockIn == 0 ? START : ONBREAK);
//                }
//            }
//        });
//    }
//
//    public void myLocation(View view) throws Exception {
//        LatLng latLng = getLastKnownLocation();
//        if (latLng != null) {
//            updateMyLocation(latLng);
//        }
//
//        if (map != null && myLocation != null && latLng != null) {
//            isFollowing = true;
//            map.focusMyLocation(latLng);
//        }
//    }
//
//    public void back(View view) {
//        onBackPressed();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mainMenu.isMenuOpen) {
//            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            if (mainMenu.isMenuOpen)
//                mainMenu.toggleMenu();
//        } else if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tracking", false)
//                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Display d = getWindowManager().getDefaultDisplay();
//            Point p = new Point();
//            d.getSize(p);
//            Rational ratio = new Rational(p.x, p.x);
//            bound = new Rect(0, 0, 100, 100);
//            PictureInPictureParams.Builder pip_Builder = new PictureInPictureParams.Builder();
//            pip_Builder.setAspectRatio(ratio).setSourceRectHint(bound).build();
//            enterPictureInPictureMode(pip_Builder.build());
//            Intent i = new Intent(MapsActivity.this, MainActivity.class);
//            startActivity(i);
//        } else {
//            Intent i = new Intent(MapsActivity.this, MainActivity.class);
//            startActivity(i);
//            finish();
//        }
//    }
//
//    @Override
//    protected void onUserLeaveHint() {
//        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mainMenu.isMenuOpen) {
//            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            if (mainMenu.isMenuOpen)
//                mainMenu.toggleMenu();
//        } else if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tracking", false)
//                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Display d = getWindowManager().getDefaultDisplay();
//            Point p = new Point();
//            d.getSize(p);
//            Rational ratio;
//            PictureInPictureParams.Builder pip_Builder;
//            ratio = new Rational(p.x, p.x);
//            bound = new Rect(0, 0, 1000, 1000);
//            pip_Builder = new PictureInPictureParams.Builder();
//            pip_Builder.setAspectRatio(ratio).setSourceRectHint(bound).build();
//            enterPictureInPictureMode(pip_Builder.build());
//        } else {
//            super.onUserLeaveHint();
//        }
//    }
//
//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (isInPictureInPictureMode()) {
//                panel.setAlpha(0);
//                findViewById(R.id.back_button).setAlpha(0);
//                findViewById(R.id.location_button).setAlpha(0);
//                findViewById(R.id.satellite_view_option_button).setAlpha(0);
//                findViewById(R.id.text_location).setAlpha(0);
//                findViewById(R.id.main_menu_button).setAlpha(0);
//                mainMenu.setAlpha(0);
//                ConstraintLayout.LayoutParams lay = (ConstraintLayout.LayoutParams) findViewById(R.id.data).getLayoutParams();
//                lay.bottomMargin = 0;
//                findViewById(R.id.data).setLayoutParams(lay);
//            } else {
//                panel.setAlpha(1);
//                findViewById(R.id.back_button).setAlpha(1);
//                findViewById(R.id.text_location).setAlpha(1);
//                findViewById(R.id.location_button).setAlpha(1);
//                findViewById(R.id.satellite_view_option_button).setAlpha(1);
//                findViewById(R.id.main_menu_button).setAlpha(1);
//                mainMenu.setAlpha(1);
//                ConstraintLayout.LayoutParams lay = (ConstraintLayout.LayoutParams) findViewById(R.id.data).getLayoutParams();
//                lay.bottomMargin = (int) com.harsh.hkutils.Utils.dpToPixel(231, getApplicationContext());
//                findViewById(R.id.data).setLayoutParams(lay);
//            }
//        }
//    }
//
//    public void focusAllPaths(View view) throws Exception {
//        map.focus(locationData);
//    }
//
//    private void onGPS() throws Exception {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(Utils.INTERVAL);
//        locationRequest.setFastestInterval(Utils.FASTEST_INTERVAL);
//        locationRequest.setPriority(Utils.PRIORITY);
//        locationRequest.setMaxWaitTime(Utils.MAX_WAIT_TIME);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//
//        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
//        result.addOnSuccessListener(this, locationSettingsResponse -> {
//        })
//                .addOnFailureListener(MapsActivity.this, e -> {
//                    if (e instanceof ResolvableApiException) {
//                        try {
//                            ResolvableApiException exc = (ResolvableApiException) e;
//                            exc.startResolutionForResult(MapsActivity.this, 5);
//                        } catch (IntentSender.SendIntentException exception) {
//                            exception.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 5 && resultCode == RESULT_OK) {
//            try {
//                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
//                if (!sharedPrefUtils.isGps() && !sharedPrefUtils.isPowerSave()) {
//                    SQLiteHelper sqlHelper = new SQLiteHelper(this);
//                    sharedPrefUtils.preferences.edit()
//                            .putBoolean(SharedPrefUtils.GPS, true)
//                            .apply();
//                    sqlHelper.addAction(Action.GeoActivity.GPS, 1, System.currentTimeMillis(), 0);
//                }
//                startTracking();
//            } catch (Exception e) {
//                Utils.debug(this, e, e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        activity = null;
//        super.onDestroy();
//    }
//
//    private void InOfficeNotification() throws Exception {
//        if (currentState == ONBREAK)
//            return;
//        SharedPrefUtils preferences = new SharedPrefUtils(this);
//        SQLiteHelper sqlHelper = new SQLiteHelper(this);
//        if (currentState != INOFFICE) {
//            sqlHelper.addAction(Action.GeoActivity.Office, 0, System.currentTimeMillis(), 0);
//            preferences.setStartOffice();
//            updateUI(INOFFICE);
//            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tracking", false)) {
//                manager.notify(1000, builder.build());
//                stopTracking();
//            }
//        } else {
//            sqlHelper.addAction(Action.GeoActivity.Office, 1, System.currentTimeMillis(), 0);
//            findViewById(R.id.swipe_up_meeting_recycler_empty_view).setVisibility(View.GONE);
//            meetingAdapter = new MapMeetingAdapter(this, visitHelper.visits, Action.fromJSONArray(sqlHelper.getActions()), slidingUpPanelLayout, map.gmap);
//            RecyclerView visitList = findViewById(R.id.swipe_up_meeting_recycler_view);
//            visitList.setAdapter(meetingAdapter);
//            preferences.setOfficeTime();
//            mainMenu.labels.get(1).setText("In Office");
//            manager.cancelAll();
//            startTracking();
//        }
//    }
//
//    public void startTracking() throws Exception {
//        locationHelper.addLocation(new Date().getTime(), 0, 0, 0);
//        final Intent intent = new Intent(getApplicationContext(), LocationService.class);
//        intent.setAction(LocationService.START_COMMAND);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else
//            startService(intent);
//        startReadingLocations();
//        startPowerSaving();
//    }
//
//    private void startPowerSaving() {
//        final Intent intent = new Intent(this, LocationService.class);
//        intent.setAction(LocationService.POWER_SAVER_ON_COMMAND);
//        MapsActivity.this.startService(intent);
//    }
//
//    public void stopTracking() throws Exception {
//        final Intent intent = new Intent(getApplicationContext(), LocationService.class);
//        intent.setAction(LocationService.STOP_COMMAND);
//        startService(intent);
//        if (locationReadingTask != null)
//            locationReadingTask.cancel(false);
//        locationReadingTask = null;
//        stopPowerSaving();
//    }
//
//    private void stopPowerSaving() {
//        final Intent intent = new Intent(this, LocationService.class);
//        intent.setAction(LocationService.POWER_SAVER_OFF_COMMAND);
//        MapsActivity.this.startService(intent);
//    }
//
//
//    private LatLng getLastKnownLocation() {
//        LatLng latLng = null;
//        try {
//            JSONArray locations = locationData.getJSONArray("locations");
//            int i = locations.length() - 1;
//            JSONArray lastLine = locations.getJSONArray(i);
//            while (lastLine.length() == 0 && i >= 0) {
//                lastLine = locations.getJSONArray(i);
//                i--;
//            }
//            latLng = new LatLng(lastLine.getJSONArray(lastLine.length() - 1).getDouble(0), lastLine.getJSONArray(lastLine.length() - 1).getDouble(1));
//        } catch (Exception e) {
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return null;
//            }
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location == null)
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (location == null)
//                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//            if (location != null) {
//                latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            }
//        }
//        return latLng;
//    }
//
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//
//		final int actionMasked = ev.getActionMasked();
//
//		switch (actionMasked) {
//			case MotionEvent.ACTION_MOVE:
//				final float x = ev.getX(activePointerIndex);
//				final float y = ev.getY(activePointerIndex);
//				float deltaY = y - mLastMotionY;
//				float pullDistance = deltaY / getHeight();
//				float displacement = x / getWidth();
//
//				if (deltaY < 0 && mEdgeEffectTop.getDistance() > 0) {
//					deltaY -= getHeight() * mEdgeEffectTop
//							.onPullDistance(pullDistance, displacement);
//				}
//				if (deltaY > 0 && mEdgeEffectBottom.getDistance() > 0) {
//					deltaY += getHeight() * mEdgeEffectBottom
//							.onPullDistance(-pullDistance, 1 - displacement);
//				}
//		}
//	}
//
//}