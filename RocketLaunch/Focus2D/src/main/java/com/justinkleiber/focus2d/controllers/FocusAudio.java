package com.justinkleiber.focus2d.controllers;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.justinkleiber.focus2d.base.Audio;
import com.justinkleiber.focus2d.base.Music;
import com.justinkleiber.focus2d.base.SFX;

public class FocusAudio implements Audio{

	AssetManager assets;
	SoundPool soundPool;
	
	public FocusAudio(Activity act)
	{
		act.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = act.getAssets();
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}
	
	@Override
	public Music createMusic(String file) {
		// TODO Auto-generated method stub
		try{
			AssetFileDescriptor assetDescriptor = assets.openFd(file);
			return new FocusMusic(assetDescriptor);
		}catch(IOException e){
			throw new RuntimeException("Couldn't load music '" + file + "'");
		}
	}

	@Override
	public SFX createSFX(String file) {
		// TODO Auto-generated method stub
		try{
			AssetFileDescriptor assetDescriptor = assets.openFd(file);
			int soundId = soundPool.load(assetDescriptor,0);
			return new FocusSFX(soundPool, soundId);
		}catch(IOException e){
			throw new RuntimeException("Couldn't load music '" + file + "'");
		}
	}

}
