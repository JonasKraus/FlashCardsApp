package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-01-05
 */
public class AsyncPatchRemoteCategory extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private long parentId;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     *
     * @param jsonObject
     */
    public AsyncPatchRemoteCategory(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        for (Long categoryId : params) {

            this.parentId = categoryId;

            String urlString = Routes.URL + Routes.SLASH + Routes.CATEGORIES + Routes.SLASH + categoryId + Routes.QUESTION_MARK + Routes.APPEND + Routes.EQUAL + Routes.BOOL_TRUE;

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

                //kLog.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

                // TODO give message to main thread
                /*
                if (urlConnection.getResponseCode() >= 400) {

                    Toast.makeText(Globals.getContext(),urlConnection.getResponseMessage(), Toast.LENGTH_SHORT);
                }
                */

                return JsonParser.readResponse(urlConnection.getInputStream());

            } catch (Exception e) {

                Log.e("doInBack carddeck " + urlConnection.getRequestMethod(), e.toString() + " " + jsonObject.toString());
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
            Log.w("PATCH DECK", "FAILED");
        }

    }


    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
