package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteCard extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private Long carddeckId;
    private Long userGroupId;
    private AsyncPostRemoteCardResponse delegate;


    public interface AsyncPostRemoteCardResponse {

        public void processFinished(long id);
    }


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteCard(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }


    /**
     * Constructs with delegate
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     * @param delegate
     */
    public AsyncPostRemoteCard(JSONObject jsonObject, AsyncPostRemoteCardResponse delegate) {

        this.jsonObject = jsonObject;
        this.delegate = delegate;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        this.carddeckId = params[0];

        this.userGroupId = Globals.getDb().getUserGroup(carddeckId).getId();

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
                urlConnection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());
                urlConnection.setRequestMethod("POST");

                urlConnection.connect();

                //Send request JSON Data
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();

                Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());
                Log.d("Auth", urlConnection.getRequestProperty("Authorization"));

                if (urlConnection.getResponseCode() >= 400) {

                    Log.e("resp", urlConnection.getResponseCode()+ " " + urlConnection.getResponseMessage() + "");
                }

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack card " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
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
            JSONObject jsonObjectGroup = new JSONObject();

            try {

                jsonObjectFlashcardID.put(JsonKeys.FLASHCARD_ID, id);
                jsonArray.put(jsonObjectFlashcardID);
                jsonObjectCards.put(JsonKeys.CARDS, jsonArray);
                jsonObjectGroup.put(JsonKeys.GROUP_ID, this.userGroupId);
                jsonObjectCards.put(JsonKeys.CARDDECK_GROUP, jsonObjectGroup);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            //patch carddeck mit carddeckid und cardid
            AsyncPatchRemoteCarddeck task = new AsyncPatchRemoteCarddeck(jsonObjectCards, true);
            task.execute(this.carddeckId);

            if (delegate != null) {

                delegate.processFinished(id);
            }


        } else {
            Log.w("POST CARD", "FAILED");
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
