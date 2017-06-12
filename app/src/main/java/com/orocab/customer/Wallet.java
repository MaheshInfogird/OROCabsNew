package com.orocab.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Wallet extends BaseActivity
{

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;

    public TextView AvailableCash;
    private EditText EnterAmount;
    private Button Depositcash;
    String Usersessionid,PaidAmount;


    public static final String Email = "emailKey";
    public static final String Name  = "nameKey";
    public static final String phone = "phonekey";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    public static final String Tag_Sign = "responseCode";
    private static final String TAG_RESULTS = "result";
    private static final String Tag_balance = "balance";
    private static final String Tag_amount = "amount";
    private static final String Tag_Availbalance = "Availbalance";
    public static final String TagprefernceBalance = "balance";
    private static final String Tag_tdate = "tdate";
    private static final String Tag_referenceno = "referenceNo";
    private static final String Tag_crdrStatus = "crdrStatus";
    public static final String Tagmobile = "mobile";
    public static final String Tagemail = "email";


    String Balance, refno, Date,amount;
    final ArrayList<HashMap<String, String>> Cashwallet = new ArrayList<HashMap<String, String>>();
    ListView list;
    String AvailableBalance;
    SharedPreferences sharedpreferences;
    int cashwalletbalance;
    String name,email,mobile;
    int  CR,DR;
    String myJSON = null;
    JSONArray CashData = null;
    String reference;
    ProgressDialog dialog = null;

    ProgressDialog progressDialog;
    LinearLayout Addwallet100,Addwallet500,Addwallet1000;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    TextView Test100wallet,Test500wallet,Test1000wallet;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        TextView text = (TextView)findViewById(R.id.toolText);
        text.setText("");
        ImageView img = (ImageView)findViewById(R.id.historyicon);
        img.setImageDrawable(getResources().getDrawable(R.drawable.wallet_history_icon));
        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(getApplicationContext(),wallethistorydetails.class);
                startActivity(in);
                finish();
            }
        });

        Test100wallet = (TextView)findViewById(R.id.text100wallet);
        Test500wallet = (TextView)findViewById(R.id.text500wallet);
        Test1000wallet = (TextView)findViewById(R.id.text1000wallet);

        // if (getSupportActionBar() != null)
        // {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  }

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);
        Usersessionid = (sharedpreferences.getString(uid, ""));
        Log.i("Usersessionid",Usersessionid);
        name          = (sharedpreferences.getString(Name,""));
        Log.i("name",name);
        email         = (sharedpreferences.getString(Tagemail,""));
        Log.i("email",email);
        mobile        = (sharedpreferences).getString(Tagmobile,"");
        Log.i("mobile",mobile);
        cashwalletbalance = (sharedpreferences.getInt(TagprefernceBalance, 0));
        cd = new ConnectionDetector(getApplicationContext());
        APIKEY = cd.getAPIKEY();
        IPAddress =cd.getLocalIpAddress();
        Url = cd.geturl();

        Addwallet100 = (LinearLayout)findViewById(R.id.Wallet100);
        Addwallet500 = (LinearLayout)findViewById(R.id.Wallet500);
        Addwallet1000 = (LinearLayout)findViewById(R.id.Wallet1000);
        AvailableCash = (TextView)findViewById(R.id.Availbal);

        EnterAmount = (EditText)findViewById(R.id.EnterAmount);
        Depositcash = (Button)findViewById(R.id.AddCashtowallet);
        Depositcash.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(isvalidamount())
                {
                    if(isvalidintamount())
                    {
                        dialog = ProgressDialog.show(Wallet.this, "", "Please Wait while we are processing", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {

                                /*PaidAmount = EnterAmount.getText().toString();
                                int amount = 100 * Integer.parseInt(PaidAmount);
                                Intent intent = new Intent(getApplicationContext(), Rajoractivity.class);
                                intent.putExtra("Name", name);
                                intent.putExtra("Amount", String.valueOf(amount));
                                intent.putExtra("Email", email);
                                intent.putExtra("Mobile", mobile);
                                intent.putExtra("uId", Usersessionid);
                                intent.putExtra("reference", reference);
                                startActivity(intent);
                                finish();
*/
                                 AddCCrCash();

                            }
                        }).start();
                    }
                }


            }
        });

        Addwallet100.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                EnterAmount.setText("100");
                Drawable dr = getResources().getDrawable(R.drawable.wallet_hover);
                dr.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
              //  triproot = "6";
              //  Citytransfer.setText("");
              //  Drop.setText("");
              //  pickup.setText("");

                if (Addwallet100 == null)
                {
                    Addwallet100 = (LinearLayout) findViewById(v.getId());
                }
                else
                {
                    Addwallet500.setBackgroundResource(R.drawable.wallet_normal);
                    Addwallet1000.setBackgroundResource(R.drawable.wallet_normal);
                    Test100wallet.setTextColor(getResources().getColor(R.color.WhiteTextColor));
                    Addwallet100.setBackgroundResource(R.drawable.wallet_hover);
                    Test1000wallet.setTextColor(getResources().getColor(R.color.TitleColor));
                    Test500wallet.setTextColor(getResources().getColor(R.color.TitleColor));

                    // /  Addwallet100.setTextColor(getResources().getColor(R.color.transparentBlack));
                    Addwallet100 = (LinearLayout) findViewById(v.getId());

                }

                Addwallet100.setBackgroundDrawable(dr);



            }
        });

        Addwallet500.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                EnterAmount.setText("500");
                Drawable dr = getResources().getDrawable(R.drawable.wallet_hover);
                dr.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                //  triproot = "6";
                //  Citytransfer.setText("");
                //  Drop.setText("");
                //  pickup.setText("");

                if (Addwallet500 == null)
                {
                    Addwallet500 = (LinearLayout) findViewById(v.getId());
                }
                else
                {
                    Addwallet100.setBackgroundResource(R.drawable.wallet_normal);
                     Addwallet1000.setBackgroundResource(R.drawable.wallet_normal);
                    Addwallet500.setBackgroundResource(R.drawable.wallet_hover);
                    Test500wallet.setTextColor(getResources().getColor(R.color.WhiteTextColor));

                    Test1000wallet.setTextColor(getResources().getColor(R.color.TitleColor));
                    Test100wallet.setTextColor(getResources().getColor(R.color.TitleColor));

                    //   Addwallet100.setTextColor(getResources().getColor(R.color.transparentBlack));
                    Addwallet500 = (LinearLayout) findViewById(v.getId());

                }

                Addwallet500.setBackgroundDrawable(dr);




            }
        });

        Addwallet1000.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EnterAmount.setText("1000");
                Drawable dr = getResources().getDrawable(R.drawable.wallet_hover);
                dr.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                //  triproot = "6";
                //  Citytransfer.setText("");
                //  Drop.setText("");
                //  pickup.setText("");

                if (Addwallet1000 == null)
                {
                    Addwallet1000 = (LinearLayout) findViewById(v.getId());
                }

                else
                {
                    Addwallet500.setBackgroundResource(R.drawable.wallet_normal);
                     Addwallet100.setBackgroundResource(R.drawable.wallet_normal);
                    Addwallet1000.setBackgroundResource(R.drawable.wallet_hover);
                    Test1000wallet.setTextColor(getResources().getColor(R.color.WhiteTextColor));
                    Test100wallet.setTextColor(getResources().getColor(R.color.TitleColor));
                    Test500wallet.setTextColor(getResources().getColor(R.color.TitleColor));
                    Addwallet1000 = (LinearLayout) findViewById(v.getId());

                }

                Addwallet1000.setBackgroundDrawable(dr);



            }
        });

        getData();
        Log.i("name", name);
        Log.i("mobile",mobile);


    }

    public void startPayment()
    {
        /**
         * Replace with your public key
         */
        final String public_key = "rzp_test_q9DddqDPQDbn0r";

        /**
         * You need to pass current activity in order to let razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setPublicKey(public_key);

        try{
            JSONObject options = new JSONObject("{" +
                    "description: 'Demoing Charges'," +
                    "image: 'https://rzp-mobile.s3.amazonaws.com/images/rzp.png'," +
                    "currency: 'INR'}"
            );

            options.put("amount", "1");
            options.put("name", name);
            options.put("prefill", new JSONObject("{email: '"+email+"', contact: '"+mobile+"'}"));

            co.open(activity, options);

        } catch(Exception e){
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentSuccess(String razorpayPaymentID){
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentError(int code, String response){
        try {
            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }


    public void getData()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                progressDialog = ProgressDialog.show(Wallet.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                String cashurl = ""+Url+"/cashWallet/?uId=" + Usersessionid + "&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=androidapp&responsetype=2";
                HttpPost httppost = new HttpPost(cashurl);
                Log.i("Json", "Get Json");
                // Depends on your web service
                // httppost.setHeader("Content-type", "application/json");

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
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e)
                {
                    // Oops
                } finally
                {
                    try {

                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                Log.i("myJson",myJSON);
                showcash();
                progressDialog.dismiss();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    public void showcash()
    {
        try
        {
            JSONObject jsonObj = new JSONObject(myJSON);
            CashData = jsonObj.getJSONArray(TAG_RESULTS);
            Log.i("json", "" + CashData);
            for (int i = 0; i < CashData.length(); i++)
            {

                JSONObject c = CashData.getJSONObject(i);
                Balance = c.getString(Tag_balance);
                Log.i("Balance", Balance);


            }
            String Str =  "null";
            if(Balance.equals(Str))
            {
                AvailableCash.setText("0");
            }
            else
            {
                AvailableCash.setText(Balance);
            }


        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }


    public void AddCCrCash()
    {
        String responsetype="2";
        PaidAmount = EnterAmount.getText().toString();
        int amount = 100 * Integer.parseInt(PaidAmount);
        //http://jaidevcoolcab.cabsaas.in/sandbox/index/bookingRequestNew/?uniqueRefNo=100&guestName=modi&guestMobileNo=9090909090&guestMailId=modi@demo.com&guestCity=Aurangabad&guestCountry=India&pickupCity=Aurangabad&dropCity=Pune&pickUpaddress=ajab%20nager&travelDate=10-10-2016&pickupTime=1:45pm&vehicleName=Tata%20Indica&days=2&cars=2&paidAmount=100&tripType=1&tripRoot=2&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=yu76&UserID=76&UserAgent=hj87&responsetype=2


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();




        nameValuePairs.add(new BasicNameValuePair("uId", Usersessionid));
        nameValuePairs.add(new BasicNameValuePair("paidAmount", PaidAmount));
        nameValuePairs.add(new BasicNameValuePair("transactionId", ""));


        try
        {



            HttpClient httpclient = new DefaultHttpClient();
            //  String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String AddCash =""+Url+"/cashWalletAdd/?";

            String uri = Uri.parse(AddCash)
                    .buildUpon().appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("paidAmount", PaidAmount)
                    .appendQueryParameter("transactionId","")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "234")
                    .appendQueryParameter("UserIPAddress","127.0.0.1")
                    .appendQueryParameter("UserAgent","androidApp")
                    .appendQueryParameter("responsetype", responsetype)
                    .build().toString();

            Log.i("uri",uri);
            //String encodedUrl = URLEncoder.encode(local, "UTF-8");
            //String encoded    = Uri.encode(local);
            // Log.i("Encoded",""+encodedUrl);
            // HttpPost post = new HttpPost(URLEncoder.encode(local));

            //HttpPost httppost = new HttpPost(URLEncoder.encode(local));

            HttpPost httppost = new HttpPost(uri);

            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Log.i("Httppost",""+httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            //   Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();


            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });

            Log.i("response", "" + response);
            JSONObject jsonObject = new JSONObject(response);
            Log.i("jsonobject",""+jsonObject);
            reference = jsonObject.getString("reference");
            Log.i("reference", reference);

            Intent intent = new Intent(getApplicationContext(), Rajoractivity.class);
            intent.putExtra("Name", name);
            intent.putExtra("Amount", String.valueOf(amount));
            intent.putExtra("Email", email);
            intent.putExtra("Mobile", mobile);
            intent.putExtra("uId", Usersessionid);
            intent.putExtra("reference", reference);
            startActivity(intent);
            finish();




            //JSONArray json = new JSONArray(response);
            // Tripid = json.getString(0);
            //  Log.i("reference",Tripid);
            /*for(int i=0;i<json.length();i++)
            {
                  JSONObject c = json.getJSONObject(i);
                  Tripid  = c.getString("reference");
                  Log.i("Tripid",Tripid);

                //triplist.add(Tripid);
            }*/


            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail1", e.toString());
            // Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }


    }

    public  boolean isvalidamount()
    {

        if (EnterAmount.getText().toString().trim().equals(""))
        {
            EnterAmount.setError("Enter valid Amount");
            return false;
        }
        return  true;
    }

    public boolean isvalidintamount()
    {
        String cash= EnterAmount.getText().toString();
        try
        {
            int amount = Integer.parseInt(cash);
            if(amount == 0)
            {
                EnterAmount.setError("enter amount greater than Zero");
                return false;

            }

        }

        catch (NumberFormatException nfe)
        {
            nfe.printStackTrace();

        }

        return true;
    }




}
