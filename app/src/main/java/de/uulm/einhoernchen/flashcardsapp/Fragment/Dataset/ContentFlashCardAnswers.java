package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncSaveLocalFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentFlashCardAnswers {

    private static List<Answer> answers = new ArrayList<Answer>();

    public static FragmentFlashCardAnswers fragment;

    private static boolean isUpToDate = false;
    private static DbManager db = Globals.getDb();



    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.-12-16
     *
     * @param parentId
     * @param backPressed
     *
     */
    public void collectItemsFromServer(final long parentId, final boolean backPressed) {

        AsyncGetRemoteFlashCardAnswers asyncGetFlashCardAnswers = new AsyncGetRemoteFlashCardAnswers(parentId, new AsyncGetRemoteFlashCardAnswers.AsyncResponseFlashCardAnswers() {

            @Override
            public void processFinish(List<Answer> answers) {


                AsyncSaveLocalFlashCardAnswers asyncSaveAnswersLocal = new AsyncSaveLocalFlashCardAnswers(parentId);
                asyncSaveAnswersLocal.setFlashCardAnswers(answers);
                asyncSaveAnswersLocal.execute();

                ContentFlashCardAnswers.answers = answers;
                isUpToDate = true;

                FragmentFlashCardAnswers fragment = new FragmentFlashCardAnswers();
                fragment.setItemList(answers);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

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

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetFlashCardAnswers.execute();
        }

    }


    /**
     * Get data fro local sqlite db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-16
     *
     * @param parentId
     * @param backPressed
     */
    public void collectItemsFromDb(final long parentId, final boolean backPressed) {

        AsyncGetLocalFlashCardAnswers asyncGetAnswersLocal = new AsyncGetLocalFlashCardAnswers(parentId, new AsyncGetLocalFlashCardAnswers.AsyncResponseLocalFlashCardAnswers() {

            @Override
            public void processFinish(List<Answer> answers) {

                ContentFlashCardAnswers.answers = answers;
                isUpToDate = false;

                FragmentFlashCardAnswers fragment = new FragmentFlashCardAnswers();
                fragment.setItemList(answers);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentFlashcards.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

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

        asyncGetAnswersLocal.execute();

    }



}
