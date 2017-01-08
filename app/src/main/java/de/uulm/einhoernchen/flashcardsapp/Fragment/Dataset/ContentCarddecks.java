package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalCarddecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.AsyncGetRemoteCarddecks;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalCardDecks;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentCarddecks {

    private static List<CardDeck> cardDecks = new ArrayList<>();
    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param parentId given by the main activity
     * @param backPressed
     *
     */
    public void collectItemsFromServer(final long parentId, final boolean backPressed) {

        this.db = db;

        AsyncGetRemoteCarddecks asyncGetCarddeck = new AsyncGetRemoteCarddecks(parentId, new AsyncGetRemoteCarddecks.AsyncResponseRemoteCarddecks() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                // Saving the collected categories localy
                AsyncSaveLocalCardDecks asyncSaveLocalCarddeck = new AsyncSaveLocalCardDecks(parentId);
                asyncSaveLocalCarddeck.setCardDecks(cardDecks);
                asyncSaveLocalCarddeck.execute();

                isUpToDate = true;

                ContentCarddecks.cardDecks = cardDecks;
                FragmentCarddecks fragment = new FragmentCarddecks();
                fragment.setItemList(cardDecks);
                fragment.setParentId(parentId);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

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

        if (ProcessConnectivity.isOk(Globals.getContext())) {

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
     * @param backPressed
     */
    public void collectItemsFromDb(final long parentId,final boolean backPressed) {

        AsyncGetLocalCarddecks asyncGetLocalCardDeck = new AsyncGetLocalCarddecks(parentId, new AsyncGetLocalCarddecks.AsyncResponseLocalCarddecks() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                ContentCarddecks.cardDecks = cardDecks;

                isUpToDate = false;

                FragmentCarddecks fragment = new FragmentCarddecks();
                fragment.setItemList(cardDecks);
                fragment.setParentId(parentId);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

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

        asyncGetLocalCardDeck.execute();


    }




}
