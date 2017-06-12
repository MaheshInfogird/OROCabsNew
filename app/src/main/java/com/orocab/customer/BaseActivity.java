package com.orocab.customer;

/**
 * Created by infogird73 on 08/03/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFER_NAME = "MyPref";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    // public static final String LName = "Lastname";

    public static final String Tag_Sign = "responseCode";
    public static final String Tag_Subuserid = "subuserid";
    public static final String Tag_uid = "uId";
    public static final String TagprefernceBalance = "balance";
    public static final String Tagemail = "email";
    public static final String Tagmobile = "mobile";
    public static final String Tagname = "firstName";
    public static final String TagLast = "Lastname";


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    protected RelativeLayout _completeLayout, _activityLayout;
    private CharSequence mDrawerTitle;
    Toolbar toolbar;
    private CharSequence mTitle;
    SharedPreferences pref;
    String firstName, LastName, Email;
    UserSessionManager session;
    ImageView EditProfilebutton;
    TextView Headername;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_drawer);

        session = new UserSessionManager(getApplicationContext());

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        firstName = (shared.getString(Name, ""));
        Log.i("firstName12", "" + firstName);
    }

    public void set(String[] navMenuTitles, TypedArray navMenuIcons)
    {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList   = (ListView) findViewById(R.id.left_drawer);

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.side_menu_header, null, false);

        Headername = (TextView)listHeaderView.findViewById(R.id.headername);
        Headername.setText(firstName);

        mDrawerList.addHeaderView(listHeaderView);

        EditProfilebutton = (ImageView)findViewById(R.id.EditButton);
        EditProfilebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(getApplicationContext(),Profile.class);
                startActivity(in);
                //finish();
            }


        });


        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items
        if (navMenuIcons == null)
        {
            for (int i = 0; i < navMenuTitles.length; i++)
            {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]));
            }
        }
        else
        {
            for (int i = 0; i < navMenuTitles.length; i++)
            {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
            }
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(mDrawerTitle);

                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    //new class
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (mDrawerLayout.isDrawerOpen(mDrawerList))
            {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
            else
            {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayView(int position)
    {



        switch (position)
        {
            case 0:
               /* Intent intent = new Intent(this,BaseActivity.class);
                startActivity(intent);
                finish();*/
                break;

            case 1:
                Intent intent1 = new Intent(this, MapActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MapActivity.intent_pas = false;
                startActivity(intent1);
//                finish();
                break;

            case 2:
                Intent intent2 = new Intent(this, Booking.class);
                startActivity(intent2);
                //finish();
                break;

            case 3:
                Intent intent4 = new Intent(this, FareCard.class);
                startActivity(intent4);
                //finish();
                break;

            case 4:
                Intent in = new Intent(this,Wallet.class);
                startActivity(in);
                //finish();
                break;

            case 5:
                Intent intent8 = new Intent(this, emergencycontact.class);
                startActivity(intent8);
                //finish();
               break;

            case 6:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.orocab.customer&hl=en"));
                startActivity(intent);
                break;

            case 7:
                String url1 = "http://www.orocab.com";
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url1));
                startActivity(i1);
                break;


            case 8:
                Intent intent10 = new Intent(this, logout.class);
                startActivity(intent10);
                //finish();
                break;

            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
      super.onBackPressed();
    }
}
