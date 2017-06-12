package com.orocab.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Booking extends BaseActivity
{
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;

    int status;

    //{"result":[{"bookingId":5336,"refNo":"OW1MW25","pCity":"25","tDate":"2016-02-16","pickupTime":"01:30:00","travelType":"1"}]}

    private String Bookingnumber,Bookingcity,Referenceno,BookingDate,pickuptime,tarrifname,tarrifsubname,VehicleName,pickupaddress,Dropaddress,totalBasic,Bookingid;
    //private static final String TAG_RESULTS = "result";
    private static final String Tag_booking_id = "refNo";
    private static final String Tag_bookid = "bookingId";
    private static final String TAG_refno = "refNo";
    private static final String TAG_pCity = "ctname";
    private static final String TAG_tdate = "tDate";
    private static final String TAG_pickupTime = "pickupTime";
    private static final String TAG_travelType = "travelType";
    private static final String Tag_tariffName="tariffName";
    private static final String Tag_vehicleName="vehicleName";
    private static final String Tag_tariffSubName="tariffSubName";
    private static final String Tag_tariffStatus="status";
    private static final String Tag_pickupAddress="pickupAddress";
    private static final String Tag_dropAddress="dropAddress";
    private static final String Tag_totalBasicFare="totalBasicFare";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";

    final ArrayList<HashMap<String, String>> Bookinglist = new ArrayList<HashMap<String, String>>();

    ListView Mybooking;
    ProgressDialog   progressDialog;
    ConnectionDetector cd;

    String Url,IPAddress,APIKEY,Usersessionid;
    String myJSON;
    String both;
    String response;

    public static boolean num = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView text = (TextView)findViewById(R.id.toolText);
        text.setText("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        Mybooking = (ListView)findViewById(R.id.ORObookinglist);

        cd = new ConnectionDetector(getApplicationContext());
        APIKEY = cd.getAPIKEY();
        IPAddress =cd.getLocalIpAddress();
        Url = cd.geturl();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        Log.i("Usersessionid", Usersessionid);
        getData();
    }


    public void showbooking()
    {
        try
        {
            JSONArray jsonArray = new JSONArray(myJSON);
            Log.i("json", "" + jsonArray);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject c = jsonArray.getJSONObject(i);
                Bookingnumber = c.getString(Tag_booking_id);
                Referenceno = c.getString(TAG_refno);
                BookingDate  = c.getString(TAG_tdate);
                pickuptime = c.getString(TAG_pickupTime);
                tarrifname = c.getString(Tag_tariffName);
                tarrifsubname = c.getString(Tag_tariffSubName);
                pickupaddress = c.getString(Tag_pickupAddress);
                Dropaddress  = c.getString(Tag_dropAddress);
                totalBasic   = c.getString(Tag_totalBasicFare);
                status = c.getInt(Tag_tariffStatus);
                Bookingid = c.getString(Tag_bookid);
                Log.i("status", "" + status);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(Tag_booking_id, Bookingnumber);
                map.put(TAG_refno,Referenceno);
                map.put(TAG_tdate, BookingDate);
                map.put(TAG_pickupTime, pickuptime);
                map.put(Tag_tariffName,tarrifname);
                map.put(Tag_tariffSubName,tarrifsubname);
                map.put(Tag_pickupAddress,pickupaddress);
                map.put(Tag_dropAddress,Dropaddress);
                map.put(Tag_totalBasicFare,totalBasic);
                map.put(Tag_bookid,Bookingid);

                if(status==0)
                {
                    map.put(Tag_tariffStatus,"0");
                }

                if (status==1)
                {
                    Log.i("Runnning","Runnning");
                    map.put(Tag_tariffStatus,"1");
                }

                if (status==2)
                {
                    Log.i("Cancelled","Cancelled");
                    map.put(Tag_tariffStatus,"2");
                }

                if (status==3)
                {
                    map.put(Tag_tariffStatus,"3");
                }

                if (status==4)
                {
                    Log.i("Completed","Completed");
                    map.put(Tag_tariffStatus,"4");
                }

                Bookinglist.add(map);
                final ListAdapter adapter1 = new CustomList(getApplicationContext(), Bookinglist, R.layout.mybooking, new String[]{}, new int[]{});
                Mybooking.setAdapter(adapter1);
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
            protected void onPreExecute()
            {
                progressDialog = ProgressDialog.show(Booking.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost(""+Url+"/customerBooking/?uId="+Usersessionid+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=Mozilla&responsetype=2");
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
                Log.i("myjson",myJSON);
                showbooking();
                progressDialog.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    public class  CustomList extends SimpleAdapter
    {
        private Context mContext;
        public LayoutInflater inflater=null;
        public CustomList(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
        {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent)
        {
            View vi=convertView;
            if(convertView==null)
            {
                vi = inflater.inflate(R.layout.mybooking, null);
            }

            HashMap< String, Object > data = (HashMap<String, Object>) getItem(position);
            final Button textView4 = (Button)vi.findViewById(R.id.orobookingstatus);
            final LinearLayout  Myride = (LinearLayout)vi.findViewById(R.id.Myrideinvoice);
            final  TextView text3 = (TextView)vi.findViewById(R.id.Orocrno);
            final  TextView orobookingid = (TextView)vi.findViewById(R.id.orobookingid);
            String Crno = (String)data.get(Tag_booking_id);
            text3.setText(Crno);
            final String vehistatus = (String)data.get(Tag_tariffStatus);
            Log.i("vehistatus", "" + vehistatus);
            final  TextView status = (TextView)vi.findViewById(R.id.orostatus);
            status.setText(vehistatus);
            final String str =status.getText().toString();
            final String Bookingid = (String)data.get(Tag_bookid);
            orobookingid.setText(Bookingid);

            textView4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   if(str.equalsIgnoreCase("1"))
                   {
                       String bookingid = orobookingid.getText().toString();
                       Intent in = new Intent(getApplicationContext(),MapActivity_cstmr.class);
                       in.putExtra("BookingId",bookingid);
                       startActivity(in);
                       finish();
                   }
                   else
                   {
                       Log.i("ButtonClick", "ButtonClick");
                       String Crno = text3.getText().toString();
                       num = true;
                       Intent in = new Intent(getApplicationContext(), Customerinvoice.class);
                       in.putExtra("Crno", Crno);
                       in.putExtra("num", "1");
                       startActivity(in);
                       finish();
                   }
                }



            });


            TextView text2 = (TextView)vi.findViewById(R.id.orobookingdatetime);
            String name2 = (String)data.get(TAG_tdate);
            //text2.setText(name2);

            TextView textView2 = (TextView)vi.findViewById(R.id.orobookingdatetime);
            String name6 = (String)data.get(TAG_pickupTime);
            textView2.setText(name2+ " " +name6);

            TextView Textbookingnumber = (TextView)vi.findViewById(R.id.Bookingnumber);
            int pos = position + 1;
            Textbookingnumber.setText("Booking#" + String.valueOf(pos));

            TextView pickup = (TextView)vi.findViewById(R.id.oropickup);
            String pick = (String)data.get(Tag_pickupAddress);
            if(pick!=null)
            {
                pickup.setText(pick);
            }

            TextView Dropaddress = (TextView)vi.findViewById(R.id.oroDrop);
            String Drop = (String)data.get(Tag_dropAddress);
            if(Drop!=null)
            {
                Dropaddress.setText(Drop);
            }

            TextView Bookingamt = (TextView)vi.findViewById(R.id.orobookingamount);
            String amt = (String)data.get(Tag_totalBasicFare);
            Bookingamt.setText(amt);

            if(str.equalsIgnoreCase("4"))
            {
               // cancellink.setVisibility(View.VISIBLE);
                textView4.setText("Completed");
                textView4.setBackground(getResources().getDrawable(R.drawable.green_btn));
                textView4.setTextColor(getResources().getColor(R.color.white));
            }

            if(str.equalsIgnoreCase("2"))
            {
                textView4.setText("Cancelled");
                textView4.setTextColor(getResources().getColor(R.color.white));
                textView4.setBackground(getResources().getDrawable(R.drawable.red_btn));

            }

            if(str.equalsIgnoreCase("1"))
            {
                textView4.setText("Running");
                textView4.setTextColor(getResources().getColor(R.color.white));
                textView4.setBackground(getResources().getDrawable(R.drawable.golden_btn));

                // cancellink.setVisibility(View.INVISIBLE);
            }

            if(str.equalsIgnoreCase("0"))
            {
                textView4.setVisibility(View.GONE);
            }

            return vi;
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
