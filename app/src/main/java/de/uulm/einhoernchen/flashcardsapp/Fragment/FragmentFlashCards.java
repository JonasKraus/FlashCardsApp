package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class FragmentFlashCards extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_PARENT_ID = "parentId";

    private int mColumnCount = 1;
    private OnFragmentInteractionListenerFlashcard mListener;

    private long parentId = -1;
    private List<FlashCard> itemList;
    private DbManager db = Globals.getDb();
    private boolean isUpToDate;
    private ProgressBar progressBar = Globals.getProgressBar();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentFlashCards() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentFlashCards newInstance(int columnCount, long parentId) {
        FragmentFlashCards fragment = new FragmentFlashCards();
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
        View view = inflater.inflate(R.layout.fragment_item_list_cards, container, false);

        Globals.getFloatingActionButton().setVisibility(View.VISIBLE);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_cards);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), mColumnCount));
        }

        // Set the view with the data
        mRecyclerView.setAdapter(new RecyclerViewAdapterFlashcards(
                db, itemList, mListener, isUpToDate, mRecyclerView.getContext(), progressBar,
                getFragmentManager(), parentId));


        Button button = (Button) view.findViewById(R.id.button_card_add);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerFlashcard) {
            mListener = (OnFragmentInteractionListenerFlashcard) context;
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

    public void setItemList(List<FlashCard> itemList) {
        this.itemList = itemList;
    }

    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }


}

