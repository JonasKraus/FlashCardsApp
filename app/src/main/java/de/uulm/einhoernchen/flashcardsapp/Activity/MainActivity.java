package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteHeartbeat;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentHome;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCategories;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerCategory;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Consts.Constants;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentHome.OnFragmentInteractionListener, OnFragmentInteractionListenerFlashcard, OnFragmentInteractionListenerCategory, OnFragmentInteractionListenerCarddeck, FragmentFlashCard.OnFlashCardFragmentInteractionListener {


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
    private boolean isAlive;
    // the Flashcard that was loaded last in details fragment
    private FlashCard currentFlashCard;

    private static final int MY_INTENT_CLICK=302;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_home);

        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        FragmentHome fragment = new FragmentHome();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_main, fragment);
        fragmentTransaction.commit();

        // Set the fragment initially

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        openDb();

        user = db.getLocalAccountUser(); Log.d("user--->", user.toString());

        profileImage = (ImageView) header.findViewById(R.id.imageViewProfilePhoto);
        setProfileImage();

        TextView profileName = (TextView) header.findViewById(R.id.textViewProfileName);
        TextView profileEmail = (TextView) header.findViewById(R.id.textViewProfileEmail);
        TextView profileRating = (TextView) header.findViewById(R.id.textViewProfileRating);

        toolbarTextViewTitle = (TextView ) findViewById(R.id.toolbar_text_view_title);
        breadCrumbs = new ArrayList<String>();
        breadCrumbs.add("");
        parentIds = new ArrayList<Long>();

        profileName.setText(user.getName());
        profileEmail.setText(user.getEmail());
        profileRating.setText(user.getRating()+"");

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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

            FragmentHome fragment = new FragmentHome();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentTransaction.replace(R.id.fragment_container_main, fragment);
            fragmentTransaction.commit();

            toolbarTextViewTitle.setText(R.string.app_name);

        } else if (id == R.id.nav_catalogue) {

            isServerAlive ();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * When returning to the catalog drawer then set the last state
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
     * Instantiates the DatabesManager and opens a connection
     *
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


    @Override
    protected void onDestroy() {

        super.onDestroy();

        db.close();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }



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

    @Override
    public void onCarddeckListFragmentInteraction(CardDeck item) {

        breadCrumbs.add(item.getName());
        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));

        this.parentIds.add(this.childrenId);
        this.childrenId = item.getId();

        setFlashcardList(false);

    }


    @Override
    public void onFlashcardListFragmentInteraction(FlashCard item) {

        breadCrumbs.add("Flashcard #" + item.getId());
        toolbarTextViewTitle.setText(breadCrumbs.get(breadCrumbs.size() - 1));

        this.parentIds.add(this.childrenId);
        this.childrenId = item.getId();

        createFragmentFlashCard(item, false);

    }

    private void setFlashcardList(boolean backPressed) {

        isServerAlive();
        new ContentFlashCards().collectItemsFromDb(this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);

        if (isNetworkAvailable() && isAlive) {
            new ContentFlashCards().collectItemsFromServer(this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);
        }

        catalogueState = Constants.FLASH_CARD_LIST;
    }

    private void setCarddeckList(boolean backPressed) {

        isServerAlive();
        new ContentCarddecks().collectItemsFromDb(this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);

        if (isNetworkAvailable() && isAlive) {
            new ContentCarddecks().collectItemsFromServer(this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);
        }

        catalogueState = Constants.CARD_DECK_LIST;
    }

    private void setCategoryList(boolean backPressed) {

        isServerAlive();
        new ContentCategories().collectItemsFromDb(this.categoryLevel, this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);

        if (isNetworkAvailable() && isAlive) {
            new ContentCategories().collectItemsFromServer(this.categoryLevel, this.childrenId, getSupportFragmentManager(), progressBar, backPressed, db);

        }

        catalogueState = Constants.CATEGORY_LIST;

    }


    private void createFragmentFlashCard (FlashCard flashCard, boolean backPressed) {

        isServerAlive();
        new ContentFlashCard().collectItemFromDb(flashCard.getId(), getSupportFragmentManager(), progressBar, backPressed, db);

        if (isNetworkAvailable() && isAlive) {
            new  ContentFlashCard().collectItemFromServer(flashCard.getId(), getSupportFragmentManager(), progressBar, backPressed, db);

        }

        this.currentFlashCard = flashCard;
        this.catalogueState = Constants.FLASH_CARD_DETAIL;

        /*
        FragmentFlashCard fragment = new FragmentFlashCard();
        fragment.setItem(flashCard);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
                */
        /*
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        */
        /*
        fragmentTransaction.replace(R.id.fragment_container_main, fragment);
        fragmentTransaction.commit();
        */
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Call this before any async task that requests the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-03
     */
    private void isServerAlive () {

        AsyncGetRemoteHeartbeat asyncGetRemoteHeartbeat = new AsyncGetRemoteHeartbeat(new AsyncGetRemoteHeartbeat.AsyncResponseHeartbeat() {
            
            @Override
            public void processFinish(Boolean isAlive) {

                setAlive(isAlive);
            }
        });

        asyncGetRemoteHeartbeat.execute();
    }

    private void setAlive (boolean isAlive) {
        this.isAlive = isAlive;
    }

}
