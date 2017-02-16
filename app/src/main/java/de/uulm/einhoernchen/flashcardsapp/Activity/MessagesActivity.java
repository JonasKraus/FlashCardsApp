package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentMessages;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentPlayTabs;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerMessage;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class MessagesActivity extends AppCompatActivity implements OnFragmentInteractionListenerMessage, OnFragmentInteractionListenerAnswer {

    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO create new message
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Globals.setFragmentManager(getSupportFragmentManager());
        ContentMessages contentMessages = new ContentMessages();
        contentMessages.collectItemsFromDb(false);
        contentMessages.collectItemsFromServer(false);

    }



    /**
     * Inflates the fragment for playing selected cards
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void inflateFragmentPlay() {


        FragmentPlayTabs fragmentPlay = new FragmentPlayTabs();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        // Keep attention that this is replaced and not added
        fragmentTransaction.replace(R.id.fragment_container_messages, fragmentPlay);
        fragmentTransaction.commit();

    }


    @Override
    public void onMessageListFragmentInteraction(Message item) {

        inflateFragmentPlay();
        Log.d("click", item.toString());

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
    public void onAnswerListFragmentInteraction(Answer item) {

        Log.d("Click", item.toString());
    }
}
