package com.justinkleiber.rocketlaunch;

/**
 * Created by Justin on 9/2/2015.
 */
public class RocketStats {

    private int _lives,_agility;

    public RocketStats(int lives,int agility){
        this._lives = lives;
        this._agility = agility;
    }

    public int get_lives()
    {
        return _lives;
    }

    public int get_agility()
    {
        return _agility;
    }

}
