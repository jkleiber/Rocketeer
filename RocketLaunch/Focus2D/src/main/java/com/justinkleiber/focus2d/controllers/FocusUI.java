package com.justinkleiber.focus2d.controllers;

import com.justinkleiber.focus2d.base.*;
import com.justinkleiber.focus2d.base.Input.TouchEvent;

public class FocusUI implements UI{

	@Override
	public boolean inBounds(TouchEvent event, int x, int y, int wid, int hi) {
		// TODO Auto-generated method stub
		if (event.x > x && event.x < x + wid - 1 && event.y > y && event.y < y + hi - 1)
		{
			return true;
		}
	    else
	    {
	    	return false;
	    }
	}

	@Override
	public boolean isButtonPress(TouchEvent event, Sprite b) {
		// TODO Auto-generated method stub
		if (event.x > b.getPosition().x && event.x < b.getPosition().x + b.getWidth() - 1 && event.y > b.getPosition().y && event.y < b.getPosition().y + b.getHeight() - 1)
		{
			return true;
		}
	    else
	    {
	    	return false;
	    }
	}

}
