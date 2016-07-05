package com.justinkleiber.focus2d.controllers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.justinkleiber.focus2d.base.Storage;

public class FileManager implements Storage{
	
	Context context;
	SharedPreferences prefs;
	Editor editor;
	
	public FileManager(Context c)
	{
		this.context=c;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
	}
	@Override
	public void save(String file, String value) {
		// TODO Auto-generated method stub
		try
		{
			FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> loadToArray(String file) {
		// TODO Auto-generated method stub
		try
	    {
			FileInputStream fis = context.openFileInput(file);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	        String line = null;
	        ArrayList<String> input = new ArrayList<String>();
	        while ((line = reader.readLine()) != null)
	        {
	            input.add(line);
	        }
	        reader.close();
	       
	        //toast("File successfully loaded.");
	        return input;
	    }
	    catch (Exception ex)
	    {
	        //toast("Error loading file: " + ex.getLocalizedMessage());
	        return null;
	    }
	}

	@Override
	public String loadToString(String file) {
		// TODO Auto-generated method stub
		
		try
	    {
	        FileInputStream fis = context.openFileInput(file);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	        String line = null, input="";
	        while ((line = reader.readLine()) != null)
	            input += line;
	        reader.close();
	        fis.close();
	        //toast("File successfully loaded.");
	        return input;
	    }
	    catch (Exception ex)
	    {
	        //toast("Error loading file: " + ex.getLocalizedMessage());
	        return "";
	    }
		
		
	}

	@Override
	public void setPref(String key, String str) {
		// TODO Auto-generated method stub
		editor.putString(key, str);
		editor.commit();
	}
	@Override
	public void setPref(String key, int i) {
		// TODO Auto-generated method stub
		editor.putInt(key, i);
		editor.commit();
	}
	@Override
	public void setPref(String key, boolean b) {
		// TODO Auto-generated method stub
		editor.putBoolean(key, b);
		editor.commit();
	}

	@Override
	public void setFloatPref(String key, float f) {
		editor.putFloat(key, f);
	}

	@Override
	public String getStringPref(String key, String def) {
		// TODO Auto-generated method stub
		String str;
		str = prefs.getString(key, def);
		return str;
	}

	@Override
	public int getIntPref(String key, int def) {
		// TODO Auto-generated method stub
		int i;
		i = prefs.getInt(key, def);
		return i;
	}

	@Override
	public boolean getBoolPref(String key, boolean def) {
		// TODO Auto-generated method stub
		boolean bool;
		bool = prefs.getBoolean(key, def);
		return bool;
	}

	@Override
	public float getFloatPref(String key, float def) {
		float f;
		f=prefs.getFloat(key, def);
		return f;
	}

	@Override
	public boolean isExist(String file) {
		// TODO Auto-generated method stub
		try
	    {
	        FileInputStream fis = context.openFileInput(file);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	        String line = null, input="";
	        while ((line = reader.readLine()) != null)
	            input += line;
	        reader.close();
	        fis.close();
	        //toast("File successfully loaded.");
	        return true;
	    }
	    catch (Exception ex)
	    {
	        //toast("Error loading file: " + ex.getLocalizedMessage());
	        return false;
	    }
	}
	@Override
	public void save(String file, ArrayList<String> value) {
		// TODO Auto-generated method stub
		try
		{
			FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			String newline = System.getProperty("line.separator");
			for(String str : value)
			{
				fos.write(str.getBytes());
				fos.write(newline.getBytes());
			}
			
			fos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	

}
