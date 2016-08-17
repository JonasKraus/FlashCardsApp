package de.uulm.einhoernchen.flashcardsapp.Activity;

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
import android.widget.TextView;

import java.io.File;
import java.sql.SQLException;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.HomeFragment;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentCategory;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentCategory.OnCategoryListFragmentInteractionListener;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.Categroy;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.ImageProcessor;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, ItemFragmentFlashcard.OnFlashcardListFragmentInteractionListener, OnCategoryListFragmentInteractionListener, ItemFragmentCarddeck.OnCarddeckListFragmentInteractionListener {


    private DbManager db;
    private User user;
    private Context context;
    private ImageView profileImage;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private long parentId = -100;



    private static final int MY_INTENT_CLICK=302;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_home);

        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_home, fragment);
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

        user = db.getUser(); Log.d("user--->", user.toString());

        profileImage = (ImageView) header.findViewById(R.id.imageViewProfilePhoto);
        setProfileImage();

        TextView profileName = (TextView) header.findViewById(R.id.textViewProfileName);
        TextView profileEmail = (TextView) header.findViewById(R.id.textViewProfileEmail);

        profileName.setText(user.getName());
        profileEmail.setText(user.getEmail());

        /**
         * Adds clicklistener to the profile image
         * you can choose image of gallery or from camera
         */
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                builder.setMessage(R.string.prompt_choose_profile)
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
            ImageProcessor.savebitmap(bitmap, user.getId());
            setProfileImage();

        }

        // Get Data from Gallery intent
        if (requestCode == MY_INTENT_CLICK) {

            if (null == data) return;

            String selectedImagePath;
            Uri selectedImageUri = data.getData();

            //MEDIA GALLERY
            PermissionManager.verifyStoragePermissionsWrite(this);
            selectedImagePath = ImageProcessor.getPath(getApplicationContext(), selectedImageUri);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, bmOptions);
            ImageProcessor.savebitmap(bitmap, user.getId());
            setProfileImage();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

            HomeFragment fragment = new HomeFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_home, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_catalogue) {

            setCategoryList();

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_challenge) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_logout) {
            Log.d("Click", "log out");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onCategoryListFragmentInteraction(Categroy item) {
        Log.d("click category", item.toString());
        setCarddeckList();
    }

    @Override
    public void onCarddeckListFragmentInteraction(CardDeck item) {
        Log.d("click carddeck", item.toString());
        setFlashcardList();
    }


    @Override
    public void onFlashcardListFragmentInteraction(FlashCard item) {
        Log.d("click", item.toString());
        parentId = item.getId(); // TODO
        setFlashcardList();

    }

    private void setFlashcardList() {
        ItemFragmentFlashcard fragment = new ItemFragmentFlashcard();
        Bundle args = new Bundle();
        args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, this.parentId);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_home, fragment);
        fragmentTransaction.commit();
    }

    private void setCarddeckList() {
        Log.d("hier", "");
        ItemFragmentCarddeck fragment = new ItemFragmentCarddeck();
        Bundle args = new Bundle();
        args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, this.parentId);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_home, fragment);
        fragmentTransaction.commit();
    }

    private void setCategoryList() {
        ItemFragmentCategory fragment = new ItemFragmentCategory();
        Bundle args = new Bundle();
        args.putLong(ItemFragmentFlashcard.ARG_PARENT_ID, this.parentId);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_home, fragment);
        fragmentTransaction.commit();
    }
}
