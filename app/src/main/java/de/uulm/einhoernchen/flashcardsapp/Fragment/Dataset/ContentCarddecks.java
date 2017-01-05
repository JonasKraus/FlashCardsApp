package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalCarddecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteCarddecks;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncSaveLocalCardDecks;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentCarddecks {

    private static List<CardDeck> cardDecks = new ArrayList<>();
    private static boolean isUpToDate = false;
    private static DbManager db;
    private static Context context;

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param parentId given by the main activity
     * @param fragmentManager given by main activity
     *
     */
    public void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed, final DbManager db, Context context) {

        this.db = db;

        AsyncGetRemoteCarddecks asyncGetCarddeck = new AsyncGetRemoteCarddecks(parentId, new AsyncGetRemoteCarddecks.AsyncResponseRemoteCarddecks() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                // Saving the collected categories localy
                AsyncSaveLocalCardDecks asyncSaveLocalCarddeck = new AsyncSaveLocalCardDecks(parentId);
                asyncSaveLocalCarddeck.setDbManager(db);
                asyncSaveLocalCarddeck.setCardDecks(cardDecks);
                asyncSaveLocalCarddeck.execute();

                isUpToDate = true;

                ContentCarddecks.cardDecks = cardDecks;
                FragmentCarddecks fragment = new FragmentCarddecks();
                fragment.setItemList(cardDecks);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();

                /*
                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                */
                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetCarddeck.setProgressbar(progressBarMain);

        if (ProcessConnectivity.isOk(context)) {

            asyncGetCarddeck.execute();
        }

    }


    /**
     * Collects Carddecks from sqLite
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param parentId
     * @param supportFragmentManager
     * @param progressBar
     * @param backPressed
     * @param db
     */
    public void collectItemsFromDb(final long parentId, final FragmentManager supportFragmentManager, final ProgressBar progressBar, final boolean backPressed, final DbManager db, Context context) {

        this.db = db;

        AsyncGetLocalCarddecks asyncGetLocalCardDeck = new AsyncGetLocalCarddecks(parentId, new AsyncGetLocalCarddecks.AsyncResponseLocalCarddecks() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                ContentCarddecks.cardDecks = cardDecks;

                isUpToDate = false;

                FragmentCarddecks fragment = new FragmentCarddecks();
                fragment.setItemList(cardDecks);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        supportFragmentManager.beginTransaction();

                // TODO delete if always loaded from local db
                /*
                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (!fragmentAnimated) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentAnimated = true;
                }
                */

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalCardDeck.setProgressbar(progressBar);
        asyncGetLocalCardDeck.setDbManager(db);
        asyncGetLocalCardDeck.execute();


    }




}
