package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 04.07.2016.
 */
public class AsyncGetRemoteUsersOfUserGroup extends AsyncTask<Long, Void, List<User>>{

    /**
     * Interface to receive the users in the activity that called this async task
     */
    public interface AsyncResponseUsers {
        void processFinish(List<User> users);
    }

    public AsyncResponseUsers delegate = null;

    public AsyncGetRemoteUsersOfUserGroup(AsyncResponseUsers delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<User> doInBackground(Long... params) {


        for (Long groupId : params) {
            List<User> users;

            String urlString = Routes.URL + Routes.SLASH + Routes.USER_GROUPS + Routes.SLASH + groupId + Routes.SLASH + Routes.USERS; // URL to call

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

                if (response >= 200 && response <= 399) {
                    users = JsonParser.parseUsers(urlConnection.getInputStream());
                    return users;
                }

            } catch (Exception e) {

                System.out.println(e.getMessage());

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<User> users) {

        if (users != null) {

            delegate.processFinish(users);

        }
    }
}
