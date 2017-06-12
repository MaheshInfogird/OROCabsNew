package com.orocab.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

public class MobileActivity extends AppCompatActivity
{

    public EditText Usermobile;
    public Button updatemobile;
    public static final String phone = "phonekey";
    SharedPreferences sharedpreferences;
    String Fname, Lname, Mobile, token, otp;
    ProgressDialog dialog = null;
    public LinearLayout Resetlayout;
    public Button Facebooksignup;
    public EditText Faceotp;
    public static final String MyPREFERENCES = "MyPrefs";

    public static final String Tag_Sign = "responseCode";
    public static final String Tag_Subuserid = "subuserid";
    public static final String Tag_uid = "uId";
    public static final String TagprefernceBalance = "balance";
    public static final String Tagemail = "email";
    public static final String Tagmobile = "mobile";
    public static final String Tagname = "firstName";
    public static final String TagLname = "lastName";
    int Cashwalletbalance;
    String uId, fname;
    String LoginStatus, Useremail, lname, Email;
    String mobile;
    UserSessionManager session;
    SharedPreferences.Editor editor;
    ConnectionDetector cd;
    String APIKEY, IPAddress, Url;
    public static final String passkey1 = "passkey1";
    public static boolean CurrentlyRunning_pass= false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        Usermobile = (EditText) findViewById(R.id.facemobile);
        updatemobile = (Button) findViewById(R.id.updatemobile);
        Fname = getIntent().getStringExtra("firstName");
        Lname = getIntent().getStringExtra("lastName");
        Email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token");
        Resetlayout = (LinearLayout) findViewById(R.id.faceotplayout);
        Faceotp = (EditText) findViewById(R.id.faceotp);
        Facebooksignup = (Button) findViewById(R.id.updatefbdetials);
        session = new UserSessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd= new ConnectionDetector(getApplicationContext());
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();
        Url = cd.geturl();

        Facebooksignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = ProgressDialog.show(MobileActivity.this, "", "Signing In... Please Wait", true);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        Updatefbotp();
                    }
                }).start();


            }
        });

        updatemobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isValidPhoneNumber(Usermobile.getText().toString()))
                {
                    dialog = ProgressDialog.show(MobileActivity.this, "", "Signing In... Please Wait", true);
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            Updatemobile();
                        }
                    }).start();

                }

            }
        });

        IncomingSms.bindListener(new getSmsListener()
        {
            @Override
            public void messageReceived(String messageText)
            {
                Log.i("messageText", messageText);
                Faceotp.setText(messageText);

            }
        });



    }


    @Override
    public void onStart()
    {

        CurrentlyRunning_pass = true;
        SharedPref();
        super.onStart();
      //  Toast.makeText(MobileActivity.this, "Settings value inserted true", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop()
    {

        CurrentlyRunning_pass = false;
        SharedPref();
       // Toast.makeText(MobileActivity.this, "Settings value inserted false", Toast.LENGTH_SHORT).show();
        super.onStop();
    }
    public void SharedPref()
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(passkey1,CurrentlyRunning_pass);
        editor.putString("otp","Fb");
        editor.apply();

    }


    public interface getSmsListener
    {
        public void messageReceived(String messageText);
    }




    public void Updatemobile()
    {
        Mobile = Usermobile.getText().toString();
        otp = Faceotp.getText().toString();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("firstName", Fname));
        nameValuePairs.add(new BasicNameValuePair("lastName", Lname));
        nameValuePairs.add(new BasicNameValuePair("email", Email));
        nameValuePairs.add(new BasicNameValuePair("mobile", Mobile));
        try {
            String Facebookmobile = "" + Url + "/faceSignUp/?";
            // String Facebookmobile = "http://jaidevcoolcab.cabsaas.in/sandbox/index/faceSignUp/?";
            // String APIKEY="S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
            // String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(Facebookmobile)
                    .buildUpon()
                    .appendQueryParameter("firstName", Fname)
                    .appendQueryParameter("lastName", Lname)
                    .appendQueryParameter("email", Email)
                    .appendQueryParameter("mobile", Mobile)
                    .appendQueryParameter("token", token)
                    .appendQueryParameter("otp", otp)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            Log.i("uri", uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            //   Toast.makeText(this, "" + response, Toast.LENGTH_LONG).show();
            Log.i("response", "" + response);

            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });


            JSONArray json = new JSONArray(response);
            Log.i("json", "" + json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus = jsonObject.getString(Tag_Sign);
            final String Message = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);

            Log.i("LoginStatus", LoginStatus);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileActivity.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Resetlayout.setVisibility(View.VISIBLE);

                            }
                        });
                        alertDialog.show();
                    }

                    if (LoginStatus.equals("0"))

                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileActivity.this, R.style.AlertDialog);
                        alertDialog.setTitle("");
                        alertDialog.setMessage(Status_res_massge1);
                        //   alertDialog.setIcon(R.drawable.fail);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {


                            }
                        });
                        alertDialog.show();
                    }

                   /* else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileActivity.this);
                        alertDialog.setTitle("");
                        alertDialog.setMessage("Invalid OTP");
                        //   alertDialog.setIcon(R.drawable.fail);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {


                            }
                        });
                        alertDialog.show();


                    }*/
                }
            });
        } catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
            //  Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }

    }


    public void Updatefbotp()
    {


        Mobile = Usermobile.getText().toString();
        otp = Faceotp.getText().toString();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("firstName", Fname));
        nameValuePairs.add(new BasicNameValuePair("lastName", Lname));
        nameValuePairs.add(new BasicNameValuePair("email", Email));
        nameValuePairs.add(new BasicNameValuePair("mobile", Mobile));
        try {
            String Facebookmobile = "" + Url + "/faceSignUp/?";
            // String Facebookmobile = "http://jaidevcoolcab.cabsaas.in/sandbox/index/faceSignUp/?";
            // String APIKEY="S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
            //  String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(Facebookmobile)
                    .buildUpon()
                    .appendQueryParameter("firstName", Fname)
                    .appendQueryParameter("lastName", Lname)
                    .appendQueryParameter("email", Email)
                    .appendQueryParameter("mobile", Mobile)
                    .appendQueryParameter("token", token)
                    .appendQueryParameter("otp", otp)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
           // Log.i("uri", uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            //   Toast.makeText(this, "" + response, Toast.LENGTH_LONG).show();
           // Log.i("response", "" + response);

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    dialog.dismiss();
                }
            });



          /*  JSONArray json = new JSONArray(response);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus =jsonObject.getString(Tag_Sign);
            Log.i("LoginStatus", LoginStatus);*/

            final String Str = "1";
            JSONArray json = new JSONArray(response);
            //Log.i("json", "" + json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str = "4";
            final String Message = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);
            final String LoginStatus = jsonObject.getString(Tag_Subuserid);
            //Log.i("LoginStatus", LoginStatus);


            if (LoginStatus.compareTo(str) == 0)
            {
                Log.i("str", str);
                session.createUserLoginSession(Email, token);
                uId = jsonObject.getString(Tag_uid);
                Cashwalletbalance = jsonObject.getInt(TagprefernceBalance);
                Useremail = jsonObject.getString(Tagemail);
                mobile = jsonObject.getString(Tagmobile);
                fname = jsonObject.getString(Tagname);
                //  fname = jsonObject.getString(Tagname);
                lname = jsonObject.getString(TagLname);
                Log.i("fname", fname);
                editor = sharedpreferences.edit();
                editor.putString(Email, Useremail);
                editor.putString(Tag_uid, uId);
                editor.putString(Tagname, fname);
                editor.putString(TagLname, lname);
                editor.putInt(TagprefernceBalance, Cashwalletbalance);
                editor.putString(Tagemail, Useremail);
                editor.putString(Tagmobile, mobile);
                editor.apply();
            }


            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    if (LoginStatus.equals("4")) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileActivity.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent in = new Intent(MobileActivity.this, MapActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                        alertDialog.show();
                    } else

                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileActivity.this, R.style.AlertDialog);
                        alertDialog.setTitle("Invalid");
                        alertDialog.setMessage(Status_res_massge1);
                        //   alertDialog.setIcon(R.drawable.fail);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        alertDialog.show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
            //  Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }

    }


    public boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        if (mobile.compareTo(regEx) != 0)
        {
            return mobile.matches(regEx);
        } else
        {
            Usermobile.setError("Enter 10 digit number");
            return false;
        }

    }
}

