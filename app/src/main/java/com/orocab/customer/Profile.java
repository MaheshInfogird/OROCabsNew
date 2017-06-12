package com.orocab.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

    String myJSON=null;
    EditText Name,LastName,Email,Contact,Editoldpwd,EditNewpwd,EditCnfpwd,Pincode;
    JSONArray ProfileData = null;
    String Fname,Lname,email,Mobile;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String uid = "uId";
    public static final String Tag_Sign = "responseCode";
    private static final String TAG_RESULTS = "result";
    private static final String Tag_FName = "firstName";
    private static final String Tag_LName = "lastName";
    private static final String Tag_mobile = "mobile";
    private static final String Tag_email = "email";
    String Usersessionid;
    ConnectionDetector cd;
    String Url,IPaddress,APIKEY;
    String Signup_status="";
    final ArrayList<HashMap<String, String>> ProfileList = new ArrayList<HashMap<String, String>>();

    ProgressDialog dialog = null;
    ProgressDialog progressDialog;
    Toolbar toolbar;

    Button Saveprofile;
    Boolean pwd = true;
    TextView Changepwdclick;
    LinearLayout Passwordlayout,Profilelayout,editprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar)findViewById(R.id.toolbar);


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
                    Intent in = new Intent(getApplicationContext(),MapActivity.class);
                    startActivity(in);
                    finish();
                }
            });
        }

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);
        Usersessionid =   (shared.getString(uid,  ""));
        Name = (EditText)findViewById(R.id.Editfirstname);
        LastName = (EditText)findViewById(R.id.Editlastname);
        Email = (EditText)findViewById(R.id.Editemail);
        Contact = (EditText)findViewById(R.id.Editmobno);
        Editoldpwd = (EditText)findViewById(R.id.EditCurrentpwd);
        EditNewpwd = (EditText)findViewById(R.id.Editnewpwd);
        EditCnfpwd = (EditText)findViewById(R.id.Editcnfpwd);
        Changepwdclick = (TextView)findViewById(R.id.txtchangepwd);
        Passwordlayout = (LinearLayout)findViewById(R.id.pwdlayout);
        Profilelayout= (LinearLayout)findViewById(R.id.txteditprofile);
        editprofile = (LinearLayout)findViewById(R.id.editprofile);
        Saveprofile = (Button)findViewById(R.id.updateprofilebutton);
        Profilelayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editprofile.setVisibility(View.VISIBLE);
                Passwordlayout.setVisibility(View.GONE);
                Saveprofile.setText("Save Profile");
            }
        });
        Changepwdclick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Passwordlayout.setVisibility(View.VISIBLE);
                editprofile.setVisibility(View.GONE);
                Saveprofile.setText("Change Password");
            }
        });

        cd = new ConnectionDetector(getApplicationContext());
        IPaddress =cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        Url = cd.geturl();

        getData();




        Saveprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isvalidname())
                {
                    if (isvalidlastname())
                    {
                        if (isValidEmail(Email.getText().toString()))
                        {
                            if (isValidPhoneNumber())
                            {
                                if (pwd)
                                {
                                    dialog = ProgressDialog.show(Profile.this, "", "Updating Profile... Please Wait", true);
                                    new Thread(new Runnable()
                                    {
                                        public void run()
                                        {

                                            updateuserprofile();
                                        }

                                    }).start();

                                }
                            }
                        }
                    }
                }
            }
        });

        Editoldpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                pwd = true;
                checkpassword();

            }

        });
        EditCnfpwd.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(pwdnotmatch())
                {
                    pwd =true;
                    Saveprofile.setClickable(true);
                }

            }
        });

    }




    public void updateuserprofile()
    {

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        String name = Name.getText().toString();
        String lname = LastName.getText().toString();
        String email = Email.getText().toString();
        String mobile = Contact.getText().toString();
        String oldpassword = Editoldpwd.getText().toString();
        String newpassword = EditNewpwd.getText().toString();
        String confirmpassword = EditCnfpwd.getText().toString();

        try {

            String Transurl = ""+Url+"/basicDetails/?";


            String uri = Uri.parse(Transurl)
                    .buildUpon()
                    .appendQueryParameter("uId",Usersessionid)
                    .appendQueryParameter("firstName", name)
                    .appendQueryParameter("lastName", lname)
                    .appendQueryParameter("email",email)
                    .appendQueryParameter("mobile",mobile)
                    .appendQueryParameter("oldPass", oldpassword)
                    .appendQueryParameter("newPass",newpassword)
                    .appendQueryParameter("confirmPass", confirmpassword)
                    .appendQueryParameter("updateId", "1")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserIPAddress", IPaddress)
                    .appendQueryParameter("UserID","1212")
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype","2")
                    .build().toString();

            Log.i("uri", uri);



            // String url = "?tripTypeOption=2"+"&sourceCity="+Cityname+""+"&travelDate="+Startday+""+"&pTime="+Time+"";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);



            Log.i("response", "" + response);

//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//
//                }
//            });


            // JSONArray json = new JSONArray(response);
            // Log.i("json", "" + json);
            JSONArray json = new JSONArray(response);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus =jsonObject.getString(Tag_Sign);
            final String Message = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);




            // JSONObject jsonObj = new JSONObject(response);
            // Log.i("jsonObj",""+jsonObj);
            //  Signup_status= jsonObj.getString(Tag_Sign);
            // Log.i("Signup_status",""+Signup_status);
            // Log.i("jsonArray",""+jsonArray);
            // Signup_status = jsonArray.getString(0);
            // Log.i("jsonObject",""+jsonObject);
            // Signup_status = jsonObject.getString(Tag_Sign);



               /* for (int i = 0; i < jsonArray.length(); i++)
                {

                    JSONObject c = jsonArray.getJSONObject(i);
                    Signup_status = c.getString(Tag_Sign);
                }*/


            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    dialog.dismiss();

                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this, R.style.AlertDialog);
                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(Status_res_massge1);
                        //alertDialog.setIcon(R.drawable.);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Editoldpwd.setText("");
                                EditNewpwd.setText("");
                                EditCnfpwd.setText("");
                                Intent in = new Intent(getApplicationContext(),MapActivity.class);
                                startActivity(in);
                                finish();


                            }
                        });
                        alertDialog.show();
                    }

                        else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this, R.style.AlertDialog);
                        alertDialog.setTitle("Invalid");
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

                }
            });

        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
            //  Toast.makeText(getActivity(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }

    }
    public boolean checkpassword()
    {
        String oldpassword = Editoldpwd.getText().toString();


        if(Editoldpwd.getText().toString().equals(""))
        {
            Editoldpwd.setError("Enter Old password");
            return false;
        }

        if(EditNewpwd.getText().toString().equals("") && EditCnfpwd.getText().toString().equals(""))
        {
            EditNewpwd.setError("Password do not blank");
            EditCnfpwd.setError("Password do not blank");
          //  Saveprofile.setClickable(false);
            return false;

        }


        return true;
    }

    public boolean pwdnotmatch()
    {

        String newpassword = EditNewpwd.getText().toString();
        String confirmpassword = EditCnfpwd.getText().toString();
        if(newpassword.compareTo(confirmpassword)!=0)
        {
            EditCnfpwd.setError("Password do not Match");
          //  EditNewpwd.setText("");
          //  EditCnfpwd.setText("");
           // Saveprofile.setClickable(false);
            //Saveprofile.setBackgroundColor(getResources().getColor(R.color.GreyBgColor));
            pwd = false;
            return false;
        }

        return true;

    }












    public void showprofile()
    {
        try
        {
            JSONObject jsonObj = new JSONObject(myJSON);
            ProfileData = jsonObj.getJSONArray(TAG_RESULTS);
            Log.i("json", "" + ProfileData);
            // JSONArray jsonArray = new JSONArray(myJSON);



            for (int i = 0; i < ProfileData.length(); i++)
            {

                JSONObject c = ProfileData.getJSONObject(i);
                Fname = c.getString(Tag_FName);
                Lname  = c.getString(Tag_LName);
                email  = c.getString(Tag_email);
                Mobile  = c.getString(Tag_mobile);
                Name.setText(Fname);
                LastName.setText(Lname);
                Email.setText(email);
                Contact.setText(Mobile);
            }

        }


        catch (JSONException e)
        {
            e.printStackTrace();
        }




    }



    public void getData()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(Profile.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                String url = "http://jaidevcoolcab.cabsaas.in/sandbox";
                String url1 = "http://admin.clearcarrental.com/api";
                HttpPost httppost = new HttpPost(""+Url+"/basicDetails/?uId="+Usersessionid+"&firstName=&lastName=&email=&updateId=0&ApiKey="+APIKEY+"&UserIPAddress="+IPaddress+"&UserID=1212&UserAgent=Mozilla&responsetype=2");
                Log.i("Json", "Get Json");
                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e)
                {
                    // Oops
                } finally
                {
                    try
                    {

                        if (inputStream != null)
                            inputStream.close();
                    }
                    catch (Exception squish)
                    {

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                showprofile();
                progressDialog.dismiss();


            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }







   /* public class  CustomList extends SimpleAdapter
    {

        private Context mContext;
        public LayoutInflater inflater = null;

        public CustomList(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
        {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent)
        {
            View vi = convertView;
            if (convertView == null)
            {
                vi = inflater.inflate(R.layout.dynamicbooking, null);
            }

            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            // TextView text = (TextView)vi.findViewById(R.id.carprice);

            // Car  = (String) data.get(Tag_booking_id);
            //// text.setText(Car);

            TextView text2 = (TextView) vi.findViewById(R.id.traveldate);
            // String name2 = (String) data.get(TAG_tdate) + " Rs.";
            // text2.setText(name2);

            TextView text3 = (TextView) vi.findViewById(R.id.refno);
            //  String name3 = (String) data.get(TAG_refno);
            //  text3.setText(name3);

            TextView textView = (TextView) vi.findViewById(R.id.traveltype);
            //  String name4 = (String) data.get(TAG_travelType);
            //  textView.setText(name4);


            // ImageView image=(ImageView)vi.findViewById(R.id.carimg);
            // String image_url = (String) data.get(TAG_tdate);
            // Picasso.with(this.mContext).load(image_url).into(image);
            return vi;

        }


    }*/

    /* EMAIL VALIDATION */
    private boolean isValidEmail(String email)
    {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (Email.getText().toString().trim().equals(""))
        {

            //Toast.makeText(bookingdetails.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            Email.setError("Enter Valid Email address");
            return false;
        }
        else
        {
            if (matcher.matches())
            {
                return true;
            } else
            {

                Email.setError("Enter Valid Email address");
                return false;
            }
        }
    }

    public boolean isValidPhoneNumber()
    {

        if(Contact.getText().toString().trim().length() < 10)
        {
            Contact.setError("Enter valid mobile number");
            return false;
        }
        return true;
    }


    public boolean isvalidname()
    {

        if(Name.getText().toString().equals(""))
        {
            Name.setError("Enter First Name");
            return false;
        }
        return true;
    }

    public boolean isvalidlastname()
    {

        if(LastName.getText().toString().equals(""))
        {
            LastName.setError("Enter Last Name");
            return false;
        }
        return true;
    }

}
