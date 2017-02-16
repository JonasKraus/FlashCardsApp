package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local;

import android.os.AsyncTask;
import android.view.View;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalMessages extends AsyncTask<Long, Long, List<Message>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalMessages {
        void processFinish(List<Message> messages);
    }

    public AsyncResponseLocalMessages delegate = null;


    public AsyncGetLocalMessages(AsyncResponseLocalMessages delegate) {

        this.delegate = delegate;
    }

    @Override
    protected List<Message> doInBackground(Long... params) {

        return  Globals.getDb().getMessages();
    }

    @Override
    protected void onPostExecute(List<Message> messages) {
        super.onPostExecute(messages);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(messages);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
