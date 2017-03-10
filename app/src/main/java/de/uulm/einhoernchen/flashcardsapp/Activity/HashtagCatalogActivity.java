package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentHashtagCatalog;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

public class HashtagCatalogActivity extends AppCompatActivity
        implements OnFragmentInteractionListenerFlashcard,
        FragmentFlashCard.OnFlashCardFragmentInteractionListener,
        OnFragmentInteractionListenerAnswer {

    private DbManager db;
    private List<Tag> tags = new ArrayList<Tag>();
    private List<String> tagNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_hashtag_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals.setFragmentManager(getSupportFragmentManager());

        ContentHashtagCatalog hastagCatalog = new ContentHashtagCatalog();
        hastagCatalog.collectItemsFromDb(tags, tagNames, false);
        hastagCatalog.collectItemsFromServer(tags, tagNames, false);


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
    public void onFlashcardListFragmentInteraction(FlashCard item) {
        // TODO card details

        FragmentFlashCard fragment = new FragmentFlashCard();
        fragment.setItem(item);
        fragment.setUpToDate(false); // TODO

        Bundle args = new Bundle();
        args.putLong(FragmentFlashcards.ARG_PARENT_ID, item.getId());
        fragment.setArguments(args);

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

        fragmentTransaction.replace(R.id.fragment_container_catalog_hashtag_cards, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        Log.d("clikc", uri.toString());
    }

    @Override
    public void onAnswerListFragmentInteraction(Answer item) {
        Log.d("Click", item.toString());
    }
}
