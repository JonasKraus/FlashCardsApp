package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016-12-30
 */
public class AsyncDeleteRemoteFlashCardRating extends AsyncTask<Long, Long, Boolean> {

    private long ratingId;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param ratingId
     */
    public AsyncDeleteRemoteFlashCardRating(long ratingId) {

        this.ratingId = ratingId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.RATINGS + Routes.SLASH + ratingId;

        Log.d("back call to", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for delete request
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("DELETE");

            urlConnection.connect();

            Log.d("response delete", urlConnection.getResponseCode() + "");

            return urlConnection.getResponseCode() == 204;

        } catch (Exception e) {

            Log.e("doInBack rating delete", e.toString());
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        // TODO for testing only
        // Should collect data from db
        if (success) {

            Log.d("do delete rating", "deleted");

        } else {

            Log.d("do delete rating", "nothing to delete");
        }


    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
