package com.justinkleiber.focus2d.base;

public interface Sprite {

	public int getWidth();
	
	public int getHeight();
	
	public Position getPosition();
	
	public void setPosition(int x, int y);
	
	public void setPosition(Position p);
	
	public void dispose();
	
	public void setSpriteStats(SpriteStats ss);
	
	public SpriteStats getSpriteStats();
	
}
