package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Saves users
 *
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-29-01
 */
public class AsyncSaveLocalUserGroupJoinTable extends AsyncTask<Long, Long, Void> {

    private List<User> users;

    public void setUsers(List<User> users) {

        this.users = users;
    }

    @Override
    protected Void doInBackground(Long... params) {


        for (User user : this.users) {

            Globals.getDb().saveUserGroupJoinTable(user.getId(), params[0]);
        }

        return null;
    }

}
