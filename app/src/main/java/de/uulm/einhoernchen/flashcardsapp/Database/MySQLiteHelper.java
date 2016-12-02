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
    public static final String COLUMN_USER_ID = "userId";                               //0
    public static final String COLUMN_USER_NAME = "name";                               //1
    public static final String COLUMN_USER_AVATAR = "avatar";                           //2
    public static final String COLUMN_USER_PASSWORD = "password";                       //3
    public static final String COLUMN_USER_EMAIL = "email";                             //4
    public static final String COLUMN_USER_RATING = "rating";                           //5
    public static final String COLUMN_USER_GROUP_ID = "groupId";                        //6
    public static final String COLUMN_USER_CREATED= "created";                          //7
    public static final String COLUMN_USER_LAST_LOGIN= "lastLogin";                     //8
    public static final String COLUMN_USER_LOCAL_ACCOUNT= "localAccount";               //9
    // @TODO Auth Token

    // Strings for table flashcard
    public static final String TABLE_FLASHCARD = "flashcard";
    public static final String COLUMN_FLASHCARD_ID = "flashcardId";                     //0
    public static final String COLUMN_FLASHCARD_CARDDECK_ID = "cardDeckId";             //1
    public static final String COLUMN_FLASHCARD_RATING = "rating";                      //2
    public static final String COLUMN_FLASHCARD_QUESTION_ID = "questionId";             //3
    public static final String COLUMN_FLASHCARD_MULTIPLE_CHOICE = "multipleChoice";     //4
    public static final String COLUMN_FLASHCARD_CREATED = "created";                    //5
    public static final String COLUMN_FLASHCARD_LAST_UPDATED = "lastUpdated";           //6
    public static final String COLUMN_FLASHCARD_USER_ID = "userId";                     //7

    // Strings for table question
    public static final String TABLE_QUESTION = "question";
    public static final String COLUMN_QUESTION_ID = "questionId";                       //0
    public static final String COLUMN_QUESTION_TEXT = "questionText";                   //1
    public static final String COLUMN_QUESTION_MEDIA_URI = "mediaURI";                  //2
    public static final String COLUMN_QUESTION_AUTHOR_ID = "authorId";                  //3

    // Strings for table answer
    public static final String TABLE_ANSWER = "answer";
    public static final String COLUMN_ANSWER_ID = "answerId";                           //0
    public static final String COLUMN_ANSWER_TEXT = "answerText";                       //1
    public static final String COLUMN_ANSWER_HINT = "answerHint";                       //2
    public static final String COLUMN_ANSWER_MEDIA_URI = "mediaURI";                    //3
    public static final String COLUMN_ANSWER_USER_ID = "userId";                        //4
    public static final String COLUMN_ANSWER_PARENT_CARD_ID = "parentCardId";           //5
    public static final String COLUMN_ANSWER_RATING = "rating";                         //6
    public static final String COLUMN_ANSWER_CORRECT = "answerCorrect";                 //7
    public static final String COLUMN_ANSWER_CREATED = "created";                       //8
    public static final String COLUMN_ANSWER_LAST_UPDATED = "lastUpdated";              //9

    // Strings for table linking tables card-tag
    public static final String TABLE_CARD_TAG = "cardtagjointable";
    public static final String COLUMN_CARD_TAG_FLASHCARD_ID = "flashcardId";            //0
    public static final String COLUMN_CARD_TAG_TAG_ID = "tagId";                        //1

    // Strings for table tag
    public static final String TABLE_TAG = "tag";
    public static final String COLUMN_TAG_ID = "tagId";                                 //0
    public static final String COLUMN_TAG_NAME = "name";                                //1

    // Strings for table user group
    public static final String TABLE_USER_GROUP = "usergroup";
    public static final String COLUMN_GROUP_ID = "groupId";                             //0
    public static final String COLUMN_GROUP_NAME = "name";                              //1
    public static final String COLUMN_GROUP_DESCRIPTION = "description";                //2

    // Strings for table rating
    public static final String TABLE_RATING = "rating";
    public static final String COLUMN_RATING_ID = "ratingId";                           //0
    public static final String COLUMN_RATING_TYPE = "ratingType";                       //1
    public static final String COLUMN_RATING_USER_ID = "userId";                        //2
    public static final String COLUMN_RATING_MODIFIER = "ratingModifier";               //3
    public static final String COLUMN_RATING_ANSWER_ID = "answerId";                    //4
    public static final String COLUMN_RATING_FLASHCARD_ID = "flashcardId";              //5

    // Strings for table auth_token
    public static final String TABLE_AUTH_TOKEN = "authtoken";
    public static final String COLUMN_AUTH_TOKEN_ID = "tokenId";                        //0
    public static final String COLUMN_AUTH_TOKEN_USER_ID = "userId";                    //1
    public static final String COLUMN_AUTH_TOKEN_TOKEN = "token";                       //2
    public static final String COLUMN_AUTH_TOKEN_CREATED = "created";                   //3

    // Strings for table auth_token
    public static final String TABLE_CARD_DECK = "carddeck";
    public static final String COLUMN_CARD_DECK_ID = "cardDeckId";                      //0
    public static final String COLUMN_CARD_DECK_NAME = "cardDeckName";                  //1
    public static final String COLUMN_CARD_DECK_DESCRIPTION = "description";            //2


    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "categoryId";                       //0
    public static final String COLUMN_CATEGORY_NAME = "categoryName";                   //1
    public static final String COLUMN_CATEGORY_PARENT = "parentId";                     //2

    // Database name and version - increase when existing table is altered
    private static final String DATABASE_NAME = "flashcardsDb.db";
    private static final int DATABASE_VERSION = 2; // @TODO revert version before first release

    /**
     * Database creation sql statement for table user
     *
     */
    private static final String USER_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_USER_ID
            + " integer primary key, "
            + COLUMN_USER_NAME
            + " text not null, "
            + COLUMN_USER_AVATAR
            + " blob, "
            + COLUMN_USER_PASSWORD
            + " text, "
            + COLUMN_USER_EMAIL
            + " text not null, "
            + COLUMN_USER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_USER_GROUP_ID
            + " integer, "
            + COLUMN_USER_CREATED
            + " text, " // TODO should be not null
            + COLUMN_USER_LAST_LOGIN
            + " text, "  // TODO should be not null
            + COLUMN_USER_LOCAL_ACCOUNT
            + " integer default 0"
            + ");";

    private static final String FLASHCARD_CREATE = "create table "
            + TABLE_FLASHCARD + "("
            + COLUMN_FLASHCARD_ID
            + " integer primary key not null, "
            + COLUMN_FLASHCARD_CARDDECK_ID
            + " integer not null, "
            + COLUMN_FLASHCARD_RATING
            + " integer DEFAULT 0, "
            + COLUMN_FLASHCARD_QUESTION_ID
            + " integer, "
            + COLUMN_FLASHCARD_MULTIPLE_CHOICE
            + " integer DEFAULT 0, "
            + COLUMN_FLASHCARD_CREATED
            + " text, "  // TODO should be not null
            + COLUMN_FLASHCARD_LAST_UPDATED
            + " text, "  // TODO should be not null
            + COLUMN_FLASHCARD_USER_ID
            + " integer " // TODO maybe should be not null ??
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
            + " text, "  // TODO should be not null
            + COLUMN_ANSWER_LAST_UPDATED
            + " text "  // TODO should be not null
            + ");";

    private static final String CARD_TAG_CREATE = "create table "
            + TABLE_CARD_TAG + "("
            + COLUMN_CARD_TAG_FLASHCARD_ID
            + " integer not null, "
            + COLUMN_CARD_TAG_TAG_ID
            + " integer not null "
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
            + " integer primary key, "
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
            + " integer primary key, "
            + COLUMN_AUTH_TOKEN_USER_ID
            + " integer not null, "
            + COLUMN_AUTH_TOKEN_TOKEN
            + " text not null, "
            + COLUMN_AUTH_TOKEN_CREATED
            + " text not null "
            + ");";

    private static final String CARD_DECK_CREATE = "create table "
            + TABLE_CARD_DECK + "("
            + COLUMN_CARD_DECK_ID
            + " integer primary key, "
            + COLUMN_CARD_DECK_NAME
            + " text not null, "
            + COLUMN_CARD_DECK_DESCRIPTION
            + " text "
            + ");";

    private static final String CATEGORY_CREATE = "create table "
            + TABLE_CATEGORY + "("
            + COLUMN_CATEGORY_ID
            + " integer primary key, "
            + COLUMN_CATEGORY_NAME
            + " text not null, "
            + COLUMN_CATEGORY_PARENT
            + " integer "
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
        db.execSQL(CARD_DECK_CREATE);
        db.execSQL(CATEGORY_CREATE);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_DECK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        onCreate(db);
    }
}

