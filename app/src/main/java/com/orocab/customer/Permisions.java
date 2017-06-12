package com.orocab.customer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by infogird31 on 07/09/2016.
 */
public class Permisions {
    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    private int LOCATION_PERMISSION_CODE = 24;
    private int SMS_PERMISSION_CODE = 25;
    private int CONTACTS_PERMISSION_CODE = 26;
    private int ALL_PERMISSION_CODE = 27;
    List<String> permissions = new ArrayList<String>();
    // variable to hold context
    private Context context;

    public Permisions(Context context)
    {
        this.context=context;
    }


    //We are calling this method to check the permission status
    public boolean isAllPermAllowed()
    {

        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String locationpermissioncoarse = Manifest.permission.ACCESS_COARSE_LOCATION;
        String ContactsPermission = Manifest.permission.READ_CONTACTS;
        String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String smsPermission = Manifest.permission.RECEIVE_SMS;
        String GCMgsf = Manifest.permission.WRITE_GSERVICES;
        String Phonestate = Manifest.permission.READ_PHONE_STATE;



        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(context,  Manifest.permission.RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_GSERVICES);
        int result6=  ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int result7 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);


        Log.i("Log1",permissions.toString());

        if (result1 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(smsPermission);
            Log.i("Log2",permissions.toString());
        }
        if (result2 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(locationPermission);
            Log.i("Log3",permissions.toString());
        }
        if (result3 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(ContactsPermission);
            Log.i("Log4",permissions.toString());
        }
        if (result4 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(storagePermission);
            Log.i("Log5",permissions.toString());
        }
        if (result5 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(GCMgsf);
            Log.i("Log5",permissions.toString());
        }
        if (result6 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Phonestate);
            Log.i("Log5",permissions.toString());
        }
        if (result7 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(locationpermissioncoarse);
            Log.i("Log5",permissions.toString());
        }

        if (!permissions.isEmpty())
        {
            String[] params = permissions.toArray(new String[permissions.size()]);
            //   requestPermissions(params, REQUEST_PERMISSIONS);
            requestAllPermission();
            Log.i("Log6","false");
            return false;

        }
        else
        {

            Log.i("Log7","true");
            return true;
            // We already have permission, so handle as normal
        }

    }

    public void requestAllPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.RECEIVE_SMS)
                && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_CONTACTS)
                && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.WRITE_GSERVICES))
        {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission

        Log.i("Log10","permisions to req :"+ permissions.toArray(new String[permissions.size()]));
        ActivityCompat.requestPermissions((Activity) context,
                permissions.toArray(new String[permissions.size()]),ALL_PERMISSION_CODE);

    }

    public boolean check_permision(String permision, int perm_code)
    {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, permision);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permision))
                {
                    //If the user has denied the permission previously your code will come to this block
                    //Here you can explain why you need this permission
                    //Explain here why you need this permission
                }

                //And finally ask for the permission
                ActivityCompat.requestPermissions((Activity) context, new String[]{permision}, perm_code);

            if (result == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
            return false;}

        }
    }
}


