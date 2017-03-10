package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetRemoteHashtags extends AsyncTask<String, Void, List<Tag>>{

    private int start = 0;
    private int limit = 10;

    /**
     * Interface to receive the user in the activity that called this async task
     */
    public interface AsyncResponseHashtags {
        void processFinish(List<Tag> tags);
    }

    public AsyncResponseHashtags delegate = null;

    public AsyncGetRemoteHashtags(AsyncResponseHashtags delegate) {
        this.delegate = delegate;
    }

    /**
     * Sets the chunked start and endpoint
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-10
     *
     * @param start
     * @param limit
     */
    public void setStartAndLimit(int start, int limit) {

        this.start = start;
        this.limit = limit;
    }

    @Override
    protected List<Tag> doInBackground(String... params) {

        List<Tag> tags;


        String urlString = Routes.URL + Routes.SLASH + Routes.TAGS
                + Routes.QUESTION_MARK + Routes.START + Routes.EQUAL + this.start
                + Routes.AND + Routes.SIZE + Routes.EQUAL + this.limit
                + Routes.AND + Routes.STARTS_WITH + Routes.EQUAL + params[0]
                + Routes.AND + Routes.SORT_BY + Routes.EQUAL + Routes.USAGE_COUNT + Routes.SPACE + Routes.ASC;

        Log.d("back call to ", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            if (response >= 200 && response <=399){

                tags = JsonParser.parseTags(urlConnection.getInputStream());
                return tags;
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Tag> tags) {

        if (tags != null) {

            delegate.processFinish(tags);

        }
    }
}
