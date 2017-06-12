package com.orocab.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class payuwallet extends Activity
{

    WebView webView;
    Context activity;

    //Live payu Key

   // private String merchant_key = "fIxIuc";
   // private String salt = "As01bZn4";

  //  private String mBaseURL = "https://test.payu.in";

    private String merchant_key = "gtKFFx";
    private String salt = "eCwWELxi";

    // String merchant_key = "JBZaLc";
    // String salt = "GQs7yium";
    String action1 = "";
    //String base_url = "https://secure.payu.in";
    String base_url = "https://test.payu.in";
    // int error = 0;
    // String hashString = "";
    // Map<String, String> params;
    //String txnid = "TXN8367286482920";
    String txnid;
    String amount = "10";
    String productInfo = "";
   // String firstName = "siddharth";
   // String emailId = "siddhjain@gmail.com";
    String mFirstName,mEmailId,mPhone,modeofPayment,uId;
    String mAmount;
   // String Triplist;
    int mId;
    String CCnumber,ccExpiry,CCCVV;
    Boolean isfrombooking;
    // private String SUCCESS_URL="https://test.payu.in/_payment";
    private String SUCCESS_URL="http://admin.clearcarrental.com/api/index/success/?";
    private String FAILED_URL ="http://admin.clearcarrental.com/api/index/fail/?";
    // private String SUCCESS_URL="http://jaidevcoolcab.cabsaas.in/sandbox/index/bookingConfirmNew/uniqueRefNo=118";
    // private String SUCCESS_URL="http://jaidevcoolcab.cabsaas.in/website/local/paymentprocessdone/";
    //  private String SUCCESS_URL = "http://localhost:8080/ZendSkeletonApplication-master/ZendSkeletonApplication-master/public/application/index/api/";
    //private String FAILED_URL = "<Your Transaction FailedPage URL>";
    private String phone = "<Your Mobile No>";
    private String serviceProvider = "payu_paisa";
    private String hash = "";
    String reference;

    Handler mHandler = new Handler();
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paywallet);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        /// FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        webView = new WebView(this);
        setContentView(webView);


        // Log.i("mFirstName",mFirstName+""+mEmailId+""+mAmount+""+mPhone);


        mFirstName = getIntent().getStringExtra("Name");
        mEmailId =   getIntent().getStringExtra("Email");
        mAmount  =  getIntent().getStringExtra("Amount");
        mPhone   =  getIntent().getStringExtra("Mobile");
        uId     =   getIntent().getStringExtra("uId");
        reference = getIntent().getStringExtra("reference");
        cd = new ConnectionDetector(getApplicationContext());
        APIKEY = cd.getAPIKEY();
        IPAddress =cd.getLocalIpAddress();
        Url = cd.geturl();


        //   Log.i("mFirstName",mFirstName);
     //   Log.i("mPhone",mPhone);
      //  Log.i("mEmailId",mEmailId);
      //  Log.i("mAmount", mAmount);
       // Log.i("uId", uId);
       // Log.i("reference", reference);
