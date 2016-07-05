package com.justinkleiber.rocketlaunch;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.justinkleiber.focus2d.base.Audio;
import com.justinkleiber.focus2d.base.Collision;
import com.justinkleiber.focus2d.base.Graphics;
import com.justinkleiber.focus2d.base.Input;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Justin on 6/11/2015.
 */
public class MenuLoop extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Graphics g;
    Audio audio;
    Input input;
    Storage storage;
    Vibrate vibrate;
    MenuRenderer renderer;
    UI ui;
    Collision collision;
    Paint paint;

    GoogleApiClient mGoogleApiClient;

    enum SelectedMenu {
        SETTINGS, STATISTICS, STORE,CONFIRM_PURCHASE,BUYING
    };

    enum StoreMode{
        BOOST, ROCKET, COINS
    }

    SelectedMenu menuState;
    StoreMode storeToggle;

    Paint right_p, big_p;

    boolean board_a, board_b, board_c, achiever, flighter;
    ArrayList<String> boosts,rockets;
    ArrayList<Integer> boost_costs,rocket_costs;
    ArrayList<Boolean> boost_sold, rocket_sold;

    ArrayList<String> user_prefs;

    int selectedRocket;

    int rLife, rAgil;

    FocusClock clock;

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


        collision = new FocusCollision();
        renderer=new MenuRenderer(this,frame);
        input = new FocusInput(this, renderer, scaleX, scaleY);

        setContentView(renderer);

        ui = new FocusUI();

        Typeface space = Typeface.createFromAsset(this.getAssets(), "BMSPACE.TTF");

        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTypeface(space);
        paint.setColor(Color.WHITE);


        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        Bundle b = getIntent().getExtras();
        int i;
        i=b.getInt("MENU");


        switch(i)
        {
            case 0:
                menuState=SelectedMenu.SETTINGS;
                break;
            case 1:
                menuState=SelectedMenu.STATISTICS;
                break;
            case 2:
                menuState=SelectedMenu.STORE;
                break;
            default:
                menuState=SelectedMenu.SETTINGS;
                break;
        }

        storeToggle=StoreMode.BOOST;


        Assets.coin_desel.setPosition(521, 295);
        Assets.rock_desel.setPosition(268, 295);
        Assets.boost_desel.setPosition(15, 295);

        //Boost
        Assets.shields.setPosition(10,425);
        Assets.agility.setPosition(10,610);
       // Assets.radiation.setPosition(10,980);
       // Assets.store_laser.setPosition(10,795);

        //Rockets
        Assets.store_tri.setPosition(10,425);
        Assets.store_orb.setPosition(10,610);

        //Coins
        Assets.store_coinzz.setPosition(10,425);



        right_p = new Paint();
        right_p.setTextSize(30);
        right_p.setTextAlign(Paint.Align.RIGHT);
        right_p.setAntiAlias(true);
        right_p.setTypeface(space);
        right_p.setColor(Color.WHITE);

        big_p = new Paint();
        big_p.setTextSize(48);
        big_p.setTextAlign(Paint.Align.LEFT);
        big_p.setAntiAlias(true);
        big_p.setTypeface(space);
        big_p.setColor(Color.WHITE);

        Assets.yes_button.setPosition(127, 470);
        Assets.no_button.setPosition(407, 470);

        boost_costs = new ArrayList<Integer>();
        rocket_costs = new ArrayList<Integer>();

        boost_sold = new ArrayList<Boolean>();
        rocket_sold = new ArrayList<Boolean>();

        user_prefs = new ArrayList<String>();

        if(!storage.isExist("user_prefs.txt"))
        {
            user_prefs.add("1");
        }
        else
        {
            user_prefs = storage.loadToArray("user_prefs.txt");
        }

        calculateCosts();
        isSoldOut();
        checkMoney();

        selectedRocket = storage.getIntPref("CURRENT_ROCKET",0);

        Assets.sel_std.setPosition(100, 400);
        Assets.desel_std.setPosition(100,400);

        Assets.sel_tri.setPosition(400,400);
        Assets.desel_tri.setPosition(400,400);

        Assets.sel_orb.setPosition(100,700);
        Assets.desel_orb.setPosition(100,700);


        if(selectedRocket==0)
        {
            rLife=3;
            rAgil=12;
        }
        else if(selectedRocket==1)
        {
            rLife=3;
            rAgil=18;
        }
        else if(selectedRocket==2)
        {
            rLife=5;
            rAgil=10;
        }

        clock = new FocusClock();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(storage.getBoolPref("GPGS_Login_status",false)) {
            mGoogleApiClient.connect();
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
        renderer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        storage.setPref("GPGS_Login_status", mGoogleApiClient.isConnected() && mGoogleApiClient != null);
        mGoogleApiClient.disconnect();
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }

    int currentBank;
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


    int pendingCost, pendingCategory=-1, pendingItem=-1;
    private void withdrawCoinsAndBuy(int cost,int category, int item)
    {
        currentBank-=cost;
        storage.save("coin_bank.txt",String.valueOf(currentBank));

        if(category==0)
        {
            int bp = Integer.parseInt(boosts.get(item)) + 1;
            boosts.set(item, String.valueOf(bp));
            storage.save("boost_progress.txt",boosts);
        }
        else if(category==1)
        {
            rockets.set(item,"1");
            storage.save("owned_rockets.txt",rockets);
        }

        requestBackup();
    }

    private void calculateCosts()
    {
        for(int i=0;i<boosts.size();i++)
        {
            int c;
            //c = (int) ((100 * Math.pow(2, i + 1)));
            c=400;
            boost_costs.add(c);
        }
        for(int ii=0;ii<rockets.size();ii++)
        {
            int rc = 1000;
            rocket_costs.add(rc);
        }
    }

    private boolean isPurchasable(int category, int item)
    {
        if(category==0)
        {
            return currentBank >= boost_costs.get(item);
        }
        else if(category==1)
        {
            return currentBank >= rocket_costs.get(item);
        }
        return false;
    }

    private void isSoldOut()
    {
        boost_sold.clear();
        rocket_sold.clear();
        for(int i=0;i<boosts.size();i++)
        {
            int c = Integer.parseInt(boosts.get(i));

            boost_sold.add(c>=8);
        }
        for(int ii=0;ii<rockets.size();ii++)
        {
            int rc = Integer.parseInt(rockets.get(ii));
            rocket_sold.add(rc>=1);
        }
    }

    private boolean amISoldOut(int category, int item)
    {
        if(category==0)
        {
            return boost_sold.get(item);
        }
        else if (category==1)
        {
            return rocket_sold.get(item);
        }
        return true;
    }

    private void saveUserPrefs()
    {
        storage.save("user_prefs.txt",user_prefs);
        requestBackup();
    }

    boolean vibrate_press, old_vibrate_press;
    boolean select_std, select_tri, select_orb;
    void settings_update()
    {
        if(mGoogleApiClient.isConnected() && mGoogleApiClient!=null) {

            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_craft));
        }

        List<Input.TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++)
        {
            Input.TouchEvent event = touchEvents.get(i);

            if(event.type == Input.TouchEvent.TOUCH_DRAGGED ||event.type == Input.TouchEvent.TOUCH_DOWN )
            {
                vibrate_press = ui.inBounds(event, vx,vy,vw,vh);
                select_std = ui.isButtonPress(event, Assets.desel_std);
                select_tri = ui.isButtonPress(event, Assets.desel_tri);
                select_orb = ui.isButtonPress(event, Assets.desel_orb);
            }

            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                vibrate_press = false;
            }
        }

        if(vibrate_press && !old_vibrate_press)
        {
            if(user_prefs.get(0).equals("0"))
            {
                user_prefs.set(0,"1");
            }
            else
            {
                user_prefs.set(0,"0");
            }

            saveUserPrefs();
        }

        if(select_std && selectedRocket!=0)
        {
            selectedRocket=0;
            storage.setPref("CURRENT_ROCKET",selectedRocket);
        }

        if(select_tri && selectedRocket!=1)
        {
            selectedRocket=1;
            storage.setPref("CURRENT_ROCKET",selectedRocket);
        }

        if(select_orb && selectedRocket!=2)
        {
            selectedRocket=2;
            storage.setPref("CURRENT_ROCKET",selectedRocket);
        }

        old_vibrate_press = vibrate_press;
    }
    void stats_update()
    {
        Assets.achieve.setPosition(100, 650);
        Assets.highavg_boards.setPosition(100, 735);

        List<Input.TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++)
        {
            Input.TouchEvent event = touchEvents.get(i);

            if(event.type == Input.TouchEvent.TOUCH_DRAGGED ||event.type == Input.TouchEvent.TOUCH_DOWN )
            {
                board_a = ui.isButtonPress(event, Assets.highavg_boards);
                achiever = ui.isButtonPress(event, Assets.achieve);
            }

            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                board_a = false;
                achiever = false;
            }
        }
        if(mGoogleApiClient.isConnected() && mGoogleApiClient!=null) {

            Games.Achievements.unlock(mGoogleApiClient,getResources().getString(R.string.achievement_stats));
            if (board_a) {

                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),2000);
            }

            if (achiever) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                        600);
            }

        }
    }
    boolean boost, rock, con;
    boolean shield, agile, laser, rad;
    boolean triangle, orbiter;
    boolean flagger;
    void store_update()
    {
        List<Input.TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++) {
            Input.TouchEvent event = touchEvents.get(i);

            if (event.type == Input.TouchEvent.TOUCH_DRAGGED || event.type == Input.TouchEvent.TOUCH_DOWN) {
                boost = ui.isButtonPress(event, Assets.boost_desel);
                rock = ui.isButtonPress(event, Assets.rock_desel);
                con = ui.isButtonPress(event, Assets.coin_desel);

                if(!amISoldOut(0,0) && storeToggle==StoreMode.BOOST)
                {
                    shield = ui.isButtonPress(event, Assets.shields);
                }
                if(!amISoldOut(0,1) && storeToggle==StoreMode.BOOST) {
                    agile = ui.isButtonPress(event, Assets.agility);
                }
              /*  if(!amISoldOut(0,2) && storeToggle==StoreMode.BOOST) {
                    laser = ui.isButtonPress(event, Assets.store_laser);
                }
                if(!amISoldOut(0,0) && storeToggle==StoreMode.BOOST) {
                    rad = ui.isButtonPress(event, Assets.radiation);
                }*/
                if(!amISoldOut(1,0) && storeToggle==StoreMode.ROCKET) {
                    triangle = ui.isButtonPress(event, Assets.store_tri);
                }
                if(!amISoldOut(1,1) && storeToggle==StoreMode.ROCKET) {
                    orbiter = ui.isButtonPress(event, Assets.store_orb);
                }

            }
            else
            {
                boost=false;
                rock=false;
                con=false;
                shield=false;
                agile=false;
                laser=false;
                rad=false;
                triangle=false;
                orbiter=false;
            }
        }

        if(boost)
        {
            storeToggle=StoreMode.BOOST;
        }
        if(rock)
        {
            storeToggle=StoreMode.ROCKET;
        }
        if(con)
        {
            storeToggle=StoreMode.COINS;
        }

        //BOOSTS
        if(shield && isPurchasable(0,0))
        {
            pendingCost = boost_costs.get(0);
            pendingCategory=0;
            pendingItem=0;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        }

        if(agile && isPurchasable(0,1))
        {
            pendingCost = boost_costs.get(1);
            pendingCategory=0;
            pendingItem=1;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        }
