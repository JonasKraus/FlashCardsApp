package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalChallenges extends AsyncTask<Long, Long, Void> {

    private List<Challenge> challenges;


    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Long... params) {

        for (Challenge challenge : challenges) {

            for (Statistic statistic : challenge.getStatistics()) {
                statistic.save();
                Globals.getDb().saveChallenge(challenge.getMessage().getId(), statistic.getId());
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
