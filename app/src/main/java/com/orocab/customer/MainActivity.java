package com.orocab.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    // public static final String LName = "Lastname";
    public static final String Email = "emailKey";
    public static final String Tag_Sign = "responseCode";
    public static final String Tag_Subuserid = "subuserid";
    public static final String Tag_uid = "uId";
    public static final String TagprefernceBalance = "balance";
    public static final String Tagemail = "email";
    public static final String Tagmobile = "mobile";
    public static final String Tagname = "firstName";
    public static final String TagLast = "Lastname";

    int Cashwalletbalance;
    String uId,fname,Lname;
    String LoginStatus,Useremail,Usermobile;
    SharedPreferences sharedpreferences;

    Button SignInButton;
    EditText UserMob,Userpwd;
    TextView SignupLink;
    LinearLayout Passwordlink;
    ProgressDialog dialog;

    UserSessionManager session;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    ImageView FaceBooklogin;
    int version_code;
    String android_id;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    BroadcastReceiver mBroadcastReceiver;
    String token,Device_id,Androidversion;
    Date Start,End;
    Permisions pm ;

    private static final int ALL_PERMISSION_CODE = 27;
    List<String> permissions = new ArrayList<String>();
    String[] params;
    private static final int PERMISSION_REQUEST_CODE = 200;
    boolean phoneStateAccepted = false;
    int currentapiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();
        pm = new Permisions(getApplicationContext());

        if (cd.isConnectingToInternet(MainActivity.this))
        {
             currentapiVersion = Build.VERSION.SDK_INT;
            //Log.i("currentapiVersion",""+currentapiVersion);
            if (currentapiVersion >= 23)
            {
                if(checkPermission())
                {
                    deviceData();
                }
                else
                {
                    requestPermission();
                }
            }
            else
            {
                deviceData();
            }

                session = new UserSessionManager(getApplicationContext());
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                session = new UserSessionManager(getApplicationContext());
                if (session.isUserLoggedIn())
                {
                    Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent1);
                    finish();
                }

                FaceBooklogin = (ImageView) findViewById(R.id.facebooklogin);
                FaceBooklogin.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Facebook.class);
                        startActivity(in);
                        finish();
                    }
                });


                UserMob = (EditText) findViewById(R.id.Username);
                Userpwd = (EditText) findViewById(R.id.Password);
                Passwordlink = (LinearLayout) findViewById(R.id.passwordlink);
                SignupLink = (TextView) findViewById(R.id.Signuplink);
                SignInButton = (Button) findViewById(R.id.SignIn);
                SignInButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (UserMob.getText().toString().trim().equals("") && Userpwd.getText().toString().trim().equals(""))
                        {
                            UserMob.setError("Enter Mobile or Email");
                            Userpwd.setError("Enter password");
                        }
                        else
                        {
                            dialog = ProgressDialog.show(MainActivity.this, "", "Signing In... Please Wait", true);

                            new Thread(new Runnable()
                            {
                                public void run()
                                {


                                    Sign_in();
                                }
                            }).start();

                        }
                    }
                });

                SignupLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Signup.class);
                        in.putExtra("token", token);
                        startActivity(in);
                        finish();
                    }
                });
                Passwordlink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Forgotpassword.class);
                        startActivity(in);
                        finish();
                    }
                });
            }
            else
            {
                cd.showNetDisabledAlertToUser(MainActivity.this);
            }


    }

    public void deviceData()
    {
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Device_id = telephonyManager.getDeviceId();

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        Androidversion = manufacturer + model + version + versionRelease;

        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version_code = info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {

        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        Log.i("resultCode", "" + resultCode);
        if (ConnectionResult.SUCCESS != resultCode)
        {
            Log.i("SUCCESS", "SUCCESS");
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                Log.i("isUserRecoverableError", "isUserRecoverableError");
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }

            else
            {
                 Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        }
        else
        {
            //Starting intent to register device
            Log.i("itent", "itent");
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
        //// end push

        ///code push notification
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS))
                {
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    Log.i("token", "" + token);

                }
                else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR))
                {
                    //   Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    public void requestAllPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.RECEIVE_SMS)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_GSERVICES))
        {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

            Log.i("Log11","ask for permisions");
        }

        //And finally ask for the permission

        Log.i("Log12","permisions to req :"+ permissions.toString());
        ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(new String[permissions.size()]),ALL_PERMISSION_CODE);

    }

    public boolean isAllPermAllowed()
    {
        String locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String locationpermissioncoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        String ContactsPermission = android.Manifest.permission.READ_CONTACTS;
        String storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        String smsPermission = android.Manifest.permission.RECEIVE_SMS;
        String GCMgsf = android.Manifest.permission.WRITE_GSERVICES;
        String Phonestate = android.Manifest.permission.READ_PHONE_STATE;

        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this,  android.Manifest.permission.RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_GSERVICES);
        int result6=  ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        int result7 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.i("Log1",permissions.toString());

        if (result1 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(smsPermission);
        }

        if (result2 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(locationPermission);
        }

        if (result3 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(ContactsPermission);
        }

        if (result4 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(storagePermission);
        }

        if (result5 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(GCMgsf);
        }

        if (result6 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Phonestate);
        }

        if (result7 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(locationpermissioncoarse);
        }

        if (!permissions.isEmpty())
        {
            params = permissions.toArray(new String[permissions.size()]);
            //   requestPermissions(params, REQUEST_PERMISSIONS);
            requestAllPermission();
            return false;
        }
        else
        {
            return true;
            // We already have permission, so handle as normal
        }
    }

    private boolean checkPermission()
    {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),  android.Manifest.permission.RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_GSERVICES);
        int result6=  ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int result8 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS);


        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED
                && result7 == PackageManager.PERMISSION_GRANTED && result8 == PackageManager.PERMISSION_GRANTED;



    }

    public void requestPermission()
    {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_GSERVICES,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_SETTINGS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean smsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean contactsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean extStrgAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean gserviceAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    phoneStateAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocAccepted = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExtStrgAccepted = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean callPhoneAccepted = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean outCallAccepted = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean readCalenderAccepted = grantResults[10] == PackageManager.PERMISSION_GRANTED;
                    boolean wrriteSettings = grantResults[11] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && smsAccepted && contactsAccepted && extStrgAccepted
                            && gserviceAccepted && phoneStateAccepted && coarseLocAccepted && writeExtStrgAccepted
                            && callPhoneAccepted && outCallAccepted && readCalenderAccepted && wrriteSettings)
                    {
                        //Log.i("Granted","You can use");
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            //Log.i("Not Granted","You can't use");
                        }
                    }
                }
                break;
        }
    }

    public void Sign_in()
    {
        String uname = UserMob.getText().toString();
        String upass = Userpwd.getText().toString();

        String responsetype = "2";
        // String APIKEY = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
        String url=""+Url+"/signIn/?";

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", uname));
        nameValuePairs.add(new BasicNameValuePair("password", upass));

        try
        {
            String uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("email",uname)
                    .appendQueryParameter("password",upass)
                    .appendQueryParameter("token",token)
                    .appendQueryParameter("version",String.valueOf(version_code))
                    .appendQueryParameter("androidVersion",Androidversion)
                    .appendQueryParameter("imeiNumber",Device_id)
                    .appendQueryParameter("androidId",android_id)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserID","1212")
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype",responsetype)
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    dialog.dismiss();
                }
            });
            final String Str = "1";
            JSONArray json = new JSONArray(response);
            //Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str ="1";
            final String responsecode = jsonObject.getString(Tag_Sign);
            final String Message = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);


            if(responsecode.compareTo(str)==0)
            {
                final String LoginStatus =jsonObject.getString(Tag_Subuserid);
                session.createUserLoginSession(uname, upass);
                uId = jsonObject.getString(Tag_uid);
                Cashwalletbalance = jsonObject.getInt(TagprefernceBalance);
                Useremail = jsonObject.getString(Tagemail);
                Usermobile = jsonObject.getString(Tagmobile);
                fname = jsonObject.getString(Tagname);
                Lname = jsonObject.getString("lastName");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Email, uname);
                editor.putString(Tag_uid, uId);
                editor.putString(Name,fname);
                editor.putInt(TagprefernceBalance, Cashwalletbalance);
                editor.putString(Tagemail, Useremail);
                editor.putString(Tagmobile,Usermobile);
                editor.putString("versioncode",String.valueOf(version_code));
                editor.apply();
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (responsecode.equals("1"))
                    {
                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(in);
                        finish();
                    }
                    else
                    {
                       // Log.i("LoginStatus", LoginStatus);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
                        alertDialog.setTitle("");
                        alertDialog.setMessage(Status_res_massge1);
                        //   alertDialog.setIcon(R.drawable.fail);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                        alertDialog.show();
                    }
                }
            });
            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
        }
    }



    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.w("MainActivity", "onResume");
        Log.i("internet",""+cd.isConnectingToInternet(MainActivity.this));
        if(cd.isConnectingToInternet(MainActivity.this))
        {
           if(currentapiVersion<23)
           {
               Log.i("lolipopresume", "lollipop");

               LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                       new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
               LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                       new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
           }
            if (phoneStateAccepted)
            {
                deviceData();
                Log.i("onResume12315", "onResume12315");

                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
            }
        }
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.w("MainActivity", "onPause");
        Log.i("onpauseinternet", "" + cd.isConnectingToInternet(MainActivity.this));
        if (phoneStateAccepted)
        {
            Log.w("onPause", "unregisterReceiver");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            finish();
        }
        if(currentapiVersion<23)
        {
            Log.w("onPause", "unregisterReceiver");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            finish();
        }
    }
}