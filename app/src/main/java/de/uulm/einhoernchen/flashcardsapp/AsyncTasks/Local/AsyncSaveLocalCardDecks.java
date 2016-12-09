package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalCardDecks extends AsyncTask<Long, Long, Void> {

    private List<CardDeck> cardDecks;
    private DbManager db;


    public void setCardDecks(List<CardDeck> cardDecks) {
        this.cardDecks = cardDecks;
    }

    public void setDbManager(DbManager dbManager) {
        this.db = dbManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final Long parentId;

    public AsyncSaveLocalCardDecks(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    protected Void doInBackground(Long... params) {
        db.saveCardDecks(cardDecks, parentId);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
