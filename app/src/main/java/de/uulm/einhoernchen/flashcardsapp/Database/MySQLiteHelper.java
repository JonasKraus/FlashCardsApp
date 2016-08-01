package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jonas on 02.07.2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Strings for table user
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_USER_AVATAR = "avatar";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_RATING = "rating";
    public static final String COLUMN_USER_GROUP_ID = "groupId";
    public static final String COLUMN_USER_CREATED= "created";
    public static final String COLUMN_USER_LAST_LOGIN= "lastLogin";

    // Strings for table flashcard
    public static final String TABLE_FLASHCARD = "flash_card";
    public static final String COLUMN_FLASHCARD_ID = "flashcardId";
    public static final String COLUMN_FLASHCARD_RATING = "rating";
    public static final String COLUMN_FLASHCARD_QUESTION_ID = "questionId";
    public static final String COLUMN_FLASHCARD_MULTIPLE_CHOICE = "multipleChoice";
    public static final String COLUMN_FLASHCARD_CREATED = "created";
    public static final String COLUMN_FLASHCARD_LAST_UPDATED = "lastUpdated";

    // Strings for table question
    public static final String TABLE_QUESTION = "question";
    public static final String COLUMN_QUESTION_ID = "questionId";
    public static final String COLUMN_QUESTION_TEXT = "questionText";
    public static final String COLUMN_QUESTION_MEDIA_URI = "mediaURI";
    public static final String COLUMN_QUESTION_AUTHOR_ID = "authorId";

    // Strings for table answer
    public static final String TABLE_ANSWER = "answer";
    public static final String COLUMN_ANSWER_ID = "answerId";
    public static final String COLUMN_ANSWER_TEXT = "answerText";
    public static final String COLUMN_ANSWER_HINT = "answerHint";
    public static final String COLUMN_ANSWER_MEDIA_URI = "mediaURI";
    public static final String COLUMN_ANSWER_USER_ID = "userId";
    public static final String COLUMN_ANSWER_PARENT_CARD_ID = "parentCardId";
    public static final String COLUMN_ANSWER_RATING = "rating";
    public static final String COLUMN_ANSWER_CORRECT = "answerCorrect";
    public static final String COLUMN_ANSWER_CREATED = "created";
    public static final String COLUMN_ANSWER_LAST_UPDATED = "lastUpdated";

    // Strings for table linking tables card-tag
    public static final String TABLE_CARD_TAG = "card_tag";
    public static final String COLUMN_CARD_TAG_FLASHCARD_ID = "tagId";
    public static final String COLUMN_CARD_TAG_TAG_ID = "tagId";

    // Strings for table tag
    public static final String TABLE_TAG = "tag";
    public static final String COLUMN_TAG_ID = "tagId";
    public static final String COLUMN_TAG_NAME = "name";

    // Strings for table user group
    public static final String TABLE_USER_GROUP = "user_group";
    public static final String COLUMN_GROUP_ID = "groupId";
    public static final String COLUMN_GROUP_NAME = "name";
    public static final String COLUMN_GROUP_DESCRIPTION = "description";

    // Strings for table rating
    public static final String TABLE_RATING = "rating";
    public static final String COLUMN_RATING_ID = "ratingId";
    public static final String COLUMN_RATING_TYPE = "ratingType";
    public static final String COLUMN_RATING_USER_ID = "userId";
    public static final String COLUMN_RATING_MODIFIER = "ratingModifier";
    public static final String COLUMN_RATING_FLASHCARD_ID = "flashcardId";
    public static final String COLUMN_RATING_ANSWER_ID = "answerId";

    // Strings for table auth_token
    public static final String TABLE_AUTH_TOKEN = "auth_token";
    public static final String COLUMN_AUTH_TOKEN_ID = "tokenId";
    public static final String COLUMN_AUTH_TOKEN_USER_ID = "userId";
    public static final String COLUMN_AUTH_TOKEN_TOKEN = "token";
    public static final String COLUMN_AUTH_TOKEN_CREATED = "created";

    // Database name and version - increase when existing table is altered
    private static final String DATABASE_NAME = "flashcardsDb.db";
    private static final int DATABASE_VERSION = 1;


    /**
     * Database creation sql statement for table user
     *
     */
    private static final String USER_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_USER_ID
            + " integer primary key autoincrement, "
            + COLUMN_USER_NAME
            + " text not null, "
            + COLUMN_USER_AVATAR
            + " blob, "
            + COLUMN_USER_PASSWORD
            + " text not null, "
            + COLUMN_USER_EMAIL
            + " text not null, "
            + COLUMN_USER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_USER_GROUP_ID
            + " integer, "
            + COLUMN_USER_CREATED
            + " text not null, "
            + COLUMN_USER_LAST_LOGIN
            + " text "
            + ");";

    private static final String FLASHCARD_CREATE = "create table "
            + TABLE_FLASHCARD + "("
            + COLUMN_FLASHCARD_ID
            + " integer primary key autoincrement, "
            + COLUMN_FLASHCARD_RATING
            + " integer DEFAULT 0, "
            + COLUMN_FLASHCARD_QUESTION_ID
            + " integer, "
            + COLUMN_FLASHCARD_MULTIPLE_CHOICE
            + " integer DEFAULT 0, "
            + COLUMN_USER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_FLASHCARD_CREATED
            + " not null DEFAULT time('now'), "
            + COLUMN_FLASHCARD_LAST_UPDATED
            + " text "
            + ");";

    private static final String QUESTION_CREATE = "create table "
            + TABLE_QUESTION + "("
            + COLUMN_QUESTION_ID
            + " integer primary key autoincrement, "
            + COLUMN_QUESTION_TEXT
            + " text not null, "
            + COLUMN_QUESTION_MEDIA_URI
            + " text, "
            + COLUMN_QUESTION_AUTHOR_ID
            + " integer "
            + ");";

    private static final String ANSWER_CREATE = "create table "
            + TABLE_ANSWER + "("
            + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, "
            + COLUMN_ANSWER_TEXT
            + " text not null, "
            + COLUMN_ANSWER_HINT
            + " text, "
            + COLUMN_ANSWER_MEDIA_URI
            + " text, "
            + COLUMN_ANSWER_USER_ID
            + " integer not null, "
            + COLUMN_ANSWER_PARENT_CARD_ID
            + " integer not null, "
            + COLUMN_ANSWER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_ANSWER_CORRECT
            + " integer DEFAULT 1, "
            + COLUMN_ANSWER_CREATED
            + " not null DEFAULT time('now'), "
            + COLUMN_ANSWER_LAST_UPDATED
            + " text "
            + ");";

    private static final String CARD_TAG_CREATE = "create table "
            + TABLE_CARD_TAG + "("
            + COLUMN_CARD_TAG_FLASHCARD_ID
            + " integer primary not null, "
            + COLUMN_CARD_TAG_TAG_ID
            + " integer primary not null "
            + ");";

    private static final String TAG_CREATE = "create table "
            + TABLE_TAG + "("
            + COLUMN_TAG_ID
            + " integer primary key, "
            + COLUMN_TAG_NAME
            + " text not null "
            + ");";

    private static final String USER_GROUP_CREATE = "create table "
            + TABLE_USER_GROUP + "("
            + COLUMN_GROUP_ID
            + " integer primary key not null, "
            + COLUMN_GROUP_NAME
            + " text not null, "
            + COLUMN_GROUP_DESCRIPTION
            + " text "
            + ");";

    private static final String RATING_CREATE = "create table "
            + TABLE_RATING + "("
            + COLUMN_RATING_ID
            + " integer primary key, "
            + COLUMN_RATING_TYPE
            + " text not null, "
            + COLUMN_RATING_USER_ID
            + " integer not null, "
            + COLUMN_RATING_MODIFIER
            + " integer not null, "
            + COLUMN_RATING_FLASHCARD_ID
            + " integer not null, "
            + COLUMN_RATING_ANSWER_ID
            + " integer not null "
            + ");";

    private static final String AUTH_TOKEN_CREATE = "create table "
            + TABLE_AUTH_TOKEN + "("
            + COLUMN_AUTH_TOKEN_ID
            + " integer primary key not null, "
            + COLUMN_AUTH_TOKEN_USER_ID
            + " integer not null, "
            + COLUMN_AUTH_TOKEN_TOKEN
            + " text not null, "
            + COLUMN_AUTH_TOKEN_CREATED
            + " DEFAULT time('now') "
            + ");";

    /**
     * Constructor
     *
     * @param context
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_CREATE);
        db.execSQL(FLASHCARD_CREATE);
        db.execSQL(QUESTION_CREATE);
        db.execSQL(ANSWER_CREATE);
        db.execSQL(CARD_TAG_CREATE);
        db.execSQL(TAG_CREATE);
        db.execSQL(USER_GROUP_CREATE);
        db.execSQL(RATING_CREATE);
        db.execSQL(AUTH_TOKEN_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH_TOKEN);

        onCreate(db);
    }
}

