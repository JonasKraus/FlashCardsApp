package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.GET.AsyncGetLocalCategories;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteCategories;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.SAVE.AsyncSaveLocalCategories;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentCategories;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
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

    public void collectItemsFromServer(final int categoryLevel, final long parentId, final boolean backPressed) {

        AsyncGetRemoteCategories asyncGetCategory = new AsyncGetRemoteCategories(categoryLevel, parentId, new AsyncGetRemoteCategories.AsyncResponseRemoteCategories() {

            @Override
            public void processFinish(List<Category> categories) {

                // Saving the collected categories localy
                AsyncSaveLocalCategories asyncSaveLocalCategory = new AsyncSaveLocalCategories(parentId);
                asyncSaveLocalCategory.setCategories(categories);
                asyncSaveLocalCategory.execute();

                ContentCategories.categories = categories;
                isUpToDate = true;

                FragmentCategories fragment = new FragmentCategories();
                fragment.setItemList(categories);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCarddecks.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

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

        if (ProcessConnectivity.isOk(Globals.getContext())) {

            asyncGetCategory.execute();
        }
    }


    public void collectItemsFromDb(final int categoryLevel, final long parentId, final boolean backPressed) {

        AsyncGetLocalCategories asyncGetLocalCategory = new AsyncGetLocalCategories(parentId, new AsyncGetLocalCategories.AsyncResponseLocalCategories() {

            @Override
            public void processFinish(List<Category> categories) {

                ContentCategories.categories = categories;

                isUpToDate = false;

                FragmentCategories fragment = new FragmentCategories();

                fragment.setItemList(categories);
                fragment.setUpToDate(isUpToDate);

                Bundle args = new Bundle();
                args.putLong(FragmentCategories.ARG_PARENT_ID, parentId);
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

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalCategory.execute();

    }





}
