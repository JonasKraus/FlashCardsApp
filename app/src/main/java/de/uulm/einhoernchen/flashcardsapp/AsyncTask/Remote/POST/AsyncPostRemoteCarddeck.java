package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCategory;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteCarddeck extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private Long parentId;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteCarddeck(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        this.parentId = params[0];

        for (Long parentId : params) {

            this.parentId = parentId;

            String urlString = Routes.URL + Routes.SLASH + Routes.CARD_DECKS;
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


                Log.d("body carddeck create ", jsonObject.toString());
                //Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack carddeck " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
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

            JSONObject jsonObjectDeckID = new JSONObject();
            JSONObject jsonObjectCarddecks = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            try {

                jsonObjectDeckID.put(JsonKeys.CARDDECK_ID, id);
                jsonArray.put(jsonObjectDeckID);
                jsonObjectCarddecks.put(JsonKeys.CATEGORY_CARDDECKS, jsonArray);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            //patch carddeck mit carddeckid und cardid
            AsyncPatchRemoteCategory task = new AsyncPatchRemoteCategory(jsonObjectCarddecks);
            task.execute(parentId);


        } else {
            Log.w("POST CARDDECK", "FAILED");
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
