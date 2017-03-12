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
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterHashtags;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerHashtag;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A fragment representing a list of Category Items.
 * <p/>
 * interface.
 */
public class FragmentHashtags extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnFragmentInteractionListenerHashtag mListener;
    private List<Tag> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapterHashtags recyclerViewAdapterHashtags;
    private android.support.v7.widget.RecyclerView.OnScrollListener scrollListener;
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHashtags() {

    }

    public void setOnScrollListener (android.support.v7.widget.RecyclerView.OnScrollListener onScrollListener1) {

        this.scrollListener = onScrollListener1;
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentHashtags newInstance(int columnCount) {

        FragmentHashtags fragment = new FragmentHashtags();

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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_hashtags, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new android.support.v7.widget.GridLayoutManager(context, mColumnCount));
            }

            recyclerViewAdapterHashtags =
                    new RecyclerViewAdapterHashtags(itemList, mListener);

            recyclerView.addOnScrollListener(scrollListener);

            recyclerView.setAdapter(recyclerViewAdapterHashtags);
        }
        return view;
    }


    /**
     * Updates the adapters list and notifies it
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-10
     *@param newItemList
     * @param appendChunk
     */
    public void updateItemList(List<Tag> newItemList, boolean appendChunk) {

        int listSize = this.itemList.size();

        if (!appendChunk) {

            itemList.clear();
            recyclerViewAdapterHashtags.notifyItemRangeRemoved(0, listSize);

            itemList.addAll(newItemList);
            recyclerViewAdapterHashtags.notifyItemRangeInserted(0, newItemList.size());
            recyclerViewAdapterHashtags.notifyDataSetChanged();
        } else {

            itemList.addAll(newItemList);

            recyclerViewAdapterHashtags.notifyItemRangeInserted(listSize, itemList.size());
            recyclerViewAdapterHashtags.notifyDataSetChanged();

            recyclerView.scrollToPosition(listSize);
        }


        recyclerView.setAdapter(recyclerViewAdapterHashtags);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerHashtag) {
            mListener = (OnFragmentInteractionListenerHashtag) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerHashtag");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}