package com.justinkleiber.focus2d.controllers;

import android.graphics.Bitmap;

import com.justinkleiber.focus2d.base.Backdrop;
import com.justinkleiber.focus2d.base.Graphics.ImageFormat;

public class FocusBackdrop implements Backdrop{

	Bitmap bitmap;
	ImageFormat format;
	
	FocusBackdrop(Bitmap b, ImageFormat format)
	{
		this.bitmap=b;
		this.format=format;
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

}
