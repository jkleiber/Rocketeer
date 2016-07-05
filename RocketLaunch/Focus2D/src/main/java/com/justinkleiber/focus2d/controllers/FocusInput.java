package com.justinkleiber.focus2d.controllers;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.justinkleiber.focus2d.base.Input;

public class FocusInput implements Input {

	TouchHandler touchHandler;
	
	public FocusInput(Context c, View v,
			float scaleX, float scaleY) {
		// TODO Auto-generated constructor stub
		if(android.os.Build.VERSION.SDK_INT<5)
		{
			touchHandler = new FocusOneTouch(v,scaleX,scaleY);
		}
		else
		{
			touchHandler = new FocusMultiTouch(v,scaleX,scaleY);
		}
	}

	@Override
	public boolean isTouchDown(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		// TODO Auto-generated method stub
		return touchHandler.getTouchEvents();
	}

}
