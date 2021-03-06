package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.Response.Response;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteToken extends AsyncTask<Long, String, Response> {

    private final AsyncResponse delegate;
    private JSONObject jsonObject;
    private DbManager db;
    private Response errorResponse;

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteToken(JSONObject jsonObject, DbManager db) {

        this.delegate = null;
        this.jsonObject = jsonObject;
        this.db = db;
    }

    public interface AsyncResponse {

        void processFinish(Response response);
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteToken(JSONObject jsonObject, DbManager db, AsyncPostRemoteToken.AsyncResponse delegate) {

        this.jsonObject = jsonObject;
        this.db = db;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Response doInBackground(Long... params) {

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

            if (urlConnection.getResponseCode() >= 400) {

                errorResponse = new Response(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
            } else {

                InputStream inputStream = urlConnection.getInputStream();
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                return JsonParser.readResponseObject(reader);
            }

            return errorResponse;
        } catch (Exception e) {

            Log.d("doInBack token " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
            //System.out.println(e.toString());
            return errorResponse;
        }

    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

        Log.d("response", response.toString());

        if (delegate != null) {

            delegate.processFinish(response);
        }

        if (response != null && response.getStatuscode() < 400) {

            //db.saveToken(userId, token);
            try {

                db.updateUserAfterLogin(response.getUserId(), jsonObject.getString(JsonKeys.USER_EMAIL), jsonObject.getString(JsonKeys.USER_PASSWORD), response.getToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Globals.setToken(response.getToken());

        } else {
            Log.w("POST TOKEN", "FAILED " + response.toString());
        }

    }

}
