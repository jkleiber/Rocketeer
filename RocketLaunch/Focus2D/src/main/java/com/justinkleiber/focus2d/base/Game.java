package com.justinkleiber.focus2d.base;

public interface Game {

	public Audio getAudio();
	
	public Input getInput();
	
	public Storage getStorage();
	
	public Graphics getGraphics();
	
	public void setScreen(Screen screen);
	
	public Screen getCurrentScreen();
	
	public Screen getInitScreen();
	
	public Vibrate getVibrate();
}
