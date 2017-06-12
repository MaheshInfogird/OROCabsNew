package com.orocab.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by admin on 29-08-2016.
 */
public class MySupportMapFragment extends SupportMapFragment
{
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        Log.i("mOriginalContentView", "mOriginalContentView");
        return mTouchView;

    }

    @Override
    public View getView() {
        Log.i("getView", "getView");
        return mOriginalContentView;
    }
}