package com.justinkleiber.rocketlaunch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.TimeUnit;

public class MenuRenderer extends SurfaceView implements Runnable{

	//Foundation foundation;
	MenuLoop menuLoop;
	Bitmap frame;
	Thread renderThread=null;
	SurfaceHolder holder;
	volatile boolean running=false;

	long startTime;

	int TICKS_PER_SECOND = 25;
    int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    int MAX_FRAMESKIP = 5;
    int loops;
    float interpolation;
    int next_game_tick;
	/*
	public MainRenderer(Context c) {
		super(c);
		gameLoop = new GameLoop();
		frame=Bitmap.createBitmap(800, 1280, Bitmap.Config.RGB_565);
		this.holder=getHolder();
	}

	public MainRenderer(Context c, AttributeSet attr)
	{
		super(c,attr);
		//gameLoop = new GameLoop();
		frame=Bitmap.createBitmap(800, 1280, Bitmap.Config.RGB_565);
		this.holder=getHolder();
	}*/
	public MenuRenderer(MenuLoop m, Bitmap frame)
	{
		super(m);
		this.menuLoop=m;
		this.frame=frame;
		this.holder=getHolder();
	}
	/*
	public void setActivity(GameLoop g)
	{
		this.gameLoop=g;
		frame=Bitmap.createBitmap(800, 1280, Bitmap.Config.RGB_565);
		this.holder=getHolder();
	}*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Rect dstRect = new Rect();
		startTime = System.nanoTime();
		next_game_tick = 0;

		while(running){

			if(!holder.getSurface().isValid()){
				continue;
			}

			//time in ms since game start
			float deltaTime = TimeUnit.MILLISECONDS.convert(System.nanoTime()-startTime, TimeUnit.NANOSECONDS);

			loops = 0;

	        while( deltaTime > next_game_tick && loops < MAX_FRAMESKIP) {
	        	menuLoop.update();
	            next_game_tick += SKIP_TICKS;
	            loops++;
	        }

	        interpolation = ((float) deltaTime + SKIP_TICKS - next_game_tick)/ (float) SKIP_TICKS;

			menuLoop.paint(interpolation);
	        Canvas canvas = holder.lockCanvas();
			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(frame, null, dstRect, null);
			holder.unlockCanvasAndPost(canvas);
		}
	}

	public void pause(){
		running = false;
		while(true){
			try{
				renderThread.join();
				break;
			}catch(InterruptedException e){
				//retry
			}
		}
	}
	
	public void resume() {
		running=true;
		renderThread=new Thread(this);
		renderThread.start();
	}

}
