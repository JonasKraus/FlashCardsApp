package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteFlashCard extends AsyncTask<Long, Long, List<FlashCard>> {

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
    public interface AsyncResponseFlashCard {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseFlashCard delegate = null;
    private final Long parentId;

    public AsyncGetRemoteFlashCard(Long parentId, AsyncResponseFlashCard delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<FlashCard> doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.CARD_DECKS + Routes.SLASH
                + parentId + Routes.SLASH + Routes.FLASH_CARDS;
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

            return JsonParser.readFlashCards(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Card", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<FlashCard> flashCards) {
        super.onPostExecute(flashCards);

        // TODO for testing only
        // Should collect data from db
        if (flashCards == null || flashCards.size() == 0) {

            Log.d("AsyncGetRemoteFlashCard", "no flashcards");

        }

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(flashCards);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
