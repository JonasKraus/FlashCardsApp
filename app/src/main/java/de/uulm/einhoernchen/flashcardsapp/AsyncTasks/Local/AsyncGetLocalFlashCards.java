package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalFlashCards extends AsyncTask<Long, Long, List<FlashCard>> {

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

        return  db.getFlashCards(parentId);
    }

    @Override
    protected void onPostExecute(List<FlashCard> flashCards) {
        super.onPostExecute(flashCards);

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(flashCards);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}