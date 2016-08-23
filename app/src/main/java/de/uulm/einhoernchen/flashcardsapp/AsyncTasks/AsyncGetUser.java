package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.einhoernchen.flashcardsapp.Consts.Routes;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetUser extends AsyncTask<Long, Void, User>{

    /**
     * Interface to receive the user in the activity that called this async task
     */
    public interface AsyncResponseUser {
        void processFinish(User user);
    }

    private final long id;
    public AsyncResponseUser delegate = null;

    public AsyncGetUser(long id, AsyncResponseUser delegate) {
        this.delegate = delegate;
        this.id = id;
    }

    @Override
    protected User doInBackground(Long... params) {

        User user;


        String urlString = Routes.URL + Routes.SLASH + Routes.USERS + Routes.SLASH + id; // URL to call

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            if (response >= 200 && response <=399){
                user = JsonParser.parseUser(urlConnection.getInputStream());
                return user;
            }

        } catch (Exception e) {

            Log.e("Async getUser Fehler", e.toString());
            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {

        if (user != null) {

            delegate.processFinish(user);

        }
    }
}
