package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET.AsyncGetLocalChallenges;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalChallenges;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteChallenges;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentChallengesRanking;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentChallengesRanking {

    private static List<Challenge> challenges = new ArrayList<Challenge>();

    public static FragmentChallengesRanking fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-28
     *
     * @param backPressed
     *
     */
    public void collectItemsFromServer(final boolean backPressed) {

        AsyncGetRemoteChallenges asyncGetRemoteChallenges = new AsyncGetRemoteChallenges(new AsyncGetRemoteChallenges.AsyncResponseChallenges() {

            @Override
            public void processFinish(List<Challenge> challenges) {

                AsyncSaveLocalChallenges asyncSaveChallenges = new AsyncSaveLocalChallenges();
                asyncSaveChallenges.setChallenges(challenges);
                asyncSaveChallenges.execute();

                ContentChallengesRanking.challenges = challenges;
                isUpToDate = true;

                fragment = new FragmentChallengesRanking();
                fragment.setItemList(challenges);
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

                fragmentTransaction.replace(R.id.fragment_container_challenges_ranking, fragment);
                fragmentTransaction.commit();

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetRemoteChallenges.execute();
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

        AsyncGetLocalChallenges asyncGetLocalChallenges = new AsyncGetLocalChallenges(new AsyncGetLocalChallenges.AsyncResponseLocalChallenges() {

            @Override
            public void processFinish(List<Challenge> challenges) {


                ContentChallengesRanking.challenges = challenges;
                isUpToDate = false;

                fragment = new FragmentChallengesRanking();
                fragment.setItemList(challenges);
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

                fragmentTransaction.replace(R.id.fragment_container_challenges_ranking, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalChallenges.execute();

    }

}
