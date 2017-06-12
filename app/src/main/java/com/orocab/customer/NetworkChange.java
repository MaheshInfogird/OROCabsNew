package com.orocab.customer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Infogird80 on 9/16/2016.
 */
public abstract class NetworkChange extends BroadcastReceiver
{
    public static boolean isConnected = false;
    public static boolean isTouched = false;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        isNetworkAvailable(context);
    }


    private boolean isNetworkAvailable( final Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        if(!isConnected)
                        {
                            isConnected = true;
                            onNetworkChange();
                            //checkNetwork();
                            Log.i("connected","connected");
                            //Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
            }
        }

        isConnected = false;
        onNetworkChange();
        //checkNetwork();
        Log.i("No internet connection", "No internet connection");
        //Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();

        return false;
    }
    protected abstract void onNetworkChange();

    //protected void checkNetwork(){}
}


