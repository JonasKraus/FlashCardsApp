package de.uulm.einhoernchen.flashcardsapp.Activity.Explore;

import android.net.Uri;
import android.os.Bundle;
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
    private RecyclerView recyclerView;
    private FragmentHashtags fragmentHashtags;
    private ArrayList<Tag> itemList;
    //private RecyclerViewAdapterHashtags recyclerViewAdapterHashtags;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

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
        adapter.addFragment(fragmentHashtags, getResources().getString(R.string.toolbar_title_hashtag));


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

        AsyncGetRemoteHashtags asyncGetRemoteHashtags =
                new AsyncGetRemoteHashtags(new AsyncGetRemoteHashtags.AsyncResponseHashtags() {
            @Override
            public void processFinish(List<Tag> tags) {

                fragmentHashtags.updateItemList(tags);
            }
        });

        asyncGetRemoteHashtags.setStartAndLimit(0, 20);
        asyncGetRemoteHashtags.execute(query);

        return false;
    }


    @Override
    public void onHashtagFragmentInteraction(Tag item) {

        // TODO to be implemented
        Log.d("click", item.toString());
    }
}
