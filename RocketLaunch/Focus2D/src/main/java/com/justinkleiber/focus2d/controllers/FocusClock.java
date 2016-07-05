package com.justinkleiber.focus2d.controllers;
import java.util.concurrent.TimeUnit;

import com.justinkleiber.focus2d.base.*;

public class FocusClock implements Clock{

	float eTime = 0, oTime = 0,cTime = 0;
	boolean paused;
	@Override
	public void resetClock() {
		// TODO Auto-generated method stub
		cTime=System.nanoTime();
		oTime=cTime;
		eTime=0;
		eTime+=cTime-oTime;
	}

	@Override
	public void startClock() {
		// TODO Auto-generated method stub
		cTime=System.nanoTime();
		oTime=cTime;
		eTime=0;
		eTime+=cTime-oTime;
	}

	@Override
	public long secondsElapsed() {
		// TODO Auto-generated method stub
		if(paused)
		{
			cTime=System.nanoTime();
			oTime=cTime;
			eTime+=0;
		}
		else{
			cTime = System.nanoTime();
			eTime += cTime - oTime;
			oTime = cTime;
		}
		return TimeUnit.SECONDS.convert((long) eTime, TimeUnit.NANOSECONDS);
	}

	@Override
	public void pauseClock() {
		eTime+=0;
		cTime=System.nanoTime();
		oTime=cTime;
		paused = true;
	}

	@Override
	public void resumeClock() {
		paused = false;
	}

}
