package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetRemoteChallenges extends AsyncTask<Long, Void, List<Challenge>>{

    /**
     * Interface to receive the user in the activity that called this async task
     */
    public interface AsyncResponseChallenges {
        void processFinish(List<Challenge> challenges);
    }

    public AsyncResponseChallenges delegate = null;

    public AsyncGetRemoteChallenges(AsyncResponseChallenges delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Challenge> doInBackground(Long... params) {

        List<Challenge> challenges;


        String urlString = Routes.URL + Routes.SLASH + Routes.CHALLENGES; // TODO URL to call

        Log.d("back call to ", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            if (response >= 200 && response <=399){
                challenges = JsonParser.parseChallenges(urlConnection.getInputStream());
                return challenges;
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Challenge> challenges) {

        if (challenges != null) {

            delegate.processFinish(challenges);

        }
    }
}
