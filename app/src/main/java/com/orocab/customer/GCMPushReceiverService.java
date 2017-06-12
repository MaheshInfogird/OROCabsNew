package com.orocab.customer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
/**
 * Created by infogird31 on 02/08/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {
    //This method will be called on every new message received

    Context mContext;
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        //Getting the message from the bundle
        String message = data.getString("message");
        //send directlly to alert page
       if (!message.equals(""))
       {
           Log.i("message",message);
           if (message.equals("Customer invoice"))
           {
               Intent intent = new Intent(this, Customerinvoice.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }
           else if (message.equals("trip started"))
           {
               Intent intent = new Intent(this, MapActivity_cstmr.class);
               MapActivity_cstmr.Tripstatus = true;
               intent.putExtra("Trip","0");
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);

           }
           else
           {
               Intent intent = new Intent(this, MapActivity_cstmr.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }
       }
        //Displaying a notiffication with the message
        sendNotification(message);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message)
    {

        /* message = "This is notification";
        String[] arr = message.split(" ");

        for(String ss: arr){
            Log.i("ss", ss);
        }
*/

        Intent intent = new Intent(this, MapActivity_cstmr.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //temp code
        Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_SOUND, 1);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setVibrate(new long[] {1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, noBuilder.build()); //0 = ID of notification
        notificationManager.cancel(1);
//        noBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
    }
}
