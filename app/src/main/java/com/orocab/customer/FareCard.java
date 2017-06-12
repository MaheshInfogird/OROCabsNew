package com.orocab.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FareCard extends BaseActivity
{

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    LinearLayout Dayimg,Nightimg;
    LinearLayout Nightcharges;
    ProgressDialog   progressDialog;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY,Usersessionid;
    String myJSON;

    private static final String Tag_minKm = "minKm";
    private static final String Tag_kmRate = "kmRate";
    private static final String Tag_basicRate = "basicRate";
    private static final String Tag_minuteCharge = "minuteCharge";

    String Minkm,kmrate,Basicrate,minuteCharge,Nightcharge;
    TextView MIinkm,Kmrate,Minutecharge,Nightchargestext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_card);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView text = (TextView)findViewById(R.id.toolText);
        text.setText("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cd=new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress =cd.getLocalIpAddress();
        APIKEY =cd.getAPIKEY();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        MIinkm = (TextView)findViewById(R.id.FareBase);
        Kmrate = (TextView)findViewById(R.id.FareKm);
        Nightchargestext = (TextView)findViewById(R.id.nightchargestext);
        Minutecharge= (TextView)findViewById(R.id.FareRideminute);

        Dayimg = (LinearLayout)findViewById(R.id.Daycharges);
        Nightimg = (LinearLayout)findViewById(R.id.nightcharges);
        Nightcharges = (LinearLayout)findViewById(R.id.nightchargesrate);
        getData();
        Dayimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Nightcharges.setVisibility(View.GONE);

            }
        });

        Nightimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Nightcharges.setVisibility(View.VISIBLE);
                String str = "Night charges as applicable on "+Nightcharge+" % of Total bill";
                Nightchargestext.setText(str);
            }
        });

    }

    public void getData()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                progressDialog = ProgressDialog.show(FareCard.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost(""+Url+"/rateCard/?uId="+Usersessionid+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=Mozilla&responsetype=2");
                Log.i("Json", "Get Json");
                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");
                InputStream inputStream= null;
                String result = null;
                try
                {
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
                }
                catch (Exception e)
                {
                    // Oops
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish)
                    {
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                Showfarecard();
                progressDialog.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void Showfarecard()
    {
        try
        {
            JSONArray jsonArray = new JSONArray(myJSON);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Minkm = jsonObject.getString(Tag_minKm);
            Basicrate = jsonObject.getString(Tag_basicRate);
            kmrate = jsonObject.getString(Tag_kmRate);
            minuteCharge = jsonObject.getString(Tag_minuteCharge);
            Nightcharge =  jsonObject.getString("nightCharge");
            MIinkm.setText(Basicrate + "/" + Minkm );
            Kmrate.setText(kmrate);
            Minutecharge.setText(minuteCharge);

        }
        catch (JSONException j)
        {
            j.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
        finish();
    }
}
