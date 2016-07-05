package com.justinkleiber.focus2d.controllers;

import android.graphics.Bitmap;

import com.justinkleiber.focus2d.base.Graphics.ImageFormat;
import com.justinkleiber.focus2d.base.Position;
import com.justinkleiber.focus2d.base.SpriteSheet;

public class FocusSpriteSheet implements SpriteSheet {

	Bitmap bitmap;
	int rows, cols;
	ImageFormat format;
	Position p;
	
	FocusSpriteSheet(Bitmap b, int rows, int cols, ImageFormat format)
	{
		this.bitmap=b;
		this.rows = rows;
		this.cols = cols;
		this.format=format;
		this.p = new Position(0,0);
	}

	@Override
	public int getRows() {
		// TODO Auto-generated method stub
		return rows;
	}

	@Override
	public int getCols() {
		// TODO Auto-generated method stub
		return cols;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		bitmap.recycle();
	}

	@Override
	public int getSheetWidth() {
		// TODO Auto-generated method stub
		return bitmap.getWidth();
	}

	@Override
	public int getSheetHeight() {
		// TODO Auto-generated method stub
		return bitmap.getHeight();
	}

	@Override
	public Bitmap getBitmap() {
		// TODO Auto-generated method stub
		return bitmap;
	}

	@Override
	public int getSpriteHeight() {
		// TODO Auto-generated method stub
		return bitmap.getHeight()/rows;
	}

	@Override
	public int getSpriteWidth() {
		// TODO Auto-generated method stub
		return bitmap.getWidth()/cols;
	}
	
	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return p;
	}


	@Override
	public void setPosition(int x, int y) {
		// TODO Auto-generated method stub
		p.setPosition(x, y);
	}
	
	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub
		this.p = p;
	}

}
