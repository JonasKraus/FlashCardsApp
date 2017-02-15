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
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteToken extends AsyncTask<Long, String, String> {

    private JSONObject jsonObject;
    private DbManager db;
    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteToken(JSONObject jsonObject, DbManager db) {

        this.jsonObject = jsonObject;
        this.db = db;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.LOGIN;
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

            return JsonParser.readResponseToken(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBack token " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
            System.out.println(e.toString());
            return null;
        }

    }

    @Override
    protected void onPostExecute(String token) {
        super.onPostExecute(token);

        if (token != null) {

            db.saveToken(token);
            Globals.setToken(token);
            Log.d("token", token);

        } else {
            Log.w("POST TOKEN", "FAILED");
        }

    }

}
