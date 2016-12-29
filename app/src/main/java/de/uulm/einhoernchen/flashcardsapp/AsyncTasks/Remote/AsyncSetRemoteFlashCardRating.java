package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Activity.LoginActivity;
import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSetRemoteFlashCardRating extends AsyncTask<Long, Long, Boolean> {

    private String ratingObjectName;
    private long objectId;
    private Long userId;
    private int ratingmodifier;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param ratingObjectName
     * @param objectId
     * @param userId
     * @param ratingmodifier
     */
    public AsyncSetRemoteFlashCardRating(String ratingObjectName, long objectId, Long userId, int ratingmodifier) {

        this.ratingObjectName = ratingObjectName;
        this.objectId = objectId;
        this.userId = userId;
        this.ratingmodifier = ratingmodifier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.RATINGS;

        Log.d("back call to ", urlString);
        Log.d("back call param ", ratingObjectName);
        Log.d("back call param ", objectId + "");
        Log.d("back call param ", userId + "");
        Log.d("back call param ", ratingmodifier + "");
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for delete request
            //urlConnection.setDoInput(true); // Important for delete request
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            // Construct json body
            JSONObject cred = new JSONObject();
            JSONObject author = new JSONObject();
            JSONObject ratingObject = new JSONObject();

            author.put("userId", userId);
            ratingObject.put(ratingObjectName + "Id", objectId);

            cred.put("author", author);
            cred.put(ratingObjectName, ratingObject);
            cred.put("ratingModifier", ratingmodifier);

            urlConnection.connect();

            Log.d("post cred", cred.toString());

            //Send request JSON Data
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(cred.toString());
            wr.flush();

            return JsonParser.readResponseRating(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground rating", e.toString());
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean doPost) {
        super.onPostExecute(doPost);

        // TODO for testing only
        // Should collect data from db
        if (doPost) {

            Log.d("do post update rating", ratingObjectName);

        } else {

            Log.d("do post update rating", ratingObjectName);
        }


    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
