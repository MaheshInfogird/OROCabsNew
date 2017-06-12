package com.orocab.customer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by infogird31 on 03/08/2016.
 */
public class IncomingSms extends BroadcastReceiver
{

    private static otpverify.SmsListener mListener;
    private static Forgotpassword.ForgotSmsListener mforgotListener;
    private static MobileActivity.getSmsListener mfacebooklistener;
    public static final String passkey1 = "passkey1";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Tag_Sign = "responseCode";
    public static final String Tag_responsemsg="responseMessage";
    SharedPreferences sharedpreferences;
    String statusactivity;
    Boolean chk1;
    String senderNum,title;
    String OTP_Number = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();

        //---This will match any 6 digit number in the message, can use "|" to lookup more possible combinations
        Pattern p = Pattern.compile("(|^)\\d{6}");

        try {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();
                    title =currentMessage.getDisplayOriginatingAddress();
                    String title1 =currentMessage.getOriginatingAddress();
                    String title2 = currentMessage.getMessageBody();
                  /*  Log.i("title",title);
                    Log.i("title1",title1);
                    Log.i("title2",title2);
*/

                    Log.i("message",""+message);

                    /* Now extract the otp*/
                    if(message!=null)
                    {
                        Matcher m = p.matcher(message);
                        if(m.find())
                        {
                            OTP_Number = m.group(0);
                            Log.i("OTP_Numbner",OTP_Number);

                        }
                        else
                        {
                            //something went wrong
                        }
                    }
                    try
                    {

                        SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        statusactivity = (shared.getString("otp", ""));
                        Log.i("statusactivity",statusactivity);
                       // Boolean chk = Boolean.getBoolean(statusactivity);
                      //
                      //
                      // Log.i("chk", "" + chk);
                        getdetect();
                        String str ="otp";

                        if(statusactivity.compareTo(str) == 0)
                        {

                            getdetect();

                        }

                    }

                    catch(Exception e){}

                }
            }

        } catch (Exception e)
        {

        }
    }
    public static void bindListener(otpverify.SmsListener listener)
    {
        mListener = listener;
    }

    public static void bindListener(Forgotpassword.ForgotSmsListener listener)
    {
        mforgotListener = listener;
    }

    public static void bindListener(MobileActivity.getSmsListener listener)
    {
        mfacebooklistener = listener;
    }

    public void getdetect()
    {

        Log.i("insidestatus","getdetect");
        if (senderNum.equals(title) && statusactivity.equalsIgnoreCase("ot")  )
        {
            Log.i("chkotpverify","insideOTP");
            mListener.messageReceived(OTP_Number);
        }

        if (senderNum.equals(title) &&  statusactivity.equalsIgnoreCase("fp"))
        {
            Log.i("chkforgot","insideForgot");
            mforgotListener.messageReceived(OTP_Number);
        }

        if (senderNum.equals(title)&& statusactivity.equalsIgnoreCase("Fb"))
        {
            Log.i("chkforgot","insideForgot");
            mfacebooklistener.messageReceived(OTP_Number);
        }

    }


}
