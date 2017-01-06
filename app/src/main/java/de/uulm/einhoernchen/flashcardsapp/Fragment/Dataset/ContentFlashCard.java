package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalFlashCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentFlashCard {

    private static FlashCard flashCard;

    public static FragmentFlashCard fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *
     * @param flashcardId
     * @param backPressed
     */
    public void collectItemFromServer(final long flashcardId, final boolean backPressed) {

        this.db = db;

        AsyncGetRemoteFlashCard asyncGetFlashCard = new AsyncGetRemoteFlashCard(flashcardId, new AsyncGetRemoteFlashCard.AsyncResponseFlashCard() {

            @Override
            public void processFinish(FlashCard flashCard) {

                // real dummy content generation
                if (ContentFlashCard.flashCard == null) {

                    //Log.d("ContentFlashCards", "no flashcards");
                }

                AsyncSaveLocalFlashCards asyncSaveFlashCardLocal = new AsyncSaveLocalFlashCards(flashcardId);
                asyncSaveFlashCardLocal.setFlashCard(flashCard);
                asyncSaveFlashCardLocal.execute();

                isUpToDate = true;

                FragmentFlashCard fragment = new FragmentFlashCard();
                fragment.setItem(flashCard);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, flashcardId);
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

                //Log.d("async load", "online");
            }

        });


        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetFlashCard.execute();
        }

    }


    /**
     * Get data fro local sqlite db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param flashcardId
     * @param backPressed
     */
    public void collectItemFromDb(final long flashcardId, final boolean backPressed) {

        AsyncGetLocalFlashCard asyncGetFlashCardLocal = new AsyncGetLocalFlashCard(flashcardId, new AsyncGetLocalFlashCard.AsyncResponseLocalFlashCard() {

            @Override
            public void processFinish(FlashCard flashCard) {

                ContentFlashCard.flashCard = flashCard;
                isUpToDate = false;

                FragmentFlashCard fragment = new FragmentFlashCard();
                fragment.setItem(flashCard);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, flashcardId);
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

        asyncGetFlashCardLocal.execute();

    }



}
