package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterGlobalFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class FragmentGlobalFlashCards extends Fragment
        implements SearchView.OnQueryTextListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_TAGS = "parentId";

    private int mColumnCount = 1;
    private OnFragmentInteractionListenerFlashcard mListener;

    private List<Tag> tags = new ArrayList<>();
    private List<FlashCard> itemList;
    private DbManager db = Globals.getDb();
    private boolean isUpToDate;
    private ProgressBar progressBar = Globals.getProgressBar();
    private RecyclerViewAdapterGlobalFlashcards recyclerViewGlobalFlashCards;
    private String filterForRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentGlobalFlashCards() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentGlobalFlashCards newInstance(int columnCount, long parentId) {

        FragmentGlobalFlashCards fragment = new FragmentGlobalFlashCards();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putLong(ARG_TAGS, parentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            // TODO check if needed tags = getArguments().getParcelableArrayList(ARG_TAGS);
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

            mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(mRecyclerView.getContext()));
        } else {
            mRecyclerView.setLayoutManager(
                    new GridLayoutManager(mRecyclerView.getContext(), mColumnCount));
        }

        recyclerViewGlobalFlashCards = new RecyclerViewAdapterGlobalFlashcards(db,
                itemList, mListener, isUpToDate, mRecyclerView.getContext(), progressBar,
                getFragmentManager(), filterForRecyclerView);
        // Set the view with the data
        mRecyclerView.setAdapter(recyclerViewGlobalFlashCards);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerFlashcard) {
            mListener = (OnFragmentInteractionListenerFlashcard) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerFlashcard");
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


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        recyclerViewGlobalFlashCards.getFilter().filter(query);

        return false;
    }

    public String getFilterForRecyclerView() {
        return filterForRecyclerView;
    }

    public void setFilterForRecyclerView(String filterForRecyclerView) {
        this.filterForRecyclerView = filterForRecyclerView;
    }
}

