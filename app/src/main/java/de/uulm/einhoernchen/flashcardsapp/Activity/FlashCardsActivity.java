package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentGlobalFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class FlashCardsActivity extends AppCompatActivity
        implements OnFragmentInteractionListenerFlashcard,
        FragmentFlashCard.OnFlashCardFragmentInteractionListener,
        OnFragmentInteractionListenerAnswer {

    public static final String CARD_IDS = "card_ids";
    private DbManager db;
    private List<Long> cardIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Check if extras are given
        if (getIntent().hasExtra(CARD_IDS)) {

            cardIds = (ArrayList<Long>) getIntent().getSerializableExtra(CARD_IDS);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals.setFragmentManager(getSupportFragmentManager());

        // Getting cards from server
        AsyncGetRemoteFlashCards asyncGetRemoteFlashCards =
                new AsyncGetRemoteFlashCards(cardIds,
                        new AsyncGetRemoteFlashCards.AsyncResponseFlashCards() {

            @Override
            public void processFinish(List<FlashCard> flashCards) {

                FragmentGlobalFlashCards fragment = new FragmentGlobalFlashCards();
                fragment.setItemList(flashCards);
                fragment.setUpToDate(true);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        Globals.getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container_global_flashcards, fragment);
                fragmentTransaction.commit();
            }
        });

        // Execute Async task
        asyncGetRemoteFlashCards.execute();
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


    /**
     * Click on an card item
     * Opens card in details view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-12
     *
     * @param item
     */
    @Override
    public void onFlashcardListFragmentInteraction(FlashCard item) {

        ContentFlashCard contentFlashCard = new ContentFlashCard();
        contentFlashCard.setFragmentContainder(R.id.fragment_container_global_flashcards);
        contentFlashCard.collectItemFromDb(item.getId(), false);
        contentFlashCard.collectItemFromServer(item.getId(), false);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAnswerListFragmentInteraction(Answer item) {
        Log.d("Click", item.toString());
    }
}
