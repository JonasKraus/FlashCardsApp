package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalFlashCards extends AsyncTask<Long, Long, Void> {

    private List<FlashCard> flashCards;
    private DbManager db;


    /**
     * Use this method to save a list of flashcards
     * @param flashCards
     */
    public void setFlashCards(List<FlashCard> flashCards) {
        this.flashCards = flashCards;
    }

    /**
     * User this to only save one flasshcard
     * @param flashCard
     */
    public void setFlashCard(FlashCard flashCard) {
        this.flashCards = new ArrayList<FlashCard>();
        this.flashCards.add(flashCard);
    }

    public void setDbManager(DbManager dbManager) {
        this.db = dbManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final Long parentId;

    public AsyncSaveLocalFlashCards(Long parentId) {
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