package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET.AsyncGetLocalUsers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalUsers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUsers;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUsersBinding;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentUsers {

    private static List<User> users = new ArrayList<User>();

    public static FragmentUsersBinding fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();
    private List<User> usersOfGroup;
    private boolean createMessage = false;
    private boolean editable = true;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-28
     *
     * @param backPressed
     *
     */
    public void collectItemsFromServer(final boolean backPressed) {

        AsyncGetRemoteUsers asyncGetUsers = new AsyncGetRemoteUsers(new AsyncGetRemoteUsers.AsyncResponseUsers() {

            @Override
            public void processFinish(List<User> users) {

                AsyncSaveLocalUsers asyncSaveUsers = new AsyncSaveLocalUsers();
                asyncSaveUsers.execute(users);

                ContentUsers.users = users;
                isUpToDate = true;

                fragment = new FragmentUsersBinding();
                fragment.setUsersOfGroup(usersOfGroup);
                fragment.setItemList(users);
                fragment.setUpToDate(isUpToDate);
                fragment.setEditable(editable);
                fragment.setCreateMessage(createMessage);

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

                fragmentTransaction.replace(R.id.fragment_container_users, fragment);
                fragmentTransaction.commit();

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetUsers.execute();
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

        AsyncGetLocalUsers asyncGetLocalUsers = new AsyncGetLocalUsers(new AsyncGetLocalUsers.AsyncResponseLocalUsers() {

            @Override
            public void processFinish(List<User> users) {


                ContentUsers.users = users;
                isUpToDate = false;

                fragment = new FragmentUsersBinding();
                fragment.setUsersOfGroup(usersOfGroup);
                fragment.setItemList(users);
                fragment.setUpToDate(isUpToDate);
                fragment.setEditable(editable);
                fragment.setCreateMessage(createMessage);


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

                fragmentTransaction.replace(R.id.fragment_container_users, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalUsers.execute();

    }

    public void setUsersOfGroup(List<User> usersOfGroup) {
        this.usersOfGroup = usersOfGroup;
    }

    public void setCreateMessage(boolean createMessage) {
        this.createMessage = createMessage;
    }



    /**
     * Says if the gui should be editable
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-21
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
