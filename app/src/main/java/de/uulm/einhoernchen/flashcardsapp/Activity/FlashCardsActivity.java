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

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Local.AsyncGetLocalFlashCardsByIds;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardsByIds;
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
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

public class FlashCardsActivity extends AppCompatActivity
        implements OnFragmentInteractionListenerFlashcard,
        FragmentFlashCard.OnFlashCardFragmentInteractionListener,
        OnFragmentInteractionListenerAnswer {

    public static final String CARD_IDS = "card_ids";
    public static final String ACTIVITY_TITLE = "activity_title";
    private DbManager db;
    private List<Long> cardIds;
    private String activityTitle = "Flashcards";
    private FragmentGlobalFlashCards fragment;

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

        // Check if an activity title is passed
        if (getIntent().hasExtra(ACTIVITY_TITLE)) {

            activityTitle = getIntent().getStringExtra(ACTIVITY_TITLE);
            setTitle(activityTitle);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals.setFragmentManager(getSupportFragmentManager());

        fragment = new FragmentGlobalFlashCards();
        collectItemsFromDb();
        collectItemsFromServer();
    }


    /**
     * Collects all flashcards from server as async task
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     */
    private void collectItemsFromDb() {
        // Getting cards from db
        AsyncGetLocalFlashCardsByIds asyncGetLocalFlashCards =
                new AsyncGetLocalFlashCardsByIds(cardIds,
                        new AsyncGetLocalFlashCardsByIds.AsyncResponse() {

                            @Override
                            public void processFinish(List<FlashCard> flashCards) {

                                fragment.setItemList(flashCards);
                                fragment.setUpToDate(true);

                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        Globals.getFragmentManager().beginTransaction();

                                fragmentTransaction.replace(R.id.fragment_container_global_flashcards, fragment);
                                fragmentTransaction.commit();
                            }
                        });

        asyncGetLocalFlashCards.execute();

    }


    /**
     * Collects all flashcards from server as async task
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-14
     */
    private void collectItemsFromServer() {
        // Getting cards from server
        AsyncGetRemoteFlashCardsByIds asyncGetRemoteFlashCards =
                new AsyncGetRemoteFlashCardsByIds(cardIds,
                        new AsyncGetRemoteFlashCardsByIds.AsyncResponseFlashCards() {

                            @Override
                            public void processFinish(List<FlashCard> flashCards) {

                                fragment.setItemList(flashCards);
                                fragment.setUpToDate(true);

                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        Globals.getFragmentManager().beginTransaction();

                                fragmentTransaction.replace(R.id.fragment_container_global_flashcards, fragment);
                                fragmentTransaction.commit();
                            }
                        });

        if (ProcessConnectivity.isOk(this)) {
            // Execute Async task
            asyncGetRemoteFlashCards.execute();
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
