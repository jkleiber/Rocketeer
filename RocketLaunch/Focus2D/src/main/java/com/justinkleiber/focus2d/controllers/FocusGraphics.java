package com.justinkleiber.focus2d.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.Log;

import com.justinkleiber.focus2d.base.Backdrop;
import com.justinkleiber.focus2d.base.Graphics;
import com.justinkleiber.focus2d.base.Position;
import com.justinkleiber.focus2d.base.Sprite;
import com.justinkleiber.focus2d.base.SpriteSheet;


public class FocusGraphics implements Graphics{

	AssetManager assets;
	Canvas canvas;
	Paint paint;
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();
	Bitmap frame;
	
	int last_col=0, last_row=0, del_var=0;

	int current_row=1,current_col=1;

    int[] del_arr = new int[1000];
	
	public FocusGraphics(AssetManager assets, Bitmap frame)
	{
		this.assets=assets;
		this.frame=frame;
		this.canvas=new Canvas(frame);
		this.paint=new Paint();
	}
	
	@Override
	public void drawString(String txt, int x, int y, Paint p) {
		// TODO Auto-generated method stub
		canvas.drawText(txt, x, y, p);
	}

	@Override
	public void clearScreen(int color) {
		// TODO Auto-generated method stub
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
	}

	@Override
	public void drawLine(int x, int y, int x2, int y2, int color) {
		// TODO Auto-generated method stub
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		// TODO Auto-generated method stub
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return frame.getWidth();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return frame.getHeight();
	}

	@Override
	public void drawARGB(int a, int r, int g, int b) {
		// TODO Auto-generated method stub
		paint.setStyle(Style.FILL);
		canvas.drawARGB(a, r, g, b);
	}

	@Override
	public Sprite newSprite(String file) {
		// TODO Auto-generated method stub
		Bitmap b = null;
		InputStream istr = null;
		
		Config cfg = Config.RGB_565;
		
		Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = cfg;
		
		try {
	        istr = assets.open(file);
	        b = BitmapFactory.decodeStream(istr);
	        
	        if(b==null)
			{
				throw new RuntimeException("Sprite Failed to load: " + file);
			}
	        
	    } catch (IOException e) {
            Log.d("LOAD FAILURE: ", "Sprite Failed to load: " + file);
	    	throw new RuntimeException("Sprite Failed to load: " + file);

	    }finally{
			if(istr!=null){
				try{
					istr.close();
					
				}catch(IOException e){
					
				}
			}
	    }
		ImageFormat format;
		
		format = ImageFormat.RGB565;
		

		return new FocusSprite(b,format);
	}

	@Override
	public Backdrop newBackdrop(String file) {
		// TODO Auto-generated method stub
		Bitmap b = null;
		InputStream istr = null;
		
		Config cfg = Config.RGB_565;
		
		Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = cfg;
		
		try {
	        istr = assets.open(file);
	        b = BitmapFactory.decodeStream(istr);
	        
	        if(b==null)
			{
				throw new RuntimeException("Backdrop Failed to load: " + file);
			}
	        
	    } catch (IOException e) {
	    	throw new RuntimeException("Backdrop Failed to load: " + file);
	    	//Log.d("LOAD FAILURE: ", "SpriteSheet Failed to load: " + file);
	    }finally{
			if(istr!=null){
				try{
					istr.close();
					
				}catch(IOException e){
					
				}
			}
	    }
		ImageFormat format;
		
		format = ImageFormat.RGB565;
		return new FocusBackdrop(b, format);
	}

