package com.justinkleiber.rocketlaunch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.justinkleiber.focus2d.base.Audio;
import com.justinkleiber.focus2d.base.Collision;
import com.justinkleiber.focus2d.base.Graphics;
import com.justinkleiber.focus2d.base.Input;
import com.justinkleiber.focus2d.base.Input.TouchEvent;
import com.justinkleiber.focus2d.base.Position;
import com.justinkleiber.focus2d.base.Storage;
import com.justinkleiber.focus2d.base.UI;
import com.justinkleiber.focus2d.base.Vibrate;
import com.justinkleiber.focus2d.controllers.FileManager;
import com.justinkleiber.focus2d.controllers.FocusAudio;
import com.justinkleiber.focus2d.controllers.FocusClock;
import com.justinkleiber.focus2d.controllers.FocusCollision;
import com.justinkleiber.focus2d.controllers.FocusGraphics;
import com.justinkleiber.focus2d.controllers.FocusInput;
import com.justinkleiber.focus2d.controllers.FocusUI;
import com.justinkleiber.focus2d.controllers.FocusVibrate;


public class GameLoop extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoomUpdateListener, RealTimeMessageReceivedListener,RoomStatusUpdateListener, OnInvitationReceivedListener, RealTimeMultiplayer.ReliableMessageSentCallback {

    Room room;
    String mPlayerID;
    String mCurrentParticipant;
    int activePlayers;

boolean showToast = true, showImport=true;
	LeaderboardScore average, total_dist, flights, single;
	float lb_avg = 0.0f, lb_td = 0.0f, lb_fl = 0.0f, lb_s = 0.0f;
	@Override
	public void onConnected(Bundle bundle) {
		if(showToast) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(this, "Connected To Google Play Games", duration);
			toast.show();
			showToast=false;
		}

        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,getResources().getString(R.string.leaderboard_average), LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                average = loadPlayerScoreResult.getScore();
            }
        });
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,getResources().getString(R.string.leaderboard_single),LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                single = loadPlayerScoreResult.getScore();
            }
        });
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,getResources().getString(R.string.leaderboard_total),LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                total_dist = loadPlayerScoreResult.getScore();
            }
        });
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,getResources().getString(R.string.leaderboard_flights),LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                flights = loadPlayerScoreResult.getScore();
            }
        });

        syncScores();

		if(showImport) {
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(this, "Importing User Stats...", duration);
			toast.show();
			showImport=false;
		}

        if (bundle != null) {
            Invitation inv =
                    bundle.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());

                // go to game screen
                /*
                playState = PlayState.MULTI;
                state = GameState.SHOW_MATCHUP;
                multiPlayState = MultiPlayerGameState.FRIEND;*/
            }
        }

        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

		//syncScores();
