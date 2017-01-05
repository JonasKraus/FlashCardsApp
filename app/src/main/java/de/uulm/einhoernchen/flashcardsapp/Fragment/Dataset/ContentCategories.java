package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncGetLocalCategories;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteCategories;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Local.AsyncSaveLocalCategories;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCategories;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ContentCategories {

    private static List<Category> categories = new ArrayList<>();
    private static boolean isUpToDate = false;

    public void collectItemsFromServer(final int categoryLevel, final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed, final DbManager db, Context context) {

        AsyncGetRemoteCategories asyncGetCategory = new AsyncGetRemoteCategories(categoryLevel, parentId, new AsyncGetRemoteCategories.AsyncResponseRemoteCategories() {

            @Override
            public void processFinish(List<Category> categories) {

                // Saving the collected categories localy
                AsyncSaveLocalCategories asyncSaveLocalCategory = new AsyncSaveLocalCategories(parentId);
                asyncSaveLocalCategory.setDbManager(db);
                asyncSaveLocalCategory.setCategories(categories);
                asyncSaveLocalCategory.execute();

                ContentCategories.categories = categories;
                isUpToDate = true;

                FragmentCategories fragment = new FragmentCategories();
                fragment.setItemList(categories);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();

                /*
                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                */

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetCategory.setProgressbar(progressBarMain);

        if (ProcessConnectivity.isOk(context)) {

            asyncGetCategory.execute();
        }
    }


    public void collectItemsFromDb(final int categoryLevel, final long parentId, final FragmentManager supportFragmentManager, final ProgressBar progressBar, final boolean backPressed, final DbManager db, Context context) {

        AsyncGetLocalCategories asyncGetLocalCategory = new AsyncGetLocalCategories(parentId, new AsyncGetLocalCategories.AsyncResponseLocalCategories() {

            @Override
            public void processFinish(List<Category> categories) {

                ContentCategories.categories = categories;

                isUpToDate = false;

                FragmentCategories fragment = new FragmentCategories();

                fragment.setItemList(categories);
                fragment.setDb(db);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCategories.ARG_PARENT_ID, parentId);
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

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalCategory.setProgressbar(progressBar);
        asyncGetLocalCategory.setDbManager(db);
        asyncGetLocalCategory.execute();

    }





}
