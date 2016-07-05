package com.justinkleiber.focus2d.controllers;

import android.graphics.Bitmap;

import com.justinkleiber.focus2d.base.Graphics.ImageFormat;
import com.justinkleiber.focus2d.base.Position;
import com.justinkleiber.focus2d.base.Sprite;
import com.justinkleiber.focus2d.base.SpriteStats;

public class FocusSprite implements Sprite {

	Bitmap bitmap;
	ImageFormat format;
	Position p;
	SpriteStats ss;
	
	public FocusSprite(Bitmap b, ImageFormat format) {
		// TODO Auto-generated constructor stub
		this.bitmap=b;
		this.format=format;
		this.p = new Position(0,0);
		this.ss = new SpriteStats();
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return bitmap.getWidth();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return bitmap.getHeight();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		bitmap.recycle();
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

	@Override
	public void setSpriteStats(SpriteStats ss) {
		// TODO Auto-generated method stub
		this.ss=ss;
	}

	@Override
	public SpriteStats getSpriteStats() {
		// TODO Auto-generated method stub
		return ss;
	}


}
