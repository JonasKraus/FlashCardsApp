package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 26.11.2016.
 */
public class AsyncGetRemoteCategories extends AsyncTask<Long, Long, List<Category>> {

    private ProgressBar progressBar = Globals.getProgressBar();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the categories in the activity that called this async task
     */
    public interface AsyncResponseRemoteCategories {
        void processFinish(List<Category> categories);
    }

    public AsyncResponseRemoteCategories delegate = null;
    private final Long parentId;
    private final int categoryLevel;

    public AsyncGetRemoteCategories(int categoryLevel, Long parentId, AsyncResponseRemoteCategories delegate) {
        this.parentId = parentId;
        this.categoryLevel = categoryLevel;
        this.delegate = delegate;
    }

    @Override
    protected List<Category> doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.CATEGORIES; // URL to call TODO parentId anhängen

        if (categoryLevel == 0) {

            urlString += Routes.QUESTION_MARK + Routes.ROOT + Routes.EQUAL + Routes.BOOL_TRUE;
        } else if (categoryLevel == 1) {

            urlString += Routes.SLASH + parentId + Routes.SLASH + Routes.CHILDREN;
        } else if (categoryLevel == 2) {

            urlString += Routes.SLASH + parentId + Routes.SLASH + Routes.CHILDREN;
        } else {

            urlString += Routes.SLASH + parentId + Routes.SLASH + Routes.CHILDREN;
        }

        Log.d("back call to ", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            return JsonParser.readCategroies(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Categ", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        if (categories == null || categories.size() == 0) {

            // TODO just for testing purpose change to data from sqlite
            Log.d("category", "nothing found");

        }

        progressBar.setVisibility(View.GONE);
        delegate.processFinish(categories);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
