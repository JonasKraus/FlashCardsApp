package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Saves users
 *
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017-29-01
 */
public class AsyncSaveLocalUsers extends AsyncTask<List<User>, Long, Void> {

    @Override
    protected Void doInBackground(List<User>... params) {

        Globals.getDb().saveUsers(params[0]);

        return null;
    }
}
