package com.justinkleiber.focus2d.base;

public interface Collision {

    // vvv DONT USE vv
	public boolean isCollisionExist(Sprite s, Sprite t);
	
	public boolean isCollisionExist(SpriteSheet ss, SpriteSheet tt);
	
	public boolean isCollisionExist(Sprite s, SpriteSheet ss);
    // ^^ DONT USE ^^

    //STATIC ROTATE
	public boolean isCollisionExist(int sx,int sy,int sw,int sh,int tx,int ty,int tw,int th, boolean onCenter);

    //DYNAMIC ROTATE
	public boolean isCollisionExist(int sx,int sy,int sw,int sh,int tx,int ty,int tw,int th, float angle);
	
	public boolean isInPosition(Sprite s, Position p);
	
	public boolean isInPosition(Sprite s, Position p, int tolerance);
	
	public boolean isInPosition(SpriteSheet ss, Position p);
	
	public boolean isInPosition(SpriteSheet ss, Position p, int tolerance);
	
}
