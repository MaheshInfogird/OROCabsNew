package com.orocab.customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,ResultCallback<LocationSettingsResult>,PlaceSelectionListener,
        GoogleMap.OnMapClickListener,GoogleMap.OnCameraChangeListener,GoogleMap.OnMapLoadedCallback

{
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    public static Toolbar toolbar;
    ConnectionDetector cd;
    UserSessionManager sessionManager;

    private GoogleMap mGoogleMap;
    protected LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location getmLastLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Context context;
    Location location = null;
    LocationManager locationManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODEPICK = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODEDROP = 3;
    PlaceAutocompleteFragment autocompleteFragment;
    LocationListener locationListener;
    Place place_pick, place_drop, place;
    LatLng pick_LatLong, drop_LatLong;
    LatLngBounds.Builder builder;
    LatLngBounds bounds;

    JSONArray Vehicle =null;
    JSONArray VehicleTarif =null;

    protected static final String TAG = "MainActivity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    private static final String TAG_Lat = "latitude";
    private static final String TAG_Long = "longitude";
    private static final String TAG_minu = "minute";
    private static final String TAG_Vehicle = "vehicleId";
    private static final String TAG_driverVehicleLocationResult = "driverVehicleLocationResult";
    private static final String TAG_driverVehicleTariffResult = "driverVehicleTariffResult";
    private static final String TAG_approximateFareMin="approximateFareMin";
    private static final String TAG_approximateFareMax="approximateFareMax";
    private static final String TAG_approximatedistance="distance";

    // [{"approximateFareMin":49,"approximateFareMax":79,"approximateTravelTime":"11 mins","kmRate":"10.00","distance":"4.6 km","dryRunKm":"2","dryRunRate":"2.00","dryRunDistance":2.6,"dryRunCharge":5.2}]

    TextView txtDrop, txtPick;
    public static TextView ed_auto_pick, ed_auto_drop;
    public static TextView Farepopup, FareEstimate;
    TextView Picuplocation, Droploaction, Estimatecharges1, Estimatecharges2;
    TextView AvailTime;
    TextView book_pickLocation, book_dropLocation;
    TextView PopbaseFare, PopupBasekm, popupBasemin;
    TextView DriverName, Vehiclename, Vehiclenumber, DriverNumber, Cancelnumber;
    RelativeLayout Book_Cnfrm, book_dropLayout;
    LinearLayout DriverLayout, fare_layout;
    Snackbar snackbar;
    public static Snackbar snackbar1 = null, snackbar2 = null;
    FrameLayout content_frame;
    public static LinearLayout BookingLayout;
    public static LinearLayout ed_drop, ed_pick;
    public static RelativeLayout Autoapi, Autoapi_drop;
    public static ImageView drop_arrow, pick_arrow;
    public static ImageView mark_img, cross_img;
    ImageButton cancelpopoup, Cancelrideestimate;
    public static Button Book, confirm;
    public static FloatingActionButton FAB;
//    public static Button FAB;


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    String Usersessionid, myJson;
    String city, drop_city;
    String BaseFare="", Basekm="", RideTime="", Kmrate="";
    String Approximatefaremin, Approximatefaremax;
    String BookingId, responsecode;
    String Time ,Vehicleid, CurrentDate;
    String Url, IPAddress, APIKEY;
    String pickuplocation, Droplocation;
    String searchstatus = "";
    protected String mLastUpdateTime;
    String dropAddress;
    String bestProvider, Packagename;

    Double Droplat, Droplong;
    Double Lat = 0.0, Long = 0.0;
    Double dryrunrate, dryRunKm = 0.0, dryRun = 0.0, dryRunCharges;
    double Distance = 0.0;

    int mDay, mMonth, mYear;
    int version_code;

    public static boolean isCabAvailable = false;
    protected Boolean mRequestingLocationUpdates;
    boolean isMapReady = false;
    boolean isestimateready = false;
    boolean isgpscheck = false;
    boolean mapLoad = false;
    public static boolean mMapIsTouched = false;
    boolean onCreate = true;

    public static Handler timeoutHandler;//= new Handler();
    public static Runnable finalizer;

    ProgressDialog progressDialog = null;
    public static NetworkChange receiver;
    PopupWindow popupWindow;
    public static boolean intent_pas = true;
    LinearLayout marker_img_layout;

    ImageView purple, orange, yellow, green, blue;
    boolean internetConn = false;
    LinearLayout bubbleLayout, getLoc_layout;
    LinearLayout checkConn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cd = new ConnectionDetector(getApplicationContext());

        if (getSupportActionBar() != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        timeoutHandler = new Handler();
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version_code = info.versionCode;
            Packagename = info.packageName;
        }
        catch (PackageManager.NameNotFoundException e) {
        }

        marker_img_layout = (LinearLayout) findViewById(R.id.marker_img_layout);
        mark_img = (ImageView) findViewById(R.id.marker_img);
        cross_img = (ImageView) findViewById(R.id.marker_cross);
        BookingLayout = (LinearLayout) findViewById(R.id.BookingLayout);
        Autoapi_drop = (RelativeLayout) findViewById(R.id.Autoapi_drop);
        ed_pick = (LinearLayout) findViewById(R.id.pick_loc);
        ed_drop = (LinearLayout) findViewById(R.id.drop_loc);
        txtPick = (TextView) findViewById(R.id.txt_pick);
        ed_auto_pick = (TextView) findViewById(R.id.ed_place_autocomplete_fragment);
        ed_auto_drop = (TextView) findViewById(R.id.ed_drop_place_autocomplete_fragment);
        drop_arrow = (ImageView) findViewById(R.id.drop_arrow);
        pick_arrow = (ImageView) findViewById(R.id.arrow);
        txtDrop = (TextView) findViewById(R.id.txt_drop);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        Autoapi = (RelativeLayout) findViewById(R.id.Autoapi);
        Book_Cnfrm = (RelativeLayout) findViewById(R.id.book_cnfrm_layout);
        book_dropLayout = (RelativeLayout) findViewById(R.id.book_dropLayout);
        book_pickLocation = (TextView) findViewById(R.id.pick_location);
        book_dropLocation = (TextView) findViewById(R.id.book_drop_location);
        fare_layout = (LinearLayout) findViewById(R.id.fare_layout);
        AvailTime = (TextView) findViewById(R.id.CabTime);
        Book = (Button) findViewById(R.id.Booknow);
        confirm = (Button) findViewById(R.id.confirmnow);
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
//        FAB = (Button) findViewById(R.id.myLocationButton);
        Farepopup = (TextView) findViewById(R.id.Farepopup);
        FareEstimate = (TextView) findViewById(R.id.Fareestimate);

        Book.setEnabled(true);

        receiver = new NetworkChange()
        {
            @Override
            protected void onNetworkChange()
            {
                if (receiver.isConnected)
                {
                    checkversion();
                    //sendclientLocation();
                    getVehicleLocation();
                    //BookingLayout.setVisibility(View.VISIBLE);
                    FAB.setVisibility(View.VISIBLE);

                    internetConn = true;

                    if (Book.isEnabled())
                    {
                        Log.i("isEnabled","isEnabled");
                        Book.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Log.i("isDisabled","isDisabled");
                        Book.setVisibility(View.GONE);
                    }

                    if (popupWindow != null && popupWindow.isShowing())
                    {
                        bubbleLayout.setVisibility(View.VISIBLE);
                        getLoc_layout.setVisibility(View.VISIBLE);
                        checkConn.setVisibility(View.GONE);
                        BallAnimation();
                        if (isMapReady)
                        {
                            getLocation();
                        }
                    }

                    if (snackbar != null && snackbar.isShown())
                    {
                        snackbar.dismiss();
                    }
                }
                else
                {
                    internetConn = false;
                    if (popupWindow != null && popupWindow.isShowing())
                    {
                        bubbleLayout.setVisibility(View.GONE);
                        getLoc_layout.setVisibility(View.GONE);
                        checkConn.setVisibility(View.VISIBLE);
                    }
                    BookingLayout.setVisibility(View.INVISIBLE);
                    FAB.setVisibility(View.GONE);
                    snackbar = Snackbar.make(content_frame, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.RedTextColor));
                    snackbar.show();
                }
            }
        };

        registerReceiver(receiver, filter);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));

        sessionManager = new UserSessionManager(getApplicationContext());

        if (cd.isConnectingToInternet(MapActivity.this))
        {
            checkversion();
        }
        else
        {
            BookingLayout.setVisibility(View.GONE);
            snackbar = Snackbar.make(content_frame, "Please check your internet connection and try again", Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.RedTextColor));
            snackbar.show();
        }
        TurnGpsOn();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        bestProvider = lm.getBestProvider(criteria, true);

        mapLoad = false;

        ed_auto_drop.setEnabled(false);

        Autoapi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    placeAutocomplete_Pick();
                }
            }
        });

        Autoapi_drop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    placeAutocomplete_Drop();
                }
            }
        });

        ed_pick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    Autoapi.setVisibility(View.VISIBLE);
                    Autoapi_drop.setVisibility(View.GONE);
                    ed_pick.setVisibility(View.GONE);
                    drop_arrow.setVisibility(View.GONE);
                    pick_arrow.setVisibility(View.VISIBLE);
                    ed_drop.setVisibility(View.VISIBLE);
                    if (!txtPick.getText().toString().equals(""))
                    {
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
                        mGoogleMap.animateCamera(zoom);
                        CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(pick_LatLong, 15.5f);
                        mGoogleMap.animateCamera(cameraPosition1);
                    }
                    ed_auto_pick.setEnabled(true);
                    ed_auto_drop.setEnabled(false);
                }
            }
        });


        ed_drop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    if (isCabAvailable)
                    {
                        if (txtDrop.getText().toString().equals(""))
                        {
                            placeAutocomplete_Drop();
                        }

                        Autoapi.setVisibility(View.GONE);
                        Autoapi_drop.setVisibility(View.VISIBLE);
                        ed_pick.setVisibility(View.VISIBLE);
                        drop_arrow.setVisibility(View.VISIBLE);
                        ed_drop.setVisibility(View.GONE);
                        pick_arrow.setVisibility(View.GONE);

                        if (!txtDrop.getText().toString().equals(""))
                        {
                            CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
                            mGoogleMap.animateCamera(zoom);
                            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(drop_LatLong, 15.5f);
                            mGoogleMap.animateCamera(cameraPosition);
                        }

                        ed_auto_pick.setEnabled(false);
                        ed_auto_drop.setEnabled(true);
                    }
                }
            }
        });

        Book.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mark_img.setVisibility(View.GONE);
                    Book.setEnabled(false);
                    confirm.setEnabled(true);
                    FareEstimate.setEnabled(true);

                    if (txtDrop.getText().toString().equals(""))
                    {
                        CameraUpdate cameraPosition = CameraUpdateFactory.zoomBy(3);
                        mGoogleMap.animateCamera(cameraPosition);
                    }
                    ed_auto_pick.setEnabled(false);
                    ed_auto_drop.setEnabled(false);
                    FAB.setVisibility(View.GONE);
                    drop_arrow.setVisibility(View.GONE);
                    Autoapi_drop.setVisibility(View.GONE);
                    ed_pick.setVisibility(View.GONE);
                    Autoapi.setVisibility(View.GONE);
                    ed_drop.setVisibility(View.GONE);
                    pick_arrow.setVisibility(View.GONE);
                    String pick_str = ed_auto_pick.getText().toString();
                    Book_Cnfrm.setVisibility(View.VISIBLE);
                    book_dropLayout.setVisibility(View.VISIBLE);
                    book_pickLocation.setText(pick_str);
                    mGoogleMap.clear();

                    BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
                    MarkerOptions options1 = new MarkerOptions();
                    options1.position(pick_LatLong);
                    options1.title("Pick Location");
                    options1.snippet("Latitude:" + Lat + ",Longitude:" + Long);
                    options1.icon(icon1);

                    mGoogleMap.addMarker(options1);

                    if (!txtDrop.getText().toString().equals(""))
                    {
                        String drop_str = txtDrop.getText().toString();
                        book_dropLocation.setText(drop_str);

                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
                        MarkerOptions options = new MarkerOptions();
                        options.position(drop_LatLong);
                        options.title("Drop Location");
                        options.snippet("Latitude:" + Droplat + ",Longitude:" + Droplong);
                        options.icon(icon);
                        mCurrLocationMarker = mGoogleMap.addMarker(options);

                        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                        builder = new LatLngBounds.Builder();
                        builder.include(pick_LatLong);
                        builder.include(drop_LatLong);
                        bounds = builder.build();
                        int padding = 60;
                        mGoogleMap.setPadding(0, 310, 0, 100);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mGoogleMap.animateCamera(cu);
                    }

                    if (Distance > dryRun && dryRun > 0.0)
                    {
                        alertDialoge();
                    }
                    else
                    {
                        Book.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                        Farepopup.setVisibility(View.GONE);
                        FareEstimate.setVisibility(View.VISIBLE);
                    }
                }
            }


        });

        book_dropLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                            new LatLng(19.8036125, 75.1932621),
                            new LatLng(19.9365798, 75.4171406))))
                            .build(MapActivity.this);
                    startActivityForResult(intent, 4);
                }
                catch (GooglePlayServicesRepairableException e)
                {
                    // TODO: Handle the error.
                }
                catch (GooglePlayServicesNotAvailableException e)
                {
                    // TODO: Handle the error.
                }
                isestimateready = true;
                Book.setVisibility(View.GONE);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialog);
                alertDialog.setTitle("Booking");
                alertDialog.setMessage("Are you Sure want to Confirm Booking");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        confirm.setEnabled(false);
                        progressDialog = ProgressDialog.show(MapActivity.this, "", "Confirming trip ..please wait", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                Bookingconfirm();
                            }
                        }).start();
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


        FAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    if (ed_auto_pick.isEnabled() || ed_auto_drop.isEnabled())
                    {
                        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (location != null)
                        {
                            Lat = location.getLatitude();
                            Long = location.getLongitude();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.5f);
                            mGoogleMap.animateCamera(cameraUpdate);
                        }
                    }
                }
            }
        });

        FareEstimate.setEnabled(false);

        fare_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (FareEstimate.isEnabled())
                {
                    if (book_dropLocation.getText().toString().equals(""))
                    {
                        try
                        {
                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setBoundsBias((new LatLngBounds(new LatLng(19.8036125, 75.1932621),
                                            new LatLng(19.9365798, 75.4171406))))
                                    .build(MapActivity.this);
                            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                        }
                        catch (GooglePlayServicesRepairableException e)
                        {
                            // TODO: Handle the error.
                        }
                        catch (GooglePlayServicesNotAvailableException e)
                        {
                            // TODO: Handle the error.
                        }
                        isestimateready = true;
                        Book.setVisibility(View.GONE);
                    }
                    else
                    {
                        sendrideestimate();
                        LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                        View popupView = layoutInflater.inflate(R.layout.rideestimate, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        LinearLayout Drop_Layout = (LinearLayout) popupView.findViewById(R.id.Droplocation);
                        TextView txt_DropLocation = (TextView) popupView.findViewById(R.id.txt_drop_location);
                        Picuplocation = (TextView) popupView.findViewById(R.id.pick_add);
                        Droploaction = (TextView) popupView.findViewById(R.id.drop_add);
                        Estimatecharges1 = (TextView) popupView.findViewById(R.id.estimate1);
                        Estimatecharges2 = (TextView) popupView.findViewById(R.id.estimate2);
                        Estimatecharges1.setText(Approximatefaremin + " - "+ Approximatefaremax);
                        Cancelrideestimate = (ImageButton) popupView.findViewById(R.id.cancelrideestimate);

                        Cancelrideestimate.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                popupWindow.dismiss();
                                Book.setVisibility(View.GONE);
                            }
                        });

                        Drop_Layout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    popupWindow.dismiss();
                                    Book.setVisibility(View.GONE);
                                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                            new LatLng(19.8036125, 75.1932621),
                                            new LatLng(19.9365798, 75.4171406))))
                                            .build(MapActivity.this);
                                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                                }
                                catch (GooglePlayServicesRepairableException e)
                                {
                                    // TODO: Handle the error.
                                }
                                catch (GooglePlayServicesNotAvailableException e)
                                {
                                    // TODO: Handle the error.
                                }
                                isestimateready = true;
                                Book.setVisibility(View.GONE);
                            }
                        });
                        Picuplocation.setText(pickuplocation);
                        Droploaction.setText(book_dropLocation.getText().toString());
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        Book.setVisibility(View.GONE);
                    }
                }
                else if (Farepopup.isEnabled())
                {
                    if (receiver.isConnected)
                    {
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                                        View popupView = layoutInflater.inflate(R.layout.farepopupdetails, null);
                                        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                        popupWindow.setFocusable(true);
                                        popupWindow.update();
                                        PopbaseFare = (TextView) popupView.findViewById(R.id.BaseFare);
                                        PopupBasekm = (TextView) popupView.findViewById(R.id.Basekm);
                                        popupBasemin = (TextView) popupView.findViewById(R.id.Basemin);
                                        cancelpopoup = (ImageButton) popupView.findViewById(R.id.cancelpopup);

                                        if (BaseFare.equals("") && Basekm.equals(""))
                                        {
                                            PopbaseFare.setText("");
                                            PopupBasekm.setText("");
                                            popupBasemin.setText("");
                                        }
                                        else
                                        {
                                            PopbaseFare.setText(BaseFare + "/" + Basekm);
                                            PopupBasekm.setText(Kmrate);
                                            popupBasemin.setText(RideTime);
                                        }

                                        cancelpopoup.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                popupWindow.dismiss();
                                            }
                                        });
                                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                    }
                                });
                            }
                        }).start();
                    }
                }
            }
        });

        if (savedInstanceState == null)
        {
            setUpMapIfNeeded();
            if (intent_pas)
            {
                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Log.i("inflate_layout", "inflate_layout");
                                LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                                View popupView = layoutInflater.inflate(R.layout.locationpopup, null);
                                popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                popupWindow.setFocusable(true);
                                popupWindow.update();
                                purple = (ImageView)popupView.findViewById(R.id.purple_ball);
                                orange = (ImageView)popupView.findViewById(R.id.orange_ball);
                                yellow = (ImageView)popupView.findViewById(R.id.yellow_ball);
                                green  = (ImageView)popupView.findViewById(R.id.green_ball);
                                blue   = (ImageView)popupView.findViewById(R.id.blue_ball);
                                bubbleLayout = (LinearLayout)popupView.findViewById(R.id.ball_layout);
                                getLoc_layout = (LinearLayout)popupView.findViewById(R.id.get_loc_layout);
                                checkConn = (LinearLayout)popupView.findViewById(R.id.check_conn);

                                if (!internetConn)
                                {
                                    bubbleLayout.setVisibility(View.VISIBLE);
                                    getLoc_layout.setVisibility(View.GONE);
                                    checkConn.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    bubbleLayout.setVisibility(View.VISIBLE);
                                    getLoc_layout.setVisibility(View.VISIBLE);
                                    checkConn.setVisibility(View.GONE);
                                    BallAnimation();
                                }
                               popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            }
                        });
                    }
                };
                thread.start();
            }
        }

        locationListener = new MyLocationListener();
        LocationManager lmngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lmngr.requestLocationUpdates(bestProvider, 2000, 10, locationListener);
    }

    public static void dimBehind(PopupWindow popupWindow)
    {
        View container;
        if (popupWindow.getBackground() == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            }
            else {
                container = popupWindow.getContentView();
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            }
            else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }

        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.4f;
        wm.updateViewLayout(container, p);
    }

    public void BallAnimation()
    {
        final TranslateAnimation animation_p = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -200.0f);
        animation_p.setDuration(10);
        purple.startAnimation(animation_p);

        final TranslateAnimation animation1 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -200.0f);
        animation1.setDuration(600);
        animation1.setStartOffset(-300);

        final TranslateAnimation animation2 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -200.0f);
        animation2.setDuration(600);
        animation2.setStartOffset(-300);

        final TranslateAnimation animation3 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -200.0f);
        animation3.setDuration(600);
        animation3.setStartOffset(-300);

        final TranslateAnimation animation4 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -200.0f);
        animation4.setDuration(600);
        animation4.setStartOffset(-300);

        final TranslateAnimation animation5 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
        animation5.setDuration(600);
        animation5.setStartOffset(-300);

        final TranslateAnimation animation6 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
        animation6.setDuration(600);
        animation6.setStartOffset(-300);

        final TranslateAnimation animation7 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
        animation7.setDuration(600);
        animation7.setStartOffset(-300);

        final TranslateAnimation animation8 = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
        animation8.setDuration(600);
        animation8.setStartOffset(-300);

        animation_p.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                orange.startAnimation(animation1);
            }
        });

        animation1.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                blue.startAnimation(animation2);
            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                yellow.startAnimation(animation3);
            }
        });

        animation3.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                green.startAnimation(animation4);
            }
        });

        animation4.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                purple.startAnimation(animation_p);
                animation_p.setRepeatCount(Animation.RESTART);
            }
        });

        animation5.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                orange.startAnimation(animation1);
                animation1.setRepeatCount(Animation.RESTART);
            }
        });


        animation6.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                blue.startAnimation(animation2);
                animation2.setRepeatCount(Animation.RESTART);
            }
        });

        animation7.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                yellow.startAnimation(animation3);
                animation3.setRepeatCount(Animation.RESTART);
            }
        });

        animation8.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                green.startAnimation(animation4);
                animation4.setRepeatCount(Animation.RESTART);
            }
        });
    }


    public static void hideViews()
    {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) BookingLayout.getLayoutParams();
        int BookingLayoutMargin = lp1.bottomMargin;
        BookingLayout.animate().translationY(BookingLayout.getHeight()+BookingLayoutMargin).setInterpolator(new AccelerateInterpolator(2)).start();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) FAB.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        FAB.animate().translationY(FAB.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        mark_img.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mark_img.setPadding(0, 0, 0, 0);

        if (snackbar1 != null && snackbar1.isShown())
        {
            snackbar1.dismiss();
        }

        if (ed_auto_pick.isEnabled())
        {
            Autoapi.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            ed_drop.animate().translationY(-toolbar.getHeight()-55).setInterpolator(new AccelerateInterpolator(2));
            pick_arrow.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        }
        else if (ed_auto_drop.isEnabled())
        {
            Autoapi_drop.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            //ed_pick.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            drop_arrow.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        }

        cross_img.setVisibility(View.VISIBLE);
        cross_img.setPadding(0, 0, 0, 30);
    }

    public static void showViews()
    {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        FAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//        BookingLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mark_img.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mark_img.setPadding(0, 0, 0, 15);

        if (ed_auto_pick.isEnabled())
        {
            Autoapi.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            ed_drop.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            pick_arrow.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        }
        else if (ed_auto_drop.isEnabled())
        {
            Autoapi_drop.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            //ed_pick.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            drop_arrow.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        }

        cross_img.setVisibility(View.GONE);
        cross_img.setPadding(0, 0, 0, 0);
    }

    public static void animateViews()
    {
        mark_img.animate().translationY(-40).setInterpolator(new AccelerateInterpolator(2));
        mark_img.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private void placeAutocomplete_Pick()
    {
        int var1 = -1;
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        Log.i("curScreen", ""+bounds);

        LatLngBounds latLngBounds = new LatLngBounds(
                    new LatLng(19.8036125,75.1932621),
                    new LatLng(19.9365798,75.4171406));

        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        try
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(bounds).setFilter(typeFilter).build(MapActivity.this);
            startActivityForResult(intent, 1);
        }
        catch (GooglePlayServicesRepairableException var3)
        {
            var1 = var3.getConnectionStatusCode();
        }
        catch (GooglePlayServicesNotAvailableException var4)
        {
            var1 = var4.errorCode;
        }

        if (var1 != -1)
        {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity.this, var1, 2);
        }

    }

    private void placeAutocomplete_Drop()
    {
        int var1 = -1;
        Log.i("ED_Click", "ED_Click");

        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        Log.i("curScreen", ""+bounds);

        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(19.8036125,75.1932621),
                new LatLng(19.9365798,75.4171406));

        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        try
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(bounds).setFilter(typeFilter).build(MapActivity.this);
            startActivityForResult(intent, 2);
        }
        catch (GooglePlayServicesRepairableException var3)
        {
            var1 = var3.getConnectionStatusCode();
        }
        catch (GooglePlayServicesNotAvailableException var4)
        {
            var1 = var4.errorCode;
        }

        if (var1 != -1)
        {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity.this, var1, 2);
        }
    }

    protected void startLocationUpdates()
    {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        // Request location updates

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    private void setUpMapIfNeeded()
    {
        if (mGoogleMap == null)
        {
            Log.i("mapnull","mapnull");
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mGoogleMap = mapFragment1.getMap();
            mapFragment1.getMapAsync(this);
            TurnGpsOn();

            if (mGoogleMap != null)
            {
                mGoogleMap.setMyLocationEnabled(true);
                Log.i("mGoogleMap","Map Exist");
            }
        }
    }

    public void TurnGpsOn()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(ActivityRecognition.API)
                    .build();
            mGoogleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder() .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); // this is the key ingredient

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            getLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                                isgpscheck = true;
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    private void updateValuesFromBundle(Bundle savedInstanceState)
    {

      //  Log.i("updateValuesFromBundle", "updateValuesFromBundle");

        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
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
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING))
            {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }


    protected void createLocationRequest()
    {
       // Log.i("createLocationRequest","createLocationRequest");
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

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected void buildLocationSettingsRequest()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);
    }


    protected void checkLocationSettings()
    {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult)
    {
        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode())
        {
            case LocationSettingsStatusCodes.SUCCESS:
            //    getLocation();
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {

                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e)
                {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        getLocation();
                        startLocationUpdates();
                        isgpscheck = false;
                        if (Lat > 0.0 && Long > 0.0)
                        {
                            //sendclientLocation();
                            getVehicleLocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        if (isgpscheck)
                        {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startMain);
                            finish();
                        }

                        break;
                }
                break;
        }

        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                place_pick = PlaceAutocomplete.getPlace(this, data);

                if (place_pick != null)
                {
                    stoplocupdate();
                    ed_auto_pick.setText(place_pick.getAddress().toString());
                    txtPick.setText(place_pick.getAddress().toString());
                    LatLng latLong = place_pick.getLatLng();
                    Lat = latLong.latitude;
                    Long = latLong.longitude;

                    pick_LatLong = new LatLng(Lat, Long);
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try
                    {
                        addresses = geocoder.getFromLocation(Lat, Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        if (addresses != null && addresses.size() > 0)
                        {
                            city = addresses.get(0).getLocality();
                            Log.i("citypickup", city);

                        }
                    }
                    catch (IOException e) {
                    }

                    pickuplocation = ed_auto_pick.getText().toString();
                    searchstatus = "1";
                    //sendclientLocation();
                    getVehicleLocation();

                    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latLong, 15.5f);
                    mGoogleMap.moveCamera(cameraPosition);
                    mGoogleMap.animateCamera(cameraPosition);
                }
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            }
            else if (resultCode == RESULT_CANCELED) {
            }
        }

        if (requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                place_drop = PlaceAutocomplete.getPlace(this, data);

                if (place_drop != null)
                {
                    stoplocupdate();
                    ed_auto_drop.setText(place_drop.getAddress().toString());
                    txtDrop.setText(place_drop.getAddress().toString());
                    LatLng latLong1 = place_drop.getLatLng();
                    Droplat = latLong1.latitude;
                    Droplong = latLong1.longitude;
                    drop_LatLong = new LatLng(Droplat, Droplong);

                    CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLong1, 15.5f);
                    mGoogleMap.moveCamera(cameraPosition1);
                    mGoogleMap.animateCamera(cameraPosition1);
                }
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            }
            else if (resultCode == RESULT_CANCELED)
            {
            }
        }

        if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Droplocation = place.getAddress().toString();

                if (isestimateready)
                {
                    Book.setVisibility(View.GONE);
                    LatLng Latlng = place.getLatLng();
                    Droplat = Latlng.latitude;
                    Droplong = Latlng.longitude;

                    if (mCurrLocationMarker != null)
                    {
                        mCurrLocationMarker.remove();
                    }

                    sendrideestimate();
                    Book.setVisibility(View.GONE);

                    LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                    View popupView = layoutInflater.inflate(R.layout.rideestimate, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    LinearLayout Drop_Layout = (LinearLayout) popupView.findViewById(R.id.Droplocation);
                    TextView txt_DropLocation = (TextView) popupView.findViewById(R.id.txt_drop_location);
                    Picuplocation = (TextView) popupView.findViewById(R.id.pick_add);
                    Droploaction = (TextView) popupView.findViewById(R.id.drop_add);
                    Estimatecharges1 = (TextView) popupView.findViewById(R.id.estimate1);
                    Estimatecharges2 = (TextView) popupView.findViewById(R.id.estimate2);

                    Estimatecharges1.setText(Approximatefaremin + "-");
                    Estimatecharges2.setText(Approximatefaremax);
                    Cancelrideestimate = (ImageButton) popupView.findViewById(R.id.cancelrideestimate);
                    Cancelrideestimate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Book.setVisibility(View.GONE);
                        }
                    });

                    Drop_Layout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            try {
                                popupWindow.dismiss();
                                Book.setVisibility(View.GONE);
                                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                        new LatLng(19.8036125, 75.1932621),
                                        new LatLng(19.9365798, 75.4171406))))
                                        .build(MapActivity.this);

                                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                            } catch (GooglePlayServicesRepairableException e)
                            {
                                // TODO: Handle the error.
                            } catch (GooglePlayServicesNotAvailableException e)
                            {
                                // TODO: Handle the error.
                            }

                            isestimateready = true;
                            Book.setVisibility(View.GONE);
                        }
                    });
                    Picuplocation.setText(pickuplocation);
                    Droploaction.setText(Droplocation);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    dimBehind(popupWindow);
                    Book.setVisibility(View.GONE);
                }

                book_dropLocation.setText(Droplocation);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            }
            else if (resultCode == RESULT_CANCELED) {
            }
        }

        if (requestCode == 4)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Droplocation = place.getAddress().toString();

                if (isestimateready)
                {
                    Book.setVisibility(View.GONE);
                    LatLng Latlng = place.getLatLng();
                    Droplat = Latlng.latitude;
                    Droplong = Latlng.longitude;

                    if (mCurrLocationMarker != null)
                    {
                        mCurrLocationMarker.remove();
                    }

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
                    MarkerOptions options = new MarkerOptions();
                    options.position(Latlng);
                    options.title("Drop Location");
                    options.snippet("Latitude:" + Droplat + ",Longitude:" + Droplong);
                    options.icon(icon);
                    mCurrLocationMarker = mGoogleMap.addMarker(options);

                    mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                    if (book_dropLayout.isEnabled())
                    {
                        builder = new LatLngBounds.Builder();
                        builder.include(pick_LatLong);
                        builder.include(Latlng);
                        bounds = builder.build();

                        int padding = 50; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mGoogleMap.animateCamera(cu);
                    }

                    book_dropLocation.setText(Droplocation);
                }
                else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
                {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.

                }
                else if (resultCode == RESULT_CANCELED)
                {
                }
            }
        }
    }

    public void sendrideestimate()
    {
        try
        {
            String urlnew =""+Url+"/rideEstimate/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("pickupLat", String.valueOf(Lat))
                    .appendQueryParameter("pickupLong",String.valueOf(Long))
                    .appendQueryParameter("dropLat", String.valueOf(Droplat))
                    .appendQueryParameter("dropLong", String.valueOf(Droplong))
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

         //   Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

            try
            {
                JSONArray jsonArray= new JSONArray(myJson);
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                Approximatefaremin = jsonObj.getString(TAG_approximateFareMin);
                Approximatefaremax = jsonObj.getString(TAG_approximateFareMax);

            }
            catch (JSONException e)
            {

            }
        }
        catch (IOException e)
        {
          //  Log.i("Error",""+e.toString());
        }
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        checkLocationSettings();
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view)
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
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
        }
        else
        {
           // mStartUpdatesButton.setEnabled(true);
           // mStopUpdatesButton.setEnabled(false);
        }
    }


    public void getLocation()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        location = locationManager.getLastKnownLocation(bestProvider);
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("getLocation", "getLocation");

        if (location != null)
        {
            Lat = location.getLatitude();
            Long = location.getLongitude();
            LatLng latLng1 = new LatLng(Lat, Long);

            CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLng1, 15.5f);
