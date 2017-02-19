package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalChallenges extends AsyncTask<Long, Long, List<Challenge>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalChallenges {
        void processFinish(List<Challenge> challenges);
    }

    public AsyncResponseLocalChallenges delegate = null;


    public AsyncGetLocalChallenges(AsyncResponseLocalChallenges delegate) {

        this.delegate = delegate;
    }

    @Override
    protected List<Challenge> doInBackground(Long... params) {

        return  Globals.getDb().getChallenges();
    }

    @Override
    protected void onPostExecute(List<Challenge> challenges) {
        super.onPostExecute(challenges);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(challenges);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
