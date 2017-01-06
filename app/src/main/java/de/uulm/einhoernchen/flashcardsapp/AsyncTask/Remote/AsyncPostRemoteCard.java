package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteCard extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private Long carddeckId;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteCard(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        this.carddeckId = params[0];

        for (Long carddeckId : params) {

            this.carddeckId = carddeckId;

            String urlString = Routes.URL + Routes.SLASH + Routes.FLASH_CARDS;
            Log.d("back call to", urlString);

            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(false);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                urlConnection.connect();

                //Send request JSON Data
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();

                Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack card post", e.toString());
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

            JSONObject jsonObjectFlashcardID = new JSONObject();
            JSONObject jsonObjectCards = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            try {

                jsonObjectFlashcardID.put(JsonKeys.FLASHCARD_ID, id);
                jsonArray.put(jsonObjectFlashcardID);
                jsonObjectCards.put(JsonKeys.CARDS, jsonArray);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            //patch carddeck mit carddeckid und cardid
            AsyncPatchRemoteCarddeck task = new AsyncPatchRemoteCarddeck(jsonObjectCards);
            task.execute(this.carddeckId);

        } else {
            Log.w("POST CARD", "FAILED");
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
