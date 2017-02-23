package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPatchRemoteCard extends AsyncTask<Long, Long, Long> {

    /**
     * Interface to receive the card in the activity that called this async task
     */
    public interface AsyncPatchResponseRemoteCard {
        void processFinish(long id);
    }

    private JSONObject jsonObject;
    private long cardId;
    public AsyncPatchRemoteCard.AsyncPatchResponseRemoteCard delegate = null;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPatchRemoteCard(JSONObject jsonObject, long cardId) {

        this.jsonObject = jsonObject;
        this.cardId = cardId;
    }



    /**
     * Constructor with delegate
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-23
     *
     * @param jsonObject
     * @param delegate
     */
    public AsyncPatchRemoteCard(JSONObject jsonObject, long cardId, AsyncPatchRemoteCard.AsyncPatchResponseRemoteCard delegate) {

        this.jsonObject = jsonObject;
        this.cardId = cardId;
        this.delegate = delegate;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.FLASH_CARDS + Routes.SLASH + cardId + Routes.QUESTION_MARK + Routes.APPEND + Routes.EQUAL + Routes.BOOL_TRUE;

        Log.d("back call to", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for delete request
            //urlConnection.setDoInput(true); // Important for delete request
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());
            urlConnection.setRequestMethod("PATCH");

            urlConnection.connect();

            //Send request JSON Data
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();

            Log.d("json", urlConnection.getRequestProperty("Authorization") + " \n" + jsonObject.toString() + "\n" + urlConnection.getRequestMethod());

            //Log.e("resp", urlConnection.getResponseCode()+"");

            if (urlConnection.getResponseCode() >= 400) {

                Log.e("resp", urlConnection.getResponseCode()+ "\n " + urlConnection.getResponseMessage() + "\n " + urlConnection.getRequestProperty("Authorization"));
            }

            return JsonParser.readResponse(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBack card " + urlConnection.getRequestMethod(), e.toString() + "\n body: " + jsonObject.toString());
            System.out.println(e.toString());
            return null;

        }

    }

    @Override
    protected void onPostExecute(Long id) {
        super.onPostExecute(id);

        delegate.processFinish(id);

        if (id != null) {

        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
