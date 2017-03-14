package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Class for getting the ids of all bookmarked cards
 *
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-03-14
 */
public class AsyncGetLocalBookmarkedFlashCards extends AsyncTask<Long, Long, ArrayList<Long>> {



    // Class Attributes
    ProgressBar progressBar = Globals.getProgressBar();
    public AsyncResponse delegate = null;



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

        void processFinish(ArrayList<Long> ids);
    }



    /**
     * Constructor
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param delegate
     */
    public AsyncGetLocalBookmarkedFlashCards(AsyncResponse delegate) {

        // delegate to get the response back on main ui thread
        this.delegate = delegate;
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
    protected ArrayList<Long> doInBackground(Long... params) {

        // Collecting cards from db
        return  Globals.getDb().getBookmarkedFlashCardIds();
    }



    /**
     * Called after the background task performed
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     *
     * @param ids
     */
    @Override
    protected void onPostExecute(ArrayList<Long> ids) {
        super.onPostExecute(ids);

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(ids);
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
