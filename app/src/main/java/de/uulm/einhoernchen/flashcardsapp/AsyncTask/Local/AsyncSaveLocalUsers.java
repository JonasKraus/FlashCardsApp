package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalUsers extends AsyncTask<Long, Long, Void> {

    private List<User> users;


    public void setUsers(List<User> users) {
        this.users = users;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Long... params) {
        Globals.getDb().saveUsers(users);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
