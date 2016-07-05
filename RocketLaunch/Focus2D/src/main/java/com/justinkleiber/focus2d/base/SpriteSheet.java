package com.justinkleiber.focus2d.base;

import android.graphics.Bitmap;

public interface SpriteSheet {

	public int getSheetWidth();
	
	public int getSheetHeight();
	
	public int getSpriteHeight();
	
	public int getSpriteWidth();
	
	public int getRows();
	
	public int getCols();
	
	public void dispose();
	
	public Bitmap getBitmap();
	
	public Position getPosition();
	
	public void setPosition(int x, int y);
	
	public void setPosition(Position p);
}
