package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetRemoteFlashCardsByHashtag extends AsyncTask<Long, Long, List<FlashCard>> {

    private ProgressBar progressBar = Globals.getProgressBar();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseHashtagFlashCards {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseHashtagFlashCards delegate = null;
    private final List<Tag> tags;
    private final List<String> tagNames;

    public AsyncGetRemoteFlashCardsByHashtag(List<Tag> tags, List<String> tagNames, AsyncResponseHashtagFlashCards delegate) {
        this.tagNames = tagNames;
        this.tags = tags;
        this.delegate = delegate;
    }

    @Override
    protected List<FlashCard> doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.TAGS + Routes.SLASH + Routes.FLASH_CARDS;
        boolean start = true;

        if (tags.size() > 0) {

            for (Tag tag : tags) {

                if (start) {

                    start = false;
                    urlString += Routes.QUESTION_MARK + Routes.ID + Routes.EQUAL + tag.getId();
                } else {

                    urlString += Routes.AND + Routes.ID + Routes.EQUAL + tag.getId();
                }

            }
        } else if (tagNames.size() > 0) {

            for (String tag : tagNames) {

                if (start) {

                    start = false;
                    urlString += Routes.QUESTION_MARK + Routes.NAME + Routes.EQUAL + tag;
                } else {

                    urlString += Routes.AND + Routes.NAME + Routes.EQUAL + tag;
                }

            }
        } else {

            // Getting all cards
            urlString = Routes.URL + Routes.SLASH + Routes.FLASH_CARDS;
        }

        Log.d("back call to ", urlString);
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            // TODO add tags

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

        if (flashCards == null) {

            flashCards = new ArrayList<FlashCard>();
        }

        progressBar.setVisibility(View.GONE);

        if (delegate != null) {

            delegate.processFinish(flashCards);
        }

        AsyncSaveLocalFlashCards asyncSaveLocalFlashCards = new AsyncSaveLocalFlashCards(null);
        asyncSaveLocalFlashCards.setFlashCards(flashCards);
        asyncSaveLocalFlashCards.setContext(Globals.getContext());

        asyncSaveLocalFlashCards.execute();

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
