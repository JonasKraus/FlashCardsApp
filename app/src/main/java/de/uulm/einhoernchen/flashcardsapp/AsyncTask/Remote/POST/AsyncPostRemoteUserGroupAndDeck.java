package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.UserGroupsActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalUserGroupJoinTable;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalUsers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUsersOfUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUsers;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteUserGroupAndDeck extends AsyncTask<Long, Long, Long> {

    private JSONObject jsonObjectGroup;
    private JSONObject jsonObjectDeck;

    private Long parentId;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param jsonObjectGroup
     * @param jsonObjectDeck
     *
     */
    public AsyncPostRemoteUserGroupAndDeck(JSONObject jsonObjectGroup, JSONObject jsonObjectDeck) {

        this.jsonObjectGroup = jsonObjectGroup;
        this.jsonObjectDeck = jsonObjectDeck;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Long doInBackground(Long... params) {

        this.parentId = params[0];

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
            wr.writeBytes(jsonObjectGroup.toString());
            wr.flush();

            Log.d(urlConnection.getRequestMethod() + " json", jsonObjectGroup.toString());

            if (urlConnection.getResponseCode() >= 400) {

                Log.e("resp", urlConnection.getResponseCode()+ " " + urlConnection.getResponseMessage() + "");
            }

            return JsonParser.readResponse(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBack group " + urlConnection.getRequestMethod(), e.toString() + jsonObjectGroup.toString());
            System.out.println(e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Long id) {
        super.onPostExecute(id);

        if (id != null) {

            try {

                // Add the id to the group
                jsonObjectGroup.put(JsonKeys.GROUP_ID, id);

                // Add the group objeckt to the carddeck
                jsonObjectDeck.put(JsonKeys.CARDDECK_GROUP, jsonObjectGroup);

                // Init async task
                AsyncPostRemoteCarddeck task = new AsyncPostRemoteCarddeck(jsonObjectDeck);

                // Execute task
                task.execute(parentId);

                final long userGroupId = id;

                AsyncGetRemoteUsersOfUserGroup asyncGetUsers = new AsyncGetRemoteUsersOfUserGroup(new AsyncGetRemoteUsersOfUserGroup.AsyncResponseUsers() {

                    @Override
                    public void processFinish(List<User> users) {

                        AsyncSaveLocalUsers asyncSaveUsers = new AsyncSaveLocalUsers();
                        asyncSaveUsers.execute(users);

                        AsyncSaveLocalUserGroupJoinTable localuserGroupJoinTable = new AsyncSaveLocalUserGroupJoinTable();
                        localuserGroupJoinTable.setUsers(users);
                        localuserGroupJoinTable.execute(userGroupId);

                    }

                });

                if (ProcessConnectivity.isOk(Globals.getContext())) {

                    asyncGetUsers.execute(userGroupId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.w("POST FAILED", this.getClass().getName());
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
