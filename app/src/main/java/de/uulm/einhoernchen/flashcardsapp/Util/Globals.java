package de.uulm.einhoernchen.flashcardsapp.Util;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.06
 */

public class Globals {

    private static final String TAG = "GLOBALS";

    private static Context context;
    private static ProgressBar progressBar;
    private static DbManager db;
    private static FragmentManager fragmentManager;


    /**
     * Initializes all values
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     *
     * @param context
     * @param progressBar
     * @param db
     * @param fragmentManager
     */
    public static void initGlobals(Context context, ProgressBar progressBar, DbManager db, FragmentManager fragmentManager) {

        Globals.context = context;
        Globals.progressBar = progressBar;
        Globals.db = db;
        Globals.fragmentManager = fragmentManager;
    }

    public static Context getContext() {

        if (context == null) Log.e(TAG, "context is NULL");
        return context;
    }

    public static void setContext(Context context) {
        Globals.context = context;
    }

    public static ProgressBar getProgressBar() {

        if (progressBar == null) Log.e(TAG, "bar is NULL");
        return progressBar;
    }

    public static void setProgressBar(ProgressBar progressBar) {
        Globals.progressBar = progressBar;
    }

    public static DbManager getDb() {

        if (db == null) Log.e(TAG, "db is NULL");
        return db;
    }

    public static void setDb(DbManager db) {
        Globals.db = db;
    }

    public static FragmentManager getFragmentManager() {

        if (fragmentManager == null) Log.e(TAG, "fragmentMgmt is NULL");
        return fragmentManager;
    }

    public static void setFragmentManager(FragmentManager fragmentManager) {
        Globals.fragmentManager = fragmentManager;
    }
}
