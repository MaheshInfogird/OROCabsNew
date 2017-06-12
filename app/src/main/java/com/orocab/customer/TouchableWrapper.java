package com.orocab.customer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by admin on 29-08-2016.
 */
public class TouchableWrapper extends FrameLayout {

    private UpdateMapAfterUserInterection updateMapAfterUserInterection;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        if (MapActivity.receiver.isConnected)
        {
            Log.i("dispatchTouchEvent ", ""+ev.getAction());

            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if (MapActivity.Book.isEnabled())
                    {
                        MapActivity.hideViews();
                        MapActivity.mMapIsTouched = true;
                        if (MapActivity.ed_auto_pick.isEnabled())
                        {
                            MapActivity.ed_auto_pick.setText("Getting Location");
                        }
                        else if (MapActivity.ed_auto_drop.isEnabled())
                        {
                            MapActivity.ed_auto_drop.setText("Getting Location");
                        }

//                        MapActivity.timeoutHandler.removeCallbacks(MapActivity.finalizer);
                    }
                    Log.i("ACTION_DOWN", "ACTION_DOWN");
                    break;


                case MotionEvent.ACTION_UP:
                    if (MapActivity.Book.isEnabled())
                    {
                        MapActivity.showViews();
//                        MapActivity.mMapIsTouched = false;
                    }
                    Log.i("ACTION_UP", "ACTION_UP");
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
        else
        {
            return true;
        }
    }


    // Map Activity must implement this interface
    public interface UpdateMapAfterUserInterection {
        public void onUpdateMapAfterUserInterection();
    }
}