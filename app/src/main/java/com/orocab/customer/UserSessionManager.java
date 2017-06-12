package com.orocab.customer;

/**
 * Created by adminsitrator on 08/03/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class UserSessionManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
   // private static final String PREFER_NAME = "AndroidExamplePref";

    public static final String MyPREFERENCES = "MyPrefs" ;
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASS = "";

    // Constructor
    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
        editor = pref.edit();
    }
    //Create login session
    public void createUserLoginSession(String name, String pass){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_PASS, pass);
        // commit changes
        editor.commit();
    }
    /**
              * Check login method will check user login status
              * If false it will redirect user to login page
              * Else do anything
              * */
    /////////////////////////////////////////
    //check login method
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MapActivity.class);
            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
            return true;
        }
        return false;
    }
    /**
              * Get stored session data
              * */
    ////////////////////////////////////
    //put method of hashmap
    public HashMap<String, String> getUserDetails(){
        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        // return user
        return user;
    }
    /**
              * Clear session details
              * */
    ////////////////////////
    //logout method of session
    public void logoutUser()
    {
        // Clearing all user data from Shared Preferences

        editor.clear();
        editor.commit();


      //   After logout redirect user to Login Activity
        Intent i = new Intent(_context, MainActivity.class);
//        // Closing all the Activities
       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // Add new Flag to start new Activity
       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
       _context.startActivity(i);
    }
    // Check for login
    public boolean isUserLoggedIn()
    {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
