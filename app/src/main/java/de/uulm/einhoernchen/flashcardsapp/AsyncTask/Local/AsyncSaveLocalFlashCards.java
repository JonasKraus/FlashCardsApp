package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalFlashCards extends AsyncTask<Long, Integer, Void> {

    private List<FlashCard> flashCards;
    private DbManager dbManager;
    private ProgressDialog mProgressDialog;
    private Context context;
    private final boolean DEBUG = false;


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

    public void setContext (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Saving");
        mProgressDialog.setIcon(R.drawable.ic_card);
        // Set your ProgressBar Message
        mProgressDialog.setMessage("Saving data, Please Wait!");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Show ProgressBar
        mProgressDialog.setCancelable(false);
        //  mProgressDialog.setCanceledOnTouchOutside(false);
        if (DEBUG) mProgressDialog.show();
    }

    private final Long parentId;

    public AsyncSaveLocalFlashCards(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    protected Void doInBackground(Long... params) {

        dbManager = new DbManager(Globals.getContext());

        try {

            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mProgressDialog.setMax(flashCards.size());

        if (parentId != null) {

            for (int i = 0; i < flashCards.size(); i++) {

                publishProgress(i);
                dbManager.saveFlashCard(flashCards.get(i), parentId);
            }
        } else {

            for (int i = 0; i < flashCards.size(); i++) {

                publishProgress(i);
                dbManager.saveFlashCard(flashCards.get(i), null);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (DEBUG) mProgressDialog.dismiss();
        dbManager.close();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mProgressDialog.setProgress(values[0]);
    }
}