//        Log.i("Triplist", Triplist);


       /* Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {

            mFirstName = bundle.getString("name");
            mEmailId = bundle.getString("email");
            mAmount = bundle.getDouble("amount");
            mPhone = bundle.getString("phone");
            mId = bundle.getInt("id");
            CCnumber = bundle.getString("CCnumber");
            ccExpiry = bundle.getString("ccExpiry");
            CCCVV    = bundle.getString("CCCVV");


        }*/

        // Log.i("CCnumber",CCnumber);
        // Log.i("ccExpiry",ccExpiry);
        //  Log.i("CCCVV",CCCVV);


        JSONObject productInfoObj   = new JSONObject();
        JSONArray productPartsArr  = new JSONArray();
        JSONObject productPartsObj1 = new JSONObject();
        JSONObject paymentIdenfierParent = new JSONObject();
        JSONArray paymentIdentifiersArr = new JSONArray();
        JSONObject paymentPartsObj1 = new JSONObject();
        JSONObject paymentPartsObj2 = new JSONObject();

        try
        {
            // Payment Parts
            productPartsObj1.put("name", mFirstName);
            productPartsObj1.put("description", "WalletBalance");
            productPartsObj1.put("value", mAmount);
            productPartsObj1.put("isRequired", "true");
            productPartsObj1.put("settlementEvent", "EmailConfirmation");
            productPartsArr.put(productPartsObj1);
            productInfoObj.put("paymentParts", productPartsArr);

            // Payment Identifiers
            paymentPartsObj1.put("field", "CompletionDate");
            paymentPartsObj1.put("value", "31/10/2012");
            paymentIdentifiersArr.put(paymentPartsObj1);

            paymentPartsObj2.put("field", "TxnId");
            paymentPartsObj2.put("value", txnid);
            paymentIdentifiersArr.put(paymentPartsObj2);

            paymentIdenfierParent.put("paymentIdentifiers", paymentIdentifiersArr);
            productInfoObj.put("", paymentIdenfierParent);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        productInfo = productInfoObj.toString();

        Log.e("TAG", productInfoObj.toString());

        Random rand = new Random();
        String rndm = Integer.toString(rand.nextInt())
                + (System.currentTimeMillis() / 1000L);
        txnid = hashCal("SHA-256", rndm).substring(0,10);

        hash = hashCal("SHA-512", merchant_key + "|" + reference + "|" + mAmount
                + "|" + productInfo + "|" + mFirstName + "|" + mEmailId
                + "|||||||||||" + salt);

        action1 = base_url.concat("/_payment");

        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                // TODO Auto-generated method stub
               /* Toast.makeText(getApplicationContext(), "Oh no! " + description,
                       Toast.LENGTH_SHORT).show();
                Log.e("onrecieve", "" + errorCode);
                Log.e("description",""+description);
                Log.e("failingUrl",failingUrl);*/
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error)
            {
                // TODO Auto-generated method stub

               // Toast.makeText(getApplicationContext(), "SslError! " + error, Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.AlertDialog);
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
              //  Toast.makeText(getApplicationContext(), "Page Started! " + url, Toast.LENGTH_SHORT).show();

                if (url.equalsIgnoreCase(SUCCESS_URL))
                {
                    Log.i("Sucess", url);
                   // Toast.makeText(getApplicationContext(), "Success! " + url, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.i("Failure", url);
                   // Toast.makeText(getApplicationContext(), "Failure! " + url, Toast.LENGTH_SHORT).show();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.i("Url", url);
                if (url.equalsIgnoreCase(SUCCESS_URL))
                {
                    Log.i("Url", url);
                    Log.i("Sucess", "Sucess");
                    AddCCrCash();
                    //   BookingConfirmation();
                  /*  Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.putExtra("status", true);
                    intent.putExtra("transaction_id",reference);
                    intent.putExtra("uId",uId);
                    intent.putExtra("PaidAmount", mAmount);
                    intent.putExtra("reference",reference);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();*/
                }
                else if (url.equalsIgnoreCase(FAILED_URL))
                {
                    Log.i("Url", url);
                    Log.i("Failed", "Failed");
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.putExtra("status", true);
                    intent.putExtra("transaction_id",reference);
                    intent.putExtra("uId",uId);
                    intent.putExtra("PaidAmount", mAmount);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                super.onPageFinished(view, url);

                Log.i("Url", url);
                // super.onPageFinished(view, url);


            }
        });

        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(2);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);

        webView.addJavascriptInterface(new PayUJavaScriptInterface(activity),
                "PayUMoney");
        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("key", merchant_key);
        mapParams.put("hash", hash);
        mapParams.put("txnid", reference);
      //  mapParams.put("service_provider", "payu_paisa");
        mapParams.put("amount", mAmount);
        mapParams.put("firstname", mFirstName);
        mapParams.put("email", mEmailId);
        mapParams.put("phone", mPhone);
      //  mapParams.put("CCnumber", CCnumber);
      //  mapParams.put("ccExpiry", ccExpiry);
      //  mapParams.put("CCCVV", CCCVV);

        mapParams.put("productinfo", productInfo);
        mapParams.put("surl", SUCCESS_URL);
        mapParams.put("furl", FAILED_URL);
        mapParams.put("lastname", "");

        /*mapParams.put("address1", "");
        mapParams.put("address2", "");
        mapParams.put("city", "");
        mapParams.put("state", "");

        mapParams.put("country", "");
        mapParams.put("zipcode", "");
        mapParams.put("udf1", "");
        mapParams.put("udf2", "");

        mapParams.put("udf3", "");
        mapParams.put("udf4", "");
        mapParams.put("udf5", "");*/
        // mapParams.put("pg", (empty(PayMentGateWay.this.params.get("pg"))) ?
        // ""
        // : PayMentGateWay.this.params.get("pg"));
        webview_ClientPost(webView, action1, mapParams.entrySet());






    }



    public void AddCCrCash()
    {
        String responsetype="2";
        //PaidAmount = Addcash.getText().toString();
        //http://jaidevcoolcab.cabsaas.in/sandbox/index/bookingRequestNew/?uniqueRefNo=100&guestName=modi&guestMobileNo=9090909090&guestMailId=modi@demo.com&guestCity=Aurangabad&guestCountry=India&pickupCity=Aurangabad&dropCity=Pune&pickUpaddress=ajab%20nager&travelDate=10-10-2016&pickupTime=1:45pm&vehicleName=Tata%20Indica&days=2&cars=2&paidAmount=100&tripType=1&tripRoot=2&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=yu76&UserID=76&UserAgent=hj87&responsetype=2


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


        String AddCash =""+Url+"/cashWalletAdd/?";


        nameValuePairs.add(new BasicNameValuePair("uId", uId));
        nameValuePairs.add(new BasicNameValuePair("paidAmount", mAmount));
        nameValuePairs.add(new BasicNameValuePair("transactionId", reference));
        nameValuePairs.add(new BasicNameValuePair("reference", reference));


        try
        {



            HttpClient httpclient = new DefaultHttpClient();
            //  String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(AddCash)
                    .buildUpon().appendQueryParameter("uId", uId)
                    .appendQueryParameter("paidAmount", mAmount)
                    .appendQueryParameter("transactionId",reference)
                    .appendQueryParameter("reference",reference)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "234")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", responsetype)
                    .build().toString();

            Log.i("uri", uri);
            //String encodedUrl = URLEncoder.encode(local, "UTF-8");
            //String encoded    = Uri.encode(local);
            // Log.i("Encoded",""+encodedUrl);
            // HttpPost post = new HttpPost(URLEncoder.encode(local));

            //HttpPost httppost = new HttpPost(URLEncoder.encode(local));

            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Log.i("Httppost",""+httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            //  Log.i("response", "" + response);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            Intent in = new Intent(getApplicationContext(),Wallet.class);
            startActivity(in);
            finish();
            // JSONObject jsonObject = new JSONObject(response);
            //Tripid = jsonObject.getString("reference");
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



    @Override
    public void onBackPressed()
    {
        onPressingBack();
    }


   /* public void BookingConfirmation()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String responsetype="2";
        nameValuePairs.add(new BasicNameValuePair("reference", Triplist));
        nameValuePairs.add(new BasicNameValuePair("paidAmount", mAmount));
        nameValuePairs.add(new BasicNameValuePair("transactionid", txnid));


        try {

            HttpClient httpclient = new DefaultHttpClient();
            String Transurl ="http://jaidevcoolcab.cabsaas.in/sandbox/index/bookingConfirmNew/";
            String APIKEY = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
            String uri = Uri.parse(Transurl).buildUpon()
                    .appendQueryParameter("reference", Triplist)
                    .appendQueryParameter("paidAmount",mAmount)
                    .appendQueryParameter("transactionid",txnid)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "234")
                    .appendQueryParameter("UserIPAddress","127.0.0.1")
                    .appendQueryParameter("UserAgent","androidApp")
                    .appendQueryParameter("responsetype", responsetype)
                    .build().toString();

            Log.i("uri", uri);

            HttpPost httppost = new HttpPost(uri);

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Log.i("Httppost",""+httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.i("response", "" + response);


            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail1", e.toString());
            // Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }


    }*/

    private void onPressingBack()
    {

       // final Intent intent;

       // if(isfrombooking)
         //   intent = new Intent(getApplicationContext(), bookingdetails.class);
       // else
          //  intent = new Intent(getApplicationContext(), MainActivity.class);

       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog);

        // Setting Dialog Title
        alertDialog.setTitle("Warning");

        // Setting Dialog Message
        alertDialog.setMessage("Do you cancel this transaction?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
                //startActivity(intent);
            }
        });



        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public class PayUJavaScriptInterface
    {
        Context mContext;

        /** Instantiate the interface and set the context */
        PayUJavaScriptInterface(Context c) {
            mContext = c;
        }

        public void success(long id, final String paymentId)
        {

            mHandler.post(new Runnable()
            {

                public void run()
                {
                    mHandler = null;
                   // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void webview_ClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData)
        {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");
        Log.d("WEb cleint", "webview_ClientPost called");
        webView.loadData(sb.toString(), "text/html", "utf-8");

    }

    public boolean empty(String s)
    {
        if (s == null || s.trim().equals(""))
            return true;
        else
            return false;
    }

    public String hashCal(String type, String str)
    {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try
        {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++)
            {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException nsae)
        {
        }
        return hexString.toString();

    }




}
