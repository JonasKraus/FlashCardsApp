package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016-12-04
 *
 */
public class AsyncGetRemoteHeartbeat extends AsyncTask<Long, Long, Boolean> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Interface to receive the heartbeat of the server
     */
    public interface AsyncResponseHeartbeat {
        void processFinish(Boolean isAlive);
    }

    public AsyncResponseHeartbeat delegate = null;

    public AsyncGetRemoteHeartbeat(AsyncResponseHeartbeat delegate) {

        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.HEARTBEAT;

        HttpURLConnection urlConnection = null;

        Log.d("back call to ", urlString);

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            return JsonParser.readHeartbeat(urlConnection.getInputStream());

        } catch (Exception e) {

            return false;
            //Log.e("doInBackground heart", e.toString());
            //System.out.println(e.getMessage());

        }

    }

    @Override
    protected void onPostExecute(Boolean isAlive) {
        super.onPostExecute(isAlive);

        delegate.processFinish(isAlive);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
