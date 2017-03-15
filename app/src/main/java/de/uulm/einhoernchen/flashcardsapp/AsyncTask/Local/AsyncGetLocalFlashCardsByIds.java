package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Class for getting the ids of all selected cards
 *
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-03-14
 */
public class AsyncGetLocalFlashCardsByIds extends AsyncTask<Long, Long, List<FlashCard>> {


    // Class Attributes
    ProgressBar progressBar = Globals.getProgressBar();
    public AsyncResponse delegate = null;
    private final List<Long> ids;



    /**
     * Method to be called before starting the background task
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.progressBar.setVisibility(View.VISIBLE);
    }



    /**
     * Interface to receive the card ids in the activity that called this async task
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     */
    public interface AsyncResponse {

        void processFinish(List<FlashCard> items);
    }



    /**
     * Constructor
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param delegate
     */
    public AsyncGetLocalFlashCardsByIds(List<Long> ids, AsyncResponse delegate) {

        // delegate to get the response back on main ui thread
        this.delegate = delegate;
        this.ids = ids;
    }



    /**
     * Starting the task in thread
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param params
     * @return
     */
    @Override
    protected List<FlashCard> doInBackground(Long... params) {

        // Collecting cards from db
        return  Globals.getDb().getFlashCardsByIds(ids);
    }



    /**
     * Called after the background task performed
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param items
     */
    @Override
    protected void onPostExecute(List<FlashCard> items) {
        super.onPostExecute(items);

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(items);
    }



    /**
     * Method to update progress and to notify the main ui thread
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }



    /**
     * Sets the progressbar
     * otherwise the mains progressbar will be used
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param progressBar
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
