package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncSaveLocalFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncSaveLocalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentFlashCardAnswers {

    private static List<Answer> answers = new ArrayList<Answer>();

    public static FragmentFlashcards fragment;

    private static boolean isUpToDate = false;
    private static DbManager db;



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.-12-16
     *
     * @param parentId
     * @param fragmentManager
     * @param progressBarMain
     */
    public void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed, final DbManager db) {

        this.db = db;

        AsyncGetRemoteFlashCardAnswers asyncGetFlashCardAnswers = new AsyncGetRemoteFlashCardAnswers(parentId, new AsyncGetRemoteFlashCardAnswers.AsyncResponseFlashCardAnswers() {

            @Override
            public void processFinish(List<Answer> answers) {


                AsyncSaveLocalFlashCardAnswers asyncSaveAnswersLocal = new AsyncSaveLocalFlashCardAnswers(parentId);
                asyncSaveAnswersLocal.setDbManager(db);
                asyncSaveAnswersLocal.setFlashCardAnswers(answers);
                asyncSaveAnswersLocal.execute();

                ContentFlashCardAnswers.answers = answers;
                isUpToDate = true;

                FragmentFlashCardAnswers fragment = new FragmentFlashCardAnswers();
                fragment.setItemList(answers);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();

                // TODO delete if always loaded from local db
                /*
                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (!fragmentAnimated) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentAnimated = true;
                }
                */

                fragmentTransaction.replace(R.id.fragment_container_card_answer, fragment);
                fragmentTransaction.commit();

                //Log.d("async load", "online");
            }

        });

        asyncGetFlashCardAnswers.setProgressbar(progressBarMain);
        asyncGetFlashCardAnswers.execute();

    }


    /**
     * Get data fro local sqlite db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-16
     *
     * @param parentId
     * @param supportFragmentManager
     * @param progressBar
     * @param backPressed
     * @param db
     */
    public void collectItemsFromDb(final long parentId, final FragmentManager supportFragmentManager, final ProgressBar progressBar, final boolean backPressed, final DbManager db) {

        this.db = db;

        AsyncGetLocalFlashCardAnswers asyncGetAnswersLocal = new AsyncGetLocalFlashCardAnswers(parentId, new AsyncGetLocalFlashCardAnswers.AsyncResponseLocalFlashCardAnswers() {

            @Override
            public void processFinish(List<Answer> answers) {

                ContentFlashCardAnswers.answers = answers;
                isUpToDate = false;

                FragmentFlashCardAnswers fragment = new FragmentFlashCardAnswers();
                fragment.setItemList(answers);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        supportFragmentManager.beginTransaction();

                // TODO delete if always loaded from local db
                /*
                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (!fragmentAnimated) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentAnimated = true;
                }
                */

                fragmentTransaction.replace(R.id.fragment_container_card_answer, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetAnswersLocal.setProgressbar(progressBar);
        asyncGetAnswersLocal.setDbManager(db);
        asyncGetAnswersLocal.execute();

    }



}
