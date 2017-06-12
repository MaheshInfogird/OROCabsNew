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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity
{

    EditText FirstName,LastName,MobileNumber,Email,Password,UserCnfPwd;
    Button SignupButton;
    SharedPreferences sharedpreferences;

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



    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    ProgressDialog dialog = null;
    String OTP;
    ImageView Backtomain;
    UserSessionManager session;
    CheckBox checkstate;

    int Cashwalletbalance;
    String uId,fname,Lname;
    String LoginStatus,Useremail,Usermobile,token;
    TextView Termsandcond;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirstName = (EditText)findViewById(R.id.Firstname);
        LastName = (EditText)findViewById(R.id.lastname);
        MobileNumber = (EditText)findViewById(R.id.mobilenumber);
        Email = (EditText)findViewById(R.id.emailaddress);
        Password = (EditText)findViewById(R.id.userpwd);
        UserCnfPwd = (EditText)findViewById(R.id.usercnfpwd);
        SignupButton = (Button)findViewById(R.id.SignupButton);
        checkstate = (CheckBox)findViewById(R.id.checkBoxstate);
        Termsandcond = (TextView)findViewById(R.id.Termsandcondition);
        Termsandcond.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(""));
                startActivity(intent);*/

                String url = "http://www.orocab.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        session = new UserSessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        token = getIntent().getStringExtra("token");

        Backtomain = (ImageView)findViewById(R.id.imagebacksignup);
        Backtomain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);

            }
        });



        SignupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if (isValidEmail(Email.getText().toString()))
                {
                    if (isValidPhoneNumber(MobileNumber.getText().toString()))
                    {
                        if (isValidName(FirstName.getText().toString()))
                        {
                            if (checkpassword())
                            {
                                if (checkstate.isChecked())
                                {
                                    dialog = ProgressDialog.show(Signup.this, "", "Sign Up... Please Wait", true);
                                    new Thread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            insert();
                                        }
                                    }).start();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"please Accept Terms and Condition",Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    }
                }
            }
        });


    }




    public void insert()
    {
        final String Fname = FirstName.getText().toString();
        final String Lname = LastName.getText().toString();
        final String email = Email.getText().toString();
        final String mobile= MobileNumber.getText().toString();
        final String password = Password.getText().toString();
        final String confirmpwd = UserCnfPwd.getText().toString();


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("firstName", Fname));
        nameValuePairs.add(new BasicNameValuePair("lastName", Lname));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("confirmpassword", confirmpwd));
      //  nameValuePairs.add(new BasicNameValuePair("otp", confirmpwd));
        nameValuePairs.add(new BasicNameValuePair("ApiKey", "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD"));
        nameValuePairs.add(new BasicNameValuePair("UserIPAddress", "127.0.0.1"));
        nameValuePairs.add(new BasicNameValuePair("UserID", "1212"));
        nameValuePairs.add(new BasicNameValuePair("UserAgent", "android-App"));
        nameValuePairs.add(new BasicNameValuePair("responsetype", "2"));

        try
        {

            String urlnew =""+Url+"/signUp/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("firstName", Fname)
                    .appendQueryParameter("lastName",Lname)
                    .appendQueryParameter("email",email)
                    .appendQueryParameter("mobile",mobile)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("confirmpassword", confirmpwd)
                    .appendQueryParameter("otp","")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent","androidApp").appendQueryParameter("responsetype", "2")
                    .build().toString();

           // Log.i("uri", uri);



            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

         //   Log.i("response", "" + response);
            JSONArray json = new JSONArray(response);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus =jsonObject.getString(Tag_Sign);
            final String Respmsg = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Respmsg.substring(2, Respmsg.length() - 2);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                     dialog.dismiss();
                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Signup.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent in = new Intent(Signup.this, otpverify.class);
                                in.putExtra("firstName", Fname);
                                in.putExtra("lastName", Lname);
                                in.putExtra("Email", email);
                                in.putExtra("Mobile", mobile);
                                in.putExtra("password", password);
                                in.putExtra("confirmpassword", confirmpwd);
                                in.putExtra("otp",OTP);
                                in.putExtra("token",token);
                                startActivity(in);
                                finish();

                            }
                        });
                        alertDialog.show();
                    } else

                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Signup.this, R.style.AlertDialog);
                        alertDialog.setTitle("Invalid");
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
            });



            //Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
           // Log.e("Fail 1", e.toString());

        }


    }


    public void recivedSms(String message)
    {
        try
        {

            OTP = message;
           // Log.i("message", message);
           // OTP =message;
           // Log.i("OTP",OTP);
           // InputOtp.setText(OTP);

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


    /* EMAIL VALIDATION */
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (Email.getText().toString().trim().equals(""))
        {
            Email.setError("Enter valid Email");
            // Toast.makeText(Sign_up.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (matcher.matches()) {
                return true;
            } else {
                Email.setError("Enter valid email");
                //Toast.makeText(Sign_up.this, "Invalid Email Address",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }


    public  boolean checkpassword()
    {
        String password = Password.getText().toString();
        String confirmpwd = UserCnfPwd.getText().toString();

        if(password.length()< 6)
        {
            Password.setError("Password Should be 6 Character Long");
            return false;
        }

        if(password.compareTo(confirmpwd)!=0)
        {
            UserCnfPwd.setError("password do not match");
            return false;
        }

        return true;
    }


    /********Mobile Number Validation*********************************/

 /*   private  boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        if (mobile.length()>10)
        {
            UserMobile.setError("Enter valid mobile");
            //Toast.makeText(Sign_up.this,"Please Enter valid 10 Digit Mobile Number",Toast.LENGTH_LONG).show();
        }

        return mobile.matches(regEx);

    }*/

    public  boolean isValidPhoneNumber(String mobile)
    {
        String regEx = "^[0-9]{10}$";



        if(mobile.equals(""))
        {
            MobileNumber.setError("Enter 10 digit number");
            return false;
        }


        if (mobile.compareTo(regEx) != 0)
        {
            return mobile.matches(regEx);

        }
        else
        {
            MobileNumber.setError("Enter 10 digit number");
            return false;
        }
    }





    /* MOBILE NUMBER VALIDATION METHOD */
    /*
    private boolean isValidMobile(String mNo) {
        if (UserMobile.getText().toString().trim().equals("")) {
            Toast.makeText(Sign_up.this, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (mNo.length() < 10) {
                Toast.makeText(Sign_up.this, "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }*/

	/* END */

    /* USER NAME VALIDATION METHOD */
    private boolean isValidName(String usName) {
        String expression = "^[a-z A-Z]*$";
        CharSequence inputStr = usName;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (FirstName.getText().toString().trim().equals("")&&LastName.getText().toString().trim().equals("")) {
            FirstName.setError("Enter valid First Name");
            LastName.setError("Enter Valid Last Name");
            // Toast.makeText(Sign_up.this, "Enter user name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (matcher.matches()) {
                return true;
            } else {
                FirstName.setError("Enter valid First Name");
                LastName.setError("Enter Valid Last Name");
                // Toast.makeText(Sign_up.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }



}
