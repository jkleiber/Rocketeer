package com.justinkleiber.focus2d.base;

import android.app.Activity;
import android.os.Bundle;

public abstract class Screen extends Activity{

	//protected final Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public abstract void update();
	
	public abstract void paint(float interpolation);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
	
	public abstract void backButton();
	
	public abstract void menuButton();


}
