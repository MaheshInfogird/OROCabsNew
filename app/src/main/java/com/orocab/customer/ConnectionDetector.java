package com.orocab.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by adminsitrator on 31/10/2015.
 */
public class ConnectionDetector
{
    private Context _context;

    public ConnectionDetector(Context context)
    {
        this._context = context;
    }

    public boolean isConnectingToInternet(Context context)
    {

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public String geturl()
    {
         //String url = "http://jaidevcoolcab.cabsaas.in/sandbox/ptop";
           String url = "http://ptop.cabsaas.com/api/ptop";
        //   String url = "http://orocab.cabsaas.com/api/ptop";

        return url;
    }


    public String getAPIKEY()
    {
      //  String key="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
      //  String key = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
        String key="";
        return key;
    }

    public String GetIpaddress()
    {
        WifiManager wm = (WifiManager)_context. getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        //return inetAddress.getHostAddress().toString();
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("IPAddress", "***** IP=" + ip);
                        return ip;
                       // return inetAddress.getAddress().toString();
                    }
                }
            }
        }
        catch (Exception ex)
        {
        }
        return null;
    }

    public void showNetDisabledAlertToUser(final Context context)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage("No internet connection on your device. Would you like to enable it?")
                .setTitle("No Internet Connection")
                .setCancelable(false)
                .setPositiveButton(" Enable Internet ",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(dialogIntent);
                            }
                        });

        alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                _context.startActivity(startMain);


            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alertDialog.show();
    }
}



