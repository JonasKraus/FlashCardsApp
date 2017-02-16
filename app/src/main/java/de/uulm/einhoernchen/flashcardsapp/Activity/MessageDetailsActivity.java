package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteMessage;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUser;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

public class MessageDetailsActivity extends AppCompatActivity  implements OnFragmentInteractionListenerUser {

    private TextView textViewTargetDeck;
    private EditText editTextContent;
    private ArrayList<User> users;
    private DbManager db = Globals.getDb();
    private long deckId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messages_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewTargetDeck = (TextView) findViewById(R.id.textview_message_target_deck);
        editTextContent = (EditText) findViewById(R.id.edittext_usergroup_description);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createMessage();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals.setFragmentManager(getSupportFragmentManager());

        Bundle bundle = getIntent().getExtras();
        users = bundle.getParcelableArrayList("data");
        deckId = bundle.getLong("deckId");

        Log.d("hier", "deckId" + deckId);
        String deckname = db.getCardDeckName(deckId);
        Log.d("deckname", deckname);
        textViewTargetDeck.setText(deckname);

        setUsersListFragment(users);

    }


    /**
     * Creates the json object for the async task wich starts a post to the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-19
     */
    private void createMessage() {

        String content = editTextContent.getText().toString();

        List<JSONObject> messages = new ArrayList<>();

        try {

            for (int i = 0; i < users.size(); i++) {

                JSONObject jsonObjectMessage = new JSONObject();

                JSONObject jsonObjectUser = new JSONObject();
                jsonObjectUser.put(JsonKeys.USER_ID, users.get(i).getId());
                jsonObjectMessage.put(JsonKeys.MESSAGE_RECIPIENT, jsonObjectUser);


                jsonObjectMessage.put(JsonKeys.MESSAGE_CONTENT, content);
                JSONObject jsonObjecttargetDeck = new JSONObject();
                jsonObjecttargetDeck.put(JsonKeys.CARDDECK_ID, deckId);
                jsonObjectMessage.put(JsonKeys.MESSAGE_TARGET_DECK, jsonObjecttargetDeck);

                messages.add(jsonObjectMessage);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Call for every user
        for (int i = 0; i < messages.size(); i++) {

            boolean refreshGUI = i == (messages.size()-1);

            AsyncPostRemoteMessage task = new AsyncPostRemoteMessage(messages.get(i), refreshGUI);

            if (ProcessConnectivity.isOk(this)) {

                task.execute();
            } else {

                // TODO show some error message
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
