package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalFlashCards extends AsyncTask<Long, Long, List<FlashCard>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalFlashCards {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseLocalFlashCards delegate = null;
    private final Long parentId;

    public AsyncGetLocalFlashCards(Long parentId, AsyncResponseLocalFlashCards delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<FlashCard> doInBackground(Long... params) {

        return  Globals.getDb().getFlashCards(parentId);
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
