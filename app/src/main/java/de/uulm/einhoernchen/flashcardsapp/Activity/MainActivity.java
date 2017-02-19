package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.nearby.messages.Messages;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteToken;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCardCreate;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHome;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCategories;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentPlayTabs;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeckLongClick;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCategory;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Fragment.SimpleRTEditor;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentHome.OnFragmentInteractionListener,
        OnFragmentInteractionListenerFlashcard,
        OnFragmentInteractionListenerCategory,
        OnFragmentInteractionListenerCarddeck,
        FragmentFlashCard.OnFlashCardFragmentInteractionListener,
        OnFragmentInteractionListenerAnswer {


    private DbManager db;
    private User user;
    private Context context;
    private ImageView profileImage;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView toolbarTextViewTitle;
    private long childrenId = -1;
    private List<Long> parentIds;
    private int categoryLevel = 0;
    private Constants catalogueState = Constants.CATEGORY_LIST;
    private List<String> breadCrumbs;
    private ProgressBar progressBar;
    // the Flashcard that was loaded last in details fragment
    private FlashCard currentFlashCard;

    private static final int MY_INTENT_CLICK=302;
    private DrawerLayout drawer;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileRating;
    private View header;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton floatingActionButtonAdd;
    private CollapsingToolbarLayout collapsingToolbar;
    private FragmentPlayTabs fragmentPlay;

    @Override
    protected void onResume() {
        super.onResume();
        this.context = this;
        openDb();
        Globals.initGlobals(context, progressBar, db, getSupportFragmentManager(), floatingActionButton, floatingActionButtonAdd);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        // INIT View elements
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        profileImage = (ImageView) header.findViewById(R.id.imageViewProfilePhoto);
        profileName = (TextView) header.findViewById(R.id.textViewProfileName);
        profileEmail = (TextView) header.findViewById(R.id.textViewProfileEmail);
        profileRating = (TextView) header.findViewById(R.id.textViewProfileRating);

        toolbarTextViewTitle = (TextView ) findViewById(R.id.toolbar_text_view_title);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        createFloatingActionButton();

        initBreadCrumps();

        // INIT variables
        this.context = this;
        openDb();

        // init Globals
        Globals.initGlobals(context, progressBar, db, getSupportFragmentManager(), floatingActionButton, floatingActionButtonAdd);

        setProfileView();
        ProcessConnectivity.isServerAlive ();

        setProfileImageClickListener();

        createFragmentHome();

        createToolbar();

        requestUserToken();
    }


    /**
     * Gets the token from local storage if already existent
     * otherwise requests it frfom the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-15
     */
    private void requestUserToken() {

        String localToken = db.getToken(db.getLoggedInUser().getId());

        if (localToken == null) {

            // Get the token from the server if no token exists
            JSONObject jsonObject = new JSONObject();

            try {

                jsonObject.put(JsonKeys.USER_EMAIL, db.getLoggedInUser().getEmail());
                jsonObject.put(JsonKeys.USER_PASSWORD, db.getLoggedInUser().getPassword());

            } catch (JSONException e) {

                Log.e("ERROR", "json token");
                e.printStackTrace();
            }

            // Gets the login token
            AsyncPostRemoteToken asyncToken = new AsyncPostRemoteToken(jsonObject, db);
            asyncToken.execute();
        } else {

            Globals.setToken(localToken);
        }
    }


    /**
     * Sets the users data to the views objects
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void setProfileView() {

        user = db.getLocalAccountUser(); Log.d("user--->", user.toString());
        profileName.setText(user.getName());
        profileEmail.setText(user.getEmail());
        profileRating.setText(user.getRating()+"");
        setProfileImage();
    }


    /**
     * Adds a clicklistener to the profile image
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void setProfileImageClickListener() {

        /**
         * Adds clicklistener to the profile image
         * you can choose image of gallery or from camera
         */
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MEDIA GALLERY
                PermissionManager.verifyStoragePermissionsWrite((Activity) context);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int btn) {

                        switch (btn){

                            case DialogInterface.BUTTON_POSITIVE:

                                Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                                startActivityForResult(camIntent, 1);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select File"),MY_INTENT_CLICK);
                                break;

                            default:
                                // On canel
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.prompt_choose_profile) //TODO set in constans strings
                        .setPositiveButton(R.string.prompt_choose_camera, dialogClickListener)
                        .setNegativeButton(R.string.prompt_choose_gallery, dialogClickListener)
                        .setNeutralButton(R.string.prompt_cancel, dialogClickListener).show();
            }

        });
    }


    /**
     * Init breadcrumps and navigtion
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void initBreadCrumps() {

        breadCrumbs = new ArrayList<String>();
        breadCrumbs.add("");
        parentIds = new ArrayList<Long>();
    }


    /**
     * Creates the toolbar
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void createToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_main_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_search);
        collapsingToolbar.setTitle("My Toolbar Tittle");
        toolbar.setTitle("asdasd");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    /**
     * Creates the floating action button
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void createFloatingActionButton() {


        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        resetFabPlay();

    }


    /**
     * Sets or resets the main fab
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    public void resetFabPlay () {

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_school));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inflateFragmentPlay();
            }
        });
    }


    /**
     * Inflates the fragment for playing selected cards
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void inflateFragmentPlay() {

        catalogueState = Constants.PLAY;

        Log.d("frag", (fragmentPlay == null) + "");

        fragmentPlay = fragmentPlay == null ? new FragmentPlayTabs() : fragmentPlay;
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        // Keep attention that this is replaced and not added
        fragmentTransaction.replace(R.id.fragment_container_main, fragmentPlay);
        fragmentTransaction.commit();

    }


    /**
     * Inflates the home fragment
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void createFragmentHome() {

        FragmentHome fragment = new FragmentHome();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        // Keep attention that this is replaced and not added
        fragmentTransaction.replace(R.id.fragment_container_main, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Searches for a profile image an sets it to the view
     *
     */
    private void setProfileImage() {

        PermissionManager.verifyStoragePermissionsWrite((Activity) context);
        File sd =  Environment.getExternalStorageDirectory();

        File folder = new File(sd + "/flashcards");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success) {

            File image = new File(sd+"/flashcards", user.getId()+"_flashcards_profile.png");

            if (image.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                profileImage.setImageBitmap(bitmap);
            } else {
                Bitmap bitmap = ProcessorImage.generateImage(this);
                ProcessorImage.savebitmap(bitmap, user.getId(), "_flashcards_profile");
                profileImage.setImageBitmap(bitmap);
            }

        }

    }


    /**
     * Here you get the return values of the intends
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;


        // Get Photo from camera intent
        if (resultCode == RESULT_OK && data.getData() == null && requestCode != MY_INTENT_CLICK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ProcessorImage.savebitmap(bitmap, user.getId(), "_flashcards_profile");
            setProfileImage();

        }

        // Get Data from Gallery intent
        if (requestCode == MY_INTENT_CLICK) {

            if (null == data) return;

            String selectedImagePath;
            Uri selectedImageUri = data.getData();

            selectedImagePath = ProcessorImage.getPath(getApplicationContext(), selectedImageUri);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, bmOptions);
            ProcessorImage.savebitmap(bitmap, user.getId(), "_flashcards_profile");
            setProfileImage();
        }

    }


    @Override
    public void onBackPressed() {

        resetFabPlay();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {

            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                //super.onBackPressed();

                if (categoryLevel > 0) {

                    moveBackwardsInCatalogue();

                } else {

                    createFragmentHome();
                    getFragmentManager().popBackStack();
                }


            } else {

                drawer.openDrawer(GravityCompat.START);
                getFragmentManager().popBackStack();
            }
        }
    }


    /**
     * Confirmdialog
     *
     * TODO customice by adding params as text and header
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     */
    private void confirmDialogCardCreate() {

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        final View view = (View) findViewById(R.id.drawer_layout);

        final MainActivity mainActivity = this;


        builder.setView(view.getRootView())
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        mainActivity.catalogueState = Constants.FLASH_CARD_LIST;
                        mainActivity.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        builder.create().show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("query submit", query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                              Log.d("click", v.getId() + "");
                                          }
                                      }
        );
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        item.setChecked(true);

        resetFabPlay();

        if (id == R.id.nav_home) {

            ProcessConnectivity.isServerAlive ();
            createFragmentHome();

            toolbarTextViewTitle.setText(R.string.app_name);
            collapsingToolbar.setTitle(getResources().getString(R.string.app_name));

        } else if (id == R.id.nav_catalogue) {

            Globals.setVisibilityToolbarMain(View.VISIBLE);
            Globals.setVisibilityFab(View.VISIBLE);
            ProcessConnectivity.isServerAlive ();
            moveToLastCatalogueState();

        } else if (id == R.id.nav_play) {

            inflateFragmentPlay();

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_statistics) {

            /* Uncomment to start Statistic as fragment
            FragmentStatistics fragment = new FragmentStatistics();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();

            // Keep attention that this is replaced and not added
            fragmentTransaction.replace(R.id.fragment_container_main, fragment);
            fragmentTransaction.commit();
            */

            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_challenge) {

            startActivity(new Intent(MainActivity.this, MessagesActivity.class));

        }else if (id == R.id.nav_challengeRanking) {

            startActivity(new Intent(MainActivity.this, ChallengesRankingActivity.class));

        } else if (id == R.id.nav_settings) {

            /* Uncomment to start Settings as fragment
            FragmentSettings fragment = new FragmentSettings();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();

            // Keep attention that this is replaced and not added
            fragmentTransaction.replace(R.id.fragment_container_main, fragment);
            fragmentTransaction.commit();
            */

            // Starting Settings as Activity
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_user_groups) {

            startActivity(new Intent(MainActivity.this, UserGroupsActivity.class));

        } else if (id == R.id.nav_feedback) {

            //TODO Jonas rteditor
            SimpleRTEditor fragment = new SimpleRTEditor();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();

            // Keep attention that this is replaced and not added
            fragmentTransaction.replace(R.id.fragment_container_main, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {

            // TODO Ascny task to server to invalidate this token
            // What todo when offline wihle logoff
            db.logoutUser();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * When returning to the catalog drawer then set the last state
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     */
    private void moveToLastCatalogueState() {

        switch (catalogueState) {

            case CATEGORY_LIST:

                setCategoryList(false);
                break;
            case CARD_DECK_LIST:

                setCarddeckList(true);
                break;
            case FLASH_CARD_LIST:

                setFlashcardList(true);
                break;
            case FLASH_CARD_DETAIL:

                createFragmentFlashCardDetails(this.currentFlashCard, true);
                break;
            case FLASH_CARD_CREATE:

                // TODO
                setFlashcardList(true);

                break;
            default:

                setCategoryList(true);
                break;
        }

        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
        collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));
    }


    /**
     * Sets the catalogue like the history was
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     */
    private void moveBackwardsInCatalogue() {

        if (catalogueState != Constants.FLASH_CARD_CREATE && catalogueState != Constants.PLAY && parentIds.size() > 0) {

            this.childrenId = parentIds.get(parentIds.size() - 1);
        }

        switch (this.catalogueState) {

            case CATEGORY_LIST:

                categoryLevel--;
                setCategoryList(true);
                break;
            case CARD_DECK_LIST:

                categoryLevel--;

                Globals.setVisibilityFabAdd(View.GONE);
                setCategoryList(true);
                break;
            case FLASH_CARD_LIST:

                setCarddeckList(true);
                break;
            case FLASH_CARD_DETAIL:

                setFlashcardList(true);

                collapsingToolbar.setVisibility(View.VISIBLE);
                break;

            case FLASH_CARD_CREATE:

                confirmBackPressedToCardList();

                collapsingToolbar.setVisibility(View.VISIBLE);
                break;
            default:

                setCategoryList(true);
                break;
        }

        if (breadCrumbs.size() > 1) {

            breadCrumbs.remove(breadCrumbs.size() - 1);
            toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
            collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));
        }

        if (parentIds.size() > 0) {

            parentIds.remove(parentIds.size() - 1);
        }

    }


    /**
     * Confirm if to move backwards without saving
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     */
    private void confirmBackPressedToCardList() {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_header_confirm);
        builder.setMessage(R.string.dialog_content_unsaved)
                .setPositiveButton(R.string.prompt_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        setFlashcardList(true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }


    /**
     * Instantiates the DatabaseManager and opens a connection
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     */
    private void openDb() {

        try {

            if (db == null) {

                db = new DbManager(this);
            }
            db.open();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    /**
     * Destroys the database object
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        //db.close(); TODO
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * Click on Category
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param item
     */
    @Override
    public void onCategoryListFragmentInteraction(Category item) {

        // Increment because its a click on a catgory
        categoryLevel++;

        // Get new items id to get children
        this.childrenId = item.getId();
        // Add parent id to go back in catalogue
        this.parentIds.add(item.getParentId());

        // add text to breadcrump list and display it
        breadCrumbs.add(item.getName());
        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
        collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));

        // 4 is the maximal depth of categories
        // check if go deeper in categories or to load carddecks
        if (categoryLevel < 4) {
            setCategoryList(false);

        } else {

            setCarddeckList(false);
        }

    }


    /**
     * Click on CardDeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param item
     */
    @Override
    public void onCarddeckListFragmentInteraction(CardDeck item) {

        breadCrumbs.add(item.getName());
        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
        collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));

        this.parentIds.add(this.childrenId);
        this.childrenId = item.getId();

        setFlashcardList(false);

    }


    /**
     * Click on Flashcard or if null on add new FlashCard
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param item
     */
    @Override
    public void onFlashcardListFragmentInteraction(FlashCard item) {

        // add a new flashcards
        if (item == null) {

            // Create new flashcard

            catalogueState = Constants.FLASH_CARD_CREATE;
            createFragmentFlashCardCreate();
        } else {

            breadCrumbs.add("Flashcard #" + item.getId());
            toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
            collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));

            this.parentIds.add(this.childrenId);
            this.childrenId = item.getId();

            createFragmentFlashCardDetails(item, false);
        }

    }


    /**
     * Creates the Fragment to create a new flashcard with answers
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     */
    private void createFragmentFlashCardCreate() {

        breadCrumbs.add("new Flashcard");
        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
        collapsingToolbar.setTitle(breadCrumbs.get(breadCrumbs.size() - 1));

        this.parentIds.add(this.childrenId);

        this.currentFlashCard = null;
        this.catalogueState = Constants.FLASH_CARD_DETAIL;

        // TODO Start new fragment to create a card with answer
        FragmentFlashCardCreate fragment = new FragmentFlashCardCreate();
        fragment.setCarddeckId(this.childrenId);

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container_main, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Sets the listview with content from db or server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param backPressed
     */
    private void setFlashcardList(boolean backPressed) {

        new ContentFlashCards().collectItemsFromDb(this.childrenId, backPressed);
        new ContentFlashCards().collectItemsFromServer(this.childrenId, backPressed);

        catalogueState = Constants.FLASH_CARD_LIST;
    }


    /**
     * Sets the listview with content from db or server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param backPressed
     */
    public void setCarddeckList(boolean backPressed) {

        new ContentCarddecks().collectItemsFromDb(this.childrenId, backPressed);
        new ContentCarddecks().collectItemsFromServer(this.childrenId, backPressed);

        catalogueState = Constants.CARD_DECK_LIST;
    }


    /**
     * Sets the listview with content from db or server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param backPressed
     */
    private void setCategoryList(boolean backPressed) {

        new ContentCategories().collectItemsFromDb(this.categoryLevel, this.childrenId, backPressed);
        new ContentCategories().collectItemsFromServer(this.categoryLevel, this.childrenId, backPressed);

        catalogueState = Constants.CATEGORY_LIST;

    }

    /**
     * Sets the flashCard details view with local or remote data
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param backPressed
     * @param flashCard
     */
    private void createFragmentFlashCardDetails(FlashCard flashCard, boolean backPressed) {

        new  ContentFlashCard().collectItemFromServer(flashCard.getId(), backPressed);
        new  ContentFlashCard().collectItemFromDb(flashCard.getId(), backPressed);

        this.currentFlashCard = flashCard;
        this.catalogueState = Constants.FLASH_CARD_DETAIL;
    }
    /**
     * Sets the flashCard details view with local or remote data
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param backPressed
     * @param flashCardId
     */
    public void createFragmentFlashCardById (long flashCardId, boolean backPressed) {

        new  ContentFlashCard().collectItemFromServer(flashCardId, backPressed);

        this.currentFlashCard = null;
        this.catalogueState = Constants.FLASH_CARD_DETAIL;
    }


    /**
     * Click on answer
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param item
     */
    @Override
    public void onAnswerListFragmentInteraction(Answer item) {
        Log.d("click answer", item.toString());
    }


    /**
     * Set the state
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     * @param catalogueState
     */
    public void setCatalogueState(Constants catalogueState) {
        this.catalogueState = catalogueState;
    }

}
