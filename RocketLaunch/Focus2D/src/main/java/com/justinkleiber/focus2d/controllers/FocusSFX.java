package com.justinkleiber.focus2d.controllers;

import android.media.SoundPool;

import com.justinkleiber.focus2d.base.SFX;

public class FocusSFX implements SFX {

	int soundId;
	SoundPool soundPool;
	
	public FocusSFX(SoundPool sp, int id)
	{
		this.soundId=id;
		this.soundPool=sp;
	}
	@Override
	public void play(float volume) {
		// TODO Auto-generated method stub
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		soundPool.unload(soundId);
	}

}
