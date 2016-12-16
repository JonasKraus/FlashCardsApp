package de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncSaveLocalFlashCardAnswers extends AsyncTask<Long, Long, Void> {

    private List<Answer> answers;
    private DbManager db;


    /**
     * Use this method to save a list of flashcards
     * @param answers
     */
    public void setFlashCardAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    /**
     * User this to only save one answer
     * @param answer
     */
    public void setFlashCardAnswer(Answer answer) {
        this.answers = new ArrayList<Answer>();
        this.answers.add(answer);
    }

    public void setDbManager(DbManager dbManager) {
        this.db = dbManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final Long parentId;

    public AsyncSaveLocalFlashCardAnswers(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    protected Void doInBackground(Long... params) {
        db.saveAnswers(answers, parentId);

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}