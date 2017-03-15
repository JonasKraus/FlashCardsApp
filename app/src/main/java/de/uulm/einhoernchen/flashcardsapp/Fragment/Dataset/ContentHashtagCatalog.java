package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET.AsyncGetLocalFlashCardsByHashtag;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardsByHashtag;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentGlobalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Model.Filter.HashtagFlashCardFilter;
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

                // TODO check if null works and check table constraint
                AsyncSaveLocalFlashCards asyncSaveFlashCardLocal =
                        new AsyncSaveLocalFlashCards(null);
                asyncSaveFlashCardLocal.setFlashCards(flashCards);
                asyncSaveFlashCardLocal.setContext(Globals.getContext());
                asyncSaveFlashCardLocal.execute();

                isUpToDate = true;

                FragmentGlobalFlashCards fragment = new FragmentGlobalFlashCards();
                fragment.setItemList(flashCards);
                fragment.setFilterForRecyclerView(HashtagFlashCardFilter.class.getName());
                fragment.setUpToDate(isUpToDate);

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

                fragmentTransaction.replace(R.id.fragment_container_global_flashcards, fragment);
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

                FragmentGlobalFlashCards fragment = new FragmentGlobalFlashCards();
                fragment.setItemList(flashCards);
                fragment.setFilterForRecyclerView(HashtagFlashCardFilter.class.getName());
                fragment.setUpToDate(isUpToDate);

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

                fragmentTransaction.replace(R.id.fragment_container_global_flashcards, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetFlashCardLocal.execute();

    }

}
