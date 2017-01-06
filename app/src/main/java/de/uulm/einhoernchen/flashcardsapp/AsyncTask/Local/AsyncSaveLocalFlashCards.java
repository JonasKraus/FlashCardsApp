package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalFlashCards extends AsyncTask<Long, Long, Void> {

    private List<FlashCard> flashCards;


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
        Globals.getDb().saveFlashCards(flashCards, parentId);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
