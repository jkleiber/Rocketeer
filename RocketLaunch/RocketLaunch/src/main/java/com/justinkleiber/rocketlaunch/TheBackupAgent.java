package com.justinkleiber.rocketlaunch;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

/**
 * Created by Justin on 7/20/2015.
 */
public class TheBackupAgent extends BackupAgentHelper {

    static final String HIGH_SCORES = "scores.txt";
    static final String ROCKETS = "owned_rockets.txt";
    static final String BOOST = "boost_progress.txt";
    static final String COINS = "coin_bank.txt";
    static final String SETTINGS = "user_prefs.txt";
    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "myfiles";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helper = new FileBackupHelper(this, HIGH_SCORES,ROCKETS,BOOST,COINS,SETTINGS);
        addHelper(FILES_BACKUP_KEY, helper);
    }

}
