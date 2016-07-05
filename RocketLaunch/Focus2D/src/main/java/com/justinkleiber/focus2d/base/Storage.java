package com.justinkleiber.focus2d.base;

import java.util.ArrayList;

public interface Storage {

	public void save(String file, String value);
	public void save(String file, ArrayList<String> value);
	
	public ArrayList<String> loadToArray(String file);
	public String loadToString(String file);
	
	public void setPref(String key, String str);
	public void setPref(String key, int i);
	public void setPref(String key, boolean b);
	public void setFloatPref(String key, float f);
	
	public String getStringPref(String key, String def);
	public int getIntPref(String key, int def);
	public boolean getBoolPref(String key, boolean def);
	public float getFloatPref(String key, float f);
	
	public boolean isExist(String file);
}
