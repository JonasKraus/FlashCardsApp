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

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentChallengesRanking;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerChallengesRanking;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class ChallengesRankingActivity extends AppCompatActivity implements OnFragmentInteractionListenerChallengesRanking {

    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges_ranking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ChallengesRankingActivity.this, UsersActivity.class));
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Globals.setFragmentManager(getSupportFragmentManager());
        ContentChallengesRanking contentChallengesRanking = new ContentChallengesRanking();
        contentChallengesRanking.collectItemsFromDb(false);
        contentChallengesRanking.collectItemsFromServer(false);

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
    public void onChallengesRankingListFragmentInteraction(Challenge item) {

        Log.d("click", item.toString());
    }
}
