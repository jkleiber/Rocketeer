package com.justinkleiber.rocketlaunch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.justinkleiber.focus2d.base.Audio;
import com.justinkleiber.focus2d.base.Game;
import com.justinkleiber.focus2d.base.Graphics;
import com.justinkleiber.focus2d.base.Graphics.ImageFormat;
import com.justinkleiber.focus2d.base.Input;
import com.justinkleiber.focus2d.base.Screen;
import com.justinkleiber.focus2d.base.Storage;
import com.justinkleiber.focus2d.base.Vibrate;
import com.justinkleiber.focus2d.controllers.FocusGraphics;

public class SplashLoad extends Activity{

Graphics g;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		int frameBufferWidth=800;
		int frameBufferHeight = 1280; //flip for landscape
		Bitmap frame=Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565);
		//attr = new AttributeSet();
		//render = new MainRenderer(this);
		g = new FocusGraphics(getAssets(), frame);

		Assets.asteroid = g.newSprite("asteroid.png");
		Assets.tiny_asteroid = g.newSprite("tiny_asteroid.png");

		Assets.rocket = g.newSpriteSheet("rocketSheet.png", 5, 4);
		Assets.triangle = g.newSpriteSheet("triangleSheet.png", 5, 4);
		Assets.orbiter = g.newSpriteSheet("orbiterSheet.png", 5, 4);

		Assets.stars = g.newSprite("stars.png");
		Assets.hyperdrive = g.newSprite("hyperdrive.png");

		Assets.play = g.newSprite("play.png");

		Assets.left = g.newSprite("left.png");
		Assets.right = g.newSprite("right.png");
		Assets.pause = g.newSprite("pause.png");

		Assets.pause_screen = g.newSprite("pauseScreen.png");
		Assets.crash_screen = g.newSprite("crashscreen.png");

		Assets.quit = g.newSprite("quit.png");
		Assets.quitt = g.newSprite("quit.png");
		Assets.resume = g.newSprite("resume.png");
		Assets.retry = g.newSprite("retry.png");

		Assets.stats = g.newSprite("stats.png");
		Assets.settings = g.newSprite("gears.png");
		Assets.game_service_in = g.newSprite("game_service_in.png");
		Assets.game_service_out = g.newSprite("game_service_out.png");
		Assets.store = g.newSprite("store.png");

		//Assets.singlerun_boards = g.newSprite("singlerun_boards.png");
		//Assets.totaldist_boards = g.newSprite("totaldist_boards.png");
		Assets.highavg_boards = g.newSprite("highavg_boards.png");
		//Assets.flight_boards = g.newSprite("flight_boards.png");
		Assets.achieve = g.newSprite("achieve.png");

		Assets.coin = g.newSpriteSheet("coin.png", 1, 8);

        Assets.coin_sel = g.newSprite("store/coin_select.png");
        Assets.coin_desel = g.newSprite("store/coin_not_select.png");
        Assets.rock_sel = g.newSprite("store/rocket_select.png");
        Assets.rock_desel = g.newSprite("store/rocket_not_select.png");
        Assets.boost_sel = g.newSprite("store/boost_select.png");
        Assets.boost_desel = g.newSprite("store/boost_not_select.png");

        Assets.store_tri = g.newSprite("store/triangle.png");
        Assets.store_orb = g.newSprite("store/orbiter.png");

        Assets.multiToggle = g.newSprite("multiplayer/multi_toggle.png");
        Assets.singleToggle = g.newSprite("multiplayer/single_toggle.png");

        Assets.qplay = g.newSprite("multiplayer/quick_play.png");
        Assets.fplay = g.newSprite("multiplayer/friend_play.png");
        Assets.tplay = g.newSprite("multiplayer/tourney_play.png");

        Assets.no_mail = g.newSprite("multiplayer/no_mail.png");
        Assets.mail = g.newSprite("multiplayer/yes_mail.png");

        Assets.invite_popup = g.newSprite("multiplayer/invited.png");
        Assets.accept = g.newSprite("multiplayer/accept.png");
        Assets.reject = g.newSprite("multiplayer/reject.png");

        Assets.first = g.newSprite("multiplayer/one.png");
        Assets.second = g.newSprite("multiplayer/two.png");
        Assets.third = g.newSprite("multiplayer/three.png");
        Assets.fourth = g.newSprite("multiplayer/four.png");

        Assets.rematch = g.newSprite("multiplayer/rematch.png");
        Assets.again = g.newSprite("multiplayer/play_again.png");

        Assets.continue_button = g.newSprite("multiplayer/continue.png");
        Assets.leave_button = g.newSprite("multiplayer/leave.png");

		Assets.shields = g.newSprite("store/shields.png");
		Assets.agility = g.newSprite("store/agility.png");
		//Assets.radiation = g.newSprite("store/radiation.png");
		Assets.store_coinzz = g.newSprite("store/coins.png");
		//Assets.store_laser = g.newSprite("store/laser.png");

		Assets.yes_button = g.newSprite("store/yes.png");
		Assets.no_button = g.newSprite("store/no.png");
		Assets.confirm_popup = g.newSprite("store/buy_confirm.png");

		Assets.sel_std = g.newSprite("selectstandard.png");
		Assets.sel_tri = g.newSprite("selecttriangle.png");
		Assets.sel_orb = g.newSprite("selectorbiter.png");

		Assets.desel_std = g.newSprite("deselectstandard.png");
		Assets.desel_tri = g.newSprite("deselecttriangle.png");
		Assets.desel_orb = g.newSprite("deselectorbiter.png");

		Log.d("FocusLoader: ","Files Loaded!");

		Thread splash=new Thread(){
			public void run(){
				try{
					sleep(2000);
					Intent mi=new Intent("android.intent.action.LOOP");
					startActivity(mi);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					finish();
				}
			}
		};
		splash.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void update() {
		// TODO Auto-generated method stub

		
		//Log.d("FocusLoader: ","Files Loaded!");
		//game.setScreen(new GameLoop(game, fo));
	}


	public void paint(float i) {
		// TODO Auto-generated method stub
		//g.drawARGB(255, 0, 255, 0);
	}


	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void resume() {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}


	public void backButton() {
		// TODO Auto-generated method stub
		
	}

	public void menuButton() {
		// TODO Auto-generated method stub
		
	}

}
