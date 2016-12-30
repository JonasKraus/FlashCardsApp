package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteRating extends AsyncTask<Long, Long, Long> {

    private String ratingObjectName;
    private long objectId;
    private Long userId;
    private int ratingmodifier;
    private DbManager db;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param ratingObjectName
     * @param objectId
     * @param userId
     * @param ratingmodifier
     */
    public AsyncPostRemoteRating(String ratingObjectName, long objectId, Long userId, int ratingmodifier, DbManager db) {

        this.ratingObjectName = ratingObjectName;
        this.objectId = objectId;
        this.userId = userId;
        this.ratingmodifier = ratingmodifier;
        this.db = db;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        String urlString = Routes.URL + Routes.SLASH + Routes.RATINGS;

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

            //Send request JSON Data
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(cred.toString());
            wr.flush();

            return JsonParser.readResponseRating(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground rating", e.toString());
            System.out.println(e.toString());
            return null;

        }

    }

    @Override
    protected void onPostExecute(Long ratingId) {
        super.onPostExecute(ratingId);

        if (ratingId != null) {

            if (ratingObjectName == "flashcard") {

                db.addRatingIdToCardVoting(ratingId, objectId);
            } else if (ratingObjectName == "answer") {

                db.addRatingIdToAnswerVoting(ratingId, objectId);
            }
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
