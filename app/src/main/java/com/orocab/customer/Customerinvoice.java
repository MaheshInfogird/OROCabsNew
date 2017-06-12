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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class Customerinvoice extends AppCompatActivity
{
    private static final String PREFER_NAME = "MyPref";
    private static final String TAG_total_bill="totalBasicFare";
    private static final String TAG_rides_km="distance";
    private static  final String TAG_rides_min="tripTime";
    private static final String TAG_pick_location="pickUpLocation";
    private static final String TAG_drop_location="dropLocation";
    private static  final String TAG_total_fare="totalBasicFare";
    private static final String TAG_service_tax_other="serviceTax";
    private static final String TAG_basicFare="basicFare";
    private static final String TAG_wallet="";
    private static final String TAG_Tariff="basicTariffRate";
    private static final String TAG_Totalbasicfare="totalBasicFare";

    ConnectionDetector cd;
    Toolbar toolbar;
    ProgressDialog progressDialog = null;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    String Usersessionid;
    String str = "";
    String Url,IPAddress,APIKEY,Crno;
    String Comment, Bookingid, basicFare;
    String DriverName, VehicleNo, VehicleName, VehicleId, CrnNo;
    String myJSON;

    TextView Bill,Ridekm,RideTime,Pickup,Drop,Totalfare,serviceTax,Finalbill;
    Button btnSubmit;

    int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerinvoice);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow));

        if (getSupportActionBar() != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBackPressed();
                }
            });
        }


        Bill = (TextView)findViewById(R.id.Billamount);
        Ridekm = (TextView)findViewById(R.id.Ridekm);
        RideTime = (TextView)findViewById(R.id.RideTime);
        Pickup = (TextView)findViewById(R.id.Pickupcustomer);
        Drop = (TextView)findViewById(R.id.DropCustomer);
        Totalfare = (TextView)findViewById(R.id.TotalFarecustomer);
        serviceTax = (TextView)findViewById(R.id.serviceTax);
        Finalbill = (TextView)findViewById(R.id.Totalbillcustomer);
        btnSubmit = (Button)findViewById(R.id.btn_submit);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        Log.i("Usersessionid", Usersessionid);


        Bookingid = (shared.getString("BookingId", ""));
        DriverName = (shared.getString("Drivername", ""));
        VehicleName = (shared.getString("vehiclename", ""));
        VehicleNo = (shared.getString("vehiclenumber", ""));
        CrnNo = (shared.getString("crnNumber", ""));
        VehicleId = getIntent().getStringExtra("vehicleId");
        Crno = getIntent().getStringExtra("Crno");

        if (Booking.num)
        {
            customerinvoice();
        }
        else
        {
            customerInvoiceBillPaid();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater layoutInflater = (LayoutInflater) Customerinvoice.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                View popupView = layoutInflater.inflate(R.layout.rating_popup, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                popupWindow.setFocusable(true);
                popupWindow.update();

                final ImageView driver_pic = (ImageView)popupView.findViewById(R.id.driver_pic);
                final ImageView close_popup = (ImageView)popupView.findViewById(R.id.close_img);
                final TextView driver_name = (TextView)popupView.findViewById(R.id.Rdrivername);
                final TextView vehicle_name = (TextView)popupView.findViewById(R.id.rVehiclename);
                final TextView vehicle_no = (TextView)popupView.findViewById(R.id.rVehiclno);
                final TextView remind_later = (TextView)popupView.findViewById(R.id.rclosepopup);
                final RatingBar ratingBar = (RatingBar)popupView.findViewById(R.id.driver_rating);
                final Button submit = (Button)popupView.findViewById(R.id.Ratebutton);
                final EditText comment = (EditText)popupView.findViewById(R.id.ed_comment);

                driver_name.setText(DriverName);
                vehicle_name.setText(VehicleName);
                vehicle_no.setText(VehicleNo);

                submit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        double rate = ratingBar.getRating();
                        Log.i("rate", ""+rate);
                        rating = (int)rate;
                        Log.i("rating", ""+rating);
                        Comment = comment.getText().toString();

                        progressDialog = ProgressDialog.show(Customerinvoice.this, "", "Please wait...", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                rateToDriver();
                            }
                        }).start();
                    }
                });

                close_popup.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                    }
                });

                remind_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
        });
    }


    public void rateToDriver()
    {
        try
        {
            String url =""+Url+"/review/?";

            String uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("reviewBy", Usersessionid)
                    .appendQueryParameter("reviewTo", VehicleId)
                    .appendQueryParameter("ratingpoint",String.valueOf(rating))
                    .appendQueryParameter("bookingId",Bookingid)
                    .appendQueryParameter("comment",Comment)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri_rating", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);

            JSONArray json = new JSONArray(response);
            Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            final String LoginStatus = jsonObject.getString("responseCode");
            final String Respmsg = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Respmsg.substring(2, Respmsg.length() - 2);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // dialog.dismiss();
                    progressDialog.dismiss();
                    if (LoginStatus.equals("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customerinvoice.this, R.style.AlertDialog);
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent in = new Intent(Customerinvoice.this, MapActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customerinvoice.this, R.style.AlertDialog);
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.i("Error",""+e.toString());
        }
    }
    //end
//////////////
    public void customerinvoice()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/customerInvoice/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId",Usersessionid)
                    .appendQueryParameter("crnNumber", Crno)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);
            myJSON = response;
            show_jouney_complete();
        }
        catch (IOException j)
        {
            j.printStackTrace();
        }
    }

    public void show_jouney_complete()
    {
        try
        {
            JSONArray json = new JSONArray(myJSON);
            //Log.i("json", "" + json);
            for (int i = 0; i < json.length(); i++)
            {
                JSONObject jsonObject = json.getJSONObject(i);

                String total_bill_header = jsonObject.getString(TAG_total_bill);
                //Log.i("total_bill_header", total_bill_header);

                String rides_km = jsonObject.getString(TAG_rides_km);
               // Log.i("rides_km", rides_km);

                String rides_min = jsonObject.getString(TAG_rides_min);
                //Log.i("rides_min", rides_min);

                String pic_location  = jsonObject.getString(TAG_pick_location);
                //Log.i("pic_location", pic_location);

                String drop_loaction = jsonObject.getString(TAG_drop_location);
                //Log.i("drop_loaction", drop_loaction);

                String total_fare = jsonObject.getString(TAG_total_fare);
                //Log.i("total_fare", total_fare);


                String service_tax_other = jsonObject.getString(TAG_service_tax_other);
                //Log.i("service_tax_other", service_tax_other);

                basicFare = jsonObject.getString(TAG_basicFare);
               // Log.i("basicFare", basicFare);

                Bill.setText(total_bill_header);
                Ridekm.setText(rides_km);
                RideTime.setText(rides_min);
                Pickup.setText(pic_location);
                Drop.setText(drop_loaction);
                Totalfare.setText(basicFare);
                serviceTax.setText(service_tax_other);
                Finalbill.setText(total_bill_header);
            }

            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
        }
    }

    public void customerInvoiceBillPaid()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/customerInvoice/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("crnNumber", CrnNo)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);
            myJSON = response;
            journey_complete();
        }

        catch (IOException j)
        {
            j.printStackTrace();
        }
    }

    public void journey_complete()
    {
        try
        {
            JSONArray json = new JSONArray(myJSON);
            //Log.i("json", "" + json);

            for (int i = 0; i < json.length(); i++)
            {
                JSONObject jsonObject = json.getJSONObject(i);

                String total_bill_header = jsonObject.getString(TAG_total_bill);
                //Log.i("total_bill_header", total_bill_header);

                String rides_km = jsonObject.getString(TAG_rides_km);
                //Log.i("rides_km", rides_km);

                String rides_min = jsonObject.getString(TAG_rides_min);
                //Log.i("rides_min", rides_min);

                String pic_location  = jsonObject.getString(TAG_pick_location);
                //Log.i("pic_location", pic_location);

                String drop_loaction = jsonObject.getString(TAG_drop_location);
                //Log.i("drop_loaction", drop_loaction);

                String total_fare = jsonObject.getString(TAG_total_fare);
                //Log.i("total_fare", total_fare);


                String service_tax_other = jsonObject.getString(TAG_service_tax_other);
                //Log.i("service_tax_other", service_tax_other);

                basicFare = jsonObject.getString(TAG_basicFare);
                // Log.i("basicFare", basicFare);

                Bill.setText(total_bill_header);
                Ridekm.setText(rides_km);
                RideTime.setText(rides_min);

                Pickup.setText(pic_location);
                Drop.setText(drop_loaction);
                Totalfare.setText(basicFare);
                serviceTax.setText(service_tax_other);
                Finalbill.setText(total_bill_header);
            }

            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Booking.class);
        Booking.num = false;
        startActivity(intent);
        finish();
    }
}
