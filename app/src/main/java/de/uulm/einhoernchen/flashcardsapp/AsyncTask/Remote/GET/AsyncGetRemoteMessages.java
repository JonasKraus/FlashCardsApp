package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetRemoteMessages extends AsyncTask<Long, Void, List<Message>>{

    /**
     * Interface to receive the messages in the activity that called this async task
     */
    public interface AsyncResponseMessages {
        void processFinish(List<Message> messages);
    }

    public AsyncResponseMessages delegate = null;

    public AsyncGetRemoteMessages(AsyncResponseMessages delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Message> doInBackground(Long... params) {

        List<Message> messages;


        String urlString = Routes.URL + Routes.SLASH + Routes.MESSAGES; // TODO URL to call

        Log.d("back call to ", urlString);

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // send token for identification of the user
            urlConnection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            //Log.d("response messages", response + " " + urlConnection.getResponseMessage());

            if (response >= 200 && response <=399){


                messages = JsonParser.parseMessages(urlConnection.getInputStream());

                return messages;
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Message> messages) {

        if (messages != null) {

            delegate.processFinish(messages);

        }
    }
}
