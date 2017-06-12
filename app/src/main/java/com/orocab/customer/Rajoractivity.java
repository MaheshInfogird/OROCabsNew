package com.orocab.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rajoractivity extends AppCompatActivity implements PaymentResultListener {

    String mFirstName,mEmailId,mPhone,modeofPayment,uId;
    String mAmount;
    int amount;
    private String hash = "";
    String reference;

    Handler mHandler = new Handler();
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    String RajorpaymentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rajoractivity);

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
        startPayment();






    }

    @Override
    public void onBackPressed()
    {
        onPressingBack();

    }

    public void startPayment()
    {
        /**
         * Replace with your public key
         */
        // final String public_key = "rzp_test_q9DddqDPQDbn0r";
         final String public_key = "rzp_test_dwmcblZ9aF93Xb";
        //  final String public_key = "rzp_live_kjnbeSq7nu1pUH";
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

            options.put("amount", mAmount);
            options.put("name", mFirstName);
            options.put("prefill", new JSONObject("{email: '" + mEmailId + "', contact: '" + mPhone + "'}"));
            co.open(activity, options);
            //onPaymentSuccess(RajorpaymentId);


        }
        catch(Exception e)
        {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentSuccess(String razorpayPaymentID)
    {
        try
        {
            Log.i("razorpayPaymentID", "" + razorpayPaymentID);
            RajorpaymentId = razorpayPaymentID;
           // Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
         //   capture();
              AddCCrCash();

        }
        catch (Exception e)
        {
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentError(int code, String response)
    {
        try
        {
            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    public void capture()
    {
        String responsetype="2";
        //PaidAmount = Addcash.getText().toString();
        //http://jaidevcoolcab.cabsaas.in/sandbox/index/bookingRequestNew/?uniqueRefNo=100&guestName=modi&guestMobileNo=9090909090&guestMailId=modi@demo.com&guestCity=Aurangabad&guestCountry=India&pickupCity=Aurangabad&dropCity=Pune&pickUpaddress=ajab%20nager&travelDate=10-10-2016&pickupTime=1:45pm&vehicleName=Tata%20Indica&days=2&cars=2&paidAmount=100&tripType=1&tripRoot=2&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=yu76&UserID=76&UserAgent=hj87&responsetype=2

        /* $uriStr = "https://api.razorpay.com/v1/payments/" . $postedData['token'] . "/capture";
            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $uriStr);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POST, 1);
            curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array('amount' => $msg->amount)));
            //curl_setopt($ch, CURLOPT_USERPWD, "rzp_test_q9DddqDPQDbn0r:HyRt9SicmbVqjuuuPVjIkBBv");
            curl_setopt($ch, CURLOPT_USERPWD, "rzp_live_xnQaDnR8ivJFJx:V7aEwwi3cHMow3bRY3qQIYnt");
            curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
            $output = curl_exec($ch);
            curl_close($ch);
            $msg = json_decode($output);*/

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        String token ="Cc9r93bZxpb7Un7Z9AMxFliu";

        String AddCash ="https://api.razorpay.com/v1/payments/"+RajorpaymentId+"/capture";


        nameValuePairs.add(new BasicNameValuePair("uId", uId));
        nameValuePairs.add(new BasicNameValuePair("paidAmount", mAmount));
        nameValuePairs.add(new BasicNameValuePair("transactionId", reference));
        nameValuePairs.add(new BasicNameValuePair("reference", reference));


        try
        {



            HttpClient httpclient = new DefaultHttpClient();
            //  String APIKEY = "PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(AddCash)
                    .buildUpon()
                    .appendQueryParameter("amount",String.valueOf(mAmount))
                    .appendQueryParameter("CURLOPT_RETURNTRANSFER",String.valueOf(true))
                    .appendQueryParameter("CURLOPT_USERPWD", "rzp_test_q9DddqDPQDbn0r:Cc9r93bZxpb7Un7Z9AMxFliu")
                    .build().toString();

            Log.i("uri", uri);


            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.i("responsecapture",response);
         /*   Intent in = new Intent(getApplicationContext(),Wallet.class);
            startActivity(in);
            finish();*/
        }
        catch (Exception e)
        {
            Log.e("Fail1", e.toString());
        }







    }

    private void onPressingBack()
    {

        //   final Intent intent;

       /* if(isfrombooking)

            intent = new Intent(payumoney.this, MainActivity.class);
        else
            intent = new Intent(payumoney.this, MainActivity.class);*/

        //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Rajoractivity.this, R.style.AlertDialog);

        // Setting Dialog Title
        alertDialog.setTitle("Warning");

        // Setting Dialog Message
        alertDialog.setMessage("Do you cancel this transaction?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {

                Intent in = new Intent(getApplicationContext(), Wallet.class);
                startActivity(in);
                finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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
                    .appendQueryParameter("rajorPayid",RajorpaymentId)
                    .appendQueryParameter("reference",reference)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "234")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", responsetype)
                    .build().toString();

            Log.i("uri", uri);


            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            Intent in = new Intent(getApplicationContext(),Wallet.class);
            startActivity(in);
            finish();
        }
        catch (Exception e)
        {
            Log.e("Fail1", e.toString());
        }







    }

}
