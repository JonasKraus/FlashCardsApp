package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET;

import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalUsersOfUserGroup extends AsyncTask<Long, Long, List<User>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalUsers {
        void processFinish(List<User> users);
    }

    public AsyncResponseLocalUsers delegate = null;


    public AsyncGetLocalUsersOfUserGroup(AsyncResponseLocalUsers delegate) {

        this.delegate = delegate;
    }

    @Override
    protected List<User> doInBackground(Long... params) {

        return Globals.getDb().getUsersOfUserGroup(params[0]);
    }

    @Override
    protected void onPostExecute(List<User> users) {
        super.onPostExecute(users);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(users);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
