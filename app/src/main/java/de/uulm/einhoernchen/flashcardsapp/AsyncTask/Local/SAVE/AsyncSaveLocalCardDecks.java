package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalCardDecks extends AsyncTask<Long, Long, Void> {

    private List<CardDeck> cardDecks;


    public void setCardDecks(List<CardDeck> cardDecks) {
        this.cardDecks = cardDecks;
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

        // Only save if a valid parent id is given
        if (parentId != null) {

            Globals.getDb().saveCardDecks(cardDecks, parentId);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
