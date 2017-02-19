package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Strings for table user
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "userId";                               //0
    public static final String COLUMN_USER_NAME = "name";                               //1
    public static final String COLUMN_USER_AVATAR = "avatar";                           //2
    public static final String COLUMN_USER_PASSWORD = "password";                       //3
    public static final String COLUMN_USER_EMAIL = "email";                             //4
    public static final String COLUMN_USER_RATING = "rating";                           //5
    public static final String COLUMN_USER_GROUP = "groupId";                           //6
    public static final String COLUMN_USER_CREATED= "created";                          //7
    public static final String COLUMN_USER_LAST_LOGIN= "lastLogin";                     //8
    public static final String COLUMN_USER_LOCAL_ACCOUNT= "localAccount";               //9
    public static final String COLUMN_USER_IS_LOGGED_IN= "isLoggedIn";                 //10
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

    // Strings for table userGroup JoinTable users
    public static final String TABLE_USER_GROUP_JOIN_TABLE = "userGroupJoinTable";
    public static final String COLUMN_USER_GROUP_JOIN_TABLE_USER_ID = "userId";         //0
    public static final String COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID = "groupId";       //1

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
    public static final String COLUMN_CARD_DECK_VISIBLE = "visible";                    //3
    public static final String COLUMN_CARD_DECK_GROUP = "groupId";                      //4
    public static final String COLUMN_CARD_DECK_PARENT = "parentId";                    //5


    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "categoryId";                       //0
    public static final String COLUMN_CATEGORY_NAME = "categoryName";                   //1
    public static final String COLUMN_CATEGORY_PARENT = "parentId";

    public static final String TABLE_SELECTION = "selection";
    public static final String COLUMN_SELECTION_ID = "selectionId";                       //0
    public static final String COLUMN_SELECTION_USER_ID = "userId";                       //1
    public static final String COLUMN_SELECTION_CARD_DECK_ID = "carddeckId";              //2
    public static final String COLUMN_SELECTION_CARD_ID = "cardId";                       //3
    public static final String COLUMN_SELECTION_DATE = "selectionDate";

    public static final String TABLE_VOTING = "voting";
    public static final String COLUMN_VOTING_ID = "votingId";                             //0
    public static final String COLUMN_VOTING_USER_ID = "userId";                          //1
    public static final String COLUMN_VOTING_CARD_ID = "cardId";                          //2
    public static final String COLUMN_VOTING_ANSWER_ID = "answerId";                      //3
    public static final String COLUMN_VOTING_VALUE = "value";                             //4
    public static final String COLUMN_VOTING_DATE = "votingDate";                         //5
    public static final String COLUMN_VOTING_RATING_ID = "ratingId";                      //6

    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_SETTINGS_ID = "settingsId";                                   //0
    public static final String COLUMN_SETTINGS_USER_ID = "userId";                                  //1
    public static final String COLUMN_SETTINGS_ALLOW_SYNC = "allowSync";                            //2
    public static final String COLUMN_SETTINGS_LEARN_MODE = "learnmode";                            //3
    public static final String COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM = "multiplyChoiceAnswersRandom"; //4
    public static final String COLUMN_SETTINGS_IS_NIGHT_MODE= "isNightMode";                        //5
    public static final String COLUMN_SETTINGS_SHOW_LAST_DRAWER = "showLastDrawer";                 //6
    public static final String COLUMN_SETTINGS_DATE = "changeDate";

    public static final String TABLE_STATISTICS = "statistics";
    public static final String COLUMN_STATISTICS_ID = "statisticsId";                       //0
    public static final String COLUMN_STATISTICS_USER_ID = "userId";                        //1
    public static final String COLUMN_STATISTICS_CARD_ID = "cardId";                        //2
    public static final String COLUMN_STATISTICS_KNOWLEDGE = "knowledge";                   //3
    public static final String COLUMN_STATISTICS_DRAWER = "drawer";                         //4
    public static final String COLUMN_STATISTICS_START_DATE = "startDate";                  //5
    public static final String COLUMN_STATISTICS_END_DATE = "endDate";                      //6

    public static final String TABLE_MESSAGE = "message";
    public static final String COLUMN_MESSAGE_ID = "messageId";                       //0
    public static final String COLUMN_MESSAGE_TYPE = "messageType";                   //1
    public static final String COLUMN_MESSAGE_RECIPIENT = "receipient";           //2
    public static final String COLUMN_MESSAGE_CONTENT= "content";                  //3
    public static final String COLUMN_MESSAGE_DATE_CREATED = "created";            //4
    public static final String COLUMN_MESSAGE_TARGET_DECK = "targetDeck";          //5
    public static final String COLUMN_MESSAGE_SENDER = "sender";                    //6

    public static final String TABLE_CHALLENGE = "challenge";
    public static final String COLUMN_CHALLENGE_ID = "challengeId";                     //0
    public static final String COLUMN_CHALLENGE_MESSAGE_ID = "messageId";               //1
    public static final String COLUMN_CHALLENGE_STATISTIC_ID = "statisticId";           //2

    // Database name and version - increase when existing table is altered
    private static final String DATABASE_NAME = "flashcardsDb.db";
    private static final int DATABASE_VERSION = 27; // @TODO revert version before first release

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
            + COLUMN_USER_GROUP
            + " integer, "
            + COLUMN_USER_CREATED
            + " text, " // TODO should be not null
            + COLUMN_USER_LAST_LOGIN
            + " text, "  // TODO should be not null
            + COLUMN_USER_LOCAL_ACCOUNT
            + " integer default 0, "
            + COLUMN_USER_IS_LOGGED_IN
            + " integer default 0"
            + ");";

    /**
     * Database creation sql statement for table user
     *
    private static final String USER_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_USER_ID
            + " integer primary key, "
            + COLUMN_USER_NAME
            + " text not null, "
            + COLUMN_USER_AVATAR
            + " blob, "
            + COLUMN_USER_PASSWORD
            + " text default null,"
            + COLUMN_USER_EMAIL
            + " text not null, "
            + COLUMN_USER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_USER_GROUP
            + " integer default null, "
            + COLUMN_USER_CREATED
            + " text DEFAULT CURRENT_TIMESTAMP, " // TODO should be not null
            + COLUMN_USER_LAST_LOGIN
            + " text DEFAULT CURRENT_TIMESTAMP, "  // TODO should be not null
            + COLUMN_USER_LOCAL_ACCOUNT
            + " integer default 0, "
            + COLUMN_USER_IS_LOGGED_IN
            + " integer default 0"
            + ");";
    */

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
            + " integer, "  // TODO should be not null
            + COLUMN_FLASHCARD_LAST_UPDATED
            + " integer, "  // TODO should be not null
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
            + " integer, "  // TODO should be not null
            + COLUMN_ANSWER_LAST_UPDATED
            + " interger "  // TODO should be not null
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


    /**
     * Creates the table for selection users of groups
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-01
     */
    private static final String USER_GROUP_JOIN_TABLE_CREATE = "create table "
            + TABLE_USER_GROUP_JOIN_TABLE + "("
            + COLUMN_USER_GROUP_JOIN_TABLE_USER_ID
            + " integer NOT NULL, "
            + COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID
            + " integer NOT NULL "
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
            + " text, "
            + COLUMN_CARD_DECK_VISIBLE
            + " integer, "
            + COLUMN_CARD_DECK_GROUP
            + " integer, "
            + COLUMN_CARD_DECK_PARENT
            + " integer "
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

    private static final String SELECTION_CREATE = "create table "
            + TABLE_SELECTION + "("
            + COLUMN_SELECTION_ID
            + " integer primary key AUTOINCREMENT NOT NULL, "
            + COLUMN_SELECTION_USER_ID
            + " integer not null, "
            + COLUMN_SELECTION_CARD_DECK_ID
            + " integer default null, "
            + COLUMN_SELECTION_CARD_ID
            + " integer default null, "
            + COLUMN_SELECTION_DATE
            + " INTEGER DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String VOTING_CREATE = "create table "
            + TABLE_VOTING + "("
            + COLUMN_VOTING_ID
            + " integer primary key AUTOINCREMENT NOT NULL, "
            + COLUMN_VOTING_USER_ID
            + " integer not null, "
            + COLUMN_VOTING_CARD_ID
            + " integer default null, "
            + COLUMN_VOTING_ANSWER_ID
            + " integer default null, "
            + COLUMN_VOTING_VALUE
            + " integer default 0, "
            + COLUMN_VOTING_DATE
            + " LONG DEFAULT CURRENT_TIMESTAMP, "
            + COLUMN_VOTING_RATING_ID
            + " INTEGER DEFAULT null"
            + ");";


    private static final String SETTINGS_CREATE = "create table "
            + TABLE_SETTINGS + "("
            + COLUMN_SETTINGS_ID
            + " integer primary key AUTOINCREMENT NOT NULL, "
            + COLUMN_SETTINGS_USER_ID
            + " integer not null, "
            + COLUMN_SETTINGS_ALLOW_SYNC
            + " integer default 1, "
            + COLUMN_SETTINGS_LEARN_MODE
            + " string default " + Constants.SETTINGS_LEARN_MODE_DRAWER +", "
            + COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM
            + " integer default 0, "
            + COLUMN_SETTINGS_IS_NIGHT_MODE
            + " integer default 0, "
            + COLUMN_SETTINGS_SHOW_LAST_DRAWER
            + " integer default 1, "
            + COLUMN_SETTINGS_DATE
            + " LONG DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String STATISTICS_CREATE = "create table "
            + TABLE_STATISTICS + "("
            + COLUMN_STATISTICS_ID
            + " integer primary key AUTOINCREMENT NOT NULL, "
            + COLUMN_STATISTICS_USER_ID
            + " integer not null, "
            + COLUMN_STATISTICS_CARD_ID
            + " integer not null, "
            + COLUMN_STATISTICS_KNOWLEDGE
            + " float default 0, "
            + COLUMN_STATISTICS_DRAWER
            + " integer default 0, "
            + COLUMN_STATISTICS_START_DATE
            + " time timestamp default (strftime('%s', 'now')),"
            + COLUMN_STATISTICS_END_DATE
            + " time timestamp default (strftime('%s', 'now'))"
            + ");";


    private static final String MESSAGE_CREATE = "create table "
            + TABLE_MESSAGE + "("
            + COLUMN_MESSAGE_ID
            + " integer primary key NOT NULL, "
            + COLUMN_MESSAGE_TYPE
            + " text not null, "
            + COLUMN_MESSAGE_RECIPIENT
            + " integer not null, "
            + COLUMN_MESSAGE_CONTENT
            + " text, "
            + COLUMN_MESSAGE_DATE_CREATED
            + " time timestamp default (strftime('%s', 'now')),"
            + COLUMN_MESSAGE_TARGET_DECK
            + " integer not null, "
            + COLUMN_MESSAGE_SENDER
            + " integer "
            + ");";

    private static final String CHALLENGE_CREATE = "create table "
            + TABLE_CHALLENGE + "("
            + COLUMN_CHALLENGE_ID
            + " integer primary key NOT NULL, "
            + COLUMN_CHALLENGE_MESSAGE_ID
            + " integer NOT NULL , "
            + COLUMN_CHALLENGE_STATISTIC_ID
            + " integer NOT NULL, "
            + " FOREIGN KEY(" + COLUMN_CHALLENGE_STATISTIC_ID + ") REFERENCES "  + TABLE_STATISTICS + "(" + COLUMN_STATISTICS_ID + "), "
            + " FOREIGN KEY(" + COLUMN_CHALLENGE_MESSAGE_ID + ") REFERENCES "  + TABLE_MESSAGE + "(" + COLUMN_MESSAGE_ID + ") "
            + ");";

    private static final String VOTING_CREATE_UNIQUE_INDEX_1 =
            "CREATE UNIQUE INDEX " + TABLE_VOTING + "_user_card "
                    + "ON " + TABLE_VOTING + "(" + COLUMN_VOTING_USER_ID + ", " + COLUMN_VOTING_CARD_ID + "); ";

    private static final String VOTING_CREATE_UNIQUE_INDEX_2 =
            "CREATE UNIQUE INDEX " + TABLE_VOTING + "_user_answer "
                    + "ON " + TABLE_VOTING + "(" + COLUMN_VOTING_USER_ID + ", " + COLUMN_VOTING_ANSWER_ID + ");";

    private static final String USER_GROUP_JOIN_TABLE_CREATE_UNIQUE_INDEX =
            "CREATE UNIQUE INDEX " + TABLE_USER_GROUP_JOIN_TABLE + "_userId_groupId "
                    + "ON " + TABLE_USER_GROUP_JOIN_TABLE + "(" + COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID + ", " + COLUMN_USER_GROUP_JOIN_TABLE_USER_ID + ");";


    /**
     * Constructor
     *
     * @param context
     */
    public DbHelper(Context context) {
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
        db.execSQL(USER_GROUP_JOIN_TABLE_CREATE);
        db.execSQL(RATING_CREATE);
        db.execSQL(AUTH_TOKEN_CREATE);
        db.execSQL(CARD_DECK_CREATE);
        db.execSQL(CATEGORY_CREATE);
        db.execSQL(SELECTION_CREATE);
        db.execSQL(SETTINGS_CREATE);
        db.execSQL(VOTING_CREATE);
        db.execSQL(STATISTICS_CREATE);
        db.execSQL(MESSAGE_CREATE);
        db.execSQL(CHALLENGE_CREATE);
        db.execSQL(VOTING_CREATE_UNIQUE_INDEX_1);
        db.execSQL(VOTING_CREATE_UNIQUE_INDEX_2);
        db.execSQL(USER_GROUP_JOIN_TABLE_CREATE_UNIQUE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP_JOIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH_TOKEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_DECK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGE);

        onCreate(db);
    }

    /**
     * All Columns of an user
     */
    public static String[] allUserColumns = {
            COLUMN_USER_ID,                  //0
            COLUMN_USER_AVATAR,              //1
            COLUMN_USER_NAME,                //2
            COLUMN_USER_PASSWORD,            //3
            COLUMN_USER_EMAIL,               //4
            COLUMN_USER_RATING,              //5
            COLUMN_USER_GROUP,               //6
            COLUMN_USER_CREATED,             //7
            COLUMN_USER_LAST_LOGIN,          //8
            COLUMN_USER_LOCAL_ACCOUNT,       //9
            COLUMN_USER_IS_LOGGED_IN         //10
    };

    public static String[] allFlashCardColumns = {
            COLUMN_FLASHCARD_ID,             //0
            COLUMN_FLASHCARD_CARDDECK_ID,    //1
            COLUMN_FLASHCARD_RATING,         //2
            COLUMN_FLASHCARD_QUESTION_ID,    //3
            COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
            COLUMN_FLASHCARD_CREATED,        //5
            COLUMN_FLASHCARD_LAST_UPDATED,   //6
            COLUMN_FLASHCARD_USER_ID         //7
    };

    public static String[] allQuestionColumns = {
            COLUMN_QUESTION_ID,              //0
            COLUMN_QUESTION_TEXT,            //1
            COLUMN_QUESTION_MEDIA_URI,       //2
            COLUMN_QUESTION_AUTHOR_ID,       //3
    };

    public static String[] allAnswerColumns = {
            COLUMN_ANSWER_ID,                //0
            COLUMN_ANSWER_TEXT,              //1
            COLUMN_ANSWER_HINT,              //2
            COLUMN_ANSWER_MEDIA_URI,         //3
            COLUMN_ANSWER_USER_ID,           //4
            COLUMN_ANSWER_PARENT_CARD_ID,    //5
            COLUMN_ANSWER_RATING,            //6
            COLUMN_ANSWER_CORRECT,           //7
            COLUMN_ANSWER_CREATED,           //8
            COLUMN_ANSWER_LAST_UPDATED       //9
    };

    public static String[] allCardTagColumns = {
            COLUMN_CARD_TAG_FLASHCARD_ID,    //0
            COLUMN_CARD_TAG_TAG_ID,          //1
    };

    public static String[] allTagColumns = {
            COLUMN_TAG_ID,                   //0
            COLUMN_TAG_NAME,                 //1
    };

    public static String[] allGroupColumns = {
            COLUMN_GROUP_ID,                 //0
            COLUMN_GROUP_NAME,               //1
            COLUMN_GROUP_DESCRIPTION,        //2
    };

    public static String[] allUserGroupJoinTableColumns = {
            TABLE_USER_GROUP_JOIN_TABLE + "." + COLUMN_USER_GROUP_JOIN_TABLE_USER_ID,   //0
            TABLE_USER_GROUP_JOIN_TABLE + "." + COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID,  //1
    };

    public static String[] allRatingColumns = {
            COLUMN_RATING_ID,                //0
            COLUMN_RATING_TYPE,              //1
            COLUMN_RATING_USER_ID,           //2
            COLUMN_RATING_MODIFIER,          //3
            COLUMN_RATING_FLASHCARD_ID,      //4
            COLUMN_RATING_ANSWER_ID,         //5
    };

    public static String[] allAuthTokenColumns = {
            COLUMN_AUTH_TOKEN_ID,            //1
            COLUMN_AUTH_TOKEN_USER_ID,       //2
            COLUMN_AUTH_TOKEN_TOKEN,         //3
            COLUMN_AUTH_TOKEN_CREATED,       //4
    };

    /*
    private static String[] allCardDeckColumns = {
            COLUMN_CARD_DECK_ID,             //0
            COLUMN_CARD_DECK_NAME,           //1
            COLUMN_CARD_DECK_DESCRIPTION,    //2
    };
    */

    public static String[] allCategoryColumns = {
            COLUMN_CATEGORY_ID,             //0
            COLUMN_CATEGORY_NAME,           //1
            COLUMN_CATEGORY_PARENT,         //2
    };

    public static String[] allUserGroupColumns = {
            COLUMN_GROUP_ID,                 //0
            COLUMN_GROUP_NAME,               //1
            COLUMN_GROUP_DESCRIPTION,        //2
    };

    public static String[] allCardDeckColumns = {
            COLUMN_CARD_DECK_ID,             //0
            COLUMN_CARD_DECK_NAME,           //1
            COLUMN_CARD_DECK_DESCRIPTION,    //2
            COLUMN_CARD_DECK_VISIBLE,        //3
            COLUMN_CARD_DECK_GROUP,          //4
            COLUMN_CARD_DECK_PARENT,         //5
    };

    public static String[] allSelectionColumns = {
            COLUMN_SELECTION_ID,             //0
            COLUMN_SELECTION_USER_ID,        //1
            COLUMN_SELECTION_CARD_DECK_ID,   //2
            COLUMN_SELECTION_CARD_ID,        //3
            COLUMN_SELECTION_DATE            //4
    };

    public static String[] allVotingColumns = {
            COLUMN_VOTING_ID,                //0
            COLUMN_VOTING_USER_ID,           //1
            COLUMN_VOTING_CARD_ID,           //2
            COLUMN_VOTING_ANSWER_ID,         //3
            COLUMN_VOTING_VALUE,             //4
            COLUMN_VOTING_DATE,              //5
            COLUMN_VOTING_RATING_ID          //6
    };


    public static String[] allSettingsColumns = {
            COLUMN_SETTINGS_ID,                                    //0
            COLUMN_SETTINGS_USER_ID,                               //1
            COLUMN_SETTINGS_ALLOW_SYNC,                            //2
            COLUMN_SETTINGS_LEARN_MODE,                            //3
            COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM,  //4
            COLUMN_SETTINGS_IS_NIGHT_MODE,                         //5
            COLUMN_SETTINGS_SHOW_LAST_DRAWER,                      //6
            COLUMN_SETTINGS_DATE                                   //7
    };


    public static String[] allStatisticsColumns = {
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_ID,            //0
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_USER_ID,       //1
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_CARD_ID,       //2
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_KNOWLEDGE,     //3
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_DRAWER,        //4
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_START_DATE,    //5
            TABLE_STATISTICS + "." + COLUMN_STATISTICS_END_DATE       //6
    };


    public static String[] allMessageColumns = {
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_ID,                  //0
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_TYPE,                //1
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_RECIPIENT,          //2
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_CONTENT,             //3
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_DATE_CREATED,        //4
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_TARGET_DECK,          //5
            TABLE_MESSAGE + "." + COLUMN_MESSAGE_SENDER         //5
    };


    public static String[] allChallengeColumns = {
            TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_ID,                    //0
            TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_MESSAGE_ID,            //1
            TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_STATISTIC_ID,          //2
    };
}

