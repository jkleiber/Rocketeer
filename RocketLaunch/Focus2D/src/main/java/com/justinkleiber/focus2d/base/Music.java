package com.justinkleiber.focus2d.base;

public interface Music {

	public void play();
	
	public void stop();
	
	public void pause();
	
	public void setLoop(boolean loop);
	
	public void setVolume(float volume);
	
	public boolean isPlaying();
	
	public boolean isStopped();
	
	public boolean isLooping();
	
	public void dispose();
	
	public void restart();
}