/*
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, "User Statistics Imported Successfully!", duration);
		toast.show();*/
	}




    String mIncomingInvitationId;
    String inviter;
    boolean invited;
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // show in-game popup to let user know of pending invitation
        invited = true;
        if(state==GameState.MAIN_MENU)
        {
            state = GameState.SHOW_INVITE;
        }
        // store invitation for use when player accepts this invitation
        mIncomingInvitationId = invitation.getInvitationId();
        inviter = invitation.getInviter().getDisplayName();
    }

    @Override
    public void onInvitationRemoved(String s) {

    }

    void syncScores()
	{

		if(total_dist!=null)
		{
			lb_td=total_dist.getRawScore();
		}
		if(flights!=null)
		{
			lb_fl=flights.getRawScore();
		}
		if(single!=null)
		{
			lb_s=single.getRawScore();
		}
		if(average!=null)
		{
			lb_avg=average.getRawScore();
		}

		if(lb_fl>storage.getIntPref("TOTAL_FLIGHTS",0))
		{
			storage.setPref("TOTAL_FLIGHTS",(int)lb_fl);
		}
		else
		{
			Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_flights),storage.getIntPref("TOTAL_FLIGHTS", 0));
		}

		//Total Distance Import
		if(lb_td>storage.getFloatPref("TOTAL_DISTANCE", 0.0f))
		{
			storage.setFloatPref("TOTAL_DISTANCE", lb_td);
		}
		else
		{
			Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_total),(long) storage.getFloatPref("TOTAL_DISTANCE", 0.0f));
		}

		if(lb_s>storage.getFloatPref("LONGEST_DISTANCE", 0.0f))
		{
			if(!(lb_s>lb_td))
			{
				storage.setFloatPref("LONGEST_DISTANCE",lb_s);
			}
		}
		else
		{
			Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_single),(long) storage.getFloatPref("LONGEST_DISTANCE", 0.0f));
		}

		float real_avg=0;
		if(storage.getIntPref("TOTAL_FLIGHTS",0)!=0) {
			real_avg = storage.getFloatPref("TOTAL_DISTANCE", 0.0f) / storage.getIntPref("TOTAL_FLIGHTS",1);
		}
		storage.setFloatPref("AVERAGE", real_avg);
		Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_average), (long) storage.getFloatPref("AVERAGE", 0.0f));

	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
	}

	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;

	private static int RC_SIGN_IN = 9001;
    private static int RC_SELECT_PLAYERS = 10000;
    private static int RC_WAITING_ROOM = 11000;
    private static int RC_INVITATION_INBOX = 12000;

	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInflow = true;
	private boolean mSignInClicked = false;

    boolean mWaitingRoomFinishedFromCode = false;

    String mRoomId;

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		if (mResolvingConnectionFailure) {
			// already resolving
			return;
		}

		// if the sign-in button was clicked or if auto sign-in is enabled,
		// launch the sign-in flow
		if (mSignInClicked || mAutoStartSignInflow) {
			mAutoStartSignInflow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = true;

			// Attempt to resolve the connection failure using BaseGameUtils.
			// The R.string.signin_other_error value should reference a generic
			// error string in your strings.xml file, such as "There was
			// an issue with sign-in, please try again later."
			if (!BaseGameUtils.resolveConnectionFailure(this,
					mGoogleApiClient, result,
					RC_SIGN_IN,"There was an issue with sign-in, please try again later.")) {
				mResolvingConnectionFailure = false;
			}
		}

		// Put code here to display the sign-in button

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			mSignInClicked = false;
			mResolvingConnectionFailure = false;
			if (resultCode == RESULT_OK) {
				mGoogleApiClient.connect();
			} else {
				// Bring up an error dialog to alert the user that sign-in
				// failed. The R.string.signin_failure should reference an error
				// string in your strings.xml file that tells the user they
				// could not be signed in, such as "Unable to sign in."
				BaseGameUtils.showActivityResultError(this,
						requestCode, resultCode, R.string.login_cancel);
				explicitLogOut=true;
				storage.setPref("EXPLICIT_LOGOUT",true);
			}
		}
        if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                canStartRoom=true;
                Log.d("GPGS EC: ", String.valueOf(resultCode));
                return;
            }

            // get the invitee list
            Bundle extras = intent.getExtras();
            final ArrayList<String> invitees =
                    intent.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                    intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (requestCode == RC_WAITING_ROOM) {
            if (mWaitingRoomFinishedFromCode)
            {
                return;
            }

            //room = intent.getParcelableExtra(Multiplayer.EXTRA_ROOM);
            //mRoomId = room.getRoomId();

            if (resultCode == Activity.RESULT_OK) {
                // (start game)
                activePlayers=0;
                r_x=360;
                r_y=800;
                hypertime=30;
                crash=false;
                xspd=4;
                String s = "START_GAME";
                byte[] start_byte_mess = s.getBytes();
                for(Participant p : room.getParticipants()) {
                    if(!p.getParticipantId().equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, start_byte_mess, mRoomId, p.getParticipantId());
                    }
                    activePlayers++;
                }
                playState=PlayState.MULTI;
                multiPlayState=MultiPlayerGameState.FRIEND;
                state = GameState.GAME_RUNNING;
                //mp_swag_start=true;
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                playState=PlayState.SINGLE;
                state = GameState.MAIN_MENU;
                canStartRoom=true;
            }
            else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                playState=PlayState.SINGLE;
                state = GameState.MAIN_MENU;
                canStartRoom=true;
            }
        }
        if (requestCode == RC_INVITATION_INBOX) {
            if (resultCode != Activity.RESULT_OK) {
                // canceled
                canStartRoom=true;
                return;
            }

            // get the selected invitation
            Bundle extras = intent.getExtras();
            Invitation invitation =
                    extras.getParcelable(Multiplayer.EXTRA_INVITATION);

            // accept it!
            RoomConfig roomConfig = makeBasicRoomConfigBuilder()
                    .setInvitationIdToAccept(invitation.getInvitationId())
                    .build();
            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // go to game screen
            /*
            playState = PlayState.MULTI;
            state = GameState.SHOW_MATCHUP;
            multiPlayState = MultiPlayerGameState.FRIEND;*/

           // startFriendGame();

        }
	}


    enum GameState{
		MAIN_MENU, GAME_RUNNING, GAME_PAUSE, END_GAME, SHOW_MATCHUP, REMATCH, SHOW_INVITE, SHOW_STANDINGS
	};

    enum PlayState{
      SINGLE, MULTI
    };

    enum MultiPlayerGameState{
        QUICK, FRIEND, BSC_TOURNAMENT, CPX_TOURNAMENT
    };

	GameState state;
    PlayState playState;
    MultiPlayerGameState multiPlayState;

	boolean b, bp, q, gs_init=false, stats, gs_in, gs_out, store, sets;

	boolean crash_first;

	boolean game_start=false, first_loop=true;

	//scoring
	float distance = 0;
	int life;
	int levels=0;

	//controls
	boolean right, left, right_was_old, left_was_old, pause;

	//Stars
	int stars_x = 0, stars_y = 0, stars_yy = 0, stars_h, hyp_h;
	int yspd = 15;
	int min_yspd=20;
	
	//Rocket
	//int r_x = 500, r_y = 800;
	int xspd = 4;
	int r_t = 1;
	int typ_chg = 1;
	Position r_pos;

	//rocket
	int r_x=400, r_y=800, r_turn_spd=12, r_w, r_h;
    int pTurnSpeed, nTurnSpeed;

	float r_ang=0;

	//timing
	long time_elapsed, old_elapse;
	long hypertime=30, base_time=30, hypers=1;
	boolean hyperspacer = false, hyp_up, hyp_down;
	
	//Asteroids
	int a_x = 55, a_y = -100, aa_x = 660, aa_y = -100;
	int a_yspd = 7;
	float rotate = 0, rot_spd = 2;

	int[] ax = new int[1000];
	int[] ay = new int[1000];
	int[] a_ang_spd = new int[1000];
	int[] a_spd = new int[1000];
	int[] a_typ = new int[1000];
	float[] a_ang = new float[1000];
	boolean[] a_exist = new boolean[1000];
	int asteroids=0, old_asteroids;
	int a_w, a_h;
	int a_tw, a_th;

    int coins = 0;
    int mak_cash=0;
    int cw, ch;

	UI ui;
	Paint paint, box, right_p, right_blue, black_paint;
	Position pos;
	
	Collision collision;

	Graphics g;
	Audio audio;
	Input input;
	Storage storage;
	Vibrate vibrate;
	MainRenderer renderer;

	boolean c, cool, crash=false;
	boolean l;
	int da;
	boolean quit, resume;

	boolean canChangeDirs=true;

	com.justinkleiber.focus2d.base.Clock clock, cooldown, multi_end;

	GoogleApiClient mGoogleApiClient;
	boolean explicitLogOut = false, endgame=false;

	Position crash_pos;

    int currentBank;


    boolean changePlayerMode;
    boolean p_quick, p_friend, p_tournament;
    boolean messages;

    ArrayList<Float> distances;
    ArrayList<String> crashedPlayers,scores,boosts,rockets,user_prefs;

    ArrayList<Participant> playAgain;

    boolean wentToMenu=false;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    RocketStats standard,triangle,orbiter;
    int selectedRocket;

    int maxLife, agility;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		int frameBufferWidth=720;
		int frameBufferHeight = 1280; //flip for landscape
		Bitmap frame=Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565);
		//attr = new AttributeSet();
		//render = new MainRenderer(this);
		g = new FocusGraphics(getAssets(), frame);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		float scaleX = (float) frameBufferWidth / width;
		float scaleY = (float) frameBufferHeight / height;

		storage = new FileManager(this);
		audio = new FocusAudio(this);
		vibrate = new FocusVibrate(this);
		/////////////////////////////////////////////////////////

		stars_h = Assets.stars.getHeight();
		hyp_h = Assets.hyperdrive.getHeight();

		Assets.play.setPosition(142, 250);

		Assets.stats.setPosition(15, 1136);
		Assets.settings.setPosition(250, 1136);
		Assets.store.setPosition(485,1136);
		Assets.game_service_in.setPosition(15, 1056);
		Assets.game_service_out.setPosition(15, 1056);

		pos = new Position(360, 800);
		collision = new FocusCollision();
		ui = new FocusUI();
		clock = new FocusClock();

		renderer=new MainRenderer(this,frame);
		input = new FocusInput(this, renderer, scaleX, scaleY);

		setContentView(renderer);

		state = GameState.MAIN_MENU;
        playState = PlayState.SINGLE;

		stars_h = Assets.stars.getHeight();
		hyp_h = Assets.hyperdrive.getHeight();

		Assets.left.setPosition(5, 1086);
		Assets.right.setPosition(498, 1086);
		Assets.pause.setPosition(240, 1200);

		Assets.crash_screen.setPosition(47, 150);

		Assets.resume.setPosition(206, 400);
		Assets.retry.setPosition(206, 600);
		Assets.quitt.setPosition(206, 800);
		Assets.quit.setPosition(206, 600);

		ui = new FocusUI();
		clock = new FocusClock();
		cooldown = new FocusClock();
        multi_end = new FocusClock();

		Typeface space = Typeface.createFromAsset(this.getAssets(), "BMSPACE.TTF");

		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setAntiAlias(true);
		paint.setTypeface(space);
		paint.setColor(Color.WHITE);

        right_p = new Paint();
        right_p.setTextSize(30);
        right_p.setTextAlign(Paint.Align.RIGHT);
        right_p.setAntiAlias(true);
        right_p.setTypeface(space);
        right_p.setColor(Color.WHITE);

        black_paint = new Paint();
        black_paint.setTextSize(30);
        black_paint.setTextAlign(Paint.Align.LEFT);
        black_paint.setAntiAlias(true);
        black_paint.setTypeface(space);
        black_paint.setColor(Color.BLACK);

        right_blue = new Paint();
        right_blue.setTextSize(30);
        right_blue.setTextAlign(Paint.Align.RIGHT);
        right_blue.setAntiAlias(true);
        right_blue.setTypeface(space);
        right_blue.setColor(Color.argb(255, 0, 255, 218));

		r_w=Assets.rocket.getSpriteWidth();
		r_h=Assets.rocket.getSpriteHeight();

		a_w=Assets.asteroid.getWidth();
		a_h=Assets.asteroid.getHeight();

		a_tw=Assets.tiny_asteroid.getWidth();
		a_th=Assets.tiny_asteroid.getHeight();

        cw = Assets.coin.getSpriteWidth();
        ch = Assets.coin.getSpriteHeight();

		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

		box = new Paint();
		box.setColor(Color.RED);


        Assets.multiToggle.setPosition(454, 1056);
        Assets.singleToggle.setPosition(454, 1056);

        Assets.qplay.setPosition(142, 150);
        Assets.fplay.setPosition(142, 340);
        Assets.tplay.setPosition(142,530);

        Assets.mail.setPosition(5,1137);
        Assets.no_mail.setPosition(5,1160);

        Assets.invite_popup.setPosition(5,200);
        Assets.accept.setPosition(105,440);
        distances = new ArrayList<Float>();
        crashedPlayers = new ArrayList<String>();
        playAgain = new ArrayList<Participant>();

        Assets.reject.setPosition(405,440);

        Assets.continue_button.setPosition(236,702);
        Assets.again.setPosition(74, 1060);
        Assets.leave_button.setPosition(236,876);

        playState=PlayState.SINGLE;

        boosts = new ArrayList<String>();
        rockets = new ArrayList<String>();
        user_prefs = new ArrayList<String>();
        scores = new ArrayList<String>();

        standard = new RocketStats(3,12);
        triangle = new RocketStats(3,18);
        orbiter = new RocketStats(5,10);

        refreshUserStuff();

        if(!storage.isExist("user_prefs.txt"))
        {
            user_prefs.add("1"); //Default to Vibrate On
        }
        else
        {
            user_prefs = storage.loadToArray("user_prefs.txt");
        }

        selectedRocket = storage.getIntPref("CURRENT_ROCKET",0);

        vibrate_on = user_prefs.get(0).equals("1");
	}

    public void refreshUserStuff()
    {
        boosts.clear();
        rockets.clear();
        user_prefs.clear();
        if(!storage.isExist("boost_progress.txt"))
        {
            boosts.add("0");
            boosts.add("0");
            boosts.add("0");
            boosts.add("0");
            storage.save("boost_progress.txt",boosts);
        }
        else
        {
            boosts=storage.loadToArray("boost_progress.txt");
        }

        if(!storage.isExist("owned_rockets.txt"))
        {
            rockets.add("0");
            rockets.add("0");
            rockets.add("0");
            rockets.add("0");
            storage.save("owned_rockets.txt", rockets);
        }
        else
        {
            rockets=storage.loadToArray("owned_rockets.txt");
        }

        //Update Preferences/Settings
        if(!storage.isExist("user_prefs.txt"))
        {
            user_prefs.add("1"); //Default to Vibrate On
        }
        else
        {
            user_prefs = storage.loadToArray("user_prefs.txt");
        }
/*
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-44928171-3"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
       // tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);*/

        if(selectedRocket==0) {
            life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
            maxLife=life;
            agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
        }
        else if(selectedRocket==1)
        {
            life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
            maxLife=life;
            agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
        }
        else if(selectedRocket==2)
        {
            life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
            maxLife=life;
            agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
		renderer.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(state==GameState.GAME_RUNNING)
		{
			state=GameState.GAME_PAUSE;
		}
		renderer.pause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(storage.getBoolPref("GPGS_Login_status",false)|| !storage.getBoolPref("EXPLICIT_LOGOUT",false)) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(state==GameState.GAME_RUNNING)
		{
			state=GameState.GAME_PAUSE;
		}
		storage.setPref("GPGS_Login_status", mGoogleApiClient.isConnected() && mGoogleApiClient != null && !explicitLogOut);
        if(mRoomId!=null && mRoomId!="") {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
        }
		mGoogleApiClient.disconnect();
	}

    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }

    private void checkMoney()
    {
        if(storage.isExist("coin_bank.txt"))
        {
            String str = storage.loadToString("coin_bank.txt");
            currentBank = Integer.parseInt(str);
        }
        else
        {
            currentBank = 0;
            storage.save("coin_bank.txt","0");
        }
    }

    boolean old_changeplayermode, mp_swag_start, vibrate_on;

	private void menu_update()
	{
        if(wentToMenu)
        {
            refreshUserStuff();
            //currentBank+=200;
            //storage.save("coin_bank.txt",String.valueOf(currentBank));
            selectedRocket=storage.getIntPref("CURRENT_ROCKET",0);
            wentToMenu=false;

            vibrate_on = user_prefs.get(0).equals("1");
        }
		first_loop=true;
		levels=0;
		min_yspd=20;

		hypers=1;
		crash=false;
		hypertime=30;
        mak_cash=0;
        checkMoney();
        endgame=false;

        if(playState==PlayState.SINGLE) {
            if(selectedRocket==0) {
                life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
            }
            else if(selectedRocket==1)
            {
                life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
            }
            else if(selectedRocket==2)
            {
                life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
            }
        }
        else
        {
            life=3;
            agility = 12;
        }
        distance=0;

        mak_cash=0;
        c_diff = 0;
        //game_start=false;


		List<TouchEvent> touchEvents = input.getTouchEvents();

		for(int i=0;i<touchEvents.size();i++){
			TouchEvent event = touchEvents.get(i);

			if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
			{
				bp = (ui.isButtonPress(event, Assets.play) && playState==PlayState.SINGLE);
				if(!game_start && playState==PlayState.SINGLE) {
					store = ui.isButtonPress(event, Assets.store);
					sets = ui.isButtonPress(event, Assets.settings);
					gs_in = ui.isButtonPress(event, Assets.game_service_in);
					gs_out = ui.isButtonPress(event, Assets.game_service_out);
					stats = ui.isButtonPress(event, Assets.stats);
				}
                if(!game_start && mGoogleApiClient.isConnected())
                {
                    if(playState == PlayState.SINGLE && !changePlayerMode)
                    {
                        changePlayerMode = ui.isButtonPress(event, Assets.multiToggle);
                    }
                    else if(playState == PlayState.MULTI && !changePlayerMode)
                    {
                        changePlayerMode = ui.isButtonPress(event, Assets.singleToggle);
                    }

                }

                if(playState == PlayState.MULTI)
                {
                    p_quick = ui.isButtonPress(event,Assets.qplay);
                    p_friend = ui.isButtonPress(event,Assets.fplay);
                    messages = ui.isButtonPress(event,Assets.no_mail) || ui.isButtonPress(event,Assets.mail);
                 //   p_tournament = ui.isButtonPress(event,Assets.tplay);
                }
			}
			else
			{
				bp=false;
				store=false;
				sets=false;
				gs_in=false;
				gs_out=false;
				stats=false;
                changePlayerMode=false;
                p_quick = false;
                p_friend =false;
                messages = false;
              //  p_tournament = false;
			}
		}


        //MULTIPLAYER UI***
        if(p_friend && canStartRoom)
        {
            canStartRoom=false;
            startFriendGame();
        }
        /*
        if(p_tournament)
        {
            doMagicTournament();
        }*/


        if(messages)
        {
            Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
            startActivityForResult(intent, RC_INVITATION_INBOX);
            invited = false;
        }
        //MULTIPLAYER UI***


        if(changePlayerMode && !old_changeplayermode)
        {
            if(playState==PlayState.SINGLE)
            {
                playState=PlayState.MULTI;
            }
            else
            {
                playState=PlayState.SINGLE;
            }
        }
        old_changeplayermode = changePlayerMode;

		if(!game_start)
		{
			//Stars
			stars_y+=yspd;

			if(stars_y>=stars_h)
			{
				stars_y=0;
			}
			stars_yy=stars_y-stars_h;
			//Stars

            //xspd = 4;
		}
		else
		{
			//Hyperdrive
			stars_y+=yspd;
			yspd++;

			if(stars_y>=hyp_h)
			{
				stars_y=0;
			}
			stars_yy=stars_y-hyp_h;
			//Hyperdrive
		}
		//Rocket
		if(!bp && !game_start)
		{
			r_x+=xspd;
            yspd=20;
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                Assets.rocket.setPosition(r_x, r_y);
            }
            else if(selectedRocket==1)
            {
                Assets.triangle.setPosition(r_x, r_y);
            }
            else if(selectedRocket==2)
            {
                Assets.orbiter.setPosition(r_x,r_y);
            }
            if(gs_init && canChangeDirs)
            {
                xspd = 4;
            }
			gs_init=false;
		}
		else
		{
            xspd=0;
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                r_x = Assets.rocket.getPosition().x;
                r_y = Assets.rocket.getPosition().y;
            }
            else if(selectedRocket==1)
            {
                r_x = Assets.triangle.getPosition().x;
                r_y = Assets.triangle.getPosition().y;
            }
            else if(selectedRocket==2)
            {
                r_x = Assets.orbiter.getPosition().x;
                r_y = Assets.orbiter.getPosition().y;
            }
			if(!gs_init)
			{
				gs_init = true;
				clock.startClock();
			}
			if(gs_init)
			{
                if(selectedRocket==0 || playState==PlayState.MULTI) {
                    game_start = collision.isInPosition(Assets.rocket, pos, 3);
                }
                else if(selectedRocket==1)
                {
                    game_start = collision.isInPosition(Assets.triangle, pos, 3);
                }
                else if(selectedRocket==2)
                {
                    game_start = collision.isInPosition(Assets.orbiter,pos,3);
                }
			}

		}

		if(r_x<=100)
		{
			if(canChangeDirs) {
				canChangeDirs = false;
				xspd = 4;
			}
		}
		else if(r_x >=560)
		{
			if(canChangeDirs) {
				canChangeDirs = false;
				xspd = -4;
			}
		}
		else
		{
			canChangeDirs=true;
		}

		if(r_t>=2 || r_t <=0)
		{
			typ_chg *= -1;
		}

		r_t+=typ_chg;


		//Rocket

		//Asteroids
		a_y+=a_yspd;
		aa_y+=a_yspd;

		if(a_y>=1380)
		{
			a_y=-100;
		}
		if(aa_y>=1380)
		{
			aa_y=-100;
		}

		rotate+=rot_spd;
		//Asteroids

		for(int ar=0; ar<(old_asteroids+5); ar++)
		{
			a_exist[ar]=false;
			ax[ar] = 400;
			ay[ar] = 1500;
		}

		//Transition to Game
		if(game_start)
		{
			if(yspd>=75)
			{
				//once speed is attained begin
				r_x=360;
				r_y=800;
				hypertime=30;
				crash=false;
                xspd=4;
				state=GameState.GAME_RUNNING;
				game_start=false;
                mp_swag_start=false;
			}
		}


		//Google Play Services
		if(gs_in && !explicitLogOut) {
			mSignInClicked = true;
			mGoogleApiClient.connect();
			storage.setPref("EXPLICIT_LOGOUT",false);
		}
		if(gs_out)
		{
			if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
				Games.signOut(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				explicitLogOut = true;
				mSignInClicked = false;
				storage.setPref("EXPLICIT_LOGOUT",true);
			}
		}

		if(stats)
		{
            wentToMenu=true;
			storage.setPref("GPGS_Login_status", mGoogleApiClient.isConnected() && mGoogleApiClient != null && !explicitLogOut);
			//Call a new activity
			Bundle b = new Bundle();
			b.putInt("MENU", 1);
			Intent i = new Intent("android.intent.action.MENU_LOOP");
			i.putExtras(b);
			startActivity(i);
		}
		if(sets)
		{
            wentToMenu=true;
			storage.setPref("GPGS_Login_status",mGoogleApiClient.isConnected() && mGoogleApiClient!=null && !explicitLogOut);
			//Call a new activity
			Bundle b = new Bundle();
			b.putInt("MENU", 0);
			Intent i = new Intent("android.intent.action.MENU_LOOP");
			i.putExtras(b);
			startActivity(i);
		}
		if(store)
		{
            wentToMenu=true;
            refreshUserStuff();
			storage.setPref("GPGS_Login_status",mGoogleApiClient.isConnected() && mGoogleApiClient!=null && !explicitLogOut);
			//Call a new activity
			Bundle b = new Bundle();
			b.putInt("MENU", 2);
			Intent i = new Intent("android.intent.action.MENU_LOOP");
			i.putExtras(b);
			startActivity(i);
		}
		dist_counted=false;
	}

    int spw;
	private void game_run_update()
	{
        if(playAgain.size()>0) {
            playAgain.clear();
        }
		List<TouchEvent> touchEvents = input.getTouchEvents();

		for(int i=0;i<touchEvents.size();i++)
		{
			TouchEvent event = touchEvents.get(i);

			if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
			{
				if(game_start && !crash && !endgame)
                {
					right = ui.isButtonPress(event, Assets.right);
					left = ui.isButtonPress(event, Assets.left);
					pause = ui.isButtonPress(event, Assets.pause);
				}
                else
                {
                    right = false;
                    left = false;
                    pause = false;
                }
			}

			if(event.type == TouchEvent.TOUCH_UP)
			{
				right = false;
				left = false;
				pause = false;
			}
		}

		//SCORING
		if(game_start)
		{
			if(first_loop)
			{
				clock.startClock();
				first_loop=false;
			}
			distance+=yspd*.01;
			time_elapsed = clock.secondsElapsed();

			if(hypertime-time_elapsed<=0)
			{
				clock.startClock();
				time_elapsed=0;
				hypertime=base_time+5*hypers;
				hyperspacer=true;
				hyp_up=true;
				hypers++;
				levels++;
				l=true;
			}

		//SCORING


			min_yspd=(3*levels)+20;
			if(hyperspacer)
			{
                if(playState==PlayState.SINGLE) {
                    if (life < maxLife && l) {
                        life++;
                        l = false;
                    }
                }
                else
                {
                    if (life < 3 && l) {
                        life++;
                        l = false;
                    }
                }
				if(hyp_up)
				{
					time_elapsed=0;
					yspd+=3;
					if(yspd>=(min_yspd+65))
					{
						hyp_up=false;
						hyp_down=true;
					}
				}
				if(hyp_down)
				{
					time_elapsed=0;
					yspd-=2;
					if(yspd<=min_yspd)
					{
						hyp_down=false;
						yspd=min_yspd;
						hyperspacer=false;
						clock.startClock();
					}
				}
				//hyperdrive
				stars_y+=yspd;


				if(stars_y>=hyp_h)
				{
					stars_y=0;
				}
				stars_yy=stars_y-hyp_h;
				//hyperdrive
			}
			else
			{
				//StarsStars
				stars_y+=yspd;

				if(stars_y>=stars_h)
				{
					stars_y=0;
				}
				stars_yy=stars_y-stars_h;
				//

			}

		}
		else
		{
			//Hyperdrive
			stars_y+=yspd;
			yspd--;

			if(stars_y>=hyp_h)
			{
				stars_y=0;
			}
			stars_yy=stars_y-hyp_h;
			//Hyperdrive
		}

		if(yspd <= min_yspd && !game_start)
		{
			game_start=true;
			yspd=min_yspd;
		}

		//Rocket
		if(!game_start)
		{
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                Assets.rocket.setPosition(355, 800);
            }
            else if(selectedRocket==1)
            {
                Assets.triangle.setPosition(355, 800);
            }
            else if(selectedRocket==2)
            {
                Assets.orbiter.setPosition(355, 800);
            }
		}
		else
		{
			if(left)
			{
                r_turn_spd= -agility;
				left_was_old=true;
				right_was_old=false;
				r_x+=r_turn_spd;

				if(!cool)
				{
					r_ang-=2;
					if(r_ang<=-20)
					{
						r_ang=-20;
					}
				}
			}
			else if(right)
			{
                r_turn_spd=agility;
				right_was_old=true;
				left_was_old=false;
				r_x+=r_turn_spd;
				if(!cool)
				{
					r_ang+=2;
					if(r_ang>=20)
					{
						r_ang=20;
					}
				}
			}
			else
			{
                r_turn_spd=0;
				if(right_was_old)
				{
					if(!cool)
					{
						r_ang-=2;
						if(r_ang<=0)
						{
							r_ang=0;
						}
					}
				}
				if(left_was_old)
				{
					if(!cool)
					{
						r_ang+=2;
						if(r_ang>=0)
						{
							r_ang=0;
						}
					}
				}
			}


			if(cool)
			{
				r_ang+=da;
				if(r_ang>=360)
				{
					r_ang=0;
					da=0;
				}
			}

            if(selectedRocket==0 || playState==PlayState.MULTI) {
                spw=(Assets.rocket.getSpriteWidth()/2);
            }
            else if(selectedRocket==1)
            {
                spw=(Assets.triangle.getSpriteWidth()/2);
            }
            else if(selectedRocket==2)
            {
                spw=(Assets.orbiter.getSpriteWidth()/2);
            }
			if(r_x<=spw)
			{
				r_x=spw;
			}
			if(r_x>=(720-spw))
			{
				r_x=720-spw;
			}

            if(selectedRocket==0 || playState==PlayState.MULTI) {
                Assets.rocket.setPosition(r_x, r_y);
            }
            else if(selectedRocket==1)
            {
                Assets.triangle.setPosition(r_x, r_y);
            }
            else if(selectedRocket==2)
            {
                Assets.orbiter.setPosition(r_x, r_y);
            }
		}
		//Rocket

		if(pause)
		{
			state = GameState.GAME_PAUSE;

			pause = false;
		}

		//Asteroids
		if(game_start)
		{
			for(int a_i=0; a_i < asteroids; a_i++)
			{
				if(ay[a_i]>1300 || !a_exist[a_i])
				{
					generateAsteroid(a_i, 900);
				}

				ay[a_i]+=a_spd[a_i];
				a_ang[a_i]+=a_ang_spd[a_i];

				//asteroidList[a_i].setPosition(ax[a_i], ay[a_i]);
			}
            if(playState==PlayState.SINGLE) {
                for (int c_i = 0; c_i < coins; c_i++) {
                    if (cy[c_i] > 1300 || !c_exist[c_i]) {
                        generateCoins(c_i, 1200);
                    }

                    cy[c_i] += c_spd[c_i];
                }
            }

			if(!hyperspacer)
			{
				asteroids = (int) ((.000019*Math.pow(time_elapsed, 3))-(.0042*time_elapsed*time_elapsed)+(.3488*time_elapsed)+2.5);
				old_asteroids = asteroids;
                if(playState == PlayState.SINGLE) {
                    coins = recursiveCoinCollector(levels, 2, 0);
                }
			}
			else
			{
				for(int ar=0; ar<(old_asteroids+5); ar++)
				{
					a_exist[ar]=false;
					ax[ar] = 400;
					ay[ar] = 1500;
				}
				old_asteroids=0;
				asteroids=0;

                if(playState == PlayState.SINGLE) {
                    for (int cr = 0; cr < (coins + 5); cr++) {
                        c_exist[cr] = false;
                        cx[cr] = 400;
                        cy[cr] = 1500;
                    }

                    coins = 0;
                }
			}
		}
		if(!cool && !crash && !endgame)
		{
			//Collision Detection
			for(int a_r=0; a_r<asteroids; a_r++) {

				if(a_typ[a_r]>0)
				{
					if (collision.isCollisionExist(r_x, r_y, r_w, r_h, ax[a_r], ay[a_r], a_w, a_h,  r_ang)) {
						a_exist[a_r] = false;
						ay[a_r] = 1400;
						life--;
                        if(vibrate_on) {
                            vibrate.vibrate(250);
                        }
						c = true;
						cool = true;
						break;
					}
				}
				else
				{
					if (collision.isCollisionExist(r_x, r_y, r_w, r_h, ax[a_r], ay[a_r], a_tw, a_th, r_ang)) {
						a_exist[a_r] = false;
						ay[a_r] = 1400;
						life--;
                        if(vibrate_on) {
                            vibrate.vibrate(250);
                        }
						c = true;
						cool = true;
						break;
					}
				}
			}

            if(playState == PlayState.SINGLE)
            {
                for(int c_r=0;c_r<coins;c_r++){
                    if (collision.isCollisionExist(r_x, r_y, r_w, r_h, cx[c_r], cy[c_r], cw, ch, r_ang)) {
                        c_exist[c_r] = false;
                        cy[c_r] = 1400;
                        mak_cash++;
                        if(vibrate_on) {
                            vibrate.vibrate(50);
                        }
                    }
                }
            }

		}


		crash_first=false;
		if(c && life>0)
		{
			cooldown.startClock();
			c=false;
			da=20;
		}
		else if(c && life<=0)
		{
			cool=false;
			crash=true;

            if(selectedRocket==0 || playState==PlayState.MULTI) {
                crash_pos = Assets.rocket.getPosition();
            }
            else if(selectedRocket==1)
            {
                crash_pos = Assets.triangle.getPosition();
            }
            else if(selectedRocket==2)
            {
                crash_pos = Assets.orbiter.getPosition();
            }
			c=false;

			crash_first=true;
			cooldown.startClock();
		}
		if(cooldown.secondsElapsed()>=1)
		{
			cool=false;
		}

		if(endgame)
		{

            if(playState==PlayState.MULTI) {
                String df = new DecimalFormat("##.##").format(distance);
                byte[] dist_byte_mess = df.getBytes();
                byte[] crash_byte_mess = mCurrentParticipant.getBytes();

                for (Participant p : room.getParticipants()) {
                    if (!p.getParticipantId().equals(mCurrentParticipant)) {
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, crash_byte_mess, mRoomId, p.getParticipantId());
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, dist_byte_mess, mRoomId, p.getParticipantId());
                    }
                    else
                    {
                        crashedPlayers.add(p.getParticipantId());
                        distances.add(Float.parseFloat(df));
                    }
                }



                foundPlace=false;
                sorted=false;
            }
			levels=0;
			min_yspd=20;
			hypers=1;
			hypertime=30;
			crash=false;
			b=false;
			q=false;
			time_elapsed=0;
			game_start=false;
			endgame=false;

            frist_run=true;

            state=GameState.END_GAME;
        }
		else if(crash)
		{
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                Assets.rocket.setPosition(crash_pos);
            }
            else if(selectedRocket==1)
            {
                Assets.triangle.setPosition(crash_pos);
            }
            else if(selectedRocket==2)
            {
                Assets.orbiter.setPosition(crash_pos);
            }
            r_ang += 10;
			endgame=false;
			vibrate.vibrate(250);
			if(cooldown.secondsElapsed()>=1)
			{
				crash = false;
				endgame  = true;
			}
		}

        made_bank=false;
		dist_counted=false;
	}


	private void game_pause_update()
	{
		//Assets.pause_screen.setPosition(87, 150);
		Assets.quit.setPosition(206, 600);

		clock.pauseClock();
		cooldown.pauseClock();

		List<TouchEvent> touchEvents = input.getTouchEvents();

		for(int i=0;i<touchEvents.size();i++)
		{
			TouchEvent event = touchEvents.get(i);

			if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
			{
				quit = ui.isButtonPress(event, Assets.quit);
				resume = ui.isButtonPress(event, Assets.resume);
			}

			if(event.type == TouchEvent.TOUCH_UP)
			{
				quit = false;
				resume = false;
			}
		}

		if(quit)
		{
			distance=0;
            if(playState==PlayState.SINGLE) {
                if(selectedRocket==0) {
                    life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
                }
                else if(selectedRocket==1)
                {
                    life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
                }
                else if(selectedRocket==2)
                {
                    life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
                }
            }
            else
            {
                life=3;
                agility = 12;
            }
			state=GameState.MAIN_MENU;
			time_elapsed=0;
			game_start=false;
			clock.resumeClock();
			cooldown.resumeClock();
			levels=0;
			min_yspd=20;
			hypers=1;
			crash=false;
			hypertime=30;
            quit=false;
		}
		if(resume)
		{
			clock.resumeClock();
			cooldown.resumeClock();
			state=GameState.GAME_RUNNING;
			resume = false;
		}
		dist_counted=false;
	}

	boolean dist_counted = false, made_bank = false, sorted=false;
    int c_diff=0;
    String lf;
    boolean foundPlace=false;
    int place;

    boolean frist_run;
	private void crash_update()
	{
        endgame=false;
		first_loop=true;
		time_elapsed=0;
        r_ang = 0;
		levels=0;
		min_yspd=20;
		hypers=1;
		hypertime=30;
		crash=false;
        if(playState==PlayState.SINGLE) {
            if(selectedRocket==0) {
                life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
            }
            else if(selectedRocket==1)
            {
                life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
            }
            else if(selectedRocket==2)
            {
                life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
                maxLife=life;
                agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
            }
        }
        else
        {
            life=3;
            agility = 12;
        }

        if(playState==PlayState.SINGLE) {
            if (!made_bank) {
                checkMoney();
                currentBank += mak_cash;
                c_diff = mak_cash;
                storage.save("coin_bank.txt", String.valueOf(currentBank));
                mak_cash = 0;
                made_bank = true;
            }

            if (!dist_counted) {
                int flights = storage.getIntPref("TOTAL_FLIGHTS", 0);
                flights++;
                //Log.d("WTF FLIGHTS STORAGE!!!!", String.valueOf(storage.getIntPref("TOTAL_FLIGHTS", 0)));
                //Log.d("WTF FLIGHTS!!!!!!!",String.valueOf(flights));
                storage.setPref("TOTAL_FLIGHTS", flights);
                scores.add(String.valueOf(flights));

                float total = storage.getFloatPref("TOTAL_DISTANCE", 0.0f);
                total += distance;
                storage.setFloatPref("TOTAL_DISTANCE", total);
                scores.add(String.valueOf(total));

                float avg;
                avg = total / flights;
                storage.setFloatPref("AVERAGE", avg);
                scores.add(String.valueOf(avg));

                float high = storage.getFloatPref("LONGEST_DISTANCE", 0.0f);
                if (distance > high) {
                    high = distance;
                    storage.setFloatPref("LONGEST_DISTANCE", high);
                    scores.add(String.valueOf(high));
                }
                else
                {
                    scores.add(String.valueOf(high));
                }

                if (mGoogleApiClient.isConnected() && mGoogleApiClient != null) {
                    Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_single), (long) distance);
                    Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_total), (long) total);
                    Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_average), (long) avg);
                    Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_flights), flights);
                    Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_warp));

                    if (distance > 1000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_moon));
                    }
                    if (distance > 2000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_branch));
                    }
                    if (distance > 3000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_planet));
                    }
                    if (distance > 4000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_galaxy));
                    }

                    Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_pilot), 1);
                    Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_veteran), 1);
                    Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_master), 1);

                    Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_deep), (int) distance);


                    //float inc = (int) (distance*.001);
                    //float par = (int) (distance*.0364);

                    float hyper_spacer_step = (total / 100000) * 100;
                    if ((int) hyper_spacer_step > 0) {
                        Games.Achievements.setStepsImmediate(mGoogleApiClient, getResources().getString(R.string.achievement_hyperspacer), (int) hyper_spacer_step);
                    }
                    float galaxy_across_step = (total / 500000) * 500;
                    if ((int) galaxy_across_step > 0) {
                        Games.Achievements.setStepsImmediate(mGoogleApiClient, getResources().getString(R.string.achievement_galaxyacross), (int) galaxy_across_step);
                    }
                    float light_years_step = (total / 1000000) * 1000;
                    if ((int) light_years_step > 0) {
                        Games.Achievements.setStepsImmediate(mGoogleApiClient, getResources().getString(R.string.achievement_lightyear), (int) light_years_step);
                    }
                    float parsec_step = (total / 3640000) * 10000;
                    if ((int) parsec_step > 0) {
                        Games.Achievements.setStepsImmediate(mGoogleApiClient, getResources().getString(R.string.achievement_parsec), (int) parsec_step);
                    }
				/*
				if((int)inc>0) {
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_hyperspacer), (int)inc);
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_galaxyacross), (int)inc);
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_lightyear),(int) inc);
				}
				else if((int)(storage.getFloatPref("INCREMENT_PROGRESS",0)+inc)>0)
				{
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_hyperspacer),(int)(storage.getFloatPref("INCREMENT_PROGRESS",0)+inc));
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_galaxyacross), (int)(storage.getFloatPref("INCREMENT_PROGRESS",0)+inc));
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_lightyear),(int)(storage.getFloatPref("INCREMENT_PROGRESS",0)+inc));
					storage.setFloatPref("INCREMENT_PROGRESS",0.0f);
				}
				else
				{
					float f = storage.getFloatPref("INCREMENT_PROGRESS",0);
					f+=inc;
					storage.setFloatPref("INCREMENT_PROGRESS",f);
				}
				if((int)par>0) {
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_parsec), (int)par);
				}
				else if((int)(storage.getFloatPref("PAR_INCREMENT_PROGRESS",0)+par)>0)
				{
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_hyperspacer),(int)(storage.getFloatPref("PAR_INCREMENT_PROGRESS",0)+par));
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_galaxyacross), (int)(storage.getFloatPref("PAR_INCREMENT_PROGRESS",0)+par));
					Games.Achievements.increment(mGoogleApiClient, getResources().getString(R.string.achievement_lightyear),(int)(storage.getFloatPref("PAR_INCREMENT_PROGRESS",0)+par));
					storage.setFloatPref("PAR_INCREMENT_PROGRESS",0.0f);
				}
				else
				{
					float f = storage.getFloatPref("PAR_INCREMENT_PROGRESS",0);
					f+=inc;
					storage.setFloatPref("PAR_INCREMENT_PROGRESS",f);
				}
*/
                    if (avg > 1000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_highflyer));
                    }
                    if (avg > 2000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_aviator));
                    }
                    if (avg > 3000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_premium));
                    }
                    if (avg > 4000) {
                        Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_scrape));
                    }

                }
                dist_counted = true;

                storage.save("scores.txt",scores);
                requestBackup();
                scores.clear();
            }
        }
        else
        {

            if(hasEveryoneCrashed(room))
            {

                if(frist_run)
                {
                    multi_end.startClock();
                    frist_run=false;
                }

                if(multi_end.secondsElapsed()>=2 && !sorted)
                {
                    Comparator cmp = Collections.reverseOrder();
                    Collections.sort(distances,cmp);
                    sorted=true;
                }

                if(!foundPlace && sorted)
                {
                    place=1;
                    //foundPlace=true;

                    String df = new DecimalFormat("##.##").format(distance);

                    for(Float f : distances)
                    {
                        lf = new DecimalFormat("##.##").format(f.floatValue());
                        if(place>=room.getParticipants().size())
                        {
                            place=room.getParticipants().size();
                            foundPlace=true;
                            break;
                        }
                        else if(df.equals(lf))
                        {
                            foundPlace=true;
                            break;
                        }
                        else
                        {
                            place++;
                        }
                    }

                }
                if(foundPlace && sorted && readyForStandings(room)) {
                    state = GameState.SHOW_STANDINGS;
                    sorted = false;
                    foundPlace=false;

                    life=3;
                    distance=0;
                    r_x=360;
                    r_y=800;
                    mak_cash=0;
                    c_diff = 0;
                    game_start=false;
                }
            }
        }

		List<TouchEvent> touchEvents = input.getTouchEvents();

		for(int i=0;i<touchEvents.size();i++){
			TouchEvent event = touchEvents.get(i);

			if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
			{
                if(playState==PlayState.SINGLE) {
                    b = ui.isButtonPress(event, Assets.retry);
                    q = ui.isButtonPress(event, Assets.quitt);
                }
			}
			else
			{
				b=false;
				q=false;
			}
		}


		if(!game_start)
		{
			//Stars
			stars_y+=yspd;

			if(stars_y>=stars_h)
			{
				stars_y=0;
			}
			stars_yy=stars_y-stars_h;
			//Stars
		}
		else
		{
			//Hyperdrive
			stars_y+=yspd;
			yspd++;

			if(stars_y>=hyp_h)
			{
				stars_y=0;
			}
			stars_yy=stars_y-hyp_h;
			//Hyperdrive
		}

		if(!b && !game_start)
		{
			r_x+=xspd;
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                Assets.rocket.setPosition(r_x, r_y);
            }
            else if(selectedRocket==1)
            {
                Assets.triangle.setPosition(r_x, r_y);
            }
            else if(selectedRocket==2)
            {
                Assets.orbiter.setPosition(r_x, r_y);
            }
		}
		if(r_x<=100)
		{
			if(canChangeDirs)
			{
				xspd=4;
				canChangeDirs=false;
			}

		}
		else if(r_x >=560)
		{
			if(canChangeDirs) {
				canChangeDirs = false;
				xspd = -4;
			}
		}
		else
		{
			canChangeDirs=true;
		}

		if(q)
		{
			distance=0;
            if(playState==PlayState.SINGLE) {
                if(selectedRocket==0) {
                    life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
                }
                else if(selectedRocket==1)
                {
                    life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
                }
                else if(selectedRocket==2)
                {
                    life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
                    maxLife=life;
                    agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
                }
            }
            else
            {
                life=3;
            }
			state=GameState.MAIN_MENU;
            mak_cash=0;
			game_start=false;
            q=false;
		}
		if(b)
		{
            mak_cash=0;
			game_start=true;
            b=false;
		}

		for(int ar=0; ar<(old_asteroids+5); ar++)
		{
			a_exist[ar]=false;
			ax[ar] = 400;
			ay[ar] = 1500;

            c_exist[ar] = false;
            cx[ar] = 400;
            cy[ar] = 1500;
		}

		if(game_start)
		{
            if(selectedRocket==0 || playState==PlayState.MULTI) {
                r_x = Assets.rocket.getPosition().x;
                r_y = Assets.rocket.getPosition().y;
            }
            else if(selectedRocket==1)
            {
                r_x = Assets.triangle.getPosition().x;
                r_y = Assets.triangle.getPosition().y;
            }
            else if(selectedRocket==2)
            {
                r_x = Assets.orbiter.getPosition().x;
                r_y = Assets.orbiter.getPosition().y;
            }

			if(yspd>=75)
			{
				//once speed is attained begin
                if(playState==PlayState.SINGLE) {
                    if(selectedRocket==0) {
                        life = Integer.parseInt(boosts.get(0)) + standard.get_lives();
                        agility = Integer.parseInt(boosts.get(1)) + standard.get_agility();
                        maxLife=life;
                    }
                    else if(selectedRocket==1)
                    {
                        life = Integer.parseInt(boosts.get(0)) + triangle.get_lives();
                        agility = Integer.parseInt(boosts.get(1)) + triangle.get_agility();
                        maxLife=life;
                    }
                    else if(selectedRocket==2)
                    {
                        life = Integer.parseInt(boosts.get(0)) + orbiter.get_lives();
                        agility = Integer.parseInt(boosts.get(1)) + orbiter.get_agility();
                        maxLife=life;
                    }
                }
                else
                {
                    life=3;
                    agility = 12;
                }
				distance=0;
				r_x=360;
				r_y=800;
                mak_cash=0;
                c_diff = 0;
				state=GameState.GAME_RUNNING;
				game_start=false;
			}
		}
	}


    private void show_matchup_update()
    {
        List<TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++){
            TouchEvent event = touchEvents.get(i);

            if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
            {
                accept = ui.isButtonPress(event, Assets.accept);
                reject = ui.isButtonPress(event, Assets.reject);
            }
            else
            {
                accept=false;
                reject=false;
            }
        }
    }

    boolean hasEveryoneCrashed(Room roo) {

        for (Participant p : roo.getParticipants()) {
            String pid = p.getParticipantId();
            if (p.getStatus()==Participant.STATUS_JOINED && !crashedPlayers.contains(pid)) {
                // at least one person connected hasn't crashed
                return false;
            }
        }
        // all players who are connected have crashed
        return true;
    }

    boolean readyForStandings(Room roo)
    {
        int size = 0;
        for (Participant p : roo.getParticipants()) {
            String pid = p.getParticipantId();
            if (p.isConnectedToRoom()) {
                // at least one person connected hasn't crashed
                size++;
            }
        }

        return size>=distances.size();
    }

    boolean play,leave;
    boolean gotToZero;
    private void rematch_update()
    {
        Assets.leave_button.setPosition(74,931);
        List<TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++){
            TouchEvent event = touchEvents.get(i);

            if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
            {
                if(shouldStart(room)) {
                    play = ui.isButtonPress(event, Assets.again);
                }
                leave = ui.isButtonPress(event, Assets.leave_button);
            }
            else
            {
                play=false;
            }
        }

        connectedCounter = countConnected(room);

        //Stars
        stars_y+=yspd;

        if(stars_y>=stars_h)
        {
            stars_y=0;
        }
        stars_yy=stars_y-stars_h;

       if( connectedCounter-playAgain.size()==0)
       {
           gotToZero=true;
       }
        //Stars
        if(leave || shouldCancel(room) || ((connectedCounter-playAgain.size()==0 || gotToZero) && connectedCounter<2) || activePlayers<2)
        {
            String s = "REMOVE";
            byte[] remuv_byte_mess = s.getBytes();
            for(Participant p : room.getParticipants()) {
                if(!p.getParticipantId().equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, remuv_byte_mess, mRoomId, p.getParticipantId());
                }
            }
            leave=false;
            gotToZero=false;
            state=GameState.MAIN_MENU;
            playState=PlayState.SINGLE;
            Games.RealTimeMultiplayer.leave(mGoogleApiClient,this,mRoomId);
            playAgain.remove(me);
        }
        if(play)
        {
            activePlayers=0;
            gotToZero=false;
            play=false;
            r_x=360;
            r_y=800;
            hypertime=30;
            crash=false;
            xspd=4;
            life=3;
            String s = "START_GAME";
            byte[] start_byte_mess = s.getBytes();
            for(Participant p : room.getParticipants()) {
                if(!p.getParticipantId().equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, start_byte_mess, mRoomId, p.getParticipantId());
                }
                activePlayers++;
            }
            playState=PlayState.MULTI;
            multiPlayState=MultiPlayerGameState.FRIEND;
            state = GameState.GAME_RUNNING;
        }
    }

    boolean shouldStart(Room r)
    {
        int connectedPlayers = 0;
        for (Participant p : r.getParticipants()) {
            if (p.isConnectedToRoom() && playAgain.contains(p)) ++connectedPlayers;
        }
        return connectedPlayers >= 2;
    }

    int connectedPlayers, connectedCounter;
    boolean shouldCancel(Room r)
    {
        connectedPlayers = 0;
        for (Participant p : r.getParticipants()) {
            if (p.getStatus()==Participant.STATUS_JOINED)
            {
                connectedPlayers++;
            }
        }
        return (connectedPlayers < 2);
    }

    int countConnected(Room r)
    {
        int connected = 0;
        for (Participant p : r.getParticipants()) {
            if (p.getStatus()==Participant.STATUS_JOINED)
            {
                connected++;
            }
        }
        return connected;
    }

    boolean lev, again;
    Participant me;
    private void standings_update()
    {
        Assets.leave_button.setPosition(236,876);
        List<TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++){
            TouchEvent event = touchEvents.get(i);

            if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
            {
                again = ui.isButtonPress(event, Assets.continue_button);
                lev = ui.isButtonPress(event, Assets.leave_button);
            }
            else
            {
                again=false;
                lev=false;
            }
        }

        crashedPlayers.clear();
        distances.clear();

        if(again)
        {
            life=3;
            distance=0;
            r_x=360;
            r_y=800;
            mak_cash=0;
            c_diff = 0;
            again = false;

            byte[] again_byte_mess = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)).getBytes();
            for (Participant p : room.getParticipants()) {
                if (!p.getParticipantId().equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, again_byte_mess, mRoomId, p.getParticipantId());
                }
                else
                {
                    playAgain.add(p);
                    me = p;
                }
            }
            state=GameState.REMATCH;
        }

        if(lev || shouldCancel(room) || activePlayers<2)
        {
            String s = "REMOVE";
            byte[] remuv_byte_mess = s.getBytes();
            for(Participant p : room.getParticipants()) {
                if(!p.getParticipantId().equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, remuv_byte_mess, mRoomId, p.getParticipantId());
                }
            }
            lev=false;
            state = GameState.MAIN_MENU;
            playState=PlayState.SINGLE;

            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            activePlayers--;
        }

        //Stars
        stars_y+=yspd;

        if(stars_y>=stars_h)
        {
            stars_y=0;
        }
        stars_yy=stars_y-stars_h;
        //Stars

    }
