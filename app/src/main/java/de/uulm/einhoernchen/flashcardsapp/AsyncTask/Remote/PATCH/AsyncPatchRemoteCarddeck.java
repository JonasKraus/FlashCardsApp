package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
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
public class AsyncPatchRemoteCarddeck extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;
    private boolean append;
    private RecyclerView.Adapter adapter;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     *
     * @param jsonObject
     */
    public AsyncPatchRemoteCarddeck(JSONObject jsonObject, boolean append) {

        this.append = append;
        this.jsonObject = jsonObject;
        this.adapter = adapter;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        for (Long carddeckId : params) {

            String urlString = Routes.URL + Routes.SLASH + Routes.CARD_DECKS + Routes.SLASH + carddeckId;

            // Only append if necessary
            if (append) {

                urlString +=  Routes.QUESTION_MARK + Routes.APPEND + Routes.EQUAL + Routes.BOOL_TRUE;
            }

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

                Log.d(urlConnection.getRequestMethod() + " carddeck json", jsonObject.toString());


                //Log.e("resp", urlConnection.getResponseCode()+"");

                if (urlConnection.getResponseCode() >= 400) {

                    Log.e("resp patch carddeck", urlConnection.getResponseCode()+ " " + urlConnection.getResponseMessage() + "");

                }

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

            if (!append) {

                ((MainActivity)Globals.getContext()).setCarddeckList(false);
            }

        } else {
            Log.w("PATCH DECK", "FAILED");
        }

    }


    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
