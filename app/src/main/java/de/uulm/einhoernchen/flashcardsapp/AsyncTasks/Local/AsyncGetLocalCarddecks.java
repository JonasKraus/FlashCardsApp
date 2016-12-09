package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalCarddecks extends AsyncTask<Long, Long, List<CardDeck>> {

    private ProgressBar progressBar;
    private DbManager db;


    public void setProgressbar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setDbManager(DbManager dbManager) {
        this.db = dbManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
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

        return  db.getCardDecks(parentId);
    }

    @Override
    protected void onPostExecute(List<CardDeck> cardDecks) {
        super.onPostExecute(cardDecks);

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(cardDecks);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
