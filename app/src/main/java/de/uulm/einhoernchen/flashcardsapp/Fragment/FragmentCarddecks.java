package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCarddeck;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteUserGroupAndDeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeckLongClick;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * A fragment representing a list of Carddeck Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListenerCarddeck}
 * interface.
 */
public class FragmentCarddecks extends Fragment implements View.OnClickListener, OnFragmentInteractionListenerCarddeckLongClick {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_PARENT_ID = "parentId";
    private int mColumnCount = 1;
    private OnFragmentInteractionListenerCarddeck mListener;
    private List<CardDeck> itemList;
    private boolean isUpToDate;
    private View viewFragment;
    private long parentId;
    private RecyclerView.Adapter carddeckAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentCarddecks() {

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

        Globals.getFloatingActionButtonAdd().setVisibility(View.VISIBLE);
        Globals.getFloatingActionButtonAdd().setOnClickListener(this);

        // Set the adapter
        if (viewFragment instanceof RecyclerView) {
            Context context = viewFragment.getContext();
            RecyclerView recyclerView = (RecyclerView) viewFragment;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            this.carddeckAdapter = new RecyclerViewAdapterCarddecks(itemList, mListener, this, isUpToDate);
            recyclerView.setAdapter(this.carddeckAdapter);
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

        mListener = null;
    }

    public void setItemList(List<CardDeck> itemList) {
        this.itemList = itemList;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }


    /**
     * Implements the onClickUser method
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

                if (ProcessConnectivity.isOk(this.getContext())) {

                    createDialog();
                } else {

                    Snackbar.make(this.getView(), Globals.getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * Long Click on CardDeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param item
     */
    @Override
    public void onCarddeckListFragmentInteractionLongClick(CardDeck item) {

        if (ProcessConnectivity.isOk(this.getContext())) {

            createDialog(item);
        } else {

            Snackbar.make(this.getView(), Globals.getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_SHORT).show();
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
        final EditText description = (EditText) view.findViewById(R.id.carddeck_description);
        final EditText groupName = (EditText) view.findViewById(R.id.carddeck_group_name);
        final EditText groupDescription = (EditText) view.findViewById(R.id.carddeck_group_description);
        final Spinner groupSpinner = (Spinner) view.findViewById(R.id.carddeck_group_name_spinner);
        groupSpinner.setVisibility(View.GONE);
        final CheckBox visible = (CheckBox) view.findViewById(R.id.checkbox_carddeck_visible);

        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    groupName.setText(text.getText().toString() + "_" +  getContext().getString(R.string.group));
                }
            }
        });

        final View v = viewFragment;

        text.requestFocus();

        builder.setView(view)
            // Add action buttons
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    String textString = text.getText().toString();
                    String descriptionString = description.getText().toString();
                    boolean isVisible = visible.isChecked();

                    if (textString == null || textString.equals("")) {

                        Snackbar.make(v, R.string.insert_text, Snackbar.LENGTH_SHORT).show();

                    } else if (!ProcessConnectivity.isOk(getContext(), true)){
                        // Do nothing
                    } else {

                        JSONObject jsonObjectDeck = new JSONObject();
                        JSONObject jsonObjectGroup = new JSONObject();
                        JSONArray groupUsers = new JSONArray();
                        JSONObject jsonObjectUser = new JSONObject();

                        try {

                            jsonObjectDeck.put(JsonKeys.CARDDECK_NAME, textString);
                            jsonObjectDeck.put(JsonKeys.CARDDECK_DESCRIPTION, descriptionString);
                            jsonObjectDeck.put(JsonKeys.CARDDECK_VISIBLE, isVisible);

                            jsonObjectGroup.put(JsonKeys.GROUP_NAME, groupName.getText().toString());
                            jsonObjectGroup.put(JsonKeys.GROUP_DESCRIPTION, groupDescription.getText().toString());

                            jsonObjectUser.put(JsonKeys.USER_ID, Globals.getDb().getLoggedInUser().getId());
                            groupUsers.put(jsonObjectUser);
                            jsonObjectGroup.put(JsonKeys.GROUP_USERS, groupUsers);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AsyncPostRemoteUserGroupAndDeck tasksCombined = new AsyncPostRemoteUserGroupAndDeck(jsonObjectGroup, jsonObjectDeck);

                        if (ProcessConnectivity.isOk(Globals.getContext())) {

                            tasksCombined.execute(parentId);
                        }
                    }
                }
            })
            .setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }


    /**
     * Creates a custom dialog for creating an new carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-08
     *
     */
    private void createDialog(final CardDeck deck) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_edittext_carddeck, null);

        final EditText text = (EditText) view.findViewById(R.id.carddeck_name);
        text.setText(deck.getName());

        final EditText description = (EditText) view.findViewById(R.id.carddeck_description);
        description.setText(deck.getDescription());

        final EditText groupName = (EditText) view.findViewById(R.id.carddeck_group_name);
        groupName.setText(deck.getUserGroup().getName());
        groupName.setEnabled(false);

        final EditText groupDescription = (EditText) view.findViewById(R.id.carddeck_group_description);
        groupDescription.setText(deck.getUserGroup().getDescription());
        groupDescription.setEnabled(false);

        final Spinner groupSpinner = (Spinner) view.findViewById(R.id.carddeck_group_name_spinner);
        groupSpinner.setVisibility(View.GONE);

        final CheckBox visible = (CheckBox) view.findViewById(R.id.checkbox_carddeck_visible);

        final View v = viewFragment;

        text.requestFocus();

        final RecyclerView.Adapter adapter = this.carddeckAdapter;

        builder.setView(view)
            // Add action buttons
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    String textString = text.getText().toString();
                    String descriptionString = description.getText().toString();
                    boolean isVisible = visible.isChecked();

                    if (textString == null || textString.equals("")) {

                        Snackbar.make(v, R.string.insert_text, Snackbar.LENGTH_SHORT).show();

                    } else if (!ProcessConnectivity.isOk(getContext(), true)){
                        // Do nothing
                    } else {

                        JSONObject jsonObjectDeck = new JSONObject();

                        try {

                            jsonObjectDeck.put(JsonKeys.CARDDECK_NAME, textString);
                            jsonObjectDeck.put(JsonKeys.CARDDECK_DESCRIPTION, descriptionString);
                            jsonObjectDeck.put(JsonKeys.CARDDECK_VISIBLE, isVisible);
                            // jsonObjectDeck.put(JsonKeys.CARDDECK_GROUP, deck.getUserGroup().getId());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AsyncPatchRemoteCarddeck task = new AsyncPatchRemoteCarddeck(jsonObjectDeck, false);
                        task.setCallingView(v);

                        if (ProcessConnectivity.isOk(Globals.getContext())) {

                            task.execute(deck.getId());
                        } else {

                            Snackbar.make(v, R.string.service_unavailable, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            })
            .setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }
}