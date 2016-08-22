package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetCarddeck;

import de.uulm.einhoernchen.flashcardsapp.Fragment.CarddeckRecyclerViewAdapter;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class DummyContentCarddeck {

    private static List<CardDeck> cardDecks = new ArrayList<>();

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param parentId given by the main activity
     * @param fragmentManager given by main activity
     */
    public void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager) {

        AsyncGetCarddeck asyncGetCarddeck = new AsyncGetCarddeck(parentId, new AsyncGetCarddeck.AsyncResponseCarddeck() {

            @Override
            public void processFinish(List<CardDeck> cardDecks) {

                DummyContentCarddeck.cardDecks = cardDecks;
                DummyContentCarddeck.ItemFragmentCarddeck fragment = new DummyContentCarddeck.ItemFragmentCarddeck();

                Bundle args = new Bundle();
                args.putLong(ItemFragmentCarddeck.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_home, fragment);
                fragmentTransaction.commit();

            }

        });
        asyncGetCarddeck.execute();

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

                recyclerView.setAdapter(new CarddeckRecyclerViewAdapter(cardDecks, mListener));
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
