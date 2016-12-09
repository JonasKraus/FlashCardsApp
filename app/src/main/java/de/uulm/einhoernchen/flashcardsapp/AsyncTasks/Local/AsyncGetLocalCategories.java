package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalCategories extends AsyncTask<Long, Long, List<Category>> {

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

        return  db.getCategories(parentId);
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(categories);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
