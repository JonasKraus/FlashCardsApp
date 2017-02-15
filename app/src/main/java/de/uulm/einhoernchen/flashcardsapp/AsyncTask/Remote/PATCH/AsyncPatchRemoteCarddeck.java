package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-01-05
 */
public class AsyncPatchRemoteCarddeck extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     *
     * @param jsonObject
     */
    public AsyncPatchRemoteCarddeck(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        for (Long carddeckId : params) {

            String urlString = Routes.URL + Routes.SLASH + Routes.CARD_DECKS + Routes.SLASH + carddeckId + Routes.QUESTION_MARK + Routes.APPEND + Routes.EQUAL + Routes.BOOL_TRUE;

            Log.d("back call to", urlString);

            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(false);
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

                Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

                //Log.d("resp", urlConnection.getResponseCode()+"");

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack carddeck patch", e.toString());
                System.out.println(e.toString());
                return null;
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Long id) {
        super.onPostExecute(id);

        if (id != null) {

        } else {
            Log.w("PATCH DECK", "FAILED");
        }

    }


    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
