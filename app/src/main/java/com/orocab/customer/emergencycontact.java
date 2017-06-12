package com.orocab.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class emergencycontact extends AppCompatActivity
{
    EditText EmergencyName,Emergencynumber,Emergencyemail;
    Button Submitemergency;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY,Usersessionid,myJson;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    ProgressDialog dialog = null;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencycontact);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        cd = new ConnectionDetector(getApplicationContext());

        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        Log.i("Usersessionid", Usersessionid);

        EmergencyName = (EditText)findViewById(R.id.emergencyname);
        Emergencynumber = (EditText)findViewById(R.id.emergencynumber);
        Emergencyemail = (EditText)findViewById(R.id.emergencyemail);
        Submitemergency = (Button)findViewById(R.id.submitemergency);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow));

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



        //  }

      /*  navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
*/
        Submitemergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail(Emergencyemail.getText().toString())) {
                    if (isValidPhoneNumber(Emergencynumber.getText().toString())) {
                        if (isValidName(EmergencyName.getText().toString())) {
                            dialog = ProgressDialog.show(emergencycontact.this, "", "Adding Contact... Please Wait", true);
                            new Thread(new Runnable() {
                                public void run() {
                                    Insertemergencynumber();
                                }
                            }).start();
                        }
                    }
                }


            }
        });

    }

    public  boolean isValidPhoneNumber(String mobile)
    {
        String regEx = "^[0-9]{10}$";



        if(mobile.equals(""))
        {
            Emergencynumber.setError("Enter 10 digit number");
            return false;
        }


        if (mobile.compareTo(regEx) != 0)
        {
            return mobile.matches(regEx);

        }
        else
        {
            Emergencynumber.setError("Enter 10 digit number");
            return false;
        }
    }

    public void Insertemergencynumber()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/emergencyContact/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("name", EmergencyName.getText().toString())
                    .appendQueryParameter("mobno", Emergencynumber.getText().toString())
                    .appendQueryParameter("email", Emergencyemail.getText().toString())
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

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        dialog.dismiss();
                        JSONArray json = new JSONArray(myJson);
                        Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        String responsecode = jsonObject.getString("responseCode");
                        String responsemsg = jsonObject.getString("responseMessage");
                        if(responsecode.equals("0"))
                        {
                            Toast.makeText(getApplicationContext(),responsemsg,Toast.LENGTH_LONG).show();
                            Intent in = new Intent(getApplicationContext(),MapActivity.class);
                            startActivity(in);
                            finish();
                        }
                        /////////
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

    /* EMAIL VALIDATION */
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (Emergencyemail.getText().toString().trim().equals(""))
        {
            Emergencyemail.setError("Enter valid Email");
            // Toast.makeText(Sign_up.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (matcher.matches())
            {
                return true;
            } else {
                Emergencyemail.setError("Enter valid email");
                //Toast.makeText(Sign_up.this, "Invalid Email Address",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    /* USER NAME VALIDATION METHOD */
    private boolean isValidName(String usName) {
        String expression = "^[a-z A-Z]*$";
        CharSequence inputStr = usName;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (EmergencyName.getText().toString().trim().equals(""))
        {
            EmergencyName.setError("Enter valid First Name");

            // Toast.makeText(Sign_up.this, "Enter user name", Toast.LENGTH_SHORT).show();
            return false;
        } else

        {
            if (matcher.matches())

            {
                return true;
            } else {
                EmergencyName.setError("Enter valid First Name");
                // Toast.makeText(Sign_up.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
        finish();
    }
}
