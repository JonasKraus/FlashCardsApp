package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET;

import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalFlashCardsByHashtag extends AsyncTask<Long, Long, List<FlashCard>> {

    public AsyncResponseLocalFlashCardsByHashtag delegate = null;
    private final List<Tag> tags;
    private final List<String> tagNames;

    public AsyncGetLocalFlashCardsByHashtag(List<Tag> tags, List<String> tagNames, AsyncResponseLocalFlashCardsByHashtag delegate) {
        this.tags = tags;
        this.tagNames = tagNames;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the cards in the activity that called this async task
     */
    public interface AsyncResponseLocalFlashCardsByHashtag {
        void processFinish(List<FlashCard> flashCards);
    }


    @Override
    protected List<FlashCard> doInBackground(Long... params) {

        if (tags.size() > 0) {

            return Globals.getDb().getFlashCards(tags);
        } else if (tagNames.size() > 0) {

            return Globals.getDb().getFlashCardsByTagNames(tagNames);
        }

        return Globals.getDb().getAllFlashCards();
    }

    @Override
    protected void onPostExecute(List<FlashCard> flashCards) {
        super.onPostExecute(flashCards);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(flashCards);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
