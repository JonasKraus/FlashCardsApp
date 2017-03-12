package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalFlashCards;
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
public class ContentFlashCards {

    private static List<FlashCard> flashCards = new ArrayList<FlashCard>();

    public static FragmentFlashcards fragment;

    private static boolean isUpToDate = false;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *@param parentId
     */
    public void collectItemsFromServer(final long parentId,final boolean backPressed) {

        AsyncGetRemoteFlashCards asyncGetFlashCard = new AsyncGetRemoteFlashCards(parentId, new AsyncGetRemoteFlashCards.AsyncResponseFlashCards() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                // real dummy content generation
                if (flashCards == null || flashCards.size() == 0) {

                    //Log.d("ContentFlashCards", "no flashcards");
                }

                AsyncSaveLocalFlashCards asyncSaveFlashCardLocal = new AsyncSaveLocalFlashCards(parentId);
                asyncSaveFlashCardLocal.setFlashCards(flashCards);
                asyncSaveFlashCardLocal.setContext(Globals.getContext());
                asyncSaveFlashCardLocal.execute();

                ContentFlashCards.flashCards = flashCards;
                isUpToDate = true;

                FragmentFlashcards fragment = new FragmentFlashcards();
                fragment.setItemList(flashCards);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
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
     *@param parentId
     * @param backPressed
     */
    public void collectItemsFromDb(final long parentId, final boolean backPressed) {

        AsyncGetLocalFlashCards asyncGetFlashCardLocal = new AsyncGetLocalFlashCards(parentId, new AsyncGetLocalFlashCards.AsyncResponseLocalFlashCards() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                ContentFlashCards.flashCards = flashCards;
                isUpToDate = false;

                FragmentFlashcards fragment = new FragmentFlashcards();
                fragment.setItemList(flashCards);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
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
