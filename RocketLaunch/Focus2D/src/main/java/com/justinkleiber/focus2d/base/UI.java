package com.justinkleiber.focus2d.base;

import com.justinkleiber.focus2d.base.Input.TouchEvent;

public interface UI {

	public boolean inBounds(TouchEvent event, int x, int y, int wid, int hi);
	
	public boolean isButtonPress(TouchEvent event, Sprite b);
	
}
