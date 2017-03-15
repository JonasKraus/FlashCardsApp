package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardsByIds;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentAllCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class CarddecksActivity extends AppCompatActivity implements OnFragmentInteractionListenerCarddeck {

    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carddecks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CarddecksActivity.this, UsersActivity.class);
                intent.putExtra("create_message", true);

                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Globals.setFragmentManager(getSupportFragmentManager());
        ContentAllCarddecks contentCarddecks = new ContentAllCarddecks();
        contentCarddecks.setCreateMessage(true);
        contentCarddecks.collectItemsFromDb(false);
        contentCarddecks.collectItemsFromServer(false);

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
    public void onCarddeckListFragmentInteraction(CardDeck item) {

        Intent intent = new Intent(CarddecksActivity.this, UsersActivity.class);

        AsyncGetRemoteFlashCardsByIds asyncGetRemoteFlashCards = new AsyncGetRemoteFlashCardsByIds(item.getId(), null);
        asyncGetRemoteFlashCards.execute();

        intent.putExtra("create_message", true);
        intent.putExtra("deckId", item.getId());

        startActivity(intent);
    }
}
