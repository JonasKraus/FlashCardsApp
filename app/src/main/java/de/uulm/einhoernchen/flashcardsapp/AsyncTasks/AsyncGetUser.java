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

import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetUser extends AsyncTask<Long, Void, User>{

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

        String urlString = "http://192.168.0.8:9000/users/"+id; // URL to call

        HttpURLConnection urlConnection = null;
        //InputStream in = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            String decodedString;



            while ((decodedString = in.readLine()) != null) {
                Log.d("response", decodedString);

                int response = urlConnection.getResponseCode();

                if (response >= 200 && response <=399){

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(decodedString);
                    user = new User(id,root.get("avatar").asText(), root.get("name").asText(), "", root.get("email").asText(), root.get("rating").asInt(), root.get("created").asText(), root.get("lastLogin").asText());
                    Log.d("user instanz", user.toString());
                    return user;

                } else {

                }
            }
            in.close();

        } catch (Exception e) {

            Log.e("fehler", e.toString());
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
