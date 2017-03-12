package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
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

    // Default container of main activity
    private int fragmentContainder = R.id.fragment_container_main;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *
     * @param flashcardId
     * @param backPressed
     */
    public void collectItemFromServer(final long flashcardId, final boolean backPressed) {

        AsyncGetRemoteFlashCard asyncGetFlashCard = new AsyncGetRemoteFlashCard(flashcardId, new AsyncGetRemoteFlashCard.AsyncResponseFlashCard() {

            @Override
            public void processFinish(FlashCard flashCard) {

                // real dummy content generation
                if (ContentFlashCard.flashCard == null) {

                    //Log.d("ContentFlashCards", "no flashcards");
                }

                AsyncSaveLocalFlashCards asyncSaveFlashCardLocal = new AsyncSaveLocalFlashCards(flashcardId);
                asyncSaveFlashCardLocal.setFlashCard(flashCard);
                asyncSaveFlashCardLocal.setContext(Globals.getContext());
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

                fragmentTransaction.replace(fragmentContainder, fragment);
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

                fragmentTransaction.replace(fragmentContainder, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetFlashCardLocal.execute();

    }


    /**
     * This sets the container the fragment should be placed in
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-12
     *
     * @param fragmentContainder
     */
    public void setFragmentContainder(int fragmentContainder) {
        this.fragmentContainder = fragmentContainder;
    }
}
