package com.orocab.customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity_cstmr extends AppCompatActivity implements OnMapReadyCallback, OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, OnCameraChangeListener,
        com.google.android.gms.location.LocationListener,ResultCallback<LocationSettingsResult>,
        GoogleMap.OnMyLocationChangeListener
{

    private GoogleMap mMap;
    private GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location getmLastLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;

    Location mLastLocation;
    Marker mCurrLocationMarker, mPickUpMarker;
    Context context;
    Double Lat,Long;

    String myJson;
    boolean isMapReady = false;
    boolean isDataLoaded = false;

    protected static final String TAG = "MainActivity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;

    Location location = null;
    LocationManager locationManager;
    LocationListener locationListener;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;

    private static final String TAG_Lat = "latitude";
    private static final String TAG_Long = "longitude";
    private static final String TAG_minu = "minute";
    private static final String TAG_Vehicle = "vehicleId";
    private static final String TAG_templateId = "templateId";
    private static final String TAG_templateName = "templateName";

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    String Usersessionid;
    ArrayList<String> Tempstring = new ArrayList<String>();
    ArrayList<String> TempId = new ArrayList<String>();
    ArrayList<String> selectedStrings = new ArrayList<String>();

    HashMap<String, String> map_name_value = new HashMap<String, String>();


    Toolbar toolbar;

    Place place_pick, place_drop;
    TextView DriverName,Vehiclename,Vehiclenumber,DriverNumber,Cancelnumber;
    LinearLayout Calldriver,CancelTrip,DriverLayout,Bookinglayout;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1 = null;
    public static String Bookingid;
    double pic_latitude, pic_longitude;
    SharedPreferences sharedpreferences;
    View mapView;
    SharedPreferences.Editor editor;
    RadioButton radiobut;
    Button Cancel,Cancelnot;
    LinearLayout Sharemyride;
    EditText shareMobileno;
    Button shareSubmit;
    ImageButton Closeshareride;
    public static FloatingActionButton FAB;

    String vehicleId;
    public static boolean Tripstatus=false;

    public static final String TASK_TAG_PERIODIC = "periodic_task";
    private GcmNetworkManager mGcmNetworkManager;
    private BroadcastReceiver mReceiver;
    LatLng pic_LatLong;
    LatLngBounds.Builder builder;
    LatLngBounds bounds;
    RelativeLayout.LayoutParams layoutParams;

  //  [{"templateId":1,"comSubCatId":4,"templateName":" Cab is late","status":1}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cstmr);

        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow));

        if (getSupportActionBar() != null)
        {
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBackPressed();
                }
            });
        }

        FAB = (FloatingActionButton)findViewById(R.id.sosbutton);
        FAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this, R.style.AlertDialog);
                alertDialog.setTitle("SOS Enable");
                alertDialog.setMessage("Are you Sure want to use SOS feature");
                //alertDialog.setIcon(R.drawable.);
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sosbuttonenabled();
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        DriverName = (TextView) findViewById(R.id.Drivername);
        Vehiclename = (TextView) findViewById(R.id.vehiclename);
        Vehiclenumber = (TextView) findViewById(R.id.Vehiclenumber);
        DriverNumber = (TextView) findViewById(R.id.getdrivernumber);
        Cancelnumber = (TextView) findViewById(R.id.getCrno);
        DriverLayout = (LinearLayout) findViewById(R.id.driverlayoutdetails);
        Bookinglayout = (LinearLayout) findViewById(R.id.BookingLayout);
        Calldriver = (LinearLayout) findViewById(R.id.calldriver);
        CancelTrip = (LinearLayout) findViewById(R.id.Cancel);

        Sharemyride = (LinearLayout)findViewById(R.id.sharedirection);
        Sharemyride.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater layoutInflater = (LayoutInflater) MapActivity_cstmr.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                View popupView = layoutInflater.inflate(R.layout.sharedir, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                shareMobileno = (EditText)popupView.findViewById(R.id.sharemob);
                shareSubmit = (Button)popupView.findViewById(R.id.Subshareride);
                Closeshareride = (ImageButton)popupView.findViewById(R.id.Closeride);
                Closeshareride.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                    }
                });
                shareSubmit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        progressDialog1 = ProgressDialog.show(MapActivity_cstmr.this, "", "Please wait...", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                shareride();
                            }
                        }).start();
                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.update();
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Bookingid = getIntent().getStringExtra("BookingId");
        pic_latitude = getIntent().getDoubleExtra("latitude", 0.0);
        pic_longitude = getIntent().getDoubleExtra("longitude", 0.0);

        if(Bookingid != null)
        {
            editor = sharedpreferences.edit();
            editor.putString("BookingId", Bookingid);
            editor.putString("latitude", ""+pic_latitude);
            editor.putString("longitude", ""+pic_longitude);
            editor.apply();
        }
       // Log.i("Bookingid",Bookingid);


        SharedPreferences shared_BookId = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Bookingid = (shared_BookId.getString("BookingId", ""));
        String latitude = (shared_BookId.getString("latitude", ""));
        String longitude = (shared_BookId.getString("longitude", ""));
        Log.i("Bookingidshared", Bookingid);
        Log.i("latitude", latitude);
        Log.i("longitude", longitude);

        pic_latitude = Double.parseDouble(latitude);
        Log.i("pic_latitude", ""+pic_latitude);
        pic_longitude = Double.parseDouble(longitude);
        Log.i("pic_longitude", ""+pic_longitude);

        pic_LatLong = new LatLng(pic_latitude, pic_longitude);

        /*mGcmNetworkManager = GcmNetworkManager.getInstance(this);

        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(MyTaskService.ACTION_DONE))
                {
                    String tag = intent.getStringExtra(1);
                    Log.i("tag", ""+tag);

                    int result = intent.getIntExtra(MyTaskService.EXTRA_RESULT, -1);
                    Log.i("result", ""+result);

                    String msg = String.format("DONE: %s (%d)", tag, result);
                    Log.i("msg", msg);
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        };

        startPeriodicTask();*/

        Calldriver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String mobnumber = "tel:" + DriverNumber.getText().toString();
                Uri number = Uri.parse(mobnumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        CancelTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Tripstatus = false;
                progressDialog = ProgressDialog.show(MapActivity_cstmr.this, "", "Cancelling trip ..please wait", true);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                       // Tripcancellation();
                        getcancelpopup();
                    }
                }).start();
            }
        });


        Log.i("Tripstatus",""+Tripstatus);
        if(Tripstatus)
        {
            FAB.setVisibility(View.VISIBLE);
            CancelTrip.setVisibility(View.INVISIBLE);
        }

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        Log.i("Usersessionid", Usersessionid);

        if(Bookingid!=null)
        {
            Log.i("Bookingid",Bookingid);
            driverdetails();
        }

        if (savedInstanceState == null)
        {
            Log.i("savedInstanceState", "saved");
            setUpMapIfNeeded();
        }

        locationListener = new MyLocationListener();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
    }


    public void startPeriodicTask() {
        Log.i("startPeriodicTask", "startPeriodicTask");
        // [START start_periodic_task]
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(MyTaskService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(10L)  //10sec
                .build();

        mGcmNetworkManager.schedule(task);
    }

    public void stopPeriodicTask() {
        Log.i("stopPeriodicTask", "stopPeriodicTask");
        // [START stop_periodic_task]
        mGcmNetworkManager.cancelTask(TASK_TAG_PERIODIC, MyTaskService.class);
    }

    public  void sosbuttonenabled()
    {
        try
        {
            String urlnew =""+Url+"/sos/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        JSONArray json = new JSONArray(myJson);
                        Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        FAB.setBackgroundColor(getResources().getColor(R.color.RedTextColor));
                        //code for rating

                    }
                    catch (JSONException j)
                    {
                        j.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e)
        {
            Log.i("Error", "" + e.toString());
        }
    }


    public void shareride()
    {
        try
        {
            String urlnew =""+Url+"/shareRides/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("mobile", shareMobileno.getText().toString())
                    .appendQueryParameter("vehicleId", vehicleId)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

           try
            {
                JSONArray json = new JSONArray(myJson);
                Log.i("json", "" + json);
                JSONObject jsonObject = json.getJSONObject(0);

                String responseMessage = jsonObject.getString("responseMessage");

                Log.i("responseMessage", "" + responseMessage);

                final String ResMsg = responseMessage.substring(2, responseMessage.length() - 2);
//                Toast.makeText(getApplicationContext(), ResMsg, Toast.LENGTH_LONG).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog1.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this, R.style.AlertDialog);
                        alertDialog.setMessage(ResMsg);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                });
            }
            catch (JSONException j)
            {
                j.printStackTrace();
            }
        }
        catch (IOException e) {
            Log.i("Error", "" + e.toString());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("onresume", "onresume");
        //driverdetails();
    }

    public void driverdetails()
    {
        try
        {
            String urlnew =""+Url+"/driverDetails/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                        {
                            JSONArray json = new JSONArray(myJson);
                            Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            final String Drivername = jsonObject.getString("driverName");
                            final String DriverMobile = jsonObject.getString("mobile");
                            final String vehiclename = jsonObject.getString("vehicleName");
                            final String vehiclenumber = jsonObject.getString("vehicleNumber");
                            final String Crnno = jsonObject.getString("crnNumber");
                            vehicleId = jsonObject.getString("vehicleId");
                            DriverName.setText(Drivername);
                            DriverNumber.setText(DriverMobile);
                            Vehiclename.setText(vehiclename);
                            Vehiclenumber.setText(vehiclenumber);
                            Cancelnumber.setText(Crnno);

                            if (Drivername != null)
                            {
                                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                editor = sharedpreferences.edit();
                                //code for rating review
                                editor.putString("Drivername", Drivername);
                                editor.putString("vehiclename", vehiclename);
                                editor.putString("vehiclenumber", vehiclenumber);
                                editor.putString("vehicleId", vehicleId);
                                editor.putString("crnNumber", Crnno);
                                editor.apply();
                            }
                        }
                        catch (JSONException j)
                        {
                            j.printStackTrace();
                        }
                }
            });
        }
        catch (IOException e)
        {
            Log.i("Error", "" + e.toString());
        }
    }

    protected void startLocationUpdates()
    {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    private void setUpMapIfNeeded()
    {
        if (mGoogleMap == null)
        {
            Log.i("mapnull", "mapnull");
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapView = mapFragment1.getView();
            mapFragment1.getMapAsync(this);

            if (mGoogleMap != null)
            {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {

        Log.i("updateValuesFromBundle", "updateValuesFromBundle");

        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION))
            {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                getmLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    protected void createLocationRequest() {
        Log.i("createLocationRequest","createLocationRequest");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void buildLocationSettingsRequest()
    {
        //Log.i("buildLocationSettingsRequest","buildLocationSettingsRequest");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult)
    {
        Log.i("locationSettingsResult","locationSettingsResult");
        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied." + "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(MapActivity_cstmr.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here." + "not created.");
                break;
        }
    }

    public  void getcancelpopup()
    {
        try {
            String urlnew = ""+Url+"/cancelledComment/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("comSubCatId", "4")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    showCancelType();
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }




    public void showCancelType()
    {
        try
        {
            JSONArray json = new JSONArray(myJson);
            Log.i("json_tyavel_type_inv", "" + json);
            for (int i = 0; i < json.length(); i++)
            {

                JSONObject jsonObject = json.getJSONObject(i);
                final String travel_type = jsonObject.getString(TAG_templateName);
                Log.i("travel_type", travel_type);

                final int ty_id   = jsonObject.getInt(TAG_templateId);
                Log.i("ty_id", "" + ty_id);

                Tempstring.add(travel_type);
                TempId.add(String.valueOf(ty_id));

                map_name_value.put(jsonObject.getString(TAG_templateName), jsonObject.getString(TAG_templateId));
            }

            LayoutInflater layoutInflater = (LayoutInflater) MapActivity_cstmr.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
            View popupView = layoutInflater.inflate(R.layout.cancelpopup, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            Cancel = (Button)popupView.findViewById(R.id.cancelconfirm);
            Cancelnot = (Button)popupView.findViewById(R.id.cancelnotconfirm);
            Cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(!selectedStrings.isEmpty())
                    {
                        popupWindow.dismiss();
                        Tripcancellation();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Select Reason",Toast.LENGTH_LONG).show();
                    }
                }
            });
            Cancelnot.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    popupWindow.dismiss();

                }
            });
            RadioGroup rg = (RadioGroup)popupView.findViewById(R.id.radio_group);
            for(int j=0;j<json.length();j++)
           {
               RadioButton rb = new RadioButton(getApplicationContext()); // dynamically creating RadioButton and adding to RadioGroup.
               rb.setId(Integer.parseInt(TempId.get(j)));
               rb.setText(Tempstring.get(j));
               rb.setTextColor(getResources().getColor(R.color.TitleColor));
               rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
               {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                   {
                       if (((RadioButton) buttonView).isChecked())
                       {
                           selectedStrings.add(String.valueOf(buttonView.getId()));
                           Log.i("array_list_id1",""+selectedStrings);
                       }
                       else
                       {
                           selectedStrings.remove(String.valueOf(buttonView.getId()));
                           Log.i("array_list_id23",""+selectedStrings);
                       }

                   }
               });
               rg.addView(rb);
           }
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("onActivityResult", "onActivityResult");
        Log.i("requestCode", "" + requestCode);

        switch (requestCode)
        {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("ResultCancelled","Cancelled");
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }


    private void updateUI()
    {
        setButtonsEnabledState();
        updateLocationUI();
    }

    private void setButtonsEnabledState()
    {
        if (mRequestingLocationUpdates)
        {
            // mStartUpdatesButton.setEnabled(false);
            // mStopUpdatesButton.setEnabled(true);
        } else {
            // mStartUpdatesButton.setEnabled(true);
            // mStopUpdatesButton.setEnabled(false);
        }
    }

    public void Tripcancellation()
    {
        String crno = Cancelnumber.getText().toString();
        try
        {
            String urlnew = ""+Url+"/bookingCancellation/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("crnNumber", crno)
                    .appendQueryParameter("tempId",String.valueOf(selectedStrings))
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                   // progressDialog.dismiss();
                    try
                    {
                        if (myJson != null)
                        {
                            JSONArray json = new JSONArray(response);
                            Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            String str = "4";
                            final String Message = jsonObject.getString("responseMessage");
                            final String responsecode = jsonObject.getString("responseCode");
                            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);
                            if (responsecode.equals("1"))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this, R.style.AlertDialog);
                                alertDialog.setTitle("Cancelled");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                                        MapActivity.intent_pas = false;
                                        startActivity(in);
                                        finish();
                                    }
                                });
                                alertDialog.show();
                            }
                            else
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this, R.style.AlertDialog);
                                alertDialog.setTitle("Not Cancelled");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void updateLocationUI()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            Log.i("updateLocationUI","12345");
            Lat = location.getLatitude();
            Log.i("Latitude",""+location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude",""+location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng",""+latLng);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.snippet("Latitude:"+Lat+",Longitude:"+Long);
            markerOptions.draggable(true);
            markerOptions.icon(icon);

            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    protected void stopLocationUpdates()
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates
                (
                        mGoogleApiClient,
                        this
                ).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(Status status)
            {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        isMapReady = true;

        Log.i("Ready", "Google Map");
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnCameraChangeListener(this);
        mGoogleMap.setOnMyLocationChangeListener(this);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng pos = mGoogleMap.getCameraPosition().target;
        Log.i("pos145",""+pos);

        LatLng latLng11 = new LatLng(pic_latitude, pic_longitude);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng11);
        markerOptions.title("PickUp Location");
        markerOptions.icon(icon);
        mPickUpMarker = mGoogleMap.addMarker(markerOptions);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng11, 16);
        //mGoogleMap.animateCamera(cameraUpdate);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null)
        {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mLastLocation == null)
        {
            Log.i("mLastLocation",""+mLastLocation);
            try
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            catch (SecurityException e)
            {
                Log.i("SecurityException", ""+e.toString());
            }

            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i("onLocationChange","onLocationChange");

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();

        }
        mGoogleMap.clear();

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            Log.i("updateLocationUI", "startLocationUpdates");
            Lat = location.getLatitude();
            Log.i("Latitude", "" + location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude", "" + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng", "" + latLng);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.snippet("Latitude:"+Lat+",Longitude:"+Long);
            markerOptions.icon(icon);

            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(false);
                    }

                } else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        if (Tripstatus)
        {
            if (mPickUpMarker != null)
            {
                mPickUpMarker.remove();
            }
        }
        mGoogleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded()
    {
        Log.i("onMapLoad", "OnMapLoad");
        Log.i("place_onMapLoad", ""+place_pick);
        Log.i("place_onMapLoad", ""+place_drop);
    }

    @Override
    public void onMyLocationChange(Location location)
    {
        Location prevLoc = new Location("service Provider");
        prevLoc.setLatitude(location.getLatitude());
        prevLoc.setLongitude(location.getLongitude());

        double lat = location.getLatitude();
        Log.i("Lat_LocationChange", ""+lat);
        double lng = location.getLongitude();
        Log.i("Lat_LocationChange", ""+lng);

        LatLng latLng = new LatLng(lat, lng);
        Log.i("latLng_LocationChange", ""+latLng);

        Location newLoc = new Location("service Provider");
        newLoc.setLatitude(lat);
        newLoc.setLongitude(lng);

        String address = "";
        String current_address = "";
        Geocoder geocoder = new Geocoder(MapActivity_cstmr.this, Locale.getDefault());
        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocation(pic_latitude, pic_longitude, 1);
            Log.i("addresses", ""+addresses);

            if(addresses != null && addresses.size() > 0 )
            {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                Log.i("address10", ""+address);
                String address11 = addresses.get(0).getAddressLine(1);
                Log.i("address11", ""+address11);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            Log.i("addresses", ""+addresses);

            if(addresses != null && addresses.size() > 0 )
            {
                current_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                Log.i("current_address", ""+current_address);
                String address11 = addresses.get(0).getAddressLine(1);
                Log.i("address11", ""+address11);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        float bearing = prevLoc.bearingTo(newLoc);

        if (address.equals(current_address) && mPickUpMarker != null)
        {
            mPickUpMarker.remove();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mGoogleMap.animateCamera(cameraUpdate);
        }
        else
        {
            builder = new LatLngBounds.Builder();
            builder.include(pic_LatLong);
            builder.include(latLng);
            bounds = builder.build();
            int padding = 60;
            mGoogleMap.setPadding(0, 40, 0, 40);
            layoutParams.setMargins(0, 0, 30, 0);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cu);
        }

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        BitmapDescriptor icon;
        if (Tripstatus)
        {
            if (mPickUpMarker != null)
            {
                mPickUpMarker.remove();
            }

            icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(icon);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.rotation(bearing);
            markerOptions.flat(true);
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
            layoutParams.setMargins(0, 0, 30, 30);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mGoogleMap.animateCamera(cameraUpdate);
        }
        else
        {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(icon);
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        }
    }

    private class ReverseGeocodingPicLoc extends AsyncTask<LatLng, Void, String>
    {
        String address;

        @Override
        protected String doInBackground(LatLng... params)
        {
            Geocoder geocoder = new Geocoder(MapActivity_cstmr.this, Locale.getDefault());
            List<Address> addresses = null;

            try
            {
                addresses = geocoder.getFromLocation(pic_latitude, pic_longitude, 1);
                Log.i("addresses", ""+addresses);

                if(addresses != null && addresses.size() > 0 )
                {
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    Log.i("address10", ""+address);
                    String address11 = addresses.get(0).getAddressLine(1);
                    Log.i("address11", ""+address11);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return address;
        }

        @Override
        protected void onPostExecute(String addressText)
        {

        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Booking.class);
        Tripstatus = false;
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("onpausefalse","onpausefalse");
//        Tripstatus = false;
//        stopPeriodicTask();
        //finish();
    }
}