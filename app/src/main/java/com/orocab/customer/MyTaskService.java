package com.orocab.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by admin on 25-10-2016.
 */
public class MyTaskService extends GcmTaskService {

    private static final String TAG = "MyTaskService";

    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_RESULT = "extra_result";

    private OkHttpClient mClient = new OkHttpClient();

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    ConnectionDetector cd;
    String Url, IPAddress, APIKEY, Usersessionid;

    @Override
    public void onInitializeTasks() {
        // When your package is removed or updated, all of its network tasks are cleared by
        // the GcmNetworkManager. You can override this method to reschedule them in the case of
        // an updated package. This is not called when your application is first installed.
        //
        // This is called on your application's main thread.

        // TODO(developer): In a real app, this should be implemented to re-schedule important tasks.
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.i("onRunTask: ", ""+ taskParams.getTag());

        String tag = taskParams.getTag();
        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));

        // Default result is success.
        int result = GcmNetworkManager.RESULT_SUCCESS;

        // Choose method based on the tag.

        result = doPeriodicTask();

        // Create Intent to broadcast the task information.
        Intent intent = new Intent();
        intent.setAction(ACTION_DONE);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_RESULT, result);

        // Send local broadcast, running Activities will be notified about the task.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(intent);

        // Return one of RESULT_SUCCESS, RESULT_FAILURE, or RESULT_RESCHEDULE
        return result;
    }

    private int doWifiTask() {
        String url = "https://abc.xyz/";
        return fetchUrl(mClient, url);
    }

    private int doChargingTask() {
        String url = "http://www.nasa.gov/";
        return fetchUrl(mClient, url);
    }

    private int doPeriodicTask()
    {
        String bookingId = MapActivity_cstmr.Bookingid;
        String url = ""+Url+"/driverDetails/?bookingId="+bookingId+"&ApiKey="+APIKEY+"&UserIPAddress="+IPAddress+"&UserID=1212&UserAgent=androidApp&responsetype=2";
        Log.i("url", url);
        return fetchUrl(mClient, url);
    }

    private int fetchUrl(OkHttpClient client, String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.i("fetchUrl:response:", "" + response.body().string());

            if (response.code() != 200) {
                return GcmNetworkManager.RESULT_FAILURE;
            }
        } catch (IOException e) {
            Log.e("fetchUrl:error", "" + e.toString());
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
