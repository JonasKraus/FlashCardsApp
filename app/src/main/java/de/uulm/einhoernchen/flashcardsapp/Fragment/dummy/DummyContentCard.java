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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetCarddeck;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FlashcardRecyclerViewAdapter;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class DummyContentCard {

    private static List<FlashCard> flashCards = new ArrayList<FlashCard>();

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *
     * @param parentId
     * @param fragmentManager
     * @param progressBarMain
     */
    public static void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain) {

        AsyncGetFlashCard asyncGetFlashCard = new AsyncGetFlashCard(parentId, new AsyncGetFlashCard.AsyncResponseFlashCard() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                DummyContentCard.flashCards = flashCards;
                DummyContentCard.ItemFragmentFlashcard fragment = new DummyContentCard.ItemFragmentFlashcard();

                Bundle args = new Bundle();
                args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, parentId);
                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_home, fragment);
                fragmentTransaction.commit();

            }

        });

        asyncGetFlashCard.setProgressbar(progressBarMain);
        asyncGetFlashCard.execute();

    }

    private static FlashCard createDummyFlashCard(int position) {
        Random rand = new Random();
        User author = new User((long)position,"avatar","User "+position,"pwd","user"+position+"@flashcards.de",rand.nextInt(100), new Date().toString(), new Date().toString());
        Question question = new Question("Item Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ", author);
        Answer answer = new Answer("consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ","hint ....."+position, author);
        List<String> tags = new ArrayList<>();
        for (int i = 0; i <= position; i++) {
            tags.add("tag"+i);
        }
        List<Answer>answers = new ArrayList<>();
        answers.add(answer);
        FlashCard flashCard = new FlashCard(new Date(), question, answers, author,false);

        return flashCard;
    }

    /**
     * A fragment representing a list of Items.
     * <p/>
     * Activities containing this fragment MUST implement the {@link OnFlashcardListFragmentInteractionListener}
     * interface.
     */
    public static class ItemFragmentFlashcard extends Fragment {

        // TODO: Customize parameter argument names
        private static final String ARG_COLUMN_COUNT = "column-count";
        public static final String ARG_PARENT_ID = "parentId";
        // TODO: Customize parameters
        private int mColumnCount = 1;
        private OnFlashcardListFragmentInteractionListener mListener;

        private long parentId = -1;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public ItemFragmentFlashcard() {
        }


        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static ItemFragmentFlashcard newInstance(int columnCount, long parentId) {
            ItemFragmentFlashcard fragment = new ItemFragmentFlashcard();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            args.putLong(ARG_PARENT_ID, parentId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null) {
                mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
                parentId = getArguments().getLong(ARG_PARENT_ID);
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

                //recyclerView.setAdapter(new FlashcardRecyclerViewAdapter(DummyContentCard.flashCards, mListener));

                recyclerView.setAdapter(new FlashcardRecyclerViewAdapter(flashCards, mListener));
            }
            return view;
        }


        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnFlashcardListFragmentInteractionListener) {
                mListener = (OnFlashcardListFragmentInteractionListener) context;
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
        public interface OnFlashcardListFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFlashcardListFragmentInteraction(FlashCard item);
        }
    }


}
