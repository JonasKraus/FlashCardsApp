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

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentMessages;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentPlayFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerMessage;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

public class MessagesActivity extends AppCompatActivity implements OnFragmentInteractionListenerMessage, OnFragmentInteractionListenerAnswer {

    private DbManager db;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ProcessConnectivity.isOk(getApplicationContext())) {

                    startActivity(new Intent(MessagesActivity.this, CarddecksActivity.class));
                } else {

                    Snackbar.make(
                            findViewById(R.id.content_messages),
                            getApplicationContext().getString(R.string.service_unavailable),
                            Snackbar.LENGTH_LONG)
                            .show();
                }


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
    private void inflateFragmentPlay(Message message) {

        /*
        FragmentPlayTabs fragment = new FragmentPlayTabs();
        */

        FragmentPlayFlashCards fragment = new FragmentPlayFlashCards();

        fragment.setMessage(message);
        fragment.setFab(this.fab);

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        // Keep attention that this is replaced and not added
        fragmentTransaction.replace(R.id.fragment_container_messages, fragment);
        fragmentTransaction.commit();




    }


    @Override
    public void onMessageListFragmentInteraction(Message item) {

        if (item.getMessageType().equals(Message.MessageType.DECK_CHALLENGE_MESSAGE)) {

            inflateFragmentPlay(item);
        }


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
