package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteUserGroup;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUser;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

public class UserGroupDetailsActivity extends AppCompatActivity  implements OnFragmentInteractionListenerUser {

    private EditText editTextName;
    private EditText editTextDescription;
    private ArrayList<User> users;
    private Long groupId = null;
    private UserGroup selectedUserGroup;
    private DbManager db = Globals.getDb();
    private View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_group_details);

        contentView = (View) this.findViewById(R.id.contnet_view_user_group_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextName = (EditText) findViewById(R.id.edittext_usergroup_name);
        editTextDescription = (EditText) findViewById(R.id.edittext_usergroup_description);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProcessConnectivity.isOk(getApplicationContext())) {

                    createGroup();
                } else {

                    Snackbar.make(view, getResources().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals.setFragmentManager(getSupportFragmentManager());

        Bundle bundle = getIntent().getExtras();
        users = bundle.getParcelableArrayList("data");

        groupId = bundle.containsKey("group_id") ? bundle.getLong("group_id") : null;

        // Check if a group was selected and must be edited
        if (groupId != null) {

            selectedUserGroup = db.getUserGroup(groupId);
            editTextName.setText(selectedUserGroup.getName());
            editTextDescription.setText(selectedUserGroup.getDescription());
        }

        setUsersListFragment(users);

    }


    /**
     * Creates the json object for the async task wich starts a post to the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-19
     */
    private void createGroup() {

        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        JSONObject jsonObjectGroup = new JSONObject();

        JSONArray jsonArrayUsers = new JSONArray();

        try {
            jsonObjectGroup.put(JsonKeys.GROUP_NAME, name);
            jsonObjectGroup.put(JsonKeys.GROUP_DESCRIPTION, description);

            for(User user : users) {

                JSONObject jsonObjectUser = new JSONObject();
                jsonObjectUser.put(JsonKeys.USER_ID, user.getId());

                jsonArrayUsers.put(jsonObjectUser);
            }
            jsonObjectGroup.put(JsonKeys.GROUP_USERS, jsonArrayUsers);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (groupId == null) {

            AsyncPostRemoteUserGroup task = new AsyncPostRemoteUserGroup(jsonObjectGroup);

            if (ProcessConnectivity.isOk(this)) {

                task.execute(groupId);
            } else {

                Snackbar.make(contentView, getResources().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();

                return;
            }
        } else {

            // Update group
            AsyncPatchRemoteUserGroup task = new AsyncPatchRemoteUserGroup(jsonObjectGroup);

            if (ProcessConnectivity.isOk(this)) {

                task.execute(groupId);
            } else {

                Snackbar.make(contentView, getResources().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();

                return;
            }
        }


    }


    /**
     * Sets the selected users in the thistview
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @param users
     */
    private void setUsersListFragment(List<User> users) {
        FragmentUsers fragment = new FragmentUsers();
        fragment.setItemList(users);
        fragment.setUpToDate(false);

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                Globals.getFragmentManager().beginTransaction();

        // TODO delete if always loaded from local db
        /*
        if (backPressed) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else if (!fragmentAnimated) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentAnimated = true;
        }
        */

        fragmentTransaction.replace(R.id.fragment_container_users, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onUserListFragmentInteraction(RecyclerViewAdapterUsers.ViewHolder item) {

    }
}
