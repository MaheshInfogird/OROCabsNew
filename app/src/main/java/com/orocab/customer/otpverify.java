package com.orocab.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class otpverify extends AppCompatActivity
{


    String Fname,Lastname,mobile,email,password,confirmpassword;
    EditText InputOtp;
    String Finalotp;

    Button FinalSignup;
    ProgressDialog progressDialog;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String LName = "Lastname";
    //  public static final String Emailn = "emailKey";
    public static final String phone = "phonekey";
    public static final String Tag_Sign = "responseCode";

    public static final String Tag_result = "result";

    public static final String Tag_Subuserid = "subuserid";
    public static final String Tag_uid = "uId";
    public static final String TagprefernceBalance = "balance";
    public static final String Tagemail = "email";
    public static final String Tagmobile = "mobile";
    public static final String Tagname = "firstName";
    public static final String TagLast = "Lastname";

    SharedPreferences sharedpreferences;
    public static boolean CurrentlyRunning_pass= false;
    public static final String passkey1 = "passkey1";


    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    ProgressDialog dialog = null;
    String OTP;
    ImageView Backtomain;
    UserSessionManager session;


    int Cashwalletbalance;
    String uId,fname,Lname;
    String LoginStatus,Useremail,Usermobile;
    String token,Device_id,Androidversion;
    int version_code;
    String android_id;
    TextView ResendOtp;
    String Subuserid;
    ProgressDialog Dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);

        Fname  = getIntent().getStringExtra("firstName");
        Lastname = getIntent().getStringExtra("lastName");
        email = getIntent().getStringExtra("Email");
        mobile = getIntent().getStringExtra("Mobile");
        password = getIntent().getStringExtra("password");
        confirmpassword  = getIntent().getStringExtra("confirmpassword");
        OTP  = getIntent().getStringExtra("otp");
        token = getIntent().getStringExtra("token");



    //    Dialog =  ProgressDialog.show(otpverify.this,"","Auto Detecting OTP..please wait",true);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        ResendOtp = (TextView)findViewById(R.id.Resendotp);
        session = new UserSessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        ResendOtp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dialog = ProgressDialog.show(otpverify.this, "", "Resending otp... Please Wait", true);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        setResendOtp();
                    }

                }).start();

            }
        });


        IncomingSms.bindListener(new SmsListener()
        {
            @Override
            public void messageReceived(String messageText)
            {
                Log.i("messageText",messageText);
                InputOtp.setText(messageText);
             //   Dialog.dismiss();

            }
        });


        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        Log.i("Androidid", "" + android_id);


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Device_id = telephonyManager.getDeviceId();
        Log.i("mobile_id", "" + Device_id);


        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        Androidversion = manufacturer + model + version + versionRelease;
        Log.e("MyActivity", "manufacturer " + manufacturer
                        + " \n model " + model
                        + " \n version " + version
                        + " \n versionRelease " + versionRelease
        );

        try
        {

            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            //string version_code = info.versionName;
            version_code = info.versionCode;
            Log.i("version_code", "" + version_code);
        }

        catch (PackageManager.NameNotFoundException e)
        {

        }



        InputOtp = (EditText)findViewById(R.id.otprecieved);
        FinalSignup = (Button)findViewById(R.id.OtPverifybutton);
        FinalSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isvalidotp())
                {
                    dialog = ProgressDialog.show(otpverify.this, "", "Signing In... Please Wait", true);

                    new Thread(new Runnable()
                    {
                        public void run()
                        {

                            verify_otp();


                        }

                    }).start();

                }


            }
        });



        if(OTP!=null)
        {
            InputOtp.setText(OTP);
        }


    }

    @Override
    public void onStart()
    {

        CurrentlyRunning_pass = true;
        SharedPref();
        super.onStart();
       // Toast.makeText(otpverify.this, "Settings value inserted true", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop()
    {

        CurrentlyRunning_pass = false;
        SharedPref();
       // Toast.makeText(otpverify.this, "Settings value inserted false", Toast.LENGTH_SHORT).show();
        super.onStop();
    }
    public void SharedPref()
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(passkey1,CurrentlyRunning_pass);
        editor.putString("otp","ot");
        editor.apply();

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if(OTP!=null)
        {
            InputOtp.setText(OTP);
        }



    }

    public void setResendOtp()
    {
        try
        {


            String otpurl = ""+Url+"/resendOtp/?";//firstName="+Fname+"&lastName="+Lastname+"&email="+email+"&mobile="+mobile+"&password="+password+"&confirmpassword="+confirmpassword+"&otp="+Finalotp+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=androidapp&responsetype=2";


            String uri = Uri.parse(otpurl)
                    .buildUpon()
                    .appendQueryParameter("mobile", mobile)
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent","androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();


            Log.i("uri", uri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            Log.i("url", otpurl);
           // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response1 = httpclient.execute(httppost, responseHandler);
            Log.i("response1",response1);
            dialog.dismiss();
            JSONArray json = new JSONArray(response1);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String responseMessage =jsonObject.getString("responseMessage");
            final String Status_res_massge1 = responseMessage.substring(2, responseMessage.length() - 2);
            Toast.makeText(getApplicationContext(),Status_res_massge1,Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
         e.printStackTrace();
        }
    }

    public void recivedSms(String message)
    {
        try
        {
            Log.i("message", message);
            OTP =message;
            Log.i("OTP",OTP);
            InputOtp.setText(OTP);

            //  if(OtpNumber.getText().equals("123456")){
         //   display.setText("OTP Verification Successful");
            //  }
            //  else {
            // display.setText("Unsuccessful");
            //  }
        }
        catch (Exception e)
        {
        }
    }

    public interface SmsListener
    {
        public void messageReceived(String messageText);
    }




    public void verify_otp()
    {

        Finalotp  = InputOtp.getText().toString();
        // SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  SharedPreferences.Editor editor = sharedpreferences.edit();
        //  editor.putString(Phone, mobile);
        //  editor.putString(Email, email);
        // editor.clear();
        // editor.commit();
        final String  Mobile = mobile.toString();
        final String  Email = email.toString();


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("firstName", Fname));
        nameValuePairs.add(new BasicNameValuePair("lastName", Lastname));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("password",password));
        nameValuePairs.add(new BasicNameValuePair("confirmpassword",confirmpassword));
        nameValuePairs.add(new BasicNameValuePair("otp",Finalotp));
        try
        {
            String otpurl = ""+Url+"/signUp/?";//firstName="+Fname+"&lastName="+Lastname+"&email="+email+"&mobile="+mobile+"&password="+password+"&confirmpassword="+confirmpassword+"&otp="+Finalotp+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=androidapp&responsetype=2";


            String uri = Uri.parse(otpurl)
                    .buildUpon()
                    .appendQueryParameter("firstName", Fname)
                    .appendQueryParameter("lastName",Lastname)
                    .appendQueryParameter("email",email)
                    .appendQueryParameter("mobile", mobile)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("confirmpassword", confirmpassword)
                    .appendQueryParameter("otp", Finalotp)
                    .appendQueryParameter("token",token)
                    .appendQueryParameter("version",String.valueOf(version_code))
                    .appendQueryParameter("androidVersion",Androidversion)
                    .appendQueryParameter("imeiNumber", Device_id)
                    .appendQueryParameter("androidId", android_id)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent","androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();



          //  Log.i("uri", uri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
          //  Log.i("url", otpurl);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response1 = httpclient.execute(httppost, responseHandler);
            Log.i("response1", response1);



           /* JSONArray json = new JSONArray(response1);
            Log.i("json", "" + json);
            JSONObject jsonObject = json.getJSONObject(0);
          */


            final String Str = "1";
            JSONArray json = new JSONArray(response1);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str ="1";

            final String LoginStatus =jsonObject.getString(Tag_Sign);
            final String responseMessage =jsonObject.getString("responseMessage");
            final String Status_res_massge1 = responseMessage.substring(2, responseMessage.length() - 2);

            Log.i("LoginStatus",LoginStatus);
            Log.i("LoginStatus",LoginStatus);

            if(LoginStatus.compareTo(str)==0)
            {

                Subuserid =jsonObject.getString(Tag_Subuserid);
                uId = jsonObject.getString(Tag_uid);
                Log.i("uId",uId);
                Cashwalletbalance = jsonObject.getInt(TagprefernceBalance);
                Log.i("Cashwalletbalance",""+Cashwalletbalance);
                Useremail = jsonObject.getString(Tagemail);
                Log.i("Useremail",""+Useremail);
                Usermobile = jsonObject.getString(Tagmobile);
                Log.i("Usermobile",""+Usermobile);
                fname = jsonObject.getString(Tagname);
                Log.i("fname",""+fname);
                Log.i("fname",fname);
                Lname = jsonObject.getString("lastName");
                Log.i("Lname", Lname);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Tagemail, Useremail);
                editor.putString(Tag_uid, uId);
                editor.putString(Tagname, fname);
                editor.putString("lastName", Lname);
                editor.putInt(TagprefernceBalance, Cashwalletbalance);
                editor.putString(Tagmobile, Usermobile);
                editor.apply();
                session.createUserLoginSession(Useremail, password);

            }


            // Toast.makeText(this,"Your OTP HAS Been Verified " +response1 , Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    dialog.dismiss();
                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(otpverify.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent in = new Intent(otpverify.this,MapActivity.class);
                                startActivity(in);
                                finish();

                            }
                        });
                        alertDialog.show();
                    }

                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(otpverify.this, R.style.AlertDialog);
                        alertDialog.setTitle("Failed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        alertDialog.show();
                    }
                }
            });


            // Log.i("response", "" + response1);
            // HttpResponse response11 = httpclient.execute(httppost);
            //  HttpEntity entity = response11.getEntity();
            // is = entity.getContent();
            Log.e("pass 1", "connection success ");
            //Intent in = new Intent(this,MainActivity.class);
            // startActivity(in);
        }  catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
        }
      /*  try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }   catch (Exception e)
        {
            Log.e("Fail 2", e.toString());
        }*/


    }

    public boolean isvalidotp()
    {

        if(InputOtp.getText().toString().equals(""))
        {
            InputOtp.setError("Enter OTP");
            return false;
        }
        return true;
    }



}
