package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUser;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteUser;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

import static de.uulm.einhoernchen.flashcardsapp.R.id.fab;

public class ProfileActivity extends AppCompatActivity {

    private User user = Globals.getDb().getLoggedInUser();
    private TextView textViewCreated;
    private TextView textViewLastlogin;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView imageViewProfile;

    private FloatingActionButton fab;

    private String ratingLabel;

    private static final int MY_INTENT_CLICK = 302;

    // Use this to set the title
    private CollapsingToolbarLayout collapsingToolbar;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingLabel = getResources().getString(R.string.label_rating) + ": ";

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (ProcessConnectivity.isOk(ProfileActivity.this)) {

                    toggleEditTexts();
                    toggleFabAction();
                } else {

                    Snackbar.make(view, Globals.getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                }
            }
        });


        imageViewProfile = (ImageView) findViewById(R.id.image_view_profile);
        setProfileImage();
        setProfileImageClickListener();


        buttonLogout = (Button) findViewById(R.id.button_profile_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                logoutUser();
            }
        });

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_profile);

        collapsingToolbar.setTitle(ratingLabel + user.getRating());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    /**
     * Toggles icon and action of the fab
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     */
    private void toggleFabAction() {


        boolean editMode = editTextName.isEnabled();

        if (editMode) {

            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
        } else {

            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit));

            // TODO update user


            JSONObject jsonObjectUser = new JSONObject();

            try {

                this.user = Globals.getDb().getLoggedInUser();

                String editName = editTextName.getText().toString();
                String editEmail = editTextEmail.getText().toString();
                String editPwd = editTextPassword.getText().toString();

                if (!user.getName().equals(editName)) {

                    jsonObjectUser.put(JsonKeys.USER_NAME, editName);
                }

                if (!user.getEmail().equals(editEmail)) {

                    jsonObjectUser.put(JsonKeys.USER_EMAIL, editEmail);
                }

                if (!Globals.getDb().getLoggedInUser().getPassword().equals(editPwd)) {

                    jsonObjectUser.put(JsonKeys.USER_PASSWORD, editPwd);
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            AsyncPatchRemoteUser asyncPatchRemoteUser = new AsyncPatchRemoteUser(jsonObjectUser);
            asyncPatchRemoteUser.execute(user.getId());
        }



    }


    @Override
    protected void onResume() {
        super.onResume();

        findViewElements();

        if (ProcessConnectivity.isOk(this)) {

            AsyncGetRemoteUser asyncGetUser = new AsyncGetRemoteUser(Globals.getDb().getLoggedInUser().getId(), new AsyncGetRemoteUser.AsyncResponseUser() {

                @Override
                public void processFinish(User refreshedUser) {

                    Globals.getDb().saveUser(refreshedUser, true);

                    user = refreshedUser;

                    setUserContent();

                    collapsingToolbar.setTitle(ratingLabel + user.getRating());
                }
            });

            asyncGetUser.execute();

        } else {

            setUserContent();
            collapsingToolbar.setTitle(ratingLabel + user.getRating());
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
     * Finds all relevant views
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     */
    private void findViewElements() {

        textViewCreated = (TextView) findViewById(R.id.text_view_created);
        textViewLastlogin = (TextView) findViewById(R.id.text_view_lastlogin);

        editTextName = (EditText) findViewById(R.id.editText_profile_name);
        editTextEmail = (EditText) findViewById(R.id.editText_profile_email);
        editTextPassword = (EditText) findViewById(R.id.editText_profile_password);

    }


    /**
     * Sets the users data to the view elements
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     *
     */
    private void setUserContent() {

        if (user.getLastLogin() != null) {

            textViewLastlogin.setText(user.getLastLogin().toGMTString());
        }

        if (user.getCreated() != null) {

            textViewCreated.setText(user.getCreated().toGMTString());
        }

        editTextName.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPassword.setText(Globals.getDb().getLoggedInUser().getPassword());

    }


    /**
     * toggles the enabling of the edittextes
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     */
    private void toggleEditTexts() {

        editTextName.setEnabled(!editTextName.isEnabled());
        editTextEmail.setEnabled(!editTextEmail.isEnabled());
        editTextPassword.setEnabled(!editTextPassword.isEnabled());
    }


    /**
     *
     * Searches for a profile image an sets it to the view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     */
    private void setProfileImage() {

        PermissionManager.verifyStoragePermissionsWrite(this);
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
                imageViewProfile.setImageBitmap(bitmap);
            } else {

                Bitmap bitmap = ProcessorImage.generateImage(this);
                ProcessorImage.savebitmap(bitmap, user.getId(), "_flashcards_profile");
                imageViewProfile.setImageBitmap(bitmap);
            }

        }

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
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MEDIA GALLERY
                PermissionManager.verifyStoragePermissionsWrite(ProfileActivity.this);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                builder.setMessage(R.string.prompt_choose_profile)
                        .setPositiveButton(R.string.prompt_choose_camera, dialogClickListener)
                        .setNegativeButton(R.string.prompt_choose_gallery, dialogClickListener)
                        .setNeutralButton(R.string.prompt_cancel, dialogClickListener).show();
            }

        });
    }



    /**
     * Here you get the return values of the intends
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
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


    /**
     * Logs the current user out
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     *
     */
    private void logoutUser () {

        // TODO Ascny task to server to invalidate this token
        // What todo when offline wihle logoff
        Globals.getDb().logoutUser();

        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

}
