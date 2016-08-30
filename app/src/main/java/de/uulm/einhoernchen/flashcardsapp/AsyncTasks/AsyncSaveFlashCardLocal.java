package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveFlashCardLocal extends AsyncTask<Long, Long, Void> {

    private List<FlashCard> flashCards;
    private DbManager db;


    public void setFlashCards(List<FlashCard> flashCards) {
        this.flashCards = flashCards;
    }

    public void setDbManager(DbManager dbManager) {
        this.db = dbManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final Long parentId;

    public AsyncSaveFlashCardLocal(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    protected Void doInBackground(Long... params) {
        db.saveFlashCards(flashCards, parentId);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
