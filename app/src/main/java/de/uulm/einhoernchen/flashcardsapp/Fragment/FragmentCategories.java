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

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterCategories;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCategory;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * A fragment representing a list of Category Items.
 * <p/>
 * interface.
 */
public class FragmentCategories extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_PARENT_ID = "parentId";
    private int mColumnCount = 1;
    private OnFragmentInteractionListenerCategory mListener;
    private List<Category> itemList;
    private boolean isUpToDate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentCategories() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentCategories newInstance(int columnCount) {

        FragmentCategories fragment = new FragmentCategories();

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

            recyclerView.setAdapter(new RecyclerViewAdapterCategories(itemList, mListener, isUpToDate));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerCategory) {
            mListener = (OnFragmentInteractionListenerCategory) context;
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

    public void setItemList(List<Category> itemList) {
        this.itemList = itemList;
    }


    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }
}