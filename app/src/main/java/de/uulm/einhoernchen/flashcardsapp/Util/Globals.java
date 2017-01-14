package de.uulm.einhoernchen.flashcardsapp.Util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.R;

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
    private static FloatingActionButton floatingActionButton;
    private static FloatingActionButton floatingActionButtonAdd;
    private static int scrollX;


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
     * @param floatingActionButton
     * @param floatingActionButtonAdd
     */
    public static void initGlobals(Context context, ProgressBar progressBar, DbManager db, FragmentManager fragmentManager, FloatingActionButton floatingActionButton, FloatingActionButton floatingActionButtonAdd) {

        Globals.context = context;
        Globals.progressBar = progressBar;
        Globals.db = db;
        Globals.fragmentManager = fragmentManager;
        Globals.floatingActionButton = floatingActionButton;
        Globals.floatingActionButtonAdd = floatingActionButtonAdd;
    }

    public static FloatingActionButton getFloatingActionButtonAdd() {

        if (floatingActionButtonAdd == null) Log.e(TAG, "fab_add is NULL");
        return floatingActionButtonAdd;
    }

    public static void setFloatingActionButtonAdd(FloatingActionButton floatingActionButtonAdd) {
        Globals.floatingActionButtonAdd = floatingActionButtonAdd;
    }

    public static FloatingActionButton getFloatingActionButton() {

        if (floatingActionButton == null) Log.e(TAG, "fab is NULL");
        return floatingActionButton;
    }

    public static void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        Globals.floatingActionButton = floatingActionButton;
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

    public static void setScrollX(int scrollX) {
        Globals.scrollX = scrollX;
    }

    public static int getScrollX() {
        return scrollX;
    }


    /**
     * sets visibility of toolbar of main activity
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-11
     */
    public static void setVisibilityToolbarMain(int visibility) {

        MainActivity mainActivity = (MainActivity) Globals.getContext();
        mainActivity.findViewById(R.id.collapsing_toolbar).setVisibility(visibility);
    }


    /**
     * Sets the visibility of the main fab
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     *
     * @param visibilityFab
     */
    public static void setVisibilityFab(int visibilityFab) {
        Globals.floatingActionButton.setVisibility(visibilityFab);
    }
}
