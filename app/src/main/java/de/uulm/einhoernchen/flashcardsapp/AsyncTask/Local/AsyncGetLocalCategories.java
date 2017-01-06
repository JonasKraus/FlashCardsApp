package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalCategories extends AsyncTask<Long, Long, List<Category>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalCategories {
        void processFinish(List<Category> categories);
    }

    public AsyncResponseLocalCategories delegate = null;
    private final Long parentId;

    public AsyncGetLocalCategories(Long parentId, AsyncResponseLocalCategories delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<Category> doInBackground(Long... params) {

        return  Globals.getDb().getCategories(parentId);
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(categories);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
