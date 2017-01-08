package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A fragment representing a list of Carddeck Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListenerCarddeck}
 * interface.
 */
public class FragmentCarddecks extends Fragment implements View.OnClickListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_PARENT_ID = "parentId";
    private int mColumnCount = 1;
    private OnFragmentInteractionListenerCarddeck mListener;
    private List<CardDeck> itemList;
    private boolean isUpToDate;
    private View viewFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentCarddecks() {

        Globals.getFloatingActionButtonAdd().setVisibility(View.VISIBLE);
        Globals.getFloatingActionButtonAdd().setOnClickListener(this);
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentCarddecks newInstance(int columnCount) {

        FragmentCarddecks fragment = new FragmentCarddecks();

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
        viewFragment = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (viewFragment instanceof RecyclerView) {
            Context context = viewFragment.getContext();
            RecyclerView recyclerView = (RecyclerView) viewFragment;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new RecyclerViewAdapterCarddecks(Globals.getDb(), itemList, mListener, isUpToDate));
        }
        return viewFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerCarddeck) {
            mListener = (OnFragmentInteractionListenerCarddeck) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // reset action button
        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);
        Globals.getFloatingActionButtonAdd().setOnClickListener(null);
        mListener = null;
    }

    public void setItemList(List<CardDeck> itemList) {
        this.itemList = itemList;
    }

    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }


    /**
     * Implements the onClick method
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_add:

                createDialog();
                //TODO jonas implement
                Snackbar.make(v, " TODO add Carddeck", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                break;
        }
    }


    /**
     * Creates a custom dialog for creating an new carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-08
     *
     */
    private void createDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_edittext_carddeck, null);

        final EditText text = (EditText) view.findViewById(R.id.carddeck_name);

        final View v = viewFragment;

        text.requestFocus();

        builder.setView(view)

                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String textString = text.getText().toString();

                        if (textString == null || textString.equals("")) {

                            Snackbar.make(v, R.string.insert_text, Snackbar.LENGTH_SHORT).show();

                        } else {

                            // TODO save
                        }

                        Log.d("dialog " + id, text.getText().toString());

                    }
                });

        builder.create().show();
    }
}