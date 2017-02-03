package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.UserGroupDetailsActivity;
import de.uulm.einhoernchen.flashcardsapp.Activity.UsersActivity;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsersBinding;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserBinding;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.databinding.FragmentItemListBinding;
import de.uulm.einhoernchen.flashcardsapp.databinding.ListItemUserBinding;

/**
 * A fragment representing a list of Category Items.
 * <p/>
 * interface.
 */
public class FragmentUsersBinding extends Fragment implements SearchView.OnQueryTextListener, OnFragmentInteractionListenerUserBinding {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnFragmentInteractionListenerUserBinding mListener;
    private List<User> itemList = new ArrayList<>();
    private DbManager db = Globals.getDb();
    private boolean isUpToDate;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterUsers recyclerViewAdapterUsers;
    private List<User> usersOfGroup;
    //private ListItemUserBinding mBinding;
    private FragmentItemListBinding mBinding;
    private RecyclerViewAdapterUsersBinding mAdapter;
    private ArrayList<User> checkedUsers = new ArrayList<>();
    private FloatingActionButton fab;

    /**
     * Compare the users
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-03
     */
    private static final Comparator<User> ALPHABETICAL_COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(User a, User b) {

            return a.getName().compareTo(b.getName());
        }
    };

    private TextView textViewToolbarCheckedUsers;
    private Long groupId = null;


    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentUsersBinding() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentUsersBinding newInstance(int columnCount) {

        FragmentUsersBinding fragment = new FragmentUsersBinding();

        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupId = ((UsersActivity)getActivity()).getGroupId();

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false);

        mAdapter = new RecyclerViewAdapterUsersBinding(this.getContext(), ALPHABETICAL_COMPARATOR, isUpToDate, this, usersOfGroup);

        mBinding.list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.list.setAdapter(mAdapter);


        mAdapter.edit()
                .replaceAll(itemList)
                .commit();


        textViewToolbarCheckedUsers = (TextView) ((AppCompatActivity) getActivity()).findViewById(R.id.textView_checked_users);

        fab = (FloatingActionButton) ((AppCompatActivity) getActivity()).findViewById(R.id.fab);

        setFabListener();

        return mBinding.getRoot();
    }

    private void setFabListener() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), UserGroupDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", checkedUsers);

                if (groupId != null) {

                    intent.putExtra("group_id", groupId);
                }

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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

        mListener = (OnFragmentInteractionListenerUserBinding) this;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setItemList(List<User> itemList) {
        this.itemList = itemList;
    }

    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    public void setUsersOfGroup(List<User> usersOfGroup) {
        this.usersOfGroup = usersOfGroup;

    }


    @Override
    public boolean onQueryTextChange(String query) {
        final List<User> filteredModelList = filter(itemList, query);
        mAdapter.edit()
                .replaceAll(filteredModelList)
                .commit();
        mBinding.list.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<User> filter(List<User> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<User> filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    @Override
    public void onClickUser(View view, User user) {
        Log.d("click user", user.toString());
        // TODO

    }

    @Override
    public void onClickBinding(ListItemUserBinding binding) {
        TextDrawable drawable;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color

        if (binding.imageViewRoundIcon.getTag().equals("checked")) {

            checkedUsers.remove(binding.getModel());

            final int color = generator.getColor(binding.getModel().getId());

            drawable = TextDrawable.builder()
                    .buildRound(binding.getModel().getName().charAt(0) + "", color); // radius in px
            binding.imageViewRoundIcon.setTag("unchecked");

        } else {

            checkedUsers.add(binding.getModel());

            drawable = TextDrawable.builder()
                    .buildRound(String.valueOf("✓"), Color.GRAY); // radius in px
            binding.imageViewRoundIcon.setTag("checked");
        }

        binding.imageViewRoundIcon.setImageDrawable(drawable);

        setCheckedUsersListToolbar();
    }



    /**
     * Sets the list of checked users to the  toolbar textview
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     */
    private void setCheckedUsersListToolbar() {


        String text = "";

        for (int i = 0; i < checkedUsers.size(); i++) {

            text += checkedUsers.get(i).getName();

            if (i < checkedUsers.size() - 1) {
                text += (", ");
            }
        }

        textViewToolbarCheckedUsers.setText(text);

    }


    /**
     * Sets the list of group users to the checked list and adds them to the header list in the view
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-02
     */
    private void setUsersOfGroupToCheckedList() {

        if (groupId != null) {

            usersOfGroup = db.getUsersOfUserGroup(groupId);

            for (User user : usersOfGroup) {

                if (!checkedUsers.contains(user)) {
                    checkedUsers.add(user);
                }
            }
        }
    }

    /**
     * Adds the logged in user to the list
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-02
     */
    private void setSelfToCheckedList() {

        // Add only when no group was loaded
        // otherwise the user must be in the group
        if (groupId == null) {
            // add currently logged in user to the group
            if (!checkedUsers.contains(Globals.getDb().getLoggedInUser())) {
                checkedUsers.add(Globals.getDb().getLoggedInUser());
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        setSelfToCheckedList();
        setUsersOfGroupToCheckedList();
        // Set if users are checked because a group was selected
        setCheckedUsersListToolbar();
    }

}