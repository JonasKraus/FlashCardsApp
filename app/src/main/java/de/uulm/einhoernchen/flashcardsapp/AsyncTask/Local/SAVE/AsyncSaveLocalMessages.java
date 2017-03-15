package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE;

import android.os.AsyncTask;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalMessages extends AsyncTask<Long, Long, Void> {

    private List<Message> messages;


    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Long... params) {
        Globals.getDb().saveMessages(messages);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
