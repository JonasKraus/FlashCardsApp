package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbManager {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;

    /**
     * All Columns of an user
     */
    private String[] allUserColumns = {
            MySQLiteHelper.COLUMN_USER_ID,                  //0
            MySQLiteHelper.COLUMN_USER_AVATAR,              //1
            MySQLiteHelper.COLUMN_USER_NAME,                //2
            MySQLiteHelper.COLUMN_USER_PASSWORD,            //3
            MySQLiteHelper.COLUMN_USER_EMAIL,               //4
            MySQLiteHelper.COLUMN_USER_RATING,              //5
            MySQLiteHelper.COLUMN_USER_GROUP_ID,            //6
            MySQLiteHelper.COLUMN_USER_CREATED,             //7
            MySQLiteHelper.COLUMN_USER_LAST_LOGIN           //8
    };

    private String[] allFlashCardColumns = {
            MySQLiteHelper.COLUMN_FLASHCARD_ID,             //0
            MySQLiteHelper.COLUMN_FLASHCARD_CARDDECK_ID,    //1
            MySQLiteHelper.COLUMN_FLASHCARD_RATING,         //2
            MySQLiteHelper.COLUMN_FLASHCARD_QUESTION_ID,    //3
            MySQLiteHelper.COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
            MySQLiteHelper.COLUMN_FLASHCARD_CREATED,        //5
            MySQLiteHelper.COLUMN_FLASHCARD_LAST_UPDATED    //6
    };

    private String[] allQuestionColumns = {
            MySQLiteHelper.COLUMN_QUESTION_ID,              //0
            MySQLiteHelper.COLUMN_QUESTION_TEXT,            //1
            MySQLiteHelper.COLUMN_QUESTION_MEDIA_URI,       //2
            MySQLiteHelper.COLUMN_QUESTION_AUTHOR_ID,       //3
    };

    private String[] allAnswerColumns = {
            MySQLiteHelper.COLUMN_ANSWER_ID,                //0
            MySQLiteHelper.COLUMN_ANSWER_TEXT,              //1
            MySQLiteHelper.COLUMN_ANSWER_HINT,              //2
            MySQLiteHelper.COLUMN_ANSWER_MEDIA_URI,         //3
            MySQLiteHelper.COLUMN_ANSWER_USER_ID,           //4
            MySQLiteHelper.COLUMN_ANSWER_PARENT_CARD_ID,    //5
            MySQLiteHelper.COLUMN_ANSWER_RATING,            //6
            MySQLiteHelper.COLUMN_ANSWER_CORRECT,           //7
            MySQLiteHelper.COLUMN_ANSWER_CREATED,           //8
            MySQLiteHelper.COLUMN_ANSWER_LAST_UPDATED       //9
    };

    private String[] allCardTagColumns = {
            MySQLiteHelper.COLUMN_CARD_TAG_FLASHCARD_ID,    //0
            MySQLiteHelper.COLUMN_CARD_TAG_TAG_ID,          //1
    };

    private String[] allTagColumns = {
            MySQLiteHelper.COLUMN_TAG_ID,                   //0
            MySQLiteHelper.COLUMN_TAG_NAME,                 //1
    };

    private String[] allGroupColumns = {
            MySQLiteHelper.COLUMN_GROUP_ID,                 //0
            MySQLiteHelper.COLUMN_GROUP_NAME,               //1
            MySQLiteHelper.COLUMN_GROUP_DESCRIPTION,        //2
    };

    private String[] allRatingColumns = {
            MySQLiteHelper.COLUMN_RATING_ID,                //0
            MySQLiteHelper.COLUMN_RATING_TYPE,              //1
            MySQLiteHelper.COLUMN_RATING_USER_ID,           //2
            MySQLiteHelper.COLUMN_RATING_MODIFIER,          //3
            MySQLiteHelper.COLUMN_RATING_FLASHCARD_ID,      //4
            MySQLiteHelper.COLUMN_RATING_ANSWER_ID,         //5
    };

    private String[] allAuthTokenColumns = {
            MySQLiteHelper.COLUMN_RATING_ID,                //0
            MySQLiteHelper.COLUMN_AUTH_TOKEN_ID,            //1
            MySQLiteHelper.COLUMN_AUTH_TOKEN_USER_ID,       //2
            MySQLiteHelper.COLUMN_AUTH_TOKEN_TOKEN,         //3
            MySQLiteHelper.COLUMN_AUTH_TOKEN_CREATED,       //4
    };

    private String[] allCardDeckColumns = {
            MySQLiteHelper.COLUMN_CARD_DECK_ID,             //0
            MySQLiteHelper.COLUMN_CARD_DECK_NAME,           //1
            MySQLiteHelper.COLUMN_CARD_DECK_DESCRIPTION,    //2
    };

    /**
     * Constructor
     *
     * @param context
     */
    public DbManager(Context context) {

        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }


    /**
     * Opens a database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {

        this.database = dbHelper.getWritableDatabase();
    }


    /**
     * Closes a database connection
     *
     */
    public void close() {

        dbHelper.close();
        Log.d("db", "closed");
    }


    /**
     * Creates a user with all given values
     * @param user
     */
    public void createUser(User user) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_ID, user.getId());
        values.put(MySQLiteHelper.COLUMN_USER_AVATAR, user.getAvatar());
        values.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
        values.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(MySQLiteHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(MySQLiteHelper.COLUMN_USER_RATING, user.getRating());
        //@TODO GroupID ??? values.put(MySQLiteHelper.COLUMN_USER_GROUP_ID, user.getGroup().getId());
        String created = user.getCreated() != null ? user.getCreated().toString() : "";
        String lastLogin = user.getLastLogin() != null ? user.getLastLogin().toString() : "";
        values.put(MySQLiteHelper.COLUMN_USER_CREATED, created); // @TODO check correct date
        values.put(MySQLiteHelper.COLUMN_USER_LAST_LOGIN, lastLogin); // @TODO check correct date

        // Executes the query
        database.insert(MySQLiteHelper.TABLE_USER, null, values);
    }


    /**
     * Gets the saved User
     *
     * @return User | null
     */
    public User getUser() {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, allUserColumns, null, null, null, null, null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    //cursor.getLong(6), // @TODO
                    cursor.getString(7),
                    cursor.getString(8)
                    // @TODO add last login
            );
        }
        cursor.close();

        return user;
    }


    /**
     * Checks if a user is already existant
     *
     * @return boolean
     */
    public boolean checkIfUserExists() {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, allUserColumns, null, null, null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

}
