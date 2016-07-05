package com.justinkleiber.focus2d.controllers;

import android.content.Context;
import android.os.Vibrator;

import com.justinkleiber.focus2d.base.Vibrate;

public class FocusVibrate implements Vibrate{

	Vibrator v;
	Context context;
	
	public FocusVibrate(Context c)
	{
		this.context = c;
		v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
	}
	@Override
	public void vibrate(long ms) {
		// TODO Auto-generated method stub
		v.vibrate(ms);
	}

	@Override
	public void vibratePattern(long[] ms) {
		// TODO Auto-generated method stub
		v.vibrate(ms, 0);
	}

}
