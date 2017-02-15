package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PUT;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPutRemoteCategory extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPutRemoteCategory(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        for (long carddeckId: params) {

            String urlString = Routes.URL + Routes.SLASH + Routes.CATEGORIES + Routes.SLASH + carddeckId;
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
                urlConnection.setRequestMethod("PUT");

                urlConnection.connect();

                //Send request JSON Data
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();

                //Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack categories " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
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

            MainActivity mainActivity = (MainActivity) Globals.getContext();
            mainActivity.setCarddeckList(false);

        } else {

            Toast.makeText(Globals.getContext(), R.string.failed_to_save, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
