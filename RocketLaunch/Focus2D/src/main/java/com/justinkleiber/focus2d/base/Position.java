package com.justinkleiber.focus2d.base;

public class Position {

	public int x;
	public int y;
	
	
	public Position(int _x, int _y)
	{
		this.x = _x;
		this.y = _y;
	}
	
	public Position getPosition()
	{
		return this;
	}
	
	public void setPosition(int _x, int _y)
	{
		this.x = _x;
		this.y = _y;
	}
	
	public void setPosition(Position _p)
	{
		this.x = _p.x;
		this.y = _p.y;
	}
}
