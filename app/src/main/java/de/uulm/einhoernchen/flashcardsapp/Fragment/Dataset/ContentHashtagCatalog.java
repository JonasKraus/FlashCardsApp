package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCardsByHashtag;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardsByHashtag;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHashtagFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentHashtagCatalog {

    private static List<FlashCard> flashCards = new ArrayList<FlashCard>();

    public static FragmentFlashcards fragment;

    private static boolean isUpToDate = false;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *@param tags
     */
    public void collectItemsFromServer(final List<Tag> tags, final List<String> tagNames, final boolean backPressed) {

        AsyncGetRemoteFlashCardsByHashtag asyncGetFlashCard =
                new AsyncGetRemoteFlashCardsByHashtag (tags, tagNames,
                        new AsyncGetRemoteFlashCardsByHashtag.AsyncResponseHashtagFlashCards() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                // real dummy content generation
                if (flashCards == null || flashCards.size() == 0) {

                    //Log.d("ContentFlashCards", "no flashcards");
                }


                // TODO check if null works and check table constraint
                AsyncSaveLocalFlashCards asyncSaveFlashCardLocal =
                        new AsyncSaveLocalFlashCards(null);
                asyncSaveFlashCardLocal.setFlashCards(flashCards);
                asyncSaveFlashCardLocal.setContext(Globals.getContext());
                asyncSaveFlashCardLocal.execute();

                isUpToDate = true;

                FragmentHashtagFlashCards fragment = new FragmentHashtagFlashCards();
                fragment.setItemList(flashCards);
                fragment.setUpToDate(isUpToDate);

                /* TODO if needed
                Bundle args = new Bundle();
                args.putParcelableArrayList(FragmentHashtagFlashCards.ARG_TAGS, tags);
                fragment.setArguments(args);
                */

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

                fragmentTransaction.replace(R.id.fragment_container_catalog_hashtag_cards, fragment);
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
     *@param tags
     * @param backPressed
     */
    public void collectItemsFromDb(final List<Tag> tags, final List<String> tagNames, final boolean backPressed) {

        AsyncGetLocalFlashCardsByHashtag asyncGetFlashCardLocal = new AsyncGetLocalFlashCardsByHashtag(tags, tagNames, new AsyncGetLocalFlashCardsByHashtag.AsyncResponseLocalFlashCardsByHashtag() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                isUpToDate = false;

                FragmentHashtagFlashCards fragment = new FragmentHashtagFlashCards();
                fragment.setItemList(flashCards);
                fragment.setUpToDate(isUpToDate);

                /* TODO Check if needed
                Bundle args = new Bundle();
                args.putParcelableArrayList(FragmentHashtagFlashCards.ARG_TAGS, tags);
                fragment.setArguments(args);
                 */

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

                fragmentTransaction.replace(R.id.fragment_container_catalog_hashtag_cards, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetFlashCardLocal.execute();

    }



}
