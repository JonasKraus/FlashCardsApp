package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET.AsyncGetLocalUsersOfUserGroup;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalUserGroupJoinTable;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalUsers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUsersOfUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUsersBinding;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentUsersOfUserGroup {

    public static FragmentUsersBinding fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-28
     *@param backPressed
     * @param isEditable
     *
     */
    public void collectItemsFromServer(final boolean backPressed, final Long userGroupId, final boolean isEditable) {

        AsyncGetRemoteUsersOfUserGroup asyncGetUsers = new AsyncGetRemoteUsersOfUserGroup(new AsyncGetRemoteUsersOfUserGroup.AsyncResponseUsers() {

            @Override
            public void processFinish(List<User> users) {

                AsyncSaveLocalUsers asyncSaveUsers = new AsyncSaveLocalUsers();
                asyncSaveUsers.execute(users);

                AsyncSaveLocalUserGroupJoinTable localuserGroupJoinTable = new AsyncSaveLocalUserGroupJoinTable();
                localuserGroupJoinTable.setUsers(users);
                localuserGroupJoinTable.execute(userGroupId);

                isUpToDate = true;

                ContentUsers contentUsers = new ContentUsers();
                contentUsers.setUsersOfGroup(users);
                contentUsers.setEditable(isEditable);
                contentUsers.collectItemsFromDb(false);
                contentUsers.collectItemsFromServer(false);

                // Dont do anything

                /*
                fragment = new FragmentUsers();
                fragment.setItemList(users);
                fragment.setUpToDate(isUpToDate);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

                // TODO delete if always loaded from local db

                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (!fragmentAnimated) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentAnimated = true;
                }


                fragmentTransaction.replace(R.id.fragment_container_users, fragment);
                fragmentTransaction.commit();
                */

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetUsers.execute(userGroupId);
        }

    }


    /**
     * Get data fro local sqlite db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-16
     *@param backPressed
     * @param isEditable
     */
    public void collectItemsFromDb(final boolean backPressed, Long userGroupId, final boolean isEditable) {

        AsyncGetLocalUsersOfUserGroup asyncGetLocalUsersOfUserGroup = new AsyncGetLocalUsersOfUserGroup(new AsyncGetLocalUsersOfUserGroup.AsyncResponseLocalUsers() {

            @Override
            public void processFinish(List<User> users) {

                isUpToDate = false;

                ContentUsers contentUsers = new ContentUsers();
                contentUsers.setUsersOfGroup(users);
                contentUsers.setEditable(isEditable);
                contentUsers.collectItemsFromDb(false);
                contentUsers.collectItemsFromServer(false);

                // Dont do anything

                /*
                fragment = new FragmentUsersBinding();
                fragment.setItemList(users);
                fragment.setUpToDate(isUpToDate);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

                // TODO delete if always loaded from local db

                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (!fragmentAnimated) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentAnimated = true;
                }


                fragmentTransaction.replace(R.id.fragment_container_users, fragment);
                fragmentTransaction.commit();
                */
            }

        });

        asyncGetLocalUsersOfUserGroup.execute(userGroupId);

    }

}
