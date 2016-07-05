package com.justinkleiber.focus2d.controllers;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.justinkleiber.focus2d.base.Input.TouchEvent;
import com.justinkleiber.focus2d.base.Pool;
import com.justinkleiber.focus2d.base.Pool.PoolObjectFactory;


public class FocusOneTouch implements TouchHandler {

	boolean isTouched;
	int touchX;
	int touchY;
	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;
	
	public FocusOneTouch(View v, float scaleX, float scaleY) {
		// TODO Auto-generated constructor stub
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>(){

			@Override
			public TouchEvent createObject() {
				// TODO Auto-generated method stub
				return new TouchEvent();
			}
			
		};
		touchEventPool = new Pool<TouchEvent>(factory, 100);
		v.setOnTouchListener(this);
		
		this.scaleX=scaleX;
		this.scaleY=scaleY;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		synchronized(this){
			TouchEvent touchEvent = touchEventPool.newObject();
			switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				touchEvent.type = TouchEvent.TOUCH_DOWN;
				isTouched = true;
				break;
			case MotionEvent.ACTION_MOVE:
				touchEvent.type = TouchEvent.TOUCH_DRAGGED;
				isTouched = false;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				touchEvent.type = TouchEvent.TOUCH_UP;
				isTouched = false;
				break;
			}
			
			touchEvent.x = touchX = (int)(event.getX()*scaleX);
			touchEvent.y = touchY = (int)(event.getY()*scaleY);
			touchEventsBuffer.add(touchEvent);
			
			return true;
			}
	}

	@Override
	public boolean isTouchDown(int pointer) {
		// TODO Auto-generated method stub
		synchronized(this){
			if(pointer==0){
				return isTouched;
			}
			else{
				return false;
			}
		}
	}

	@Override
	public int getTouchX(int pointer) {
		// TODO Auto-generated method stub
		synchronized (this){
			return touchX;
		}
	}

	@Override
	public int getTouchY(int pointer) {
		// TODO Auto-generated method stub
		synchronized(this){
			return touchY;
		}
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		// TODO Auto-generated method stub
		synchronized(this){
			int len = touchEvents.size();
			for(int i=0;i<len;i++){
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}

}
