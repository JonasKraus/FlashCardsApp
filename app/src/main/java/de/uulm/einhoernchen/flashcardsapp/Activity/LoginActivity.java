package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteUser;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteToken;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Model.Response.Response;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mUserNameView;
    private View mProgressView;
    private View mLoginFormView;

    private String password;

    private Context context;

    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        openDb();

        // Immediately change to home activity
        if (db.checkIfLocalAccountUserExists()) {

           // startActivity(new Intent(LoginActivity.this, ItemListActivity.class));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }


        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    if (db.loginUser(mEmailView.getText().toString(), mUserNameView.getText().toString(), mPasswordView.getText().toString())) {

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {

                        requestUserToken();
                    }

                    return true;
                }
                return false;
            }
        });


        mUserNameView = (EditText) findViewById(R.id.user_name);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        Button mEmailForgotPwdButton = (Button) findViewById(R.id.email_forgot_pwd);

        mEmailForgotPwdButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO request to get new pwd
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (db.loginUser(mEmailView.getText().toString(), mUserNameView.getText().toString(), mPasswordView.getText().toString())) {

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {

                    requestUserToken();
                }
            }
        });

        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUserNameView.getVisibility() == View.GONE) {

                    mUserNameView.setVisibility(View.VISIBLE);
                } else {

                    //attemptToRegister();
                    Log.d("Attempts", "register");
                    attemptRegister();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }




    /**
     * Gets the token from local storage if already existent
     * otherwise requests it frfom the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-15
     */
    private void requestUserToken() {


        // Get the token from the server if no token exists
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(JsonKeys.USER_EMAIL, mEmailView.getText().toString());
            jsonObject.put(JsonKeys.USER_PASSWORD, mPasswordView.getText().toString());

        } catch (JSONException e) {

            Log.e("ERROR", "json token");
            e.printStackTrace();
        }

        // Gets the login token and saves it locally
        AsyncPostRemoteToken asyncToken = new AsyncPostRemoteToken(jsonObject, db, new AsyncPostRemoteToken.AsyncResponse(){

            @Override
            public void processFinish(Response response) {

                if (response.getStatuscode() < 400 && response.getToken() != null && response.getUserId() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {

                    Snackbar.make(mEmailView, R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        asyncToken.execute();

    }



    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        String userName = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user name
        if (!TextUtils.isEmpty(userName) && !isUserNameValid(userName)) {
            mUserNameView.setError(getString(R.string.error_invalid_username));
            focusView = mUserNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            final Context c = this.context;

            Log.d("new login", "UserLoginTask");
            mAuthTask = new UserLoginTask(email, userName, password, new AsyncResponseId(){

                @Override
                public void processFinish(Long userId) {

                    AsyncGetRemoteUser asyncGetUser = new AsyncGetRemoteUser(userId, new AsyncGetRemoteUser.AsyncResponseUser() {
                        @Override
                        public void processFinish(User user) {

                            user.setPassword(password);
                            db.saveUser(user, true);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    });

                    if (ProcessConnectivity.isOk(context)) {

                        asyncGetUser.execute();
                    }


                }

            });
            //TODO: give the string if sign in or create to async task

            if (ProcessConnectivity.isOk(context) || true) {

                mAuthTask.execute((Void) null);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    private boolean isUserNameValid(String userName) {
        return userName.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public interface AsyncResponseId {
        void processFinish(Long output);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Long, Long> {

        private final String mEmail;
        private final String mPassword;
        private final String mUserName;

        public AsyncResponseId delegate = null;

        UserLoginTask(String email, String userName, String password, AsyncResponseId delegate) {
            mEmail = email;
            mPassword = password;
            mUserName = userName;
            this.delegate = delegate;
        }

        /*
        public boolean isConnected(){
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;
        }
        */

        @Override
        protected Long doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String urlString = Routes.URL + Routes.SLASH + Routes.USERS; // URL to call

            Log.d("start do in back", urlString);

            HttpURLConnection urlConnection = null;
            //InputStream in = null;

            try {

                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                JSONObject cred = new JSONObject();
                cred.put("email", mEmail);
                cred.put("name", mUserName);
                cred.put("password", mPassword);

                urlConnection.connect();

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(cred.toString());
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {

                    Log.d("decoded", decodedString);
                    int response = urlConnection.getResponseCode();


                    Log.d("response", response +"");
                    if (response >= 200 && response <= 399) {

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(decodedString);
                        long id = root.get("id").asLong();

                        return id;
                    } else {

                        Log.d("response", response +"");
                        return null;
                    }
                }
                in.close();

            } catch (Exception e) {

                if (db.loginUser(mEmail, mUserName, mPassword)) {

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }


                Log.e("error User", e.toString());

            }
            return null;
        }


        @Override
        protected void onPostExecute(Long id) {
            mAuthTask = null;
            showProgress(false);

            if (id != null) {
                delegate.processFinish(id);
                //finish();
            } else {

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

