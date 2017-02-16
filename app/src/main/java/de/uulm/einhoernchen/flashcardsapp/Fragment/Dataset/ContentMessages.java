package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalMessages;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalMessages;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalUserGroups;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteMessages;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentMessages;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentMessages {

    private static List<Message> messages = new ArrayList<Message>();

    public static FragmentMessages fragment;

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

        AsyncGetRemoteMessages asyncGetMessages = new AsyncGetRemoteMessages(new AsyncGetRemoteMessages.AsyncResponseMessages() {

            @Override
            public void processFinish(List<Message> messages) {

                AsyncSaveLocalMessages asyncSaveLocalMessages = new AsyncSaveLocalMessages();
                asyncSaveLocalMessages.setMessages(messages);
                asyncSaveLocalMessages.execute();

                ContentMessages.messages = messages;
                isUpToDate = true;

                fragment = new FragmentMessages();
                fragment.setItemList(messages);
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

                fragmentTransaction.replace(R.id.fragment_container_messages, fragment);
                fragmentTransaction.commit();

            }

        });

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetMessages.execute();
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

        AsyncGetLocalMessages asyncGetLocalMessages = new AsyncGetLocalMessages(new AsyncGetLocalMessages.AsyncResponseLocalMessages() {

            @Override
            public void processFinish(List<Message> messages) {


                Log.d("messages", messages.size() + "");
                ContentMessages.messages = messages;
                isUpToDate = false;

                fragment = new FragmentMessages();
                fragment.setItemList(messages);
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

                fragmentTransaction.replace(R.id.fragment_container_messages, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalMessages.execute();

    }

}
