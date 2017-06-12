package com.orocab.customer;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by admin on 04-08-2016.
 */
class MyLocationListener implements LocationListener {

    String CompleteAddress;
    Location location = null;
    LocationManager locationManager;
    double Latitude, Longitude;
    Marker now;
    GoogleMap googleMap;


    @Override
    public void onLocationChanged(Location locFromGps)
    {
        // called when the listener is notified with a location update from the GPS

         Log.i("LOCATIONUPDATE", "LOCATIONUPDATE");
            Latitude = locFromGps.getLatitude();
            Log.i("Latitude123", "" + locFromGps.getLatitude());
            Longitude = locFromGps.getLongitude();
            Log.i("Longitude123", "" + locFromGps.getLongitude());

        if(now != null)
        {
            now.remove();

        }

      //  TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = locFromGps.getLatitude();

        // Getting longitude of the current location
        double longitude = locFromGps.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        /*now = googleMap.addMarker(new MarkerOptions().position(latLng));
        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Log.i("MyLocationListener","MyLocationListener");*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }
}