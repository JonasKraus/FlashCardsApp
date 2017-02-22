package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteFlashCards extends AsyncTask<Long, Long, List<FlashCard>> {

    private ProgressBar progressBar = Globals.getProgressBar();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseFlashCards {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseFlashCards delegate = null;
    private final Long parentId;

    public AsyncGetRemoteFlashCards(Long parentId, AsyncResponseFlashCards delegate) {
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

            Log.d("AsyncGetRemoteCards", "no flashcards");

        }

        progressBar.setVisibility(View.GONE);

        if (delegate != null) {

            delegate.processFinish(flashCards);
        } else {

            AsyncSaveLocalFlashCards asyncSaveLocalFlashCards = new AsyncSaveLocalFlashCards(parentId);
            asyncSaveLocalFlashCards.setFlashCards(flashCards);

            asyncSaveLocalFlashCards.execute();
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
