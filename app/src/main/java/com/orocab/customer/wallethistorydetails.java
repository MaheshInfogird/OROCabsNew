package com.orocab.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class wallethistorydetails extends AppCompatActivity
{

    ListView OrowalletHistory;
    ProgressDialog progressDialog;
    ConnectionDetector cd;
    String Url, IPAddress, APIKEY, Usersessionid, myJSON;
    JSONArray CashData;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    public static final String Tag_Sign = "responseCode";
    private static final String TAG_RESULTS = "result";
    private static final String Tag_balance = "balance";
    private static final String Tag_amount = "amount";
    private static final String Tag_tdate = "tdate";
    private static final String Tag_referenceno = "referenceNo";
    private static final String Tag_crdrStatus = "crdrStatus";


    String Balance, refno, Date, amount;
    final ArrayList<HashMap<String, String>> Cashwallet = new ArrayList<HashMap<String, String>>();
    ListView list;
    String AvailableBalance;
    //SharedPreferences sharedpreferences;
    int cashwalletbalance;
    String name, email, mobile;
    int CR, DR;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallethistorydetails);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        OrowalletHistory = (ListView) findViewById(R.id.OROhistorylist);
        cd = new ConnectionDetector(getApplicationContext());
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();
        Url = cd.geturl();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        getData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
                    onBackPressed();
                }
            });
        }


    }

    public void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(wallethistorydetails.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                String cashurl = "" + Url + "/crdrList/?uId="+Usersessionid + "&ApiKey=" + APIKEY + "&UserIPAddress=" + IPAddress + "&UserID=1212&UserAgent=androidapp&responsetype=2";
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
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {

                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                Log.i("myJson", myJSON);
                showcash();
                progressDialog.dismiss();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    public void showcash()
    {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            CashData = jsonObj.getJSONArray(TAG_RESULTS);
            Log.i("json", "" + CashData);
            for (int i = 0; i < CashData.length(); i++) {

                JSONObject c = CashData.getJSONObject(i);
                Date = c.getString(Tag_tdate);
                refno = c.getString(Tag_referenceno);
                if (refno == null) {

                }
                Balance = c.getString(Tag_balance);

                // AvailableCash.setText(Balance);
                CR = c.getInt(Tag_crdrStatus);
                amount = c.getString(Tag_amount);
                if (i == 0)
                {
                    AvailableBalance = Balance;
                    // SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    //preferences.Editor editor = preferences.edit();
                    //  preferences.Editor editor = sharedpreferences.edit();
                    //  editor.putString(uid, Usersessionid);
                    //    editor.putString(Tag_Availbalance, AvailableBalance);
                    //   editor.apply();
                    //  Log.i("Aval",AvailableBalance);

                    //   Log.i("AvailableBalance", AvailableBalance);

                }


                HashMap<String, String> map = new HashMap<String, String>();
                map.put(Tag_tdate, Date);
                map.put(Tag_referenceno, refno);
                map.put(Tag_amount, amount);
                map.put(Tag_crdrStatus,String.valueOf(CR));

                Cashwallet.add(map);
                final ListAdapter adapter1 = new CustomList(getApplicationContext(), Cashwallet, R.layout.activity_oro_wallet_history, new String[]{}, new int[]{});
                OrowalletHistory.setAdapter(adapter1);


            }

        } catch (JSONException e)

        {
            e.printStackTrace();
        }


    }

    public class CustomList extends SimpleAdapter {

        private Context mContext;
        public LayoutInflater inflater = null;

        public CustomList(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null) {
                vi = inflater.inflate(R.layout.activity_oro_wallet_history, null);
            }

            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            // TextView text = (TextView)vi.findViewById(R.id.carprice);

            // Car  = (String) data.get(Tag_booking_id);
            //// text.setText(Car);

            TextView text2 = (TextView) vi.findViewById(R.id.orotransactiondate);
            String name2 = (String) data.get(Tag_tdate);
            text2.setText(name2);

            TextView text3 = (TextView) vi.findViewById(R.id.oroTransid);
            String name3 = (String) data.get(Tag_referenceno);
            text3.setText(name3);


            String cr_drstatus = (String) data.get(Tag_crdrStatus);

            if (cr_drstatus.equals("0"))
            {
                TextView textView = (TextView) vi.findViewById(R.id.orohisamount);
                String name4 = (String) data.get(Tag_amount);
                String Available = name4.toString();
                textView.setTextColor(getResources().getColor(R.color.RedTextColor));
                String Rup = getResources().getString(R.string.rs) + " " +"DR";
                textView.setText(Rup + name4);


            }

            else
            {

                TextView textView = (TextView) vi.findViewById(R.id.orohisamount);
                String name4 = (String) data.get(Tag_amount);
                String Available = name4.toString();
                textView.setTextColor(getResources().getColor(R.color.GreenTextColor));
                String Rup = getResources().getString(R.string.rs)+ " " +"CR";
                textView.setText(Rup + name4);

            }


          /*  TextView textView1 = (TextView)vi.findViewById(R.id.walletamount);
            String name5 = (String)data.get(Tag_amount);
            textView1.setText(name5);
*/
            //  SharedPreferences shared = getActivity().getSharedPreferences(MyPREFERENCES, getActivity().MODE_PRIVATE);

           /* */


            return vi;


        }


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Wallet.class);
        startActivity(intent);
        finish();
    }
}
