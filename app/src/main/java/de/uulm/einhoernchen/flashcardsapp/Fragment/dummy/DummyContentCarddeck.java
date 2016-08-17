package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetCarddeck;

import de.uulm.einhoernchen.flashcardsapp.Fragment.CarddeckRecyclerViewAdapter;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCarddeck {

    private static List<CardDeck> cardDecks1 = new ArrayList<>();

    private Fragment fragment1;
    private Activity activity;


    public List<CardDeck> collectItemsFromServer(final long parentId, final FragmentManager fragmentManager) {

        AsyncGetCarddeck asyncGetCarddeck = new AsyncGetCarddeck(parentId, new AsyncGetCarddeck.AsyncResponseCarddeck() {
            @Override
            public List<CardDeck> processFinish(List<CardDeck> cardDecks) {
                Log.d("Fertig", cardDecks.size()+"");
                cardDecks1 = cardDecks;
                DummyContentCarddeck.ItemFragmentCarddeck fragment = new DummyContentCarddeck.ItemFragmentCarddeck();
                Bundle args = new Bundle();
                args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_home, fragment);
                fragmentTransaction.commit();

                return cardDecks;

            }

        });
        asyncGetCarddeck.execute();

        return null;
    }


    /**
     * A fragment representing a list of Items.
     * <p/>
     * Activities containing this fragment MUST implement the {@link OnCarddeckListFragmentInteractionListener}
     * interface.
     */
    public static class ItemFragmentCarddeck extends Fragment {

        // TODO: Customize parameter argument names
        private static final String ARG_COLUMN_COUNT = "column-count";
        public static final String ARG_PARENT_ID = "parentId";
        // TODO: Customize parameters
        private int mColumnCount = 1;
        private OnCarddeckListFragmentInteractionListener mListener;

        private long parentId = -1;

        private Fragment fragment;

        private RecyclerView recyclerViewTest;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public ItemFragmentCarddeck() {
            this.fragment = this;
        }


        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static ItemFragmentCarddeck newInstance(int columnCount, long parentId) {
            ItemFragmentCarddeck fragment = new ItemFragmentCarddeck();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            args.putLong(ARG_PARENT_ID, parentId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("onCreate", "yes");
            if (getArguments() != null) {
                mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
                parentId = getArguments().getLong(ARG_PARENT_ID);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_item_list, container, false);

            Log.d("onCreateView", "jetzt");
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;

                recyclerViewTest = recyclerView;

                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }

                //recyclerView.setAdapter(new FlashcardRecyclerViewAdapter(DummyContentCard.ITEMS, mListener));
                //DummyContentCarddeck dummyContentCarddeck = new DummyContentCarddeck(this.fragment);
                recyclerView.setAdapter(new CarddeckRecyclerViewAdapter(cardDecks1, mListener));
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
        // TODO: Update argument type and name
        void onCarddeckListFragmentInteraction(CardDeck item);
    }
}
