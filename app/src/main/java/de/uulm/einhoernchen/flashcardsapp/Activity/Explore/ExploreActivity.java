package de.uulm.einhoernchen.flashcardsapp.Activity.Explore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.FlashCardsActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteFlashCardsByHashtag;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteHashtags;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.MyFragmentPagerAdapter;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHashtags;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHome;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerHashtag;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
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
    private LinearLayout llSelectedItems;
    private ArrayList<Tag> selectedTags = new ArrayList<>();
    private ArrayList<Long> tagIds = new ArrayList<>();

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

        llSelectedItems = (LinearLayout) findViewById(R.id.ll_selected_items);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTags.size() > 0) {


                    AsyncGetRemoteFlashCardsByHashtag asyncGetRemoteFlashCardsByHashtag = new AsyncGetRemoteFlashCardsByHashtag(selectedTags, new ArrayList<String>(), new AsyncGetRemoteFlashCardsByHashtag.AsyncResponseHashtagFlashCards() {
                        @Override
                        public void processFinish(List<FlashCard> flashCards) {

                            // TODO serach cards by tags
                            //startActivity(new Intent(ExploreActivity.this, FlashCardsActivity.class));

                            Intent cardIntent = new Intent(new Intent(ExploreActivity.this, FlashCardsActivity.class));

                            ArrayList<Long> cardIds = new ArrayList<Long>();

                            for (FlashCard card : flashCards) {

                                cardIds.add(card.getId());
                            }

                            cardIntent.putExtra(FlashCardsActivity.CARD_IDS,  cardIds);

                            startActivity(cardIntent);

                        }
                    });
                    asyncGetRemoteFlashCardsByHashtag.execute();
                }
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

        // Check if tag is already selected
        if (!this.selectedTags.contains(item)) {

            this.selectedTags.add(item);
            createTagViewWithListener(item);
        }

    }



    /**
     * Creates textview for tag and adds an listener
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-04
     */
    private void createTagViewWithListener(final Tag tag) {

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setMinimumWidth(AppBarLayout.LayoutParams.WRAP_CONTENT);
        ll.setMinimumHeight(AppBarLayout.LayoutParams.WRAP_CONTENT);

        AppBarLayout.LayoutParams lparams = new AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
        lparams.rightMargin = 10;
        lparams.topMargin = 10;

        TextView tv = new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setTag(tag.getId());
        tv.setPadding(5,5,5,5);
        tv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tv.setText("#" + tag.getName());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        tv.setTextColor(getResources().getColor(R.color.white));

        Button button =  new Button(this);
        button.setTag(tag.getId());
        button.setText("x");
        button.setVisibility(View.GONE);
        button.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        ll.addView(tv);
        ll.addView(button);

        llSelectedItems.addView(ll);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llSelectedItems.removeView(v);

                selectedTags.remove(tag);
            }
        });

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
