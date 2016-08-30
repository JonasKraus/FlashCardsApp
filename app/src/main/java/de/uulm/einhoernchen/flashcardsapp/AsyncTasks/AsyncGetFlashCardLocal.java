package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetFlashCardLocal extends AsyncTask<Long, Long, List<FlashCard>> {

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
    public interface AsyncResponseFlashCardLocal {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseFlashCardLocal delegate = null;
    private final Long parentId;

    public AsyncGetFlashCardLocal(Long parentId, AsyncResponseFlashCardLocal delegate) {
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
