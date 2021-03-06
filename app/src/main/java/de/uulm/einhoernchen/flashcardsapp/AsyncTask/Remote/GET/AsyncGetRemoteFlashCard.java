package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteFlashCard extends AsyncTask<Long, Long, FlashCard> {

    private ProgressBar progressBar = Globals.getProgressBar();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the a flashcard in the activity that called this async task
     */
    public interface AsyncResponseFlashCard {
        void processFinish(FlashCard flashCard);
    }

    public AsyncResponseFlashCard delegate = null;
    private final Long parentId;

    public AsyncGetRemoteFlashCard(Long parentId, AsyncResponseFlashCard delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected FlashCard doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.FLASH_CARDS + Routes.SLASH
                + parentId;
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

            return JsonParser.readFlashCard(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Card", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(FlashCard flashCard) {
        super.onPostExecute(flashCard);

        // TODO for testing only
        // Should collect data from db
        if (flashCard == null) {

            Log.d("AsyncGetRemoteFlashCard", "no flashcard");

        }

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(flashCard);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
