package com.justinkleiber.focus2d.base;

public interface Clock {

	public void resetClock();
	
	public void startClock();
	
	public long secondsElapsed();

	public void pauseClock();

	public void resumeClock();
	
	
}
