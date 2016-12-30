package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteFlashCardAnswers extends AsyncTask<Long, Long, List<Answer>> {

    private ProgressBar progressBar;

    public void setProgressbar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null )progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the a flashcard in the activity that called this async task
     */
    public interface AsyncResponseFlashCardAnswers {
        void processFinish(List<Answer> answers);
    }

    public AsyncResponseFlashCardAnswers delegate = null;
    private final Long parentId;

    public AsyncGetRemoteFlashCardAnswers(Long parentId, AsyncResponseFlashCardAnswers delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<Answer> doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.FLASH_CARDS + Routes.SLASH
                + parentId + Routes.SLASH + Routes.ANSWERS;

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

            return JsonParser.readAnswers(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Card", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Answer> answers) {
        super.onPostExecute(answers);

        // TODO for testing only
        // Should collect data from db
        if (answers == null) {

            Log.d("AsyncGetRemoteFlashCard", "no flashcard");

        }

        if (progressBar != null) progressBar.setVisibility(View.GONE);
        delegate.processFinish(answers);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
