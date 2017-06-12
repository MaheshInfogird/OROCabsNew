package com.orocab.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class Facebook extends Activity
{

    public static final String LName = "Lastname";

    SharedPreferences sharedpreferences;

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
    public static final String TagLname = "lastName";
    int Cashwalletbalance;
    String uId,fname;
    String LoginStatus,Useremail,Usermobile,lname;

    AccessTokenTracker  accessTokenTracker;



    private CallbackManager callbackManager;
    private TextView info,Signup,Passwordlink;
    private EditText UserName ,Userpwd;
    private ImageView profileImgView;
    public LoginButton loginButton;
    Boolean isInternetPresent = false;

    // private SignInButton btn_sign_in;
    String id,Fname,Lname,Fullname,link,birthday,picture,gender,email,Home,cur_location;
    ProgressDialog dialog = null;
    HttpClient httpclient;
    private static final String TAG = "Check";
    public static final String PREFS_NAME = "LoginPrefs";
    ConnectionDetector cd;
    UserSessionManager session;
    SharedPreferences.Editor editor;
    String IPaddress,Url,APIKEY;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(Facebook.this);
        setContentView(R.layout.facebooklayout);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet(Facebook.this);
        callbackManager = CallbackManager.Factory.create();
        session = new UserSessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        printKeyHash();

        Url = cd.geturl();
        IPaddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();


       /* loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.i("loginResult", "" + loginResult);
                Log.i("loginButton","loginButton");
                try {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                //  String[] splited;
                                JSONObject obj = object.getJSONObject("picture").getJSONObject("data");

                                if (object.has("email"))
                                {
                                    Profile profile = Profile.getCurrentProfile();

                                    if (profile != null)
                                    {
                                        Log.i("LoginButton", "LoginButton");
                                        //  Fullname = profile.getName();
                                        Fname = profile.getFirstName();
                                        Lname = profile.getLastName();
                                        email = object.getString("email");
                                        id = object.getString("id");
                                        Sign_in();
                                    }

                                    // loginButton.setVisibility(View.INVISIBLE);
                                    //SharedPreferences.Editor editor = sharedpreferences.edit();
                                    // editor.putString(Name, Fullname);
                                    // editor.putString(Email, email);
                                    // editor.apply();
                                    //editor.commit();
                                    //  TestDemo();
                                   *//* Intent in = new Intent(getApplicationContext(), MobileActivity.class);
                                    in.putExtra("firstName",Fname);
                                    in.putExtra("lastName",Lname);
                                    in.putExtra("email",email);
                                    startActivity(in);*//*

                                    else {

                                        Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(in);

                                    }


                                } else {


                                    email = "";
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });

                    Log.i("request", "" + request);
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link,birthday,picture,email,gender,location,hometown");
                    request.setParameters(parameters);
                    request.executeAsync();
                    Log.i("request", "" + request);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.i("loginButtonclick","loginButtonclick");

                // loginButton.setReadPermissions("user_birthday", "email", "user_hometown", "user_location");
            }
        });
*/
//        accessTokenTracker.startTracking();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_birthday", "email", "user_hometown", "user_location"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.i("LoginManager", "" + loginResult);
                Log.i("Tag", "Tag");
                Log.i("loginManagerlistener", "loginManagerlistener");
                Log.d("Tag", "FF fb onSuccess");
               // loginResult.getAccessToken()
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            //  String[] splited;
                            JSONObject obj = object.getJSONObject("picture").getJSONObject("data");
                            Log.i("Tag", "Tag");
                            if (object.has("email"))
                            {
                                Log.i("Tag", "Tag");
                                Profile profile = Profile.getCurrentProfile();
                                if (profile != null)
                                {
                                    Log.i("Tag", "Tag");
                                    Log.i("AccessToken",""+AccessToken.getCurrentAccessToken());
                                    Log.i("AccessToken","AccessToken");
                                     //  Fullname = profile.getName();
                                    AccessToken.getCurrentAccessToken().getPermissions();
                                    Fname = profile.getFirstName();
                                    Lname = profile.getLastName();
                                    email = object.getString("email");
                                    id = object.getString("id");
                                    accessTokenTracker.startTracking();
                                    Sign_in();

                                }
                                else
                                {
                                    Log.i("Tag", "Tag");
                                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(in);
                                    finish();
                                }


                            }
                            else
                            {


                                email = "";
                            }
                        }
                        catch (JSONException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,birthday,picture,email,gender,location,hometown");
                request.setParameters(parameters);
                request.executeAsync();

            }


            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException e)
            {


            }
        });


        accessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken)
            {

                Log.i("currentAccessToken", "" + currentAccessToken);
                Log.i("oldAccessToken", "" + oldAccessToken);


                if (currentAccessToken == null)
                {
                    Log.i("currentAccessToken", "" + currentAccessToken);
                    //write your code here what to do when user logout
                    LoginManager.getInstance().logOut();
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        };


    }

    public void logoutFb()
    {
        LoginManager.getInstance().logOut();


    }
    @Override
    public void onResume()
    {
        super.onResume();

       //  Sign_in();
        //info.setText(message(profile));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void printKeyHash()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.orocab.customer", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }





    public void TestDemo()
    {

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.1.13:8080/ccr/fbcheck.php");
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("userFname", Fname));
            nameValuePairs.add(new BasicNameValuePair("userLname", Lname));
            nameValuePairs.add(new BasicNameValuePair("useremail", email));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            // ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String resp = httpclient.execute(httppost,responseHandler);
            //    final String resp = httpclient.execute(httppost, responseHandler);
            Log.i(TAG, "" + nameValuePairs);
            Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
            Log.i(TAG, "" + nameValuePairs);
            Log.i("response", "" + resp);

            Log.e("pass 1", "connection success ");
        } catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
          //  Toast.makeText(getApplicationContext(), "Invalid IP Address",
                  //  Toast.LENGTH_LONG).show();
        }


    }


    public void Sign_in()
    {
       // String uname = UserName.getText().toString();
      //  String upass = Userpwd.getText().toString();


        progressDialog = ProgressDialog.show(Facebook.this, "", "Please Wait..Checking Details", true);
        progressDialog.show();


        String responsetype = "2";
        // String APIKEY = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
       // String url="http://admin.clearcarrental.com/api/index/signIn/?";
        String url =""+Url+"/faceCheck?";
       // String url ="http://jaidevcoolcab.cabsaas.in/sandbox/index/faceCheck?";
        // http://admin.clearcarrental.com/api
      //  String APIKEY="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
      //  String APIKEY="S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
        // ?email=customeruser@infogird.com&mobile=8888801816&password=Customer123&&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";





        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
       // nameValuePairs.add(new BasicNameValuePair("username",uname));
      //  nameValuePairs.add(new BasicNameValuePair("password",upass));
     //   ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2

        try
        {
            String uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("token", id)
                    .appendQueryParameter("ApiKey",APIKEY)
                    .appendQueryParameter("UserIPAddress", "127.0.0.1")
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype","2")
                    .build().toString();

            Log.i("uri", uri);


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
          //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);

           /*runOnUiThread(new Runnable()
           {
                public void run()
                {
                }
            });
*/
//                 progressDialog.dismiss();

                /* final String separated[] = response.split(",");
                 final String Check[] =separated[1].split(":");
                 final String Confirmlogin = Check[1].trim();
                 Log.i("Confirm",Confirmlogin);
                 final String Confirm = "1}]";*/
           /* final String Str = "1";
            JSONArray json = new JSONArray(response);
            Log.i("json", "" + json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str ="4";
            final String LoginStatus =jsonObject.getString("responseCode");
            Log.i("LoginStatus", LoginStatus);*/


            final String Str = "1";
            JSONArray json = new JSONArray(response);
            Log.i("json", "" + json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str ="4";
            final String LoginStatus =jsonObject.getString(Tag_Subuserid);
            Log.i("LoginStatus", LoginStatus);




            if(LoginStatus.compareTo(str)==0)
            {
                Log.i("str", str);
                session.createUserLoginSession(email, id);
                uId = jsonObject.getString(Tag_uid);
                Cashwalletbalance = jsonObject.getInt(TagprefernceBalance);
                Useremail = jsonObject.getString(Tagemail);
                Usermobile = jsonObject.getString(Tagmobile);
                fname = jsonObject.getString(Tagname);
                fname = jsonObject.getString(Tagname);
                lname = jsonObject.getString(TagLname);
                Log.i("fname", fname);
                //   Log.i("Cashwalletbalance",""+Cashwalletbalance);
                editor = sharedpreferences.edit();
                editor.putString(Email, email);
                editor.putString(Tag_uid, uId);
                editor.putString(Name,fname);
                editor.putString(Tagname,fname);
                editor.putString(TagLname,lname);
                editor.putInt(TagprefernceBalance, Cashwalletbalance);
                editor.putString(Tagemail, Useremail);
                editor.putString(Tagmobile, Usermobile);
                editor.apply();
            }


//                    progressDialog.dismiss();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog.dismiss();

                    if (LoginStatus.equals("4"))
                    {


                       // Log.i("LoginStatus", LoginStatus);
                      //  AlertDialog.Builder alertDialog = new AlertDialog.Builder(Facebook.this);
                      //  alertDialog.setTitle("Welcome");
                      //  alertDialog.setMessage("Successfully Login through Facebook");

                      //  alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                         //   @Override
                         //   public void onClick(DialogInterface dialog, int which)
                         //   {
                                Intent in = new Intent(Facebook.this, MapActivity.class);
                                startActivity(in);
                                finish();


                         //   }
                        //});
                       // alertDialog.show();


                    }
                    else


                    {
                        //Log.i("LoginStatus", LoginStatus);
                       // AlertDialog.Builder alertDialog = new AlertDialog.Builder(Facebook.this);
                       // alertDialog.setTitle("");
                       // alertDialog.setMessage("Invalid Login Credentials");
                        //   alertDialog.setIcon(R.drawable.fail);
                       // alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                           // @Override
                          //  public void onClick(DialogInterface dialog, int which)
                          //  {
                                Intent in = new Intent(Facebook.this, MobileActivity.class);
                                in.putExtra("firstName",Fname);
                                in.putExtra("lastName",Lname);
                                in.putExtra("email",email);
                                in.putExtra("token",id);
                                startActivity(in);
                                 finish();
                        //    }
                      //  });
                       // alertDialog.show();
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

    public  void dologout1()
    {
        if(editor!=null)
        {
            editor.clear();
            editor.commit();
        }

    }





}
