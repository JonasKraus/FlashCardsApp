package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalCategories extends AsyncTask<Long, Long, Void> {

    private List<Category> categories;


    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final Long parentId;

    public AsyncSaveLocalCategories(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    protected Void doInBackground(Long... params) {
        Globals.getDb().saveCategories(categories);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
