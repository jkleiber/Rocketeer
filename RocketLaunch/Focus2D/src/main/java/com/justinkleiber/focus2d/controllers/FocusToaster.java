package com.justinkleiber.focus2d.controllers;

import android.content.Context;
import android.widget.Toast;

import com.justinkleiber.focus2d.base.Toaster;

/**
 * Created by Justin on 6/9/2015.
 */
public class FocusToaster implements Toaster{

    Context context;
    public FocusToaster(Context c)
    {
        this.context=c.getApplicationContext();
    }

    @Override
    public void makeToast(CharSequence txt) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, txt, duration);
        toast.show();
    }
}
