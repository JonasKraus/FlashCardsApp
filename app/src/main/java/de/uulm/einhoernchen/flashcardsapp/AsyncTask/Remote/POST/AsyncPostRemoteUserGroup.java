package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.content.Intent;
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
import de.uulm.einhoernchen.flashcardsapp.Activity.UserGroupsActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCategory;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteUserGroup extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObject;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObject
     */
    public AsyncPostRemoteUserGroup(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {


        String urlString = Routes.URL + Routes.SLASH + Routes.USER_GROUPS;
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

            //Log.d(urlConnection.getRequestMethod() + " json", jsonObject.toString());

            return JsonParser.readResponse(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBack group " + urlConnection.getRequestMethod(), e.toString() + jsonObject.toString());
            System.out.println(e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Long id) {
        super.onPostExecute(id);

        if (id != null) {

            Globals.getContext().startActivity(new Intent(Globals.getContext(), UserGroupsActivity.class));
            Toast.makeText(Globals.getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();

        } else {
            Log.w("POST FAILED", this.getClass().getName());
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