	@Override
	public void drawSprite(Sprite s, int x, int y) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(((FocusSprite) s).bitmap, x, y, null);
	}

	@Override
	public void drawBackdrop(Backdrop b) {
		// TODO Auto-generated method stub
		int x=0, y=0;
		int sh = getHeight();
		int sw = getWidth();
		int bh = b.getHeight();
		int bw = b.getWidth();
		Rect src= new Rect(x, y, x+bw, y+bh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		canvas.drawBitmap(((FocusBackdrop) b).bitmap, src, dst, null);
	}

	@Override
	public SpriteSheet newSpriteSheet(String file, int rows, int cols) {
		// TODO Auto-generated method stub
		Bitmap b = null;
		InputStream istr = null;
		
		Config cfg = Config.RGB_565;
		
		Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = cfg;
		
	    try {
	        istr = assets.open(file);
	        b = BitmapFactory.decodeStream(istr);
	        
	        if(b==null)
			{
				throw new RuntimeException("SpriteSheet Failed to load: " + file);
			}
	        
	    } catch (IOException e) {
	    	throw new RuntimeException("SpriteSheet Failed to load: " + file);
	    	//Log.d("LOAD FAILURE: ", "SpriteSheet Failed to load: " + file);
	    }finally{
			if(istr!=null){
				try{
					istr.close();
					
				}catch(IOException e){
					
				}
			}
	    }
	    
	    ImageFormat format;
		
		format = ImageFormat.RGB565;
		return new FocusSpriteSheet(b,rows,cols,format);
	}

	@Override
	public void drawSpriteSheet(SpriteSheet ss, int x, int y, int row, int col) {
		// TODO Auto-generated method stub
		
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		int ysrc = row*sh;
		int xsrc = col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
	}

	@Override
	public void drawSprite(Sprite s, int x, int y, float angle) {
		// TODO Auto-generated method stub
		Bitmap b = ((FocusSprite) s).bitmap;
		Matrix m = new Matrix();
		m.postTranslate(-b.getWidth()/2, -b.getHeight()/2);
		m.postRotate(angle);
		m.postTranslate(x, y);
		canvas.drawBitmap(b, m, null);
	}

	@Override
	public void drawSprite(Sprite s, Position p) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(((FocusSprite) s).bitmap, p.x, p.y, null);
		
		s.setPosition(p.x, p.y);
	}

	@Override
	public void drawSprite(Sprite s, Position p, float angle) {
		// TODO Auto-generated method stub
		Bitmap b = ((FocusSprite) s).bitmap;
		Matrix m = new Matrix();
		m.postTranslate(-b.getWidth()/2, -b.getHeight()/2);
		m.postRotate(angle);
		m.postTranslate(p.x, p.y);
		canvas.drawBitmap(b, m, null);
		s.setPosition(p.x, p.y);
	}

	@Override
	public void drawSprite(Sprite s) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(((FocusSprite) s).bitmap, s.getPosition().x, s.getPosition().y, null);
	}

	@Override
	public void animateSheetRow(SpriteSheet ss, int x, int y, int row) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sheetw= ss.getSheetWidth();
		
		if(last_col >= sheetw/sw)
		{
			last_col = 0;
		}
		
		int ysrc = row*sh;
		int xsrc = last_col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
		
		last_col++;
	}

	@Override
	public void animateSheetColumn(SpriteSheet ss, int x, int y, int col) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sheeth= ss.getSheetHeight();
		
		if(last_row >= sheeth/sh)
		{
			last_row = 0;
		}
		
		int ysrc = last_row*sh;
		int xsrc = col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
		
		last_row++;
	}

	@Override
	public void drawSprite(Sprite s, float angle) {
		// TODO Auto-generated method stub
		Bitmap b = ((FocusSprite) s).bitmap;
		Position _p = ((FocusSprite) s).p;
		Matrix m = new Matrix();
		m.postTranslate(-b.getWidth()/2, -b.getHeight()/2);
		m.postRotate(angle);
		m.postTranslate(_p.x, _p.y);
		canvas.drawBitmap(b, m, null);
	}

	@Override
	public void animateSheetRow(SpriteSheet ss, int x, int y, int row, int delay) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sheetw= ss.getSheetWidth();
		
		if(last_col >= sheetw/sw)
		{
			last_col = 0;
		}
		
		int ysrc = row*sh;
		int xsrc = last_col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
		
		if(del_var%delay==0)
		{
			last_col++;
		}
		
		del_var++;
	}

	@Override
	public void animateSheetColumn(SpriteSheet ss, int x, int y, int col,
			int delay) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sheeth= ss.getSheetHeight();
		
		if(last_row >= sheeth/sh)
		{
			last_row = 0;
		}
		
		int ysrc = last_row*sh;
		int xsrc = col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(x, y, x+sw, y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
		
		if(del_var%delay==0)
		{
			last_row++;
		}
		
		del_var++;
	}

	@Override
	public void animateSpriteToPosition(Sprite s, Position p, int pace) {
		// TODO Auto-generated method stub
		int _x = s.getPosition().x;
		int _y = s.getPosition().y;
		
		int pa = Math.abs(pace);
		
		int x_err, y_err;
		
		x_err = p.x - _x;
		y_err = p.y - _y;
		
		x_err *= pace;
		y_err *= pace;
		
		_x+=x_err;
		_y+=y_err;
		
		s.setPosition(_x, _y);
		
		canvas.drawBitmap(((FocusSprite) s).bitmap, s.getPosition().x, s.getPosition().y, null);
	}


	@Override
	public void animateSpriteSheetToPosition(SpriteSheet s, int row, int col,
			Position p, float pace) {
		// TODO Auto-generated method stub
		
		int _x = s.getPosition().x;
		int _y = s.getPosition().y;
		
		float x_err;
		float y_err;
		
		x_err = p.x - _x;
		y_err = p.y - _y;
		
		x_err = x_err*pace;
		y_err = y_err*pace;
		
		_x+=Math.round(x_err);
		_y+=Math.round(y_err);
		
		s.setPosition(_x, _y);
		
		int sh = s.getSpriteHeight();
		int sw = s.getSpriteWidth();
		int ysrc = row*sh;
		int xsrc = col*sw;
		
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(_x, _y, _x+sw, _y+sh);
		
		canvas.drawBitmap(s.getBitmap(), src, dst, null);
		
	}



	@Override
	public void drawSpriteSheet(SpriteSheet ss, int row, int col) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		int ysrc = row*sh;
		int xsrc = col*sw;
		
		int _x = ss.getPosition().x;
		int _y = ss.getPosition().y;
		
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(_x, _y, _x+sw, _y+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
	}

	@Override
	public void animateSheetRow(SpriteSheet ss, int row, int delay) {
		// TODO Auto-generated method stub
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sx = ss.getPosition().x;
		int sy = ss.getPosition().y;
		
		int sheetw= ss.getSheetWidth();
		
		if(last_col >= sheetw/sw)
		{
			last_col = 0;
		}
		
		int ysrc = row*sh;
		int xsrc = last_col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(sx, sy, sx+sw, sy+sh);
		
		canvas.drawBitmap(ss.getBitmap(), src, dst, null);
		
		if(del_var%delay==0)
		{
			last_col++;
		}
		
		del_var++;
	}

	@Override
	public void animateSheetRow(SpriteSheet ss, int row, int delay, float angle) {
		// TODO Auto-generated method stub
		
		
		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();
		
		int sx = ss.getPosition().x;
		int sy = ss.getPosition().y;
		
		int sheetw= ss.getSheetWidth();
		
		if(last_col >= sheetw/sw)
		{
			last_col = 0;
		}
		
		int ysrc = row*sh;
		int xsrc = last_col*sw;
		Rect src=new Rect(xsrc,ysrc,xsrc+sw,ysrc+sh);
		Rect dst= new Rect(sx, sy, sx+sw, sy+sh);
		
		Bitmap b = Bitmap.createBitmap(ss.getBitmap(), xsrc, ysrc, sw, sh);

		Matrix m = new Matrix();
		m.postTranslate(-sw / 2, -sh / 2);
		m.postRotate(angle);
		m.postTranslate(sx, sy);
		
		canvas.drawBitmap(b, m, null);
		
		if(del_var%delay==0)
		{
			last_col++;
		}
		
		del_var++;
	}

    @Override
    public void animateSheetRow(SpriteSheet ss, float x, float y, int row, int delay, float angle) {


        int sh = ss.getSpriteHeight();
        int sw = ss.getSpriteWidth();

        float sx = x;
        float sy = y;

        int sheetw= ss.getSheetWidth();

        if(last_col >= sheetw/sw)
        {
            last_col = 0;
        }

        int ysrc = row*sh;
        int xsrc = last_col*sw;

        Bitmap b = Bitmap.createBitmap(ss.getBitmap(), xsrc, ysrc, sw, sh);

        Matrix m = new Matrix();
        m.postTranslate(-sw / 2, -sh / 2);
        m.postRotate(angle);
        m.postTranslate(sx, sy);

        canvas.drawBitmap(b, m, null);

        if(del_var%delay==0)
        {
            last_col++;
        }

        del_var++;
    }

    @Override
    public void animateSheetRow(SpriteSheet ss, float x, float y, int row, int delay, int delayIndex, float angle) {


        int sh = ss.getSpriteHeight();
        int sw = ss.getSpriteWidth();

        float sx = x;
        float sy = y;

        int sheetw= ss.getSheetWidth();

        if(last_col >= sheetw/sw)
        {
            last_col = 0;
        }

        int ysrc = row*sh;
        int xsrc = last_col*sw;

        Bitmap b = Bitmap.createBitmap(ss.getBitmap(), xsrc, ysrc, sw, sh);

        Matrix m = new Matrix();
        m.postTranslate(-sw / 2, -sh / 2);
        m.postRotate(angle);
        m.postTranslate(sx, sy);

        canvas.drawBitmap(b, m, null);

        del_arr[delayIndex]++;

        if(del_arr[delayIndex]%delay==0)
        {
            last_col++;
        }

    }

    int rx=0, cy=0;
	boolean shouldStart;

	@Override
	public void animateSheetByIndex(SpriteSheet ss, int start, int end, int delay, boolean startbool, float angle) {
		shouldStart=startbool;

		int sh = ss.getSpriteHeight();
		int sw = ss.getSpriteWidth();

		int rows = ss.getRows();
		int cols = ss.getCols();

		if(shouldStart) {
			for (int i = 1; i <= rows; i++) {
				for (int ii = 1; ii <= cols; ii++) {
					if ((ii +((i-1)*cols)) == start) {
						current_row = i;
						current_col = ii;
						shouldStart=false;
						break;
					}
				}
			}
		}
/*
		if(current_col+((current_row-1)*cols)==end)
		{
			//shouldStart=true;
		}*/

		int ysrc = (current_row-1)*sh;
		int xsrc = (current_col-1)*sw;

		int sx = ss.getPosition().x;
		int sy = ss.getPosition().y;

		Bitmap b = Bitmap.createBitmap(ss.getBitmap(), xsrc, ysrc, sw, sh);

		Matrix m = new Matrix();
		m.postTranslate(-sw/2, -sh/2);
        m.postRotate(angle);
		m.postTranslate(sx, sy);


		canvas.drawBitmap(b, m, null);

		if(del_var%delay==0)
		{
			if(current_col<cols) {
				current_col++;
			}
			else if (current_col>=cols && current_row<rows)
			{
				current_col=1;
				current_row++;
			}
		}

		del_var++;
	}

    @Override
    public void animateSpriteSheetToCoordinates(SpriteSheet s, int row, int col,int cx, int cy, int x, int y, float pace, float angle) {

        float _x=cx, _y=cy;
        float x_err;
        float y_err;

        x_err = x - cx;
        y_err = y - cy;

        x_err = x_err*pace;
        y_err = y_err*pace;

        _x+=x_err;
        _y+=y_err;

        int sh = s.getSpriteHeight();
        int sw = s.getSpriteWidth();
        int ysrc = row*sh;
        int xsrc = col*sw;

        Bitmap b = Bitmap.createBitmap(s.getBitmap(), xsrc, ysrc, sw, sh);

        Matrix m = new Matrix();
        m.postTranslate(-sw/2, -sh/2);
        m.postRotate(angle);
        m.postTranslate(_x, _y);

        s.setPosition((int)_x,(int)_y);

        canvas.drawBitmap(b, m, null);

    }

    @Override
    public float animatedXSpeed(int cx, int x, float pace) {
        float err = x - cx;
        err = err * pace;

        return err;
    }

    @Override
    public float animatedYSpeed(int cy, int y, float pace) {
        float err = y - cy;
        err = err * pace;

        return err;
    }

    @Override
	public void drawHitBox(int sx, int sy, int sw, int sh, int tx, int ty, int tw, int th, float s_angle, Paint paint) {

			double s_a, c_a;
			s_a = Math.sin(-s_angle*Math.PI/180);
			c_a = Math.cos(-s_angle * Math.PI / 180);

			double ax=0, ay=0;
			double bx=0, by=0;
			double cx=0, cy=0;
			double dx=0, dy=0;

			ax = sx + (((sx-(sw/2))-sx)*c_a) + ((sy-(sh/2))-sy)*s_a;
			ay = sy - (((sx-(sw/2))-sx)*s_a) + ((sy-(sh/2))-sy)*c_a;

			bx = sx + (((sx+(sw/2))-sx)*c_a) + ((sy-(sh/2))-sy)*s_a;
			by = sy - (((sx+(sw/2))-sx)*s_a) + ((sy-(sh/2))-sy)*c_a;

			cx = sx + (((sx+(sw/2))-sx)*c_a) + ((sy+(sh/2))-sy)*s_a;
			cy = sy - (((sx+(sw/2))-sx)*s_a) + ((sy+(sh/2))-sy)*c_a;

			dx = sx + (((sx-(sw/2))-sx)*c_a) + ((sy+(sh/2))-sy)*s_a;
			dy = sy - (((sx-(sw/2))-sx)*s_a) + ((sy+(sh/2))-sy)*c_a;

			canvas.drawLine((int) ax, (int) ay, (int) bx, (int) by, paint);
			canvas.drawLine((int) bx, (int) by, (int) cx, (int) cy, paint);
			canvas.drawLine((int) cx, (int) cy, (int) dx, (int) dy, paint);
			canvas.drawLine((int) dx, (int) dy, (int) ax, (int) ay, paint);
	}

}
