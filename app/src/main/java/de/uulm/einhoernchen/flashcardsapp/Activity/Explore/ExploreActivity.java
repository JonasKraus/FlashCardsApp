package de.uulm.einhoernchen.flashcardsapp.Activity.Explore;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteHashtags;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.MyFragmentPagerAdapter;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHashtagFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHashtags;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHome;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentStatistics;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerHashtag;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.R;

public class ExploreActivity extends AppCompatActivity
        implements FragmentHome.OnFragmentInteractionListener,
        SearchView.OnQueryTextListener,
        OnFragmentInteractionListenerHashtag {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FragmentHashtags fragmentHashtags;
    private int currentStart = 0;
    private int limit = 10;
    private boolean appendChunk = false;
    private String currentTagQuery = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        //adapter.addFragment(new FragmentHome(), getResources().getString(R.string.tab_play));

        fragmentHashtags = new FragmentHashtags();
        // set the scroll listener to check when list end is reached
        fragmentHashtags.setOnScrollListener(createOnScrollListener());

        adapter.addFragment(fragmentHashtags, getResources().getString(R.string.toolbar_title_hashtag));

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_explore, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        // Reset all vars
        currentTagQuery = query;

        appendChunk = false;

        currentStart = 0;

        updateTags(query);

        return false;
    }


    /**
     * Collect new tags from server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-10
     *
     * @param query
     */
    private void updateTags(String query) {

        progressBar = (ProgressBar) findViewById(R.id.progress_small_list_end);
        progressBar.setVisibility(View.VISIBLE);

        AsyncGetRemoteHashtags asyncGetRemoteHashtags =
                new AsyncGetRemoteHashtags(new AsyncGetRemoteHashtags.AsyncResponseHashtags() {
                    @Override
                    public void processFinish(List<Tag> tags) {

                        if (tags.size() > 0 || !appendChunk) {

                            fragmentHashtags.updateItemList(tags, appendChunk);

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

        asyncGetRemoteHashtags.setStartAndLimit(currentStart, limit);
        asyncGetRemoteHashtags.execute(query);
    }


    /**
     * handles the click event on an hashtag list item
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-10
     *
     * @param item
     */
    @Override
    public void onHashtagFragmentInteraction(Tag item) {

        // TODO to be implemented
        Log.d("click", item.toString());
    }


    /**
     * Creates the scroll listener for the recyclerview
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-10
     *
     * @return
     */
    public RecyclerView.OnScrollListener createOnScrollListener() {


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

            int pastVisiblesItems, visibleItemCount, totalItemCount;
            boolean loading = true;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                switch(recyclerView.getId()) {
                    case R.id.list_hashtags:

                        if (dy > 0) { //check for scroll down

                            recyclerView.setHasFixedSize(true);

                            visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                            totalItemCount = recyclerView.getLayoutManager().getItemCount();
                            pastVisiblesItems = ((android.support.v7.widget.LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                            // Load data
                            if (((pastVisiblesItems + visibleItemCount) >= totalItemCount) && !progressBar.isShown()) {

                                currentStart += limit;
                                appendChunk = true;
                                updateTags(currentTagQuery);
                            }
                        }
                        break;
                }
            }
        };

        return onScrollListener;
    }

}
