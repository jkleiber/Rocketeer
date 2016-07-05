package com.justinkleiber.focus2d.base;

import android.graphics.Paint;

public interface Graphics {

	public static enum ImageFormat{
		ARGB8888, ARGB4444, RGB565
	}
	
	void drawString(String txt, int x, int y, Paint p);
	
	public void clearScreen(int color);
	
	public void drawLine(int x, int y, int x2, int y2, int color);
	
	public void drawRect(int x, int y, int width, int height, int color);
	
	public int getWidth();
	
	public int getHeight();
	
	public void drawARGB(int a, int r, int g, int b);
	
	public Sprite newSprite(String file);

    //BACKDROP IS UNTESTED
	public Backdrop newBackdrop(String file);
    public void drawBackdrop(Backdrop b);

    public SpriteSheet newSpriteSheet(String file, int rows, int cols);

    /*
    * STATIC ROTATION OBJECTS
    * These objects do not rotate, their 'draw points' are at the top left
    * DO NOT use DYNAMIC ROTATE FUNCTIONS with these or unexpected results will occur
    * Example: The DYNAMIC ROTATE Collision function will not work with these
     */
	public void drawSprite(Sprite s, int x, int y);
    public void drawSprite(Sprite s, Position p);
    public void drawSprite(Sprite s);

    public void drawSpriteSheet(SpriteSheet ss, int x, int y, int row, int col);
    public void drawSpriteSheet(SpriteSheet ss, int row, int col);

    public void animateSheetColumn(SpriteSheet ss, int x, int y, int col);
    public void animateSheetColumn(SpriteSheet ss, int x, int y, int col, int delay);

    public void animateSheetRow(SpriteSheet ss, int x, int y, int row, int delay);
    public void animateSheetRow(SpriteSheet ss, int row, int delay);
    public void animateSheetRow(SpriteSheet ss, int x, int y, int row);

    public void animateSpriteToPosition(Sprite s, Position p, int pace);
    public void animateSpriteSheetToPosition(SpriteSheet s, int row, int col, Position p, float pace);



    /*
    * DYNAMIC ROTATION OBJECTS
    * These objects rotate, their 'draw points' are at the center
    * DO NOT use STATIC ROTATE FUNCTIONS with these or unexpected results will occur
    * Example: The STATIC ROTATE Collision function will pick up extra collisions if these are rotated
     */
	public void drawSprite(Sprite s, Position p, float angle);
	public void drawSprite(Sprite s, float angle);
    public void drawSprite(Sprite s, int x, int y, float angle);

    public void animateSheetRow(SpriteSheet ss, int row, int delay, float angle);
    public void animateSheetRow(SpriteSheet ss, float x, float y, int row, int delay, float angle);
    public void animateSheetRow(SpriteSheet ss, float x, float y, int row, int delay, int delayIndex, float angle);
    public void animateSheetByIndex(SpriteSheet ss, int start, int end, int delay, boolean shouldStart, float angle);

    public void animateSpriteSheetToCoordinates(SpriteSheet s, int row, int col, int cx, int cy, int x, int y, float pace, float angle);
    public float animatedXSpeed(int cx, int x, float pace);
    public float animatedYSpeed(int cy,int y, float pace);

    public void drawHitBox(int x,int y,int w,int h,int tx,int ty,int tw,int th,float angle, Paint paint);

}