/*
*
* SAVE FOR RAINY DAY. Might be good to have actual resetting sequence
*
    private void begin_game_update()
    {
        //Hyperdrive
        stars_y+=yspd;
        yspd++;

        if(stars_y>=hyp_h)
        {
            stars_y=0;
        }
        stars_yy=stars_y-hyp_h;
        //Hyperdrive

        if(yspd>=75)
        {
            //once speed is attained begin
            life=3;
            distance=0;
            r_x=360;
            r_y=900;
            mak_cash=0;
            state=GameState.GAME_RUNNING;
            game_start=false;
        }
    }
    */

    boolean accept, reject;
    public void show_invite_update()
    {
        List<TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++){
            TouchEvent event = touchEvents.get(i);

            if(event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN)
            {
                accept = ui.isButtonPress(event, Assets.accept);
                reject = ui.isButtonPress(event, Assets.reject);
            }
            else
            {
                accept=false;
                reject=false;
            }
        }

        if(accept)
        {
            state=GameState.MAIN_MENU;
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.setInvitationIdToAccept(mIncomingInvitationId);
            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
            accept=false;
            invited=false;
           // state=GameState.SHOW_MATCHUP;
        }

        if(reject)
        {
            state=GameState.MAIN_MENU;
            reject=false;
            invited=false;
        }
    }


	public void update() {
		// TODO Auto-generated method stub
		if(state==GameState.END_GAME)
		{
            crash_update();
		}
		else if(state==GameState.GAME_RUNNING)
		{
			game_run_update();
		}
		else if(state==GameState.GAME_PAUSE)
		{
			game_pause_update();
		}
        else if(state==GameState.SHOW_MATCHUP)
        {
            show_matchup_update();
        }
        else if(state==GameState.REMATCH)
        {
            rematch_update();
        }
        else if(state==GameState.SHOW_INVITE)
        {
            show_invite_update();
        }
        else if(state==GameState.SHOW_STANDINGS)
        {
            standings_update();
        }
		else
		{
            menu_update();
		}
	}

	public void paint(float i) {
		// TODO Auto-generated method stub
		if(state==GameState.MAIN_MENU)
		{
			if(!game_start)
			{
				g.drawSprite(Assets.stars, stars_x, (int)(stars_y +(yspd*i)));
				g.drawSprite(Assets.stars, stars_x, (int)(stars_yy +(yspd*i)));
			}
			else
			{
				g.drawSprite(Assets.hyperdrive, stars_x, (int)(stars_y +(yspd*i)));
				g.drawSprite(Assets.hyperdrive, stars_x, (int)(stars_yy +(yspd*i)));
			}

			if(!bp && !game_start)
			{
                if(selectedRocket==0 || playState == PlayState.MULTI) {
                    g.animateSheetRow(Assets.rocket, (r_x + (xspd * i)), r_y, 1, 5, 0);
                }
                else if(selectedRocket==1 && playState != PlayState.MULTI)
                {
                    g.animateSheetRow(Assets.triangle, (r_x + (xspd * i)), r_y, 1, 5, 0);
                }
                else if(selectedRocket==2 && playState != PlayState.MULTI)
                {
                    g.animateSheetRow(Assets.orbiter, (r_x + (xspd * i)), r_y, 1, 5, 0);
                }
			}
			else
			{
				if(!game_start)
                {
                    if(selectedRocket==0 || playState == PlayState.MULTI) {
                        g.animateSpriteSheetToCoordinates(Assets.rocket, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
                    else if(selectedRocket==1 && playState!=PlayState.MULTI)
                    {
                        g.animateSpriteSheetToCoordinates(Assets.triangle, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
                    else if(selectedRocket==2 && playState != PlayState.MULTI)
                    {
                        g.animateSpriteSheetToCoordinates(Assets.orbiter, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
				}
				else
				{
                    if(selectedRocket==0 || playState == PlayState.MULTI) {
                        g.animateSheetRow(Assets.rocket, (r_x + (xspd * i)), r_y, 2, 5, 0);
                    }
                    else if(selectedRocket==1 && playState!=PlayState.MULTI)
                    {
                        g.animateSheetRow(Assets.triangle, (r_x + (xspd * i)), r_y, 2, 5, 0);
                    }
                    else if(selectedRocket==2 && playState != PlayState.MULTI)
                    {
                        g.animateSheetRow(Assets.orbiter, (r_x + (xspd * i)), r_y, 2, 5, 0);
                    }
				}
			}

			g.drawSprite(Assets.asteroid, a_x, (int)(a_y+(a_yspd*i)), rotate);
			g.drawSprite(Assets.asteroid, aa_x, (int)(aa_y+(a_yspd*i)), rotate);

			if(!game_start)
			{
                if(playState==PlayState.SINGLE)
                {
                    g.drawSprite(Assets.play);
                    g.drawSprite(Assets.stats);
                    g.drawSprite(Assets.settings);
                    g.drawSprite(Assets.store);
                    if(mGoogleApiClient.isConnected()){
                        g.drawSprite(Assets.multiToggle);
                    }

                    g.drawRect(455,0,265,45,Color.argb(150,80,80,80));
                    g.animateSheetRow(Assets.coin, 486, 21, 0, 500,1,0);
                    g.drawString(String.valueOf(currentBank),720,30,right_p);

                    if(mGoogleApiClient.isConnected())
                    {
                        g.drawSprite(Assets.game_service_out);
                    }
                    if (!explicitLogOut && !mGoogleApiClient.isConnected())
                    {
                        g.drawSprite(Assets.game_service_in);
                    }
                }
                else
                {
                    g.drawSprite(Assets.singleToggle);
                   // g.drawSprite(Assets.qplay);
                    g.drawSprite(Assets.fplay);
                   // g.drawSprite(Assets.tplay);

                    if(!invited)
                    {
                        g.drawSprite(Assets.no_mail);
                    }
                    else
                    {
                        g.drawSprite(Assets.mail);
                    }
                }

			}

        }
		else if(state==GameState.GAME_RUNNING)
		{
			if(game_start)
			{
				if(!crash && !endgame) {

					if(!hyperspacer)
					{
						g.drawSprite(Assets.stars, stars_x, (int) (stars_y + (yspd * i)));
						g.drawSprite(Assets.stars, stars_x, (int) (stars_yy + (yspd * i)));
                        if(selectedRocket==0 || playState == PlayState.MULTI) {
                            g.animateSheetRow(Assets.rocket, (r_x + (r_turn_spd * i)), r_y, 1, 5, r_ang);
                        }
                        else if(selectedRocket==1 && playState!=PlayState.MULTI)
                        {
                            g.animateSheetRow(Assets.triangle, (r_x + (r_turn_spd * i)), r_y, 1, 5, r_ang);
                        }
                        else if(selectedRocket==2 && playState != PlayState.MULTI)
                        {
                            g.animateSheetRow(Assets.orbiter, (r_x + (r_turn_spd * i)), r_y, 1, 5, r_ang);
                        }
					}
					else
					{
						g.drawSprite(Assets.hyperdrive, stars_x, (int) (stars_y + (yspd * i)));
						g.drawSprite(Assets.hyperdrive, stars_x, (int) (stars_yy + (yspd * i)));
                        if(selectedRocket==0 || playState == PlayState.MULTI) {
                            g.animateSheetRow(Assets.rocket, (r_x + (r_turn_spd * i)), r_y, 2, 5, r_ang);
                        }
                        else if(selectedRocket==1 && playState!=PlayState.MULTI)
                        {
                            g.animateSheetRow(Assets.triangle, (r_x + (r_turn_spd * i)), r_y, 2, 5, r_ang);
                        }
                        else if(selectedRocket==2 && playState != PlayState.MULTI)
                        {
                            g.animateSheetRow(Assets.orbiter, (r_x + (r_turn_spd * i)), r_y, 2, 5, r_ang);
                        }
					}

					for (int a_ii = 0; a_ii < asteroids; a_ii++) {
						if (a_typ[a_ii] > 4) {
							g.drawSprite(Assets.asteroid, ax[a_ii], (int)(ay[a_ii]+(a_spd[a_ii]*i)), a_ang[a_ii]);
						} else {
							g.drawSprite(Assets.tiny_asteroid, ax[a_ii], (int)(ay[a_ii]+(a_spd[a_ii]*i)), a_ang[a_ii]);
						}
					}

                    if(playState==PlayState.SINGLE) {
                        for (int c_ii = 0; c_ii < coins; c_ii++) {
                            g.animateSheetRow(Assets.coin, cx[c_ii], (int) (cy[c_ii] + (c_spd[c_ii] * i)), 0, 1000, 0, 0);
                        }

                        g.drawSprite(Assets.pause);
                    }
					g.drawSprite(Assets.left);
					g.drawSprite(Assets.right);


					g.drawString("Distance: " + new DecimalFormat("##.##").format(distance), 5, 36, paint);
					g.drawString("HyperTime: " + String.valueOf(hypertime - time_elapsed), 5, 72, paint);
					//g.drawString("Asteroids Incoming: " + String.valueOf(asteroids), 5, 108, paint);
					g.drawString("Coins: " + String.valueOf(mak_cash), 5, 108, paint);
					g.drawString("Speed: " + String.valueOf(yspd), 5, 144, paint);

					g.drawString("Lives: " + String.valueOf(life), 280, 1165, paint);
				}
				else
				{
					//crash animation
					g.drawSprite(Assets.stars, stars_x, stars_y);
					g.drawSprite(Assets.stars, stars_x, stars_yy);
                    if(selectedRocket==0 || playState == PlayState.MULTI) {
                        g.animateSheetByIndex(Assets.rocket, 13, 20, 10, crash_first, r_ang);
                    }
                    else if(selectedRocket==1 && playState != PlayState.MULTI)
                    {
                        g.animateSheetByIndex(Assets.triangle, 13, 20, 10, crash_first, r_ang);
                    }
                    else if(selectedRocket==2 && playState != PlayState.MULTI)
                    {
                        g.animateSheetByIndex(Assets.orbiter, 13, 20, 10, crash_first, r_ang);
                    }
				}
			}
			else
			{
				g.drawSprite(Assets.hyperdrive, stars_x, stars_y);
				g.drawSprite(Assets.hyperdrive, stars_x, stars_yy);
                if(selectedRocket==0 || playState == PlayState.MULTI) {
                    g.animateSheetRow(Assets.rocket, 2, 5);
                }
                else if(selectedRocket==1 && playState!=PlayState.MULTI)
                {
                    g.animateSheetRow(Assets.triangle, 2, 5);
                }
                else if(selectedRocket==2 && playState != PlayState.MULTI)
                {
                    g.animateSheetRow(Assets.orbiter, 2, 5);
                }
			}
            //g.drawHitBox(r_x, r_y, r_w, r_h, ax[0], ay[0], a_tw, a_th, r_ang, box);
		}
		else if(state==GameState.GAME_PAUSE)
		{
			g.drawSprite(Assets.pause_screen, 47, 150);

			g.drawSprite(Assets.resume);
			g.drawSprite(Assets.quit);
		}
		else if(state==GameState.END_GAME)
 		{
			if(!game_start)
			{
				g.drawSprite(Assets.stars, stars_x, (int)(stars_y +(yspd*i)));
				g.drawSprite(Assets.stars, stars_x, (int)(stars_yy +(yspd*i)));
			}
			else
			{
				g.drawSprite(Assets.hyperdrive, stars_x, (int)(stars_y +(yspd*i)));
				g.drawSprite(Assets.hyperdrive, stars_x, (int)(stars_yy +(yspd*i)));
			}

			if(game_start)
			{
				if(!collision.isInPosition(Assets.rocket, pos,3))
				{
                    if(selectedRocket==0 || playState == PlayState.MULTI) {
                        g.animateSpriteSheetToCoordinates(Assets.rocket, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
                    else if(selectedRocket==1 && playState != PlayState.MULTI)
                    {
                        g.animateSpriteSheetToCoordinates(Assets.triangle, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
                    else if(selectedRocket==2 && playState != PlayState.MULTI)
                    {
                        g.animateSpriteSheetToCoordinates(Assets.orbiter, 0, 0, r_x, r_y, 360, 800, .5f, 0);
                    }
				}
				else
				{
                    if(selectedRocket==0 || playState == PlayState.MULTI) {
                        g.animateSheetRow(Assets.rocket, 360, 800, 2, 5, 0);
                    }
                    else if(selectedRocket==1 && playState != PlayState.MULTI)
                    {
                        g.animateSheetRow(Assets.triangle, 360, 800, 2, 5, 0);
                    }
                    else if(selectedRocket==2 && playState != PlayState.MULTI)
                    {
                        g.animateSheetRow(Assets.orbiter, 360, 800, 2, 5, 0);
                    }
				}
			}
			else
			{
			//	g.animateSheetRow(Assets.rocket,1, 5);
			}

			if(!game_start)
			{
				g.drawSprite(Assets.crash_screen);
				g.drawString("Distance: " + String.valueOf(distance), 190, 475, paint);

                if(playState==PlayState.SINGLE) {
                    g.drawSprite(Assets.retry);
                    g.drawSprite(Assets.quitt);
                    g.drawString("+" + String.valueOf(c_diff), 450, 30, right_blue);
                    g.drawRect(455, 0, 265, 45, Color.argb(150,80,80,80));
                    g.animateSheetRow(Assets.coin, 470, 5, 0, 5);
                    g.drawString(String.valueOf(currentBank), 720, 30, right_p);
                }
			}
            if(playState == PlayState.MULTI) {
                if (!hasEveryoneCrashed(room)) {
                    g.drawString("Waiting for all Crashes", 100, 700, paint);
                } else if (hasEveryoneCrashed(room)) {
                    g.drawString("Loading Results...", 100, 700, paint);
                }
            }
		}
        else if(state==GameState.SHOW_MATCHUP)
        {

        }
        else if(state==GameState.REMATCH)
        {
            g.drawSprite(Assets.stars, stars_x, (int)(stars_y +(yspd*i)));
            g.drawSprite(Assets.stars, stars_x, (int)(stars_yy +(yspd*i)));
            g.drawSprite(Assets.rematch,32,200);

            g.drawSprite(Assets.leave_button);

            int lol = 600;
            for(Participant p : playAgain)
            {
                g.drawString(p.getDisplayName(),125,lol,black_paint);
                lol+=75;
            }

            if(shouldStart(room))
            {
                g.drawSprite(Assets.again);
            }

            g.drawString(String.valueOf(connectedCounter - playAgain.size()) + " players undecided",150,450,black_paint);
        }
        else if(state==GameState.SHOW_INVITE)
        {
            g.drawSprite(Assets.invite_popup);
            g.drawSprite(Assets.accept);
            g.drawSprite(Assets.reject);
            g.drawString("From: " + inviter,105,318,black_paint);
        }
        else if(state==GameState.SHOW_STANDINGS)
        {
            g.drawSprite(Assets.stars, stars_x, (int)(stars_y +(yspd*i)));
            g.drawSprite(Assets.stars, stars_x, (int) (stars_yy + (yspd * i)));

            if(place==1)
            {
                g.drawSprite(Assets.first,32,200);
            }
            else if(place==2)
            {
                g.drawSprite(Assets.second,32,200);
            }
            else if(place==3)
            {
                g.drawSprite(Assets.third,32,200);
            }
            else
            {
                g.drawSprite(Assets.fourth,32,200);
            }

            g.drawString("Distance: "+ lf,225,625,paint);


            g.drawSprite(Assets.continue_button);
            g.drawSprite(Assets.leave_button);

        }

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
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void menuButton() {
		// TODO Auto-generated method stub
		
	}

	private void generateAsteroid(int i, int range)
	{
		Random rx=new Random();
		ax[i]=rx.nextInt(720);
		Random ry=new Random();
		ay[i]=ry.nextInt(range)*-1;
		Random ra=new Random();

		Random rt=new Random();
		a_typ[i]=rt.nextInt(10);
		if(a_typ[i]>4) {
			a_ang_spd[i] = ra.nextInt(min_yspd + 10);
			a_spd[i] = a_ang_spd[i] + 5;
		}
		else {
			a_ang_spd[i] = ra.nextInt(min_yspd + 15);
			a_spd[i] = a_ang_spd[i] + 7;
		}
		a_exist[i]=true;
	}

    int[] cx = new int[1000];
    int[] cy = new int[1000];
    int[] c_spd = new int[1000];
    boolean[] c_exist = new boolean[1000];
    private void generateCoins(int i, int range)
    {
        Random rx=new Random();
        cx[i]=rx.nextInt(720);
        Random ry=new Random();
        cy[i]=ry.nextInt(range)*-1;
        Random ra=new Random();
        c_spd[i] = ra.nextInt(min_yspd + 10)+5;

        c_exist[i]=true;
    }

    int recursiveCoinCollector(int lvl, int coinz, int clev){
        int c = coinz;
        if(clev == lvl){
            return coinz;
        }
        else {
            if (clev++ % 5 == 0) {
                c--;
            } else {
                c+=2;
            }
        }
        return recursiveCoinCollector(lvl,c,clev++);
    }


    //THE SECRET MAGIC REAL TIME MULTIPLAYER TRICKERY!!!!!!!

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        RoomConfig.Builder builder = RoomConfig.builder(this);
        builder.setMessageReceivedListener(this);
        builder.setRoomStatusUpdateListener(this);

        // ...add other listeners as needed...

        return builder;
    }

    private void startQuickGame()
    {
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

    }
    private void startFriendGame()
    {


        // launch the player selection screen
        // minimum: 1 other player; maximum: 7 other players
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        startActivityForResult(intent, RC_SELECT_PLAYERS);

        //Amount of Games TBD by user, prolly 1-5

    }
    private void doMagicTournament()
    {


        // launch the player selection screen
        // minimum: 1 other player; maximum: 3 other players
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        startActivityForResult(intent, RC_SELECT_PLAYERS);

        /*
        * Lowest score eliminated
         */
    }

    @Override
    public void onRoomCreated(int statusCode , Room roo) {

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.d("WTF GOOGLE GAMES ROOM",String.valueOf(statusCode));
            String error="Game Creation Failed";

            if(statusCode==GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA)
            {
                error="Connection failed, connect and try again";
            }
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, error, duration);
            toast.show();

            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
        }
        else {
            room=roo;
            mRoomId=room.getRoomId();

            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 2);
            startActivityForResult(i, RC_WAITING_ROOM);

            mPlayerID = Games.Players.getCurrentPlayerId(mGoogleApiClient);
            mCurrentParticipant = room.getParticipantId(mPlayerID);
        }
    }

    @Override
    public void onJoinedRoom(int statusCode , Room roo) {

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.d("WTF GOOGLE GAMES ROOM",String.valueOf(statusCode));
            String error="Game Creation Failed";

            if(statusCode==GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA)
            {
                error="Connection failed, connect and try again";
            }
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, error, duration);
            toast.show();

            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
        }else {
            room = roo;
            mRoomId = room.getRoomId();

            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 2);
            startActivityForResult(i, RC_WAITING_ROOM);


            mPlayerID = Games.Players.getCurrentPlayerId(mGoogleApiClient);
            mCurrentParticipant = room.getParticipantId(mPlayerID);
        }
    }

    boolean canStartRoom=true;
    @Override
    public void onLeftRoom(int i, String s) {
        canStartRoom=true;
    }

    @Override
    public void onRoomConnected(int statusCode , Room roo) {
            if (statusCode != GamesStatusCodes.STATUS_OK) {

            }
            else {
                room = roo;
                mRoomId = room.getRoomId();
            }
    }

    int playersCrashed=0;

    String crasher;
    //boolean crash_flag;
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        String string = new String(realTimeMessage.getMessageData());

        if(string.equals("START_GAME"))
        {
            for(Participant p : room.getParticipants())
            {
                if(p.getParticipantId().equals(mCurrentParticipant) && !p.isConnectedToRoom())
                {
                    mWaitingRoomFinishedFromCode = true;
                    finishActivity(RC_WAITING_ROOM);
                    Games.RealTimeMultiplayer.leave(mGoogleApiClient,this,mRoomId);
                    playState=PlayState.SINGLE;
                    state=GameState.MAIN_MENU;
                }
            }
            finishActivity(RC_SELECT_PLAYERS);
            r_x=360;
            r_y=800;
            hypertime=30;
            crash=false;
            xspd=4;
            playState=PlayState.MULTI;
            multiPlayState=MultiPlayerGameState.FRIEND;
            state = GameState.GAME_RUNNING;
        }
        else if(string.equals("REMOVE"))
        {
            String remover = realTimeMessage.getSenderParticipantId();
            for(Participant p : room.getParticipants())
            {
                if(p.getParticipantId().equals(remover))
                {
                    playAgain.remove(p);
                    activePlayers--;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, p.getDisplayName() + " has left the game", duration);
                    toast.show();
                    break;
                }
            }

            if(shouldCancel(room))
            {
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                playState=PlayState.SINGLE;
            }
        }
        else if((state==GameState.SHOW_STANDINGS || state==GameState.REMATCH)&&!string.equals("START_GAME"))
        {
            for(Participant p : room.getParticipants()) {
                if(p.getParticipantId().equals(string)) {
                    playAgain.add(p);
                    break;
                }
            }
        }
        else
        {
            boolean isPlayer = false;

            for(Participant p : room.getParticipants()) {

                if(p.getParticipantId().equals(string)) {
                    crashedPlayers.add(string);
                    playersCrashed++;
                    crasher = p.getDisplayName();

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, crasher + " has crashed!!", duration);
                    toast.show();

                    isPlayer=true;
                    break;
                }
            }

            if(!isPlayer)
            {
                distances.add(Float.parseFloat(string));
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, "Distance: "+string, duration);
                toast.show();
            }
        }
    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room roo, List<String> list) {
        if(state!=GameState.GAME_RUNNING && shouldCancel(roo))
        {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            playState=PlayState.SINGLE;
        }
        else if(state==GameState.GAME_RUNNING && shouldCancel(roo))
        {
            state = GameState.SHOW_STANDINGS;
            place = 1;
        }
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {

    }

    @Override
    public void onPeerLeft(Room roo, List<String> list) {
        if(state!=GameState.GAME_RUNNING && shouldCancel(roo))
        {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            playState=PlayState.SINGLE;
        }
        else if(state==GameState.GAME_RUNNING && shouldCancel(roo))
        {
            state = GameState.SHOW_STANDINGS;
            place = 1;
        }
    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
        playState=PlayState.SINGLE;
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room roo, List<String> list) {
        if(state!=GameState.GAME_RUNNING && shouldCancel(roo))
        {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            playState=PlayState.SINGLE;
        }
        else if(state==GameState.GAME_RUNNING && shouldCancel(roo))
        {
            state = GameState.SHOW_STANDINGS;
            place = 1;
        }
    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    @Override
    public void onRealTimeMessageSent(int i, int i1, String s) {

    }
}