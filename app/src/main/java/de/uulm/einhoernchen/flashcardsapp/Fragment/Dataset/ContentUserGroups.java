package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentChallengesRanking;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentUserGroups {

    private static List<UserGroup> userGroups = new ArrayList<UserGroup>();

    public static FragmentUserGroups fragment;

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

        AsyncGetRemoteUserGroups asyncGetUserGroups = new AsyncGetRemoteUserGroups(new AsyncGetRemoteUserGroups.AsyncResponseUserGroups() {

            @Override
            public void processFinish(List<UserGroup> userGroups) {

                AsyncSaveLocalUserGroups asyncSaveUsergroups = new AsyncSaveLocalUserGroups();
                asyncSaveUsergroups.setUserGroups(userGroups);
                asyncSaveUsergroups.execute();

                ContentUserGroups.userGroups = userGroups;
                isUpToDate = true;

                fragment = new FragmentUserGroups();
                fragment.setItemList(userGroups);
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

                fragmentTransaction.replace(R.id.fragment_container_usergroups, fragment);
                fragmentTransaction.commit();

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetUserGroups.execute();
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

        AsyncGetLocalUserGroups asyncGetLocalUserGroups = new AsyncGetLocalUserGroups(new AsyncGetLocalUserGroups.AsyncResponseLocalUserGroups() {

            @Override
            public void processFinish(List<UserGroup> userGroups) {


                ContentUserGroups.userGroups = userGroups;
                isUpToDate = false;

                fragment = new FragmentUserGroups();
                fragment.setItemList(userGroups);
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

                fragmentTransaction.replace(R.id.fragment_container_usergroups, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalUserGroups.execute();

    }

}
