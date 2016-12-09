package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteCarddecks extends AsyncTask<Long, Long, List<CardDeck>> {

    private ProgressBar progressBar;

    public void setProgressbar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseRemoteCarddecks {
        void processFinish(List<CardDeck> cardDecks);
    }

    public AsyncResponseRemoteCarddecks delegate = null;
    private final Long parentId;

    public AsyncGetRemoteCarddecks(Long parentId, AsyncResponseRemoteCarddecks delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<CardDeck> doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.CATEGORIES + Routes.SLASH + parentId + Routes.SLASH + Routes.DECKS; // URL to call
        Log.d("back call to ", urlString);
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            return JsonParser.readCardDecks(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Carddeck", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<CardDeck> cardDecks) {
        super.onPostExecute(cardDecks);
        if (cardDecks == null || cardDecks.size() == 0) {

            // TODO just for testing purpose change to data from sqlite
            Log.d("carddeck", "nothing found");

        }

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(cardDecks);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
