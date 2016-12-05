package de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset;

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

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetRemoteFlashCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncGetLocalFlashCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.AsyncSaveLocalFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
public class ContentCard {

    private static List<FlashCard> flashCards = new ArrayList<FlashCard>();

    public static ContentCard.ItemFragmentFlashcard fragment;

    private static boolean isUpToDate = false;


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.25
     *
     * @param parentId
     * @param fragmentManager
     * @param progressBarMain
     */
    public static void collectItemsFromServer(final long parentId, final FragmentManager fragmentManager, ProgressBar progressBarMain, final boolean backPressed, final DbManager db) {

        AsyncGetRemoteFlashCard asyncGetFlashCard = new AsyncGetRemoteFlashCard(parentId, new AsyncGetRemoteFlashCard.AsyncResponseFlashCard() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                // real dummy content generation
                if (flashCards == null || flashCards.size() == 0) {

                    //Log.d("ContentCard", "no flashcards");
                }

                AsyncSaveLocalFlashCard asyncSaveFlashCardLocal = new AsyncSaveLocalFlashCard(parentId);
                asyncSaveFlashCardLocal.setDbManager(db);
                asyncSaveFlashCardLocal.setFlashCards(flashCards);
                asyncSaveFlashCardLocal.execute();

                ContentCard.flashCards = flashCards;
                ContentCard.ItemFragmentFlashcard fragment = new ContentCard.ItemFragmentFlashcard();

                Bundle args = new Bundle();
                args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, parentId);
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

                isUpToDate = true;

                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                fragmentTransaction.commit();

                //Log.d("async load", "online");
            }

        });

        asyncGetFlashCard.setProgressbar(progressBarMain);
        asyncGetFlashCard.execute();

    }


    /**
     * Get data fro local sqlite db
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

        AsyncGetLocalFlashCard asyncGetFlashCardLocal = new AsyncGetLocalFlashCard(parentId, new AsyncGetLocalFlashCard.AsyncResponseFlashCardLocal() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                ContentCard.flashCards = flashCards;

                ContentCard.flashCards = flashCards;
                ContentCard.ItemFragmentFlashcard fragment = new ContentCard.ItemFragmentFlashcard();

                Bundle args = new Bundle();
                args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, parentId);
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

        asyncGetFlashCardLocal.setProgressbar(progressBar);
        asyncGetFlashCardLocal.setDbManager(db);
        asyncGetFlashCardLocal.execute();

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

                // Set the view with the data
                recyclerView.setAdapter(new RecyclerViewAdapterFlashcard(flashCards, mListener, isUpToDate));
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
