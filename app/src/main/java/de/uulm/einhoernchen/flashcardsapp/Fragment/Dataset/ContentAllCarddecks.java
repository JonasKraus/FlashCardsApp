package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalCarddecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalCardDecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteCarddecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentAllCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentAllCarddecks {

    private static List<CardDeck> cardDecks = new ArrayList<CardDeck>();

    public static FragmentAllCarddecks fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();
    private boolean createMessage;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-28
     *
     * @param backPressed
     *
     */
    public void collectItemsFromServer(final boolean backPressed) {

        AsyncGetRemoteCarddecks asyncGetRemoteCarddecks = new AsyncGetRemoteCarddecks(null, new AsyncGetRemoteCarddecks.AsyncResponseRemoteCarddecks() {

            @Override
            public void processFinish(List<CardDeck> carddecks) {

                AsyncSaveLocalCardDecks asyncSaveCarddecks = new AsyncSaveLocalCardDecks(null);
                asyncSaveCarddecks.setCardDecks(carddecks);
                asyncSaveCarddecks.execute();

                ContentAllCarddecks.cardDecks = carddecks;
                isUpToDate = true;

                fragment = new FragmentAllCarddecks();
                fragment.setItemList(carddecks);
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

                fragmentTransaction.replace(R.id.fragment_container_carddecks, fragment);
                fragmentTransaction.commit();

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetRemoteCarddecks.execute();
        }

    }


    /**
     * Get data fro local sqlite db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-16
     *
     * @param backPressed
     */
    public void collectItemsFromDb(final boolean backPressed) {

        AsyncGetLocalCarddecks asyncGetLocalCarddecks = new AsyncGetLocalCarddecks(null, new AsyncGetLocalCarddecks.AsyncResponseLocalCarddecks() {

            @Override
            public void processFinish(List<CardDeck> carddecks) {


                ContentAllCarddecks.cardDecks = carddecks;
                isUpToDate = false;

                fragment = new FragmentAllCarddecks();
                fragment.setItemList(carddecks);
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

                fragmentTransaction.replace(R.id.fragment_container_carddecks, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalCarddecks.execute();

    }

    public void setCreateMessage(boolean createMessage) {
        this.createMessage = createMessage;
    }
}
