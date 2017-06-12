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
import android.widget.ImageView;
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

public class Forgotpassword extends AppCompatActivity
{

    LinearLayout ResetLayout;
    EditText EnterEmailmob,otp,pwd,cnfpwd;
    Button SendEmailmob,ResetButcnf;

    String Email;
    String Newotp,newpassword,confirmpwd;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Tag_Sign = "responseCode";
    public static final String Tag_responsemsg="responseMessage";
    SharedPreferences sharedpreferences;

    ProgressDialog dialog = null;
    ProgressDialog dialog1 = null;
    ConnectionDetector cd;
    String APIKEY,Url,IPaddress;
    ImageView Forgotpwd;

    public static boolean CurrentlyRunning_pass= false;
    public static final String passkey1 = "passkey1";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        ResetLayout = (LinearLayout)findViewById(R.id.ResetLayout);
        EnterEmailmob = (EditText)findViewById(R.id.MobileReset);
        otp = (EditText)findViewById(R.id.ResetOTP);
        pwd = (EditText)findViewById(R.id.Resetpassword);
        cnfpwd = (EditText)findViewById(R.id.Resetcnfpassword);
        SendEmailmob = (Button)findViewById(R.id.ButtonsendEmail);
        ResetButcnf = (Button)findViewById(R.id.Buttonrstpwd);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        APIKEY = cd.getAPIKEY();
        IPaddress = cd.getLocalIpAddress();
        Forgotpwd = (ImageView)findViewById(R.id.forgotpwd);

        Forgotpwd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();
            }
        });

        IncomingSms.bindListener(new ForgotSmsListener()
        {
            @Override
            public void messageReceived(String messageText)
            {
                Log.i("messageText",messageText);
                otp.setText(messageText);
            }
        });


        SendEmailmob.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkemailormobile())
                {
                    dialog = ProgressDialog.show(Forgotpassword.this, "", "Please Wait... Updating Your Password", true);
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            set_password();
                        }
                    }).start();
                }
            }
        });



        ResetButcnf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pwd.getText().toString().compareTo(cnfpwd.getText().toString())!=0)
                {
                    cnfpwd.setError("Password do not match");
                    cnfpwd.setError("Password do not match");
                }
                else
                {
                    dialog1 = ProgressDialog.show(Forgotpassword.this, "","Please Wait... Updating Your Password", true);
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            Reset_password();
                        }
                    }).start();
                }
            }




        });
    }

    @Override
    public void onStart()
    {
        CurrentlyRunning_pass = true;
        SharedPref();
      //  Toast.makeText(Forgotpassword.this, "Settings value inserted true", Toast.LENGTH_SHORT).show();
        super.onStart();
    }

    @Override
    public void onStop()
    {
        CurrentlyRunning_pass = false;
        SharedPref();
     //   Toast.makeText(Forgotpassword.this, "Settings value inserted false", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    public void SharedPref()
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(passkey1,CurrentlyRunning_pass);
        editor.putString("otp","fp");
        editor.apply();
    }

    public void recivedSms(String message)
    {
        try
        {
            Log.i("message", message);
            otp.setText(message);
        }
        catch (Exception e)
        {
        }
    }


    private boolean checkemailormobile()
    {
        if((EnterEmailmob.getText().toString().trim().equals("")))
        {
            EnterEmailmob.setError("Enter valid Email or mobile");
            return false;
        }
        return true;
    }

    public interface ForgotSmsListener
    {
        public void messageReceived(String messageText);
    }


    public void set_password()
    {
        //String Email = email.toString();
        //String Mobile = mobile.toString();
        Email = EnterEmailmob.getText().toString();

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


        nameValuePairs.add(new BasicNameValuePair("email", Email));
        try {
            //  Toast.makeText(getApplicationContext(), "Hello try", Toast.LENGTH_LONG).show();

            // 17} API For forget password:-
            //  http://jaidevcoolcab.cabsaas.in/sandbox/index/forgetPass/?email=rohit@info.com&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2
            // 18} API For reset Password:-
            // http://jaidevcoolcab.cabsaas.in/sandbox/index/resetPass/?password=123&confirmPassword=123&token=c0512cbe7367bfa2469c860a22c952ce&token1=LkMZlJq6&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2
            String passwordreset=""+Url+"/forgetPass/?";
            //String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(passwordreset)
                    .buildUpon()
                    .appendQueryParameter("email", Email)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress",IPaddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();


            Log.i("uri", uri);
            HttpClient httpclient = new DefaultHttpClient();
            // String passwordreset ="";
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.i("response", "" + response);


            // Log.i("Name_value", "" + nameValuePairs);
            JSONArray json = new JSONArray(response);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus =jsonObject.getString(Tag_Sign);
            final String responsemsg=jsonObject.getString(Tag_responsemsg);
            final String Status_res_massge1 = responsemsg.substring(2, responsemsg.length() - 2);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    dialog.dismiss();

                    Log.i("Inside runnable","Inside runnable");
                    if (LoginStatus.equals("1"))
                    {
                        Log.i("Inside LoginStatus", "Inside LoginStatus");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Forgotpassword.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ResetLayout.setVisibility(View.VISIBLE);
                                ResetButcnf.setVisibility(View.VISIBLE);
                                SendEmailmob.setVisibility(View.GONE);
                            }
                        });
                        alertDialog.show();
                    } else

                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Forgotpassword.this, R.style.AlertDialog);
                        alertDialog.setTitle("Failed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
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
        }
    }


    public void Reset_password()
    {
        Newotp = otp.getText().toString();
        newpassword = pwd.getText().toString();
        confirmpwd = cnfpwd.getText().toString();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("email", Email));
        try
        {
            // http://jaidevcoolcab.cabsaas.in/sandbox/index/resetPass/?password=&confirmPassword=&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2
            String passwordrest=""+Url+"/resetPass/?";
            // String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(passwordrest)
                    .buildUpon()
                    .appendQueryParameter("password", newpassword)
                    .appendQueryParameter("confirmPassword", confirmpwd)
                    .appendQueryParameter("otp",Newotp)
                    .appendQueryParameter("responsetype", "2")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress",IPaddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .build().toString();


            Log.i("uri",uri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);

            Log.i("Name_value", "" + nameValuePairs);
            JSONArray json = new JSONArray(response);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus =jsonObject.getString(Tag_Sign);
            Log.i("LoginStatus",LoginStatus);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    dialog1.dismiss();

                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Forgotpassword.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage("Your password has been changed!!.Please Login Again");
                        //alertDialog.setIcon(R.drawable.);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Forgotpassword.this, MainActivity.class));
                                finish();
                                //Intent in = new Intent(Password.this,ccrlogin.class);
                                // startActivity(in);
                            }
                        });
                        alertDialog.show();
                    } else
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Forgotpassword.this, R.style.AlertDialog);
                        alertDialog.setTitle("");
                        alertDialog.setMessage("OTP does not Match");
                        //alertDialog.setIcon(R.drawable.fail);
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
        catch (Exception e) {
        }
    }
}