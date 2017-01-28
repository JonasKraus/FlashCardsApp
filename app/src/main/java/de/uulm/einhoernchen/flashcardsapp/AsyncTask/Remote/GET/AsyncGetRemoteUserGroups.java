package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetRemoteUserGroups extends AsyncTask<Long, Void, List<UserGroup>>{

    /**
     * Interface to receive the user in the activity that called this async task
     */
    public interface AsyncResponseUserGroups {
        void processFinish(List<UserGroup> userGroups);
    }

    public AsyncResponseUserGroups delegate = null;

    public AsyncGetRemoteUserGroups(AsyncResponseUserGroups delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<UserGroup> doInBackground(Long... params) {

        List<UserGroup> userGroups;


        String urlString = Routes.URL + Routes.SLASH + Routes.USER_GROUPS; // URL to call

        Log.d("back call to ", urlString);

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
                userGroups = JsonParser.parseUserGroups(urlConnection.getInputStream());
                return userGroups;
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<UserGroup> userGroups) {

        if (userGroups != null) {

            delegate.processFinish(userGroups);

        }
    }
}
