package de.uulm.einhoernchen.flashcardsapp.Fragment.content;

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

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetLocalCardDeck;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetRemoteCarddeck;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncSaveLocalCardDeck;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.RecyclerViewAdapterCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentCarddeck {

    private static List<CardDeck> cardDecks = new ArrayList<>();
    private static boolean isUpToDate = false;

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param parentId given by the main activity
     * @param fragmentManager given by main activity
     *
     */
    public void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed, final DbManager db) {

        AsyncGetRemoteCarddeck asyncGetCarddeck = new AsyncGetRemoteCarddeck(parentId, new AsyncGetRemoteCarddeck.AsyncResponseCarddeck() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                // Saving the collected categories localy
                AsyncSaveLocalCardDeck asyncSaveLocalCarddeck = new AsyncSaveLocalCardDeck(parentId);
                asyncSaveLocalCarddeck.setDbManager(db);
                asyncSaveLocalCarddeck.setCardDecks(cardDecks);
                asyncSaveLocalCarddeck.execute();


                ContentCarddeck.cardDecks = cardDecks;
                ContentCarddeck.ItemFragmentCarddeck fragment = new ContentCarddeck.ItemFragmentCarddeck();

                Bundle args = new Bundle();
                args.putLong(ItemFragmentCarddeck.ARG_PARENT_ID, parentId);
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

                isUpToDate = true;
                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetCarddeck.setProgressbar(progressBarMain);
        asyncGetCarddeck.execute();

    }


    /**
     * Collects Carddecks from sqLite
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param parentId
     * @param supportFragmentManager
     * @param progressBar
     * @param backPressed
     * @param db
     */
    public void collectItemsFromDb(final long parentId, final FragmentManager supportFragmentManager, final ProgressBar progressBar, final boolean backPressed, final DbManager db) {

        AsyncGetLocalCardDeck asyncGetLocalCardDeck = new AsyncGetLocalCardDeck(parentId, new AsyncGetLocalCardDeck.AsyncResponseCardDeckLocal() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                ContentCarddeck.cardDecks = cardDecks;

                ContentCarddeck.ItemFragmentCarddeck fragment = new ContentCarddeck.ItemFragmentCarddeck();

                Bundle args = new Bundle();
                args.putLong(ContentCarddeck.ItemFragmentCarddeck.ARG_PARENT_ID, parentId);
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

                isUpToDate = false;
                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetLocalCardDeck.setProgressbar(progressBar);
        asyncGetLocalCardDeck.setDbManager(db);
        asyncGetLocalCardDeck.execute();

    }


    /**
     * A fragment representing a list of Carddeck Items.
     * <p/>
     * Activities containing this fragment MUST implement the {@link OnCarddeckListFragmentInteractionListener}
     * interface.
     */
    public static class ItemFragmentCarddeck extends Fragment {

        private static final String ARG_COLUMN_COUNT = "column-count";
        public static final String ARG_PARENT_ID = "parentId";
        private int mColumnCount = 1;
        private OnCarddeckListFragmentInteractionListener mListener;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public ItemFragmentCarddeck() {
        }


        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static ItemFragmentCarddeck newInstance(int columnCount) {

            ItemFragmentCarddeck fragment = new ItemFragmentCarddeck();

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

                recyclerView.setAdapter(new RecyclerViewAdapterCarddeck(cardDecks, mListener, isUpToDate));
            }
            return view;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnCarddeckListFragmentInteractionListener) {
                mListener = (OnCarddeckListFragmentInteractionListener) context;
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
    public interface OnCarddeckListFragmentInteractionListener {

        void onCarddeckListFragmentInteraction(CardDeck item);
    }
}
