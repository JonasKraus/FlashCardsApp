package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPatchRemoteCard extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private long cardId;


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
            urlConnection.setRequestMethod("PATCH");

            urlConnection.connect();

            //Send request JSON Data
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();

            //Log.d("json", jsonObject.toString());

            //Log.d("resp", urlConnection.getResponseCode()+"");

            return JsonParser.readResponse(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBack card patch", e.toString());
            System.out.println(e.toString());
            return null;

        }

    }

    @Override
    protected void onPostExecute(Long id) {
        super.onPostExecute(id);

        if (id != null) {


        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
