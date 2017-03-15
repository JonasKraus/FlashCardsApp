package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalCarddecks extends AsyncTask<Long, Long, List<CardDeck>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalCarddecks {
        void processFinish(List<CardDeck> categories);
    }

    public AsyncResponseLocalCarddecks delegate = null;
    private final Long parentId;

    public AsyncGetLocalCarddecks(Long parentId, AsyncResponseLocalCarddecks delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<CardDeck> doInBackground(Long... params) {

        return  Globals.getDb().getCardDecks(parentId);
    }

    @Override
    protected void onPostExecute(List<CardDeck> cardDecks) {
        super.onPostExecute(cardDecks);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(cardDecks);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
