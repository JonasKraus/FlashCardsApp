package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetLocalFlashCardAnswers extends AsyncTask<Long, Long, List<Answer>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Globals.getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseLocalFlashCardAnswers {
        void processFinish(List<Answer> answers);
    }

    public AsyncResponseLocalFlashCardAnswers delegate = null;
    private final Long parentId;

    public AsyncGetLocalFlashCardAnswers(Long parentId, AsyncResponseLocalFlashCardAnswers delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<Answer> doInBackground(Long... params) {

        return  Globals.getDb().getAnswers(parentId);
    }

    @Override
    protected void onPostExecute(List<Answer> answers) {
        super.onPostExecute(answers);

        Globals.getProgressBar().setVisibility(View.GONE);
        delegate.processFinish(answers);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
