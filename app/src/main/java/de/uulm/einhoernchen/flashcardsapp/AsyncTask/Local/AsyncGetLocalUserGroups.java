package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalUserGroups extends AsyncTask<Long, Long, List<UserGroup>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalUserGroups {
        void processFinish(List<UserGroup> userGroups);
    }

    public AsyncResponseLocalUserGroups delegate = null;


    public AsyncGetLocalUserGroups(AsyncResponseLocalUserGroups delegate) {

        this.delegate = delegate;
    }

    @Override
    protected List<UserGroup> doInBackground(Long... params) {

        return  Globals.getDb().getUserGroups();
    }

    @Override
    protected void onPostExecute(List<UserGroup> userGroups) {
        super.onPostExecute(userGroups);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(userGroups);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