//            mGoogleMap.animateCamera(cameraPosition1);
            mGoogleMap.moveCamera(cameraPosition1);

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try
            {
                addresses = geocoder.getFromLocation(Lat, Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses != null && addresses.size() > 0)
                {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String address11 = addresses.get(0).getAddressLine(1);
                    city = addresses.get(0).getLocality();
                    pickuplocation = address + ", " + address11 + ", " + city;
                    Log.i("get_pickuplocation", pickuplocation);
                    ed_auto_pick.setText(pickuplocation);
                    txtPick.setText(pickuplocation);
                }
            }
            catch (IOException e)
            {
            }
        }
    }

    private void updateLocationUI()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null)
        {
            Lat = location.getLatitude();
            Long = location.getLongitude();

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
            markerOptions.icon(icon);
        }
    }

    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates
                (mGoogleApiClient, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("onpause", "onpause");

        if (cd.isConnectingToInternet(MapActivity.this))
        {
            if(mGoogleApiClient==null)
            {
                TurnGpsOn();
            }
        }
    }

    @Override
    protected void onResume()
    {
        Log.i("OnResume", "OnResume");
        super.onResume();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if(mGoogleApiClient==null || mGoogleMap ==null)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        isMapReady = true;

        Log.i("Ready", "Google Map");
        mGoogleMap = googleMap;

        Log.i("Ready", "Google Map_ready");
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMapClickListener(this);
        //mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnCameraChangeListener(this);
        Log.i("Ready", "Google Map_load");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
          //Log.i("Permission","permission");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocation();
            }
        }
        else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocation();
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(ActivityRecognition.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mLastLocation == null)
        {
            if (mGoogleApiClient != null)
            {
                try
                {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                } catch (SecurityException e)
                {
                    Log.i("SecurityException", "" + e.toString());
                }

            }
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //getLocation();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        Log.i("onLocationChanged","onLocationChanged");

        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        if (place_pick == null)
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Lat = location.getLatitude();
            Long = location.getLongitude();
            Log.i("latLng123", "" + latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon));
            //sendclientLocation();
            getVehicleLocation();

            CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLng, 1);
            mGoogleMap.animateCamera(cameraPosition1);
        }

        if (mGoogleApiClient != null)
        {
            Log.i("remove_location", "remove_location");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onStart()
    {
        if(cd.isConnectingToInternet(MapActivity.this))
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
            mGoogleApiClient.connect();

            super.onStart();
        }
        else
        {
            super.onStart();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    public void stoplocupdate()
    {
        if (mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void getVehicleLocation()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost(""+Url+"/vehicleLocation/?uId="+Usersessionid+"&latitude="+String.valueOf(Lat)+"&longitude="+String.valueOf(Long)+"&pCity="+city+"&searchStatus="+searchstatus+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=Mozilla&responsetype=2");
                Log.i("httppost", ""+httppost);
                httppost.setHeader("Content-type", "application/json");
                InputStream inputStream= null;
                String result = null;
                try
                {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                }
                catch (Exception e)
                {
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish)
                    {
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJson = result;
                Log.i("myJson",""+myJson);
                if (Book.isEnabled())
                {
                    VehicleSearchResults();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void VehicleSearchResults()
    {
        try
        {
            final JSONObject jsonObj;
            if (myJson != null)
            {
                jsonObj = new JSONObject(myJson);
                mGoogleMap.clear();
                Vehicle = jsonObj.getJSONArray(TAG_driverVehicleLocationResult);
                myJson = String.valueOf(Vehicle);

                if (myJson.equals("[]"))
                {
                    Log.i("myJson = null", "null");
                    AvailTime.setText("No Cabs");
                    Book.setVisibility(View.GONE);
                    isCabAvailable = false;
                    BookingLayout.setVisibility(View.INVISIBLE);
                    snackbar1 = Snackbar.make(content_frame, "Sorry, No cab found", Snackbar.LENGTH_INDEFINITE);
                    View sbView = snackbar1.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.GreenTextColor));
                    snackbar1.show();
                }
                else
                {
                    if (myJson != null)
                    {
                        try
                        {
                            isCabAvailable = true;
                            Log.i("myJson!=null", "myJson!=null");

                            if (mMapIsTouched)
                            {
                                Log.i("mMapIsTouched", "mMapIsTouched");
                                if (snackbar1 != null && snackbar1.isShown())
                                {
                                    snackbar1.dismiss();
                                }
                                BookingLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                                BookingLayout.setVisibility(View.VISIBLE);
                                mMapIsTouched = false;
                            }
                            else
                            {
                                Log.i("visible", "visible");
                                if (snackbar1 != null && snackbar1.isShown()) {
                                    snackbar1.dismiss();
                                }
                                BookingLayout.setVisibility(View.VISIBLE);
                            }

                            JSONObject jsonObject1 = Vehicle.getJSONObject(0);
                            String responsecode = jsonObject1.getString("responseCode");

                            if (responsecode.equals("0"))
                            {
                                final String msg = jsonObject1.getString("responseMessage");
                                final String Status_res_massge1 = msg.substring(2, msg.length() - 2);

                                AvailTime.setText(Status_res_massge1);
                                Book.setVisibility(View.GONE);
                            }
                            if (responsecode.equals("1"))
                            {
                                try
                                {
                                    VehicleTarif = jsonObj.getJSONArray(TAG_driverVehicleTariffResult);
                                    mGoogleMap.clear();
                                    for (int i = 0; i < Vehicle.length(); i++)
                                    {
                                        JSONObject c = Vehicle.getJSONObject(i);
                                        double Lat_vehicle = c.getDouble(TAG_Lat);
                                        double Long_vehicle = c.getDouble(TAG_Long);
                                        Distance = c.getDouble(TAG_approximatedistance);
                                        if (isMapReady)
                                        {
                                            Location cstmrLoc = new Location("service Provider");
                                            cstmrLoc.setLatitude(Lat);
                                            cstmrLoc.setLongitude(Long);

                                            Location vehicleLoc = new Location("service Provider");
                                            vehicleLoc.setLatitude(Lat_vehicle);
                                            vehicleLoc.setLongitude(Long_vehicle);

                                            float bearing = cstmrLoc.bearingTo(vehicleLoc);

                                            Marker m1 = mGoogleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Lat_vehicle, Long_vehicle))
                                                    .anchor(0.5f, 0.5f)
                                                    .flat(true)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
                                        }

                                        JSONObject c1 = Vehicle.getJSONObject(0);
                                        Time = c1.getString(TAG_minu);
                                        Vehicleid = c1.getString(TAG_Vehicle);
                                        if (Time.equals("0"))
                                        {
                                            AvailTime.setText("2 Min");
                                        }
                                        else {
                                            AvailTime.setText(Time + "Min");
                                        }
                                    }
                                } catch (JSONException j) {
                                    j.printStackTrace();
                                }

                                for (int i = 0; i < VehicleTarif.length(); i++)
                                {
                                    JSONObject c1 = VehicleTarif.getJSONObject(0);
                                    Basekm = c1.getString("minKm");
                                    BaseFare = c1.getString("basicRate");
                                    RideTime = c1.getString("minuteCharge");
                                    Kmrate = c1.getString("kmRate");

                                    dryrunrate = c1.getDouble("dryRunRate");
                                    dryRunKm = c1.getDouble("dryRunKm");
                                    dryRun = c1.getDouble("dryRun");
                                    dryRunCharges = c1.getDouble("dryRunCharges");
                                }

                                Log.i("vehicleSearchResult", "vehicleSearchResult");
                                Book.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException j) {
                            j.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void Bookingconfirm()
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        CurrentDate = mDay + "-" + (mMonth + 1) + "-" + mYear;

        try
        {
            String urlnew =""+Url+"/bookingConfirm/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("tDate", CurrentDate)
                    .appendQueryParameter("pCity", city)
                    .appendQueryParameter("CustomerLat", String.valueOf(Lat))
                    .appendQueryParameter("CustomerLong", String.valueOf(Long))
                    .appendQueryParameter("pickUpaddress", pickuplocation)
                    .appendQueryParameter("CustomerDropLat", String.valueOf(Droplat))
                    .appendQueryParameter("CustomerDropLong", String.valueOf(Droplong))
                    .appendQueryParameter("dropAddress", dropAddress)
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
            runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog.dismiss();

                    try {
                        if (myJson != null) {
                            JSONArray json = new JSONArray(response);
                            JSONObject jsonObject = json.getJSONObject(0);
                            final String Message = jsonObject.getString("responseMessage");
                            final String responsecode = jsonObject.getString("responseCode");
                            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);
                            if (responsecode.equals("1"))
                            {
                                try
                                {
                                    JSONArray json1 = new JSONArray(myJson);
                                    JSONObject jsonObject1 = json1.getJSONObject(0);
                                    BookingId = jsonObject1.getString("bookingId");
                                    Intent in = new Intent(MapActivity.this, MapActivity_cstmr.class);
                                    in.putExtra("BookingId", BookingId);
                                    in.putExtra("latitude", Lat);
                                    in.putExtra("longitude",Long);
                                    in.putExtra("intent", "0");
                                    startActivity(in);
                                    unregisterReceiver(receiver);
                                    //finish();

                                }
                                catch (JSONException j)
                                {
                                    j.printStackTrace();
                                }
                            }
                            if (responsecode.equals("0"))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialog);
                                alertDialog.setTitle("Not Confirmed");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                                        startActivity(in);
                                        //finish();
                                    }
                                });
                                alertDialog.show();
                            }
                            if (responsecode.equals("3"))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialog);
                                alertDialog.setTitle("Blocked");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sessionManager.logoutUser();
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    } catch (JSONException j) {
                        j.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        mGoogleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onPlaceSelected(Place plac)
    {
        place = plac;
        pickuplocation = plac.getAddress().toString();

        autocompleteFragment.setText(pickuplocation);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15.5f);
        LatLng Latlng = plac.getLatLng();
        Lat = Latlng.latitude;
        Long = Latlng.longitude;
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        mGoogleMap.clear();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
        MarkerOptions options = new MarkerOptions();
        options.position(Latlng);
        options.title("Position");
        options.draggable(true);
        options.snippet("Latitude:" + Lat + ",Longitude:" + Long);
        options.icon(icon);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(Latlng, 15.5f);
        mGoogleMap.moveCamera(cameraPosition);
        mGoogleMap.animateCamera(cameraPosition);
        stopLocationUpdates();
    }

    @Override
    public void onError(Status status)
    {
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        if (place_pick == null)
        {
            if (ed_auto_pick.isEnabled())
            {
                LatLng position = mGoogleMap.getCameraPosition().target;
                Lat = latLng.latitude;
                Long = latLng.longitude;

                DecimalFormat dFormat = new DecimalFormat("#.#######");

                Lat = Double.valueOf(dFormat.format(Lat));
                Long = Double.valueOf(dFormat.format(Long));
                pick_LatLong = new LatLng(Lat, Long);
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(pick_LatLong));
                CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(pick_LatLong, 15.5f);
                mGoogleMap.animateCamera(cameraPosition1);

                new GetPicAddressFromLatLong().execute();

                if (Lat > 0.0&& Long > 0.0)
                {
                    searchstatus ="";
                    //sendclientLocation();
                    getVehicleLocation();
                }
            }
        }
        place_pick = null;

        if (place_drop == null)
        {
            if (ed_auto_drop.isEnabled())
            {
                Book.setVisibility(View.VISIBLE);
                LatLng position = mGoogleMap.getCameraPosition().target;
                Droplat = latLng.latitude;
                Droplong = latLng.longitude;

                DecimalFormat dFormat = new DecimalFormat("#.#######");

                Droplat = Double.valueOf(dFormat.format(Droplat));
                Droplong = Double.valueOf(dFormat.format(Droplong));
                drop_LatLong = new LatLng(Droplat, Droplong);

                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(drop_LatLong));
                CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(drop_LatLong, 15.5f);
                mGoogleMap.animateCamera(cameraPosition1);

                new GetDropAddressFromLatLong().execute();
            }
        }
        place_drop = null;
    }

   @Override
    public void onMapLoaded()
    {
        Log.i("onMapLoad", "OnMapLoad");
            if (place_pick == null)
            {
                if (ed_auto_pick.isEnabled())
                {
                    Farepopup.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    FareEstimate.setVisibility(View.GONE);

                    LatLng position = mGoogleMap.getCameraPosition().target;
                    Lat = position.latitude;
                    Long = position.longitude;

                    DecimalFormat dFormat = new DecimalFormat("#.#######");

                    Lat = Double.valueOf(dFormat.format(Lat));
                    Long = Double.valueOf(dFormat.format(Long));
                    pick_LatLong = new LatLng(Lat, Long);

                    if (Lat > 0.0 && Long > 0.0)
                    {
                        searchstatus ="";
                        //sendclientLocation();
                        new GetPicAddressFromLatLong().execute();
                        getVehicleLocation();
                        Log.i("lat_long", "not zero");
                    }
                    else
                    {
                        if (receiver.isConnected)
                        {
                            if (ed_auto_pick.isEnabled() || ed_auto_drop.isEnabled())
                            {
                                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                Log.i("enter_location", ""+location);

                                if (location != null)
                                {
                                    Lat = location.getLatitude();
                                    Long = location.getLongitude();
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.5f);
                                    //mGoogleMap.animateCamera(cameraUpdate);
                                    mGoogleMap.moveCamera(cameraUpdate);

                                    new GetPicAddressFromLatLong().execute();
                                }
                                else
                                {
                                    if (popupWindow != null && popupWindow.isShowing())
                                    {
                                        popupWindow.dismiss();
                                    }

                                    LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                                    View popupView = layoutInflater.inflate(R.layout.enter_location, null);
                                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    popupWindow.setFocusable(true);
                                    popupWindow.update();
                                    RelativeLayout enter_location = (RelativeLayout) popupView.findViewById(R.id.pick_loc_click);

                                    enter_location.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupWindow.dismiss();
                                            placeAutocomplete_Pick();
                                        }
                                    });
                                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                }
                            }
                        }
                    }
                }
            }

            place_pick = null;

            if (place_drop == null)
            {
                if (ed_auto_drop.isEnabled())
                {
                    Farepopup.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    FareEstimate.setVisibility(View.GONE);

                    LatLng position = mGoogleMap.getCameraPosition().target;
                    Droplat = position.latitude;
                    Droplong = position.longitude;

                    DecimalFormat dFormat = new DecimalFormat("#.#######");

                    Droplat = Double.valueOf(dFormat.format(Droplat));
                    Droplong = Double.valueOf(dFormat.format(Droplong));
                    drop_LatLong = new LatLng(Droplat, Droplong);

                    new GetDropAddressFromLatLong().execute();
                }
            }
            place_drop = null;
    }


    @Override
    public void onBackPressed()
    {
        if (confirm.isEnabled())
        {
            if (!txtDrop.getText().toString().equals(""))
            {
                marker_img_layout.setPadding(0, 0, 0, -90);
            }

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            confirm.setEnabled(false);
            Book.setEnabled(true);
            Farepopup.setEnabled(true);
            FareEstimate.setEnabled(false);
            ed_auto_pick.setEnabled(true);
            ed_auto_drop.setEnabled(false);
            confirm.setVisibility(View.GONE);
            FareEstimate.setVisibility(View.GONE);
            Farepopup.setVisibility(View.VISIBLE);
            mark_img.setVisibility(View.VISIBLE);
            FAB.setVisibility(View.VISIBLE);
            Autoapi.setVisibility(View.VISIBLE);
            ed_drop.setVisibility(View.VISIBLE);
            pick_arrow.setVisibility(View.VISIBLE);
            Book_Cnfrm.setVisibility(View.GONE);
            book_dropLayout.setVisibility(View.GONE);
            mGoogleMap.clear();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pick_LatLong, 15.5f);
            mGoogleMap.animateCamera(cameraUpdate);
            getVehicleLocation();
        }
    }

    public void alertDialoge()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog);
        alertDialogBuilder.setMessage("Extra Dry run charges Rs  "+ dryrunrate + "/Km will be applicable after "+ dryRunKm + " Km")
                .setTitle("Confirmed Dry Run")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Book.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                        Farepopup.setVisibility(View.GONE);
                        FareEstimate.setVisibility(View.VISIBLE);
                    }
                });

        alertDialogBuilder.setNegativeButton(" No ", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                mark_img.setVisibility(View.VISIBLE);
                Book.setEnabled(true);
                CameraUpdate cameraPosition = CameraUpdateFactory.zoomBy(-3);
                mGoogleMap.animateCamera(cameraPosition);
                ed_auto_pick.setEnabled(true);
                ed_auto_drop.setEnabled(false);
                FAB.setVisibility(View.VISIBLE);
                drop_arrow.setVisibility(View.GONE);
                Autoapi_drop.setVisibility(View.GONE);
                ed_pick.setVisibility(View.GONE);
                Autoapi.setVisibility(View.VISIBLE);
                ed_drop.setVisibility(View.VISIBLE);
                pick_arrow.setVisibility(View.VISIBLE);
                Book_Cnfrm.setVisibility(View.GONE);
                book_dropLayout.setVisibility(View.GONE);

                if (mCurrLocationMarker != null)
                {
                    mCurrLocationMarker.remove();
                }

                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void checkversion()
    {
        try
        {
            String urlnew =""+Url+"/versionCheck/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("ptopVersion", String.valueOf(version_code))
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("type", "0")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

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
                    //    Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        String resp = jsonObject.getString("responseCode");
                        String respmsg = jsonObject.getString("responseMessage");
                        final String Status_res_massge1 = respmsg.substring(2, respmsg.length() - 2);

                        if(resp.equals("0"))
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialog);
                            alertDialogBuilder.setMessage(Status_res_massge1)
                                    .setTitle("update app")
                                    .setCancelable(false)
                                    .setPositiveButton(" Yes ", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+Packagename+"&hl=en"));
                                            startActivity(intent);
                                            //finish();

                                        }
                                    });

                            alertDialogBuilder.setNegativeButton(" No ", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(startMain);
                                    finish();
                                }
                            });

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
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

    private class ReverseGeocodingPicLoc extends AsyncTask<LatLng, Void, String>
    {
        Context mContext;

        public ReverseGeocodingPicLoc(Context context)
        {
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params)
        {
            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
            List<Address> addresses = null;

            try
            {
                Log.i("latitude", ""+Lat);
                Log.i("longitude", ""+Long);
                addresses = geocoder.getFromLocation(Lat, Long, 1);
                Log.i("addresses", ""+addresses);

                if(addresses != null && addresses.size() > 0 )
                {
                    String address10 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    Log.i("address10", ""+address10);
                    String address11 = addresses.get(0).getAddressLine(1);
                    Log.i("address11", ""+address11);
                    city = addresses.get(0).getLocality();
                    Log.i("city", ""+city);
                    pickuplocation = address10 + ", " + address11 + ", " + city;
                    Log.i("pickuplocation", ""+pickuplocation);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return pickuplocation;
        }

        @Override
        protected void onPostExecute(String addressText)
        {
            Log.i("pickuplocation", "" + pickuplocation);
            ed_auto_pick.setText(pickuplocation);
            txtPick.setText(pickuplocation);
        }
    }

    private class GetPicAddressFromLatLong extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String response;
            try
            {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?latlng="+Lat+","+Long+"&sensor=false");
                Log.i("response",""+response);
                return response;
            }
            catch (Exception e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                Log.i("jsonObject",""+jsonObject);
                JSONArray results = jsonObject.getJSONArray("results");

                if (popupWindow != null && popupWindow.isShowing())
                {
                    if (internetConn)
                    {
                        popupWindow.dismiss();
                        Log.i("popup", "dismiss");
                    }
                }

                JSONObject result12 = results.getJSONObject(0);
                pickuplocation = result12.getString("formatted_address");
                Log.i("pickuplocation", pickuplocation);

                ed_auto_pick.setText(pickuplocation);
                txtPick.setText(pickuplocation);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetDropAddressFromLatLong extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String response;
            try
            {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?latlng="+Droplat+","+Droplong+"&sensor=false");
                //Log.i("response",""+response);
                return response;
            }
            catch (Exception e)
            {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                Log.i("jsonObject",""+jsonObject);
                JSONArray results = jsonObject.getJSONArray("results");

                JSONObject result12 = results.getJSONObject(0);
                String DropAddress = result12.getString("formatted_address");
                Log.i("DropAddress", DropAddress);

                ed_auto_drop.setText(DropAddress);
                txtDrop.setText(DropAddress);


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public String getLatLongByURL(String requestURL)
    {
        URL url;
        String response = "";
        try
        {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                {
                    response += line;
//                    Log.i("response_Line", response);
                }
            }
            else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Only handle with DrawerToggle if the drawer indicator is enabled.
        if (mDrawerToggle.isDrawerIndicatorEnabled() && mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId())
        {
            // Handle home button in non-drawer mode
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}