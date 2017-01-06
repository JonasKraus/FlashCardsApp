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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHome;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCategories;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCategory;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentHome.OnFragmentInteractionListener, OnFragmentInteractionListenerFlashcard, OnFragmentInteractionListenerCategory, OnFragmentInteractionListenerCarddeck, FragmentFlashCard.OnFlashCardFragmentInteractionListener, OnFragmentInteractionListenerAnswer {


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

        createFloatingActionButton();
        createToolbar();

        initBreadCrumps();

        // INIT variables
        this.context = this;
        openDb();

        // init Globals
        Globals.initGlobals(context, progressBar, db, getSupportFragmentManager());

        setProfileView();
        ProcessConnectivity.isServerAlive ();

        setProfileImageClickListener();

        createFragmentHome();
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {

            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                //super.onBackPressed();

                if (categoryLevel > 0) {

                    moveBackwardsInCatalogue();
                } else {

                    drawer.openDrawer(GravityCompat.START);
                    getFragmentManager().popBackStack();
                }


            } else {

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

        if (id == R.id.nav_home) {

            ProcessConnectivity.isServerAlive ();
            createFragmentHome();

            toolbarTextViewTitle.setText(R.string.app_name);

        } else if (id == R.id.nav_catalogue) {

            ProcessConnectivity.isServerAlive ();
            moveToLastCatalogueState();

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_challenge) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_logout) {

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

                createFragmentFlashCard(this.currentFlashCard, true);
                break;
            default:

                //setCategoryList(true); TODO
                break;
        }

        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
    }


    /**
     * Sets the catalogue like the history was
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     */
    private void moveBackwardsInCatalogue() {

        this.childrenId = parentIds.get(parentIds.size() - 1);

        switch (this.catalogueState) {

            case CATEGORY_LIST:

                categoryLevel--;
                setCategoryList(true);
                break;
            case CARD_DECK_LIST:

                categoryLevel--;
                setCategoryList(true);
                break;
            case FLASH_CARD_LIST:

                setCarddeckList(true);
                break;
            case FLASH_CARD_DETAIL:

                setFlashcardList(true);
                break;
            default:

                setCategoryList(true);
                break;
        }

        if (breadCrumbs.size() > 1) {

            breadCrumbs.remove(breadCrumbs.size() - 1);
            toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));
        }

        if (parentIds.size() > 0) {

            parentIds.remove(parentIds.size() - 1);
        }

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
        db.close();
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

            createFragmentFlashCardCreate();
        } else {

            breadCrumbs.add("Flashcard #" + item.getId());
            toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));

            this.parentIds.add(this.childrenId);
            this.childrenId = item.getId();

            createFragmentFlashCard(item, false);
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

        this.parentIds.add(this.childrenId);

        this.currentFlashCard = null;
        this.catalogueState = Constants.FLASH_CARD_DETAIL;

        // TODO Start new fragment to create a card with answer
        FragmentFlashCard fragment = new FragmentFlashCard();
        fragment.setItem(null);
        fragment.setUpToDate(false);
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
    private void setCarddeckList(boolean backPressed) {

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
    private void createFragmentFlashCard (FlashCard flashCard, boolean backPressed) {

        new  ContentFlashCard().collectItemFromServer(flashCard.getId(), backPressed);
        new  ContentFlashCard().collectItemFromDb(flashCard.getId(), backPressed);

        this.currentFlashCard = flashCard;
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
}
