package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetCarddeck;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetCategory;
import de.uulm.einhoernchen.flashcardsapp.Fragment.CarddeckRecyclerViewAdapter;
import de.uulm.einhoernchen.flashcardsapp.Fragment.CategoryRecyclerViewAdapter;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCategory {

    private static List<Category> categories = new ArrayList<>();

    public void collectItemsFromServer(final int categoryLevel, final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed) {

        AsyncGetCategory asyncGetCategory = new AsyncGetCategory(categoryLevel, parentId, new AsyncGetCategory.AsyncResponseCategory() {

            @Override
            public void processFinish(List<Category> categories) {

                DummyContentCategory.categories = categories;
                DummyContentCategory.ItemFragmentCategory fragment = new DummyContentCategory.ItemFragmentCategory();

                Bundle args = new Bundle();
                args.putLong(DummyContentCarddeck.ItemFragmentCarddeck.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();

                if (backPressed) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetCategory.setProgressbar(progressBarMain);
        asyncGetCategory.execute();

    }


    /**
     * A fragment representing a list of Category Items.
     * <p/>
     * Activities containing this fragment MUST implement the {@link DummyContentCategory.OnCategoryListFragmentInteractionListener}
     * interface.
     */
    public static class ItemFragmentCategory extends Fragment {

        private static final String ARG_COLUMN_COUNT = "column-count";
        public static final String ARG_PARENT_ID = "parentId";
        private int mColumnCount = 1;
        private DummyContentCategory.OnCategoryListFragmentInteractionListener mListener;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public ItemFragmentCategory() {
        }


        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static DummyContentCategory.ItemFragmentCategory newInstance(int columnCount) {

            DummyContentCategory.ItemFragmentCategory fragment = new DummyContentCategory.ItemFragmentCategory();

            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_item_list, container, false);

            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;

                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }

                recyclerView.setAdapter(new CategoryRecyclerViewAdapter(categories, mListener));
            }
            return view;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof DummyContentCategory.OnCategoryListFragmentInteractionListener) {
                mListener = (DummyContentCategory.OnCategoryListFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnListFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCategoryListFragmentInteractionListener {

        void onCategoryListFragmentInteraction(Category item);
    }

}
