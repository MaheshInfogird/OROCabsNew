package com.orocab.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class logout extends AppCompatActivity
{

    private Context mContext;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Email = "emailKey";
    public static final String Name  = "nameKey";
    public static final String phone = "phonekey";

    UserSessionManager session;
  //  ccrlogin cc1;
      Facebook fb1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);


        session = new UserSessionManager(getApplicationContext());
       // cc1 = new ccrlogin();
          fb1 = new Facebook();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(logout.this, R.style.AlertDialog);
        alertDialogBuilder.setMessage("Are you sure,You want to logout from App");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                session.logoutUser();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMain);
                finish();
                /*Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();*/
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                Intent in = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(in);
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
