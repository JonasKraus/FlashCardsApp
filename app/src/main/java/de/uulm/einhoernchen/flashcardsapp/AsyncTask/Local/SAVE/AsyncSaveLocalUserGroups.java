package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalUserGroups extends AsyncTask<Long, Long, Void> {

    private List<UserGroup> userGroups;


    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Long... params) {
        Globals.getDb().saveUserGroups(userGroups);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