/*
        if(laser && isPurchasable(0,2))
        {
            pendingCost = boost_costs.get(2);
            pendingCategory=0;
            pendingItem=2;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        }

        if(rad && isPurchasable(0,3))
        {
            pendingCost = boost_costs.get(3);
            pendingCategory=0;
            pendingItem=3;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        } */

        //BOOSTS ^^^

        //ROCKETS
        if(triangle && isPurchasable(1,0))
        {
            pendingCost = rocket_costs.get(0);
            pendingCategory=1;
            pendingItem=0;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        }
        if(orbiter && isPurchasable(1,1))
        {
            pendingCost = rocket_costs.get(1);
            pendingCategory=1;
            pendingItem=1;
            menuState = SelectedMenu.CONFIRM_PURCHASE;
        }
    }

    boolean yes, no;
    void confirm_update()
    {
        List<Input.TouchEvent> touchEvents = input.getTouchEvents();

        for(int i=0;i<touchEvents.size();i++)
        {
            Input.TouchEvent event = touchEvents.get(i);

            if(event.type == Input.TouchEvent.TOUCH_DRAGGED ||event.type == Input.TouchEvent.TOUCH_DOWN )
            {
                if(clock.secondsElapsed()>=1) {
                    yes = ui.isButtonPress(event, Assets.yes_button);
                    no = ui.isButtonPress(event, Assets.no_button);
                }
            }

            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                yes = false;
                no = false;
            }
        }


        if(no)
        {
            pendingCost=0;
            pendingCategory=-1;
            pendingItem=-1;
            first=true;
            menuState=SelectedMenu.BUYING;
        }

        if(yes || flagger)
        {
            withdrawCoinsAndBuy(pendingCost,pendingCategory,pendingItem);
            calculateCosts();
            isSoldOut();
            checkMoney();
            flagger=true;

            pendingCost=0;
            pendingItem=-1;
            pendingCategory=-1;
            first=true;
            menuState=SelectedMenu.BUYING;
        }

        if(first)
        {
            clock.startClock();
            first=false;
        }

    }
    boolean first;
    void buy_update()
    {
        if(first)
        {
            clock.startClock();
            first=false;
        }

        if(clock.secondsElapsed()>=1)
        {
            menuState=SelectedMenu.STORE;
        }
    }
    public void update()
    {
        if(menuState==SelectedMenu.SETTINGS)
        {
            settings_update();
        }
        else if(menuState==SelectedMenu.STATISTICS)
        {
            stats_update();
        }
        else if(menuState==SelectedMenu.STORE)
        {
            store_update();
        }
        else if(menuState==SelectedMenu.CONFIRM_PURCHASE)
        {
            confirm_update();
        }
        else if(menuState==SelectedMenu.BUYING)
        {
            buy_update();
        }
    }

    int vx=50,vy=200,vw=600,vh=70;
    void settings_paint(float i)
    {
        //g.drawARGB(255,0,0,0);
        g.drawString("Settings", 260, 30, paint);

        if(user_prefs.get(0).equals("0"))
        {
            g.drawRect(vx,vy,vw,vh,Color.GREEN);
            g.drawString("Turn Vibrate On",60,250,big_p);
        }
        else
        {
            g.drawRect(vx,vy,vw,vh,Color.RED);
            g.drawString("Turn Vibrate Off",60,250,big_p);
        }

        g. drawString("Pick Rocket", 230, 350, paint);

        if(selectedRocket==0) {
            g.drawSprite(Assets.sel_std);
        }
        else
        {
            g.drawSprite(Assets.desel_std);
        }

        if(rockets.get(0).equals("1"))
        {
            if(selectedRocket==1) {
                g.drawSprite(Assets.sel_tri);
            }
            else
            {
                g.drawSprite(Assets.desel_tri);
            }
        }
        if(rockets.get(1).equals("1"))
        {
            if(selectedRocket==2) {
                g.drawSprite(Assets.sel_orb);
            }
            else
            {
                g.drawSprite(Assets.desel_orb);
            }
        }
    }
    void stats_paint(float i)
    {
       // g.drawARGB(255,0,255,0);
        g.drawString("Statistics", 260, 30, paint);

        int flights = storage.getIntPref("TOTAL_FLIGHTS", 0);
        float avg = storage.getFloatPref("AVERAGE",0.0f);
        float tdist = storage.getFloatPref("TOTAL_DISTANCE",0.0f);
        float ldist = storage.getFloatPref("LONGEST_DISTANCE",0.0f);
        g.drawString("Flights Piloted: " + String.valueOf(flights), 5, 150, paint);
        g.drawString("Average Distance: " + String.valueOf(avg) + " m", 5, 250, paint);
        g.drawString("Total Distance: " + String.valueOf(tdist) + " m", 5, 350, paint);
        g.drawString("Longest Flight: " + String.valueOf(ldist) + " m", 5, 450, paint);


        if(mGoogleApiClient.isConnected() && mGoogleApiClient!=null) {
            g.drawString("Google Play Games", 150, 600, paint);
            g.drawSprite(Assets.achieve);
            g.drawSprite(Assets.highavg_boards);

            //g.drawString("Multiplayer Wins: ", 5, 850, paint);
            //g.drawString("Multiplayer Losses: ", 5, 950, paint);
            //g.drawString("Multiplayer Rating: ", 5, 1050, paint);
        }
    }
    void store_paint(float i) {
        // g.drawSprite(Assets.viewer,360,123,0);
        g.drawRect(0, 0, 725, 1280, Color.argb(255,0,0,88));
        g.drawRect(0,0,720,275,Color.argb(255,0,85,212));
        g.drawRect(0, 295, 720, 114, Color.argb(255, 236, 236, 236));


        g.drawString("Lives: " + String.valueOf(Integer.parseInt(boosts.get(0))+rLife),10, 25, paint);
        g.drawString("Agility: " + String.valueOf(Integer.parseInt(boosts.get(1))+rAgil),10, 75, paint);
        //g.drawString("Lasers: " + String.valueOf(boosts.get(2)),10,125,paint);
        //g.drawString("Radiation Waves: " + String.valueOf(boosts.get(3)),10, 175, paint);

        g.drawRect(455, 0, 265, 45, Color.argb(150, 80, 80, 80));
        g.animateSheetRow(Assets.coin, 486, 21, 0, 5, 1, 0);
        g.drawString(String.valueOf(currentBank), 720, 30, right_p);

        if(storeToggle==StoreMode.BOOST) {
            g.drawSprite(Assets.boost_sel, 15, 295);
            if(!boost_sold.get(0))
            {
                g.drawSprite(Assets.shields);
                g.drawString(String.valueOf(boost_costs.get(0)),55,527,paint);
            }
            if(!boost_sold.get(1))
            {
                g.drawSprite(Assets.agility);
                g.drawString(String.valueOf(boost_costs.get(1)), 55, 712, paint);
            }
            /*
            if(!boost_sold.get(2))
            {
                g.drawSprite(Assets.store_laser);
                g.drawString(String.valueOf(boost_costs.get(2)), 55, 897, paint);
            }
            if(!boost_sold.get(3))
            {
                g.drawSprite(Assets.radiation);
                g.drawString(String.valueOf(boost_costs.get(3)), 55, 1082, paint);
            } */


        }
        else
        {
            g.drawSprite(Assets.boost_desel);
        }

        if(storeToggle==StoreMode.ROCKET) {
            g.drawSprite(Assets.rock_sel, 268, 295);

            if(!rocket_sold.get(0)) {
                g.drawSprite(Assets.store_tri);
                g.drawSprite(Assets.store_orb);
                g.drawString(String.valueOf(rocket_costs.get(0)), 65, 527, paint);
                g.drawString(String.valueOf(rocket_costs.get(1)), 65, 712, paint);
            }
        }
        else
        {
            g.drawSprite(Assets.rock_desel);
        }

        if(storeToggle==StoreMode.COINS) {
            g.drawSprite(Assets.coin_sel, 521, 295);
            //g.drawSprite(Assets.store_coinzz);
            g.drawString("Coming Soon!",100,500,paint);
        }
        else
        {
            g.drawSprite(Assets.coin_desel);
        }




    }
    void confirm_paint()
    {
        g.drawARGB(100,0,0,0);
        g.drawSprite(Assets.confirm_popup, 22, 200);
        g.drawSprite(Assets.yes_button);
        g.drawSprite(Assets.no_button);
    }

    public void paint(float i)
    {
        if(menuState==SelectedMenu.SETTINGS)
        {
            settings_paint(i);
        }
        else if(menuState==SelectedMenu.STATISTICS)
        {
            stats_paint(i);
        }
        else if(menuState==SelectedMenu.STORE)
        {
            store_paint(i);
        }
        else if(menuState==SelectedMenu.CONFIRM_PURCHASE)
        {
            confirm_paint();
        }
    }

    LeaderboardScore average, total_dist, flights, single;
    float lb_avg = 0.0f, lb_td = 0.0f, lb_fl = 0.0f, lb_s = 0.0f;
    @Override
    public void onConnected(Bundle bundle) {

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

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

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
                    RC_SIGN_IN, "There was an issue with sign-in, please try again later.")) {
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
                        requestCode, resultCode, R.string.app_name);
            }
        }
    }
}
