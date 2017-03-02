package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.Question;
import de.uulm.einhoernchen.flashcardsapp.Model.Settings;
import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbManager extends DbHelper{

    private static final boolean DEBUG = false;

    private SQLiteDatabase database;
    private Context context;
    private User loggedInUser;
    private List<BarEntry> entriesForBarChart;
    private List<RadarEntry> entriesForRadarChart;
    private List<PieEntry> entriesForPieChart;
    private List<Challenge> challenges;
    private FlashCard bookmark;


    /**
     * Constructor
     *
     * @param context
     */
    public DbManager(Context context) {
        super(context);

    }


    /**
     * Opens a database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {

        this.database = getWritableDatabase();

        this.loggedInUser = getLoggedInUser();
    }


    /**
     * Closes a database connection
     *
     */
    public void close() {

        close();
         if (DEBUG) Log.d("db", "closed");
    }


    /**
     * Saves a user but without setting password, localUser, and logged in.
     * so this cacn be used to update users
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @param user
     */
    public void saveUser(User user) {

        if (DEBUG) Log.d("save user", user.toString());
        // For updating delete user

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_ID, user.getId());
        values.put(DbHelper.COLUMN_USER_AVATAR, user.getAvatar());
        values.put(DbHelper.COLUMN_USER_NAME, user.getName());
        values.put(DbHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(DbHelper.COLUMN_USER_RATING, user.getRating());
        //@TODO GroupID ??? values.put(DbHelper.COLUMN_USER_GROUP, user.getGroup().getId());
        String created = user.getCreated() != null ? user.getCreated().toString() : "";
        String lastLogin = user.getLastLogin() != null ? user.getLastLogin().toString() : "";
        values.put(DbHelper.COLUMN_USER_CREATED, created); // @TODO check correct date
        values.put(DbHelper.COLUMN_USER_LAST_LOGIN, lastLogin); // @TODO check correct date

        List<Long> localAccountIds = getLocalAccountUserIds();

        if (!localAccountIds.contains(user.getId())) {

            database.insertWithOnConflict(DbHelper.TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } else {

            database.update(DbHelper.TABLE_USER, values, DbHelper.COLUMN_USER_ID + "=" + user.getId(), null);
        }

    }


    /**
     * Gets all local account users ids
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @return
     */
    public List<Long> getLocalAccountUserIds() {

        List<Long> ids = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns, DbHelper.COLUMN_USER_LOCAL_ACCOUNT + "=" + 1, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    long id = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
                    ids.add(id);
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return ids;
    }

    /**
     * Creates a user with all given values
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param user
     */
    public void saveUser(User user, boolean localAccount) {

         if (DEBUG) Log.d("save user", user.toString());
        // For updating delete user

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_ID, user.getId());
        values.put(DbHelper.COLUMN_USER_AVATAR, user.getAvatar());
        values.put(DbHelper.COLUMN_USER_NAME, user.getName());
        values.put(DbHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(DbHelper.COLUMN_USER_RATING, user.getRating());
        //@TODO GroupID ??? values.put(DbHelper.COLUMN_USER_GROUP, user.getGroup().getId());
        String created = user.getCreated() != null ? user.getCreated().toString() : "";
        String lastLogin = user.getLastLogin() != null ? user.getLastLogin().toString() : "";
        values.put(DbHelper.COLUMN_USER_CREATED, created); // @TODO check correct date
        values.put(DbHelper.COLUMN_USER_LAST_LOGIN, lastLogin); // @TODO check correct date

        if (!localAccount) {
            localAccount = isLocalAccount(user.getId());

        } else if(user.getPassword() != null && user.getPassword() != "") {

            values.put(DbHelper.COLUMN_USER_PASSWORD, user.getPassword());
            initStandardSettings(user.getId());
        }

        values.put(DbHelper.COLUMN_USER_LOCAL_ACCOUNT, localAccount);
        values.put(DbHelper.COLUMN_USER_IS_LOGGED_IN, localAccount);


        if (doUpdateUser(user.getId())) {

            database.updateWithOnConflict(DbHelper.TABLE_USER, values, DbHelper.COLUMN_USER_ID + "=" + user.getId() , null, SQLiteDatabase.CONFLICT_REPLACE);

        } else {

            // Executes the query
            Long id = database.insertWithOnConflict(DbHelper.TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (DEBUG) Log.d("local user created", id+" "+ localAccount);
        }

    }


    /**
     * Gets the saved local account User
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @return User | null
     */
    public User getLocalAccountUser() {

        Cursor cursor = database.query(
                DbHelper.TABLE_USER, allUserColumns,
                DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1 + " AND " + DbHelper.COLUMN_USER_IS_LOGGED_IN + " = " + 1,
                null, null, null, null);

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
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @return boolean
     */
    public boolean checkIfLocalAccountUserExists() {

        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns,
                DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1 + " AND " + DbHelper.COLUMN_USER_IS_LOGGED_IN + " = " + 1
                , null, null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }


    /**
     * Get the currently logged in user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @return
     */
    public User getLoggedInUser() {

        /*
        if (this.loggedInUser != null) {

            return this.loggedInUser;
        }
        */

        User user = null;
        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns,
                DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1 + " AND " + DbHelper.COLUMN_USER_IS_LOGGED_IN + " = " + 1
                , null, null, null, null);

        // TODO JOIN TOKEN table

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(2);
                String avatar = cursor.getString(1);
                // no pwd saved
                String email = cursor.getString(4);
                String pwd = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
                int rating = cursor.getInt(5);

                //long groupId = cursor.getLong(5); not needed here
                String created = cursor.getString(7);
                String lastLogin = cursor.getString(8);

                user = new User(id, avatar, name, pwd, email, rating, created, lastLogin);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return user;
    }


    /**
     * Gets all cards with the given CardDeckId
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param parentId
     * @return
     */
    public List<FlashCard> getFlashCards(long parentId) {
        List<FlashCard> flashCards = new ArrayList<FlashCard>();

        String[] selection =  {
            COLUMN_FLASHCARD_ID,             //0
            COLUMN_FLASHCARD_CARDDECK_ID,    //1
            COLUMN_FLASHCARD_RATING,         //2
            COLUMN_FLASHCARD_QUESTION_ID,    //3
            COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
            COLUMN_FLASHCARD_CREATED,        //5
            COLUMN_FLASHCARD_LAST_UPDATED,   //6
            TABLE_FLASHCARD+ "." + COLUMN_FLASHCARD_USER_ID,         //7
            TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_ID,                      //0
            TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_CARD_ID,                 //1
            TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_MARK_DATE               //2
        };

        Cursor cursor = database.query(
                TABLE_FLASHCARD + " LEFT JOIN "  + TABLE_BOOKMARK + " ON "
                        + TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_ID + "=" + COLUMN_BOOKMARK_CARD_ID,
                selection,
                DbHelper.COLUMN_FLASHCARD_CARDDECK_ID + " = " + parentId,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(0);
                long carddeckId = cursor.getLong(1);
                int rating = cursor.getInt(2);
                long questionId = cursor.getLong(3);
                boolean multipleChoice = cursor.getInt(4) > 0;
                long created = cursor.getLong(5);
                long lastUpdated = cursor.getLong(6);
                long userId = cursor.getLong(7);
                User author = getUser(userId);
                List<Tag> tags = getTags(cardId);
                Question question = getQuestion(questionId);
                List<Answer> answers = getAnswers(cardId);
                boolean marked = cursor.isNull(cursor.getColumnIndex(COLUMN_BOOKMARK_ID)) ? false : true;

                FlashCard flashCard = new FlashCard(cardId, tags, rating, new Date(created), new Date(lastUpdated), question, answers, author, multipleChoice, marked);

                flashCards.add(flashCard);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCards;
    }


    /**
     * Gets the card with the given card Id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-12
     *
     * @param flashCardId
     * @return
     */
    public FlashCard getFlashCard(long flashCardId) {

        FlashCard flashCard = null;

        String[] selection =  {
                COLUMN_FLASHCARD_ID,             //0
                COLUMN_FLASHCARD_CARDDECK_ID,    //1
                COLUMN_FLASHCARD_RATING,         //2
                COLUMN_FLASHCARD_QUESTION_ID,    //3
                COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
                COLUMN_FLASHCARD_CREATED,        //5
                COLUMN_FLASHCARD_LAST_UPDATED,   //6
                TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_USER_ID,         //7
                TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_ID,                      //0
                TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_CARD_ID,                 //1
                TABLE_BOOKMARK + "." + COLUMN_BOOKMARK_MARK_DATE               //2
        };

        Cursor cursor = database.query(
                TABLE_FLASHCARD + " LEFT JOIN "  + TABLE_BOOKMARK + " ON "
                        + TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_ID + "=" + COLUMN_BOOKMARK_CARD_ID,
                selection,
                DbHelper.COLUMN_FLASHCARD_ID + " = " + flashCardId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(0);
                long carddeckId = cursor.getLong(1);
                int rating = cursor.getInt(2);
                long questionId = cursor.getLong(3);
                boolean multipleChoice = cursor.getInt(4) > 0;
                long created = cursor.getLong(5);
                long lastUpdated = cursor.getLong(6);
                long userId = cursor.getLong(7);
                User author = getUser(userId);
                List<Tag> tags = getTags(cardId);
                Question question = getQuestion(questionId);
                List<Answer> answers = getAnswers(cardId);
                boolean marked = cursor.isNull(cursor.getColumnIndex(COLUMN_BOOKMARK_ID)) ? false : true;


                flashCard = new FlashCard(cardId, tags, rating, new Date(created), new Date(lastUpdated), question, answers, author, multipleChoice, marked);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCard;
    }

    /**
     * Returns all answers for a given card id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param parentCardId
     * @return
     */
    public List<Answer> getAnswers(long parentCardId) {

        List<Answer> answers = new ArrayList<Answer>();

        Cursor cursor = database.query(DbHelper.TABLE_ANSWER, allAnswerColumns, DbHelper.COLUMN_ANSWER_PARENT_CARD_ID + " = " + parentCardId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long answerId = cursor.getLong(0);
                String answerText = cursor.getString(1);
                String answerHint = cursor.getString(2);
                String mediaURI = cursor.getString(3);
                long userId = cursor.getLong(4);
                // parentCardId do we already have
                int rating = cursor.getInt(6);
                boolean correct = cursor.getInt(7) > 0;
                long created = cursor.getLong(8);
                long lastupdated = cursor.getLong(9);

                User author = getUser(userId);

                answers.add(new Answer(answerId, correct, answerText, answerHint, mediaURI, author, new Date(created), new Date(lastupdated), rating, correct));
            } while (cursor.moveToNext());
        }
        return answers;

    }

    /**
     * Gets the question and dedpendencies by its id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param questionId
     * @return
     */
    public Question getQuestion(long questionId) {

        Question question = null;

        Cursor cursor = database.query(DbHelper.TABLE_QUESTION, allQuestionColumns, DbHelper.COLUMN_QUESTION_ID + " = " + questionId, null, null, null, null);

        if (cursor.moveToFirst()) {

            String questionText = cursor.getString(1);
            String mediaURI = cursor.getString(2);
            long authorId = cursor.getLong(3);


            User author = getUser(authorId);

            question = new Question(questionId, questionText, mediaURI, author);

        }

        return question;
    }

    /**
     * Gets tags of a card
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param cardId
     * @return
     */
    public List<Tag> getTags(long cardId) {

        List<Tag> tags = new ArrayList<Tag>();

        Cursor cursor = database.query(DbHelper.TABLE_CARD_TAG, allCardTagColumns, DbHelper.COLUMN_CARD_TAG_FLASHCARD_ID + " = " + cardId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long flashcardId = cursor.getLong(0);
                long tagId = cursor.getLong(1);

                tags.add(getTag(tagId));
            } while (cursor.moveToNext());
        }

        return tags;
    }

    /**
     * gets a tag by its id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tagId
     * @return
     */
    public Tag getTag(long tagId) {

        Tag tag = null;

        Cursor cursor = database.query(DbHelper.TABLE_TAG, allTagColumns, DbHelper.COLUMN_TAG_ID + " = " + tagId, null, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);

            tag = new Tag(tagId, name);

        }

        return tag;
    }

    /**
     * Gets an user by its id from the db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param userId
     * @return
     */
    public User getUser(long userId) {

        User user = null;

        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns, DbHelper.COLUMN_USER_ID + " = " + userId, null, null, null, null);


        if (cursor.moveToFirst()) {

            do {
            try {
                long id = cursor.getLong(0);
                String name = cursor.getString(2);
                String avatar = cursor.getString(1);
                // no pwd saved
                String email = cursor.getString(4);
                int rating = cursor.getInt(5);

                //long groupId = cursor.getLong(5); not needed here
                String created = cursor.getString(7);
                String lastLogin = cursor.getString(8);

                user = new User(id, avatar, name, email, rating, created, lastLogin);

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
            } while (cursor.moveToNext());

        }

        cursor.close();

        return user;
    }


    /**
     * checks if a user is a local account
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-31
     *
     * @param userId
     * @return
     */
    public boolean isLocalAccount(long userId) {

        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns, DbHelper.COLUMN_USER_ID + " = " + userId, null, null, null, null);

        boolean isLocalAccount = false;

        if (cursor.moveToFirst()) {

            try {

                isLocalAccount = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_USER_LOCAL_ACCOUNT)) > 0;
            } catch (Exception e) {

                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return isLocalAccount;
    }


    /**
     * checks if a user exists in local db - so update him
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-31
     *
     * @param userId
     * @return
     */
    public boolean doUpdateUser(long userId) {

        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns, DbHelper.COLUMN_USER_ID + " = " + userId, null, null, null, null);

        boolean exists = false;

        if (cursor.moveToFirst()) {

            try {

                exists = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_USER_ID)) > 0;
            } catch (Exception e) {

                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return exists;
    }

    /**
     * Saves/updates a flashcards list list with all its dependencies
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param flashCards
     * @param parentId
     */
    public void saveFlashCards(List<FlashCard> flashCards, long parentId) {

        for (FlashCard card : flashCards) {
            saveFlashCard(card, parentId);
        }

    }

    /**
     * Saves or updates a flashcard
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param card
     */
    public void saveFlashCard(FlashCard card, Long parentId) {

        saveUser(card.getAuthor(), false);

        // A card doesn't has to have tags
        if (card.getTags() != null && card.getTags().size() > 0) {
            saveTags(card.getTags(), card.getId());
        }

        saveQuestion(card.getQuestion());
        saveAnswers(card.getAnswers(), card.getId());

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_FLASHCARD_ID, card.getId());
        values.put(DbHelper.COLUMN_FLASHCARD_CARDDECK_ID , parentId);
        values.put(DbHelper.COLUMN_FLASHCARD_RATING , card.getRating());
        values.put(DbHelper.COLUMN_FLASHCARD_QUESTION_ID , card.getQuestion().getId());
        values.put(DbHelper.COLUMN_FLASHCARD_MULTIPLE_CHOICE , card.isMultipleChoice());
        values.put(DbHelper.COLUMN_FLASHCARD_USER_ID , card.getAuthor().getId());

        if (card.getCreated() != null) {
            values.put(DbHelper.COLUMN_FLASHCARD_CREATED, card.getCreated().getTime());
        }

        if (card.getLastUpdatedString() != null) {
            values.put(DbHelper.COLUMN_FLASHCARD_LAST_UPDATED , card.getLastUpdated().getTime());
        }

        database.insertWithOnConflict(TABLE_FLASHCARD, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Takes a list of Categories and saves them
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param categories
     */
    public void saveCategories(List<Category> categories) {

        for (Category category : categories) {
            saveCategory(category);
        }
    }


    /**
     * Tries to insert or update Category
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param category
     */
    public void saveCategory (Category category) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CATEGORY_ID, category.getId());
        values.put(DbHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DbHelper.COLUMN_CATEGORY_PARENT, category.getParentId());
        database.insertWithOnConflict(DbHelper.TABLE_CATEGORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }


    /**
     * saves a carddeck list
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param cardDecks
     * @param parentId
     */
    public void saveCardDecks(List<CardDeck> cardDecks, long parentId) {

        for (CardDeck cardDeck: cardDecks) {
            saveCardDeck(cardDeck, parentId);
        }
    }


    /**
     * Inserts or updates a cardDeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param cardDeck
     * @param parentId
     */
    private void saveCardDeck(CardDeck cardDeck, long parentId) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CARD_DECK_ID, cardDeck.getId());
        values.put(DbHelper.COLUMN_CARD_DECK_NAME, cardDeck.getName());
        values.put(DbHelper.COLUMN_CARD_DECK_DESCRIPTION, cardDeck.getDescription());
        values.put(DbHelper.COLUMN_CARD_DECK_VISIBLE, cardDeck.isVisible());
        values.put(DbHelper.COLUMN_CARD_DECK_PARENT, parentId);

        if (cardDeck.getUserGroup() != null) {

            values.put(DbHelper.COLUMN_CARD_DECK_GROUP, cardDeck.getUserGroup().getId());
            saveUserGroup(cardDeck.getUserGroup());
        }

        database.insertWithOnConflict(DbHelper.TABLE_CARD_DECK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Inserts or updates a usergroup
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param userGroup
     */
    private void saveUserGroup(UserGroup userGroup) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_GROUP_ID, userGroup.getId());
        values.put(DbHelper.COLUMN_GROUP_NAME, userGroup.getName());
        values.put(DbHelper.COLUMN_GROUP_DESCRIPTION, userGroup.getDescription());

        database.insertWithOnConflict(DbHelper.TABLE_USER_GROUP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Gets the categories which are children of parentId
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-02
     *
     * @param parentId
     * @return
     */
    public List<Category> getCategories(Long parentId) {

        List<Category> categories = new ArrayList<Category>();

        Cursor cursor = database.query(DbHelper.TABLE_CATEGORY, allCategoryColumns, DbHelper.COLUMN_CATEGORY_PARENT + " = " + parentId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long categoryId = cursor.getLong(0);
                String categoryName = cursor.getString(1);
                long categoryParentId = cursor.getLong(2);

                categories.add(new Category(categoryId,categoryParentId,categoryName));
            } while (cursor.moveToNext());
        }
        return categories;
    }

    /**
     * Collects all Carddecks by the given parent id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param parentId
     * @return
     */
    public List<CardDeck> getCardDecks(Long parentId) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // base query
        String query = DbHelper.TABLE_CARD_DECK +
                " LEFT JOIN " + DbHelper.TABLE_SELECTION + " ON "
                + DbHelper.TABLE_CARD_DECK + "." + DbHelper.COLUMN_CARD_DECK_ID + " = " +  DbHelper.TABLE_SELECTION + "." + DbHelper.COLUMN_SELECTION_CARD_DECK_ID
                + " AND " + DbHelper.TABLE_SELECTION + "." + DbHelper.COLUMN_SELECTION_USER_ID + " = " + loggedInUser.getId();

        // if null then get all carddecks
        if (parentId != null) {

            query += " WHERE " + DbHelper.COLUMN_CARD_DECK_PARENT + " = " + parentId;
        }

        qb.setTables(query);

        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        List<CardDeck> cardDecks = new ArrayList<CardDeck>();

        if (cursor.moveToFirst()) {
            do {
                long cardDeckId = cursor.getLong(0);
                String cardDeckName = cursor.getString(1);
                String cardDeckDescription = cursor.getString(2);
                boolean cardDeckVisible = cursor.getInt(3) > 0;
                long cardDeckGroupId = cursor.getLong(4);
                long cardDeckParentId = cursor.getLong(5);
                long selectionDate = cursor.getLong(cursor.getColumnIndex(COLUMN_SELECTION_DATE));

                boolean isSelected = selectionDate > 0;

                UserGroup userGroup = getUserGroup(cardDeckGroupId);

                cardDecks.add(new CardDeck(cardDeckId,cardDeckVisible, userGroup,cardDeckName, cardDeckDescription, selectionDate));

            } while (cursor.moveToNext());
        }


        return cardDecks;
    }

    /**
     * Collects a Carddeck by the given deck id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param carddeckId
     * @return
     */
    public CardDeck getCardDeck(Long carddeckId) {


        Cursor cursor = database.query(
                DbHelper.TABLE_CARD_DECK,
                allCardDeckColumns,
                COLUMN_CARD_DECK_ID  + "=" + carddeckId,
                null, null, null, null);

        CardDeck cardDeck = null;

        if (cursor.moveToFirst()) {

            long cardDeckId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_DECK_ID));
            String cardDeckName = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_DECK_NAME));
            String cardDeckDescription = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_DECK_DESCRIPTION));
            boolean cardDeckVisible = cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_DECK_VISIBLE)) > 0;
            long cardDeckGroupId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_DECK_GROUP));
            long cardDeckParentId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_DECK_PARENT));

            UserGroup userGroup = getUserGroup(cardDeckGroupId);

            cardDeck = new CardDeck(cardDeckId, cardDeckVisible, userGroup,cardDeckName, cardDeckDescription, 0);

        }

        return cardDeck;
    }


    /**
     * Gets a user group from db for a given carddeck id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param cardDeckGroupId
     * @return
     */
    public UserGroup getUserGroup(long cardDeckGroupId) {

        UserGroup userGroup = null;

        Cursor cursor = database.query(DbHelper.TABLE_USER_GROUP, allUserGroupColumns, DbHelper.COLUMN_GROUP_ID + " = " + cardDeckGroupId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long userGroupId = cursor.getLong(0);
                String userGroupName = cursor.getString(1);
                String userGroupDecscription = cursor.getString(2);
                userGroup = new UserGroup(userGroupId, userGroupName, userGroupDecscription);
            } while (cursor.moveToNext());
        }

        return userGroup == null ? new UserGroup() : userGroup;

    }


    /**
     * Gets a list of user groups from db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @return
     */
    public List<UserGroup> getUserGroups() {

        List<UserGroup> userGroups = new ArrayList<UserGroup>();

        // TODO join to user where id ... userjoin table
        Cursor cursor = database.query(DbHelper.TABLE_USER_GROUP, allUserGroupColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long userGroupId = cursor.getLong(0);
                String userGroupName = cursor.getString(1);
                String userGroupDecscription = cursor.getString(2);
                userGroups.add(new UserGroup(userGroupId, userGroupName, userGroupDecscription));
            } while (cursor.moveToNext());
        }

        return userGroups;

    }


    /**
     * Saves a list of answers and its dependencies
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param answers
     * @param cardId
     */
    public void saveAnswers(List<Answer> answers, long cardId) {

        for (Answer answer : answers) {

            saveAnswer(answer, cardId);
        }
    }

    /**
     * Saves an answers and its dependencies
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param answer
     */
    public void saveAnswer(Answer answer, long cardId) {

        saveUser(answer.getAuthor(), false);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ANSWER_ID, answer.getId());
        values.put(DbHelper.COLUMN_ANSWER_TEXT, answer.getAnswerText());
        values.put(DbHelper.COLUMN_ANSWER_HINT, answer.getHintText());
        values.put(DbHelper.COLUMN_ANSWER_MEDIA_URI, answer.getUri() != null ? answer.getUri() : null);
        values.put(DbHelper.COLUMN_ANSWER_USER_ID, answer.getAuthor().getId());
        values.put(DbHelper.COLUMN_ANSWER_PARENT_CARD_ID, cardId);
        values.put(DbHelper.COLUMN_ANSWER_RATING, answer.getRating());
        values.put(DbHelper.COLUMN_ANSWER_CORRECT, answer.isCorrect());

        if (answer.getCreated() != null) {
            values.put(DbHelper.COLUMN_ANSWER_CREATED, answer.getCreated().getTime()); // TODO make time int val
        }

        if (answer.getLastUpdated() != null) {
            values.put(DbHelper.COLUMN_ANSWER_LAST_UPDATED, answer.getLastUpdated().getTime());  // TODO make time int val
        }

        database.insertWithOnConflict(DbHelper.TABLE_ANSWER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Saves a question to the db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param question
     */
    private void saveQuestion(Question question) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_QUESTION_ID, question.getId());
        values.put(DbHelper.COLUMN_QUESTION_TEXT, question.getQuestionText());
        values.put(DbHelper.COLUMN_QUESTION_MEDIA_URI, question.getUri() != null ? question.getUri().toString() : null);
        values.put(DbHelper.COLUMN_QUESTION_AUTHOR_ID, question.getAuthor().getId());

        database.insertWithOnConflict(DbHelper.TABLE_QUESTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Saves tags
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tags
     */
    public void saveTags(List<Tag> tags, long cardId) {
        for (Tag tag : tags) {
            saveTag(tag, cardId);
        }
    }

    /**
     * Saves a tag
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tag
     */
    public void saveTag(Tag tag, long cardId) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TAG_ID, tag.getId());
        values.put(DbHelper.COLUMN_TAG_NAME, tag.getName());

        // Executes the query
        database.insertWithOnConflict(DbHelper.TABLE_TAG, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        saveCardTag(tag, cardId);

    }


    /**
     * Saves or Updates a voting of a card
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param cardId
     * @param value
     */
    public boolean saveCardVoting(long cardId, int value) {

        int voting = getCardVoting(cardId);
        int rating;

        if (value == voting) {
            return false;
        } else if (value == -1 && voting == 1) {
            rating = -2;
        } else if (value == 1 && voting == -1) {
            rating = +2;
        } else {
            rating = value;
        }

        //updateCardRating(cardId, rating);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_VOTING_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_VOTING_CARD_ID, cardId);
        values.put(DbHelper.COLUMN_VOTING_VALUE, value);
        values.put(DbHelper.COLUMN_VOTING_DATE,  System.currentTimeMillis());

        if (voting != 0) {
            database.updateWithOnConflict(DbHelper.TABLE_VOTING, values, DbHelper.COLUMN_VOTING_USER_ID + "=" + loggedInUser.getId() + " AND " + DbHelper.COLUMN_VOTING_CARD_ID + "=" + cardId, null, SQLiteDatabase.CONFLICT_REPLACE);
        } else {

            // Executes the query
            database.insertWithOnConflict(DbHelper.TABLE_VOTING, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        return true;
    }

    /**
     * Locally updates the cardrating
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param cardId
     * @param rating
     */
    private void updateCardRating(long cardId, int rating) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_FLASHCARD_RATING, + rating);

        // Executes the query
        database.updateWithOnConflict(TABLE_FLASHCARD, values, DbHelper.COLUMN_FLASHCARD_ID + "=" + cardId, null, SQLiteDatabase.CONFLICT_REPLACE);

    }


    /**
     * Locally updates the answer rating
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param answerId
     * @param rating
     */
    private void updateAnswerRating(long answerId, int rating) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ANSWER_RATING, + rating);

        // Executes the query
        database.updateWithOnConflict(DbHelper.TABLE_ANSWER, values, DbHelper.COLUMN_ANSWER_ID + "=" + answerId, null, SQLiteDatabase.CONFLICT_REPLACE);

    }


    /**
     * Saves or Updates a voting of an answer
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param answerId
     * @param value
     */
    public boolean saveAnswerVoting(long answerId, int value) {

        int voting = getAnswerVoting(answerId);
        int rating;

        if (value == voting) {
            return false;
        } else if (value == -1 && voting == 1) {
            rating = -2;
        } else if (value == 1 && voting == -1) {
            rating = +2;
        } else {
            rating = value;
        }

        //updateAnswerRating(answerId, rating);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_VOTING_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_VOTING_ANSWER_ID, answerId);
        values.put(DbHelper.COLUMN_VOTING_VALUE, value);
        values.put(DbHelper.COLUMN_VOTING_DATE, System.currentTimeMillis());


        if (voting != 0) {
            database.updateWithOnConflict(DbHelper.TABLE_VOTING, values, DbHelper.COLUMN_VOTING_USER_ID + "=" + loggedInUser.getId() + " AND " + DbHelper.COLUMN_VOTING_ANSWER_ID + "=" + answerId, null, SQLiteDatabase.CONFLICT_REPLACE);
        } else {

            // Executes the query
            database.insertWithOnConflict(DbHelper.TABLE_VOTING, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        return true;
    }


    /**
     * Finally deletes a tag
     * don't use for update purpose
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tag
     */
    private void deleteTag(Tag tag) {
        database.delete(DbHelper.TABLE_CARD_TAG, DbHelper.COLUMN_CARD_TAG_TAG_ID + "=" + tag.getId(), null);
        database.delete(DbHelper.TABLE_TAG, DbHelper.COLUMN_TAG_ID + "=" + tag.getId(), null);
    }


    /**
     * Saves or updates a tag
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tag
     * @param cardId
     */
    public void saveCardTag(Tag tag, long cardId) {

        deleteCardTag(tag.getId(), cardId);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CARD_TAG_TAG_ID, tag.getId());
        values.put(DbHelper.COLUMN_CARD_TAG_FLASHCARD_ID, cardId);

        // Executes the query
        database.insert(DbHelper.TABLE_CARD_TAG, null, values);
    }

    /**
     * deletes a card tag link
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tagId
     * @param cardId
     */
    private void deleteCardTag(long tagId, long cardId) {
        database.delete(DbHelper.TABLE_CARD_TAG, DbHelper.COLUMN_CARD_TAG_TAG_ID + "=" + tagId
                + " AND " + DbHelper.COLUMN_CARD_TAG_FLASHCARD_ID + "=" + cardId, null);
    }


    /**
     * Returns the date when a carddeck was selected
     * if 0 then carddeck is not selected
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param carddeckID
     * @return
     */
    public long getCarddeckSelectionDate(long carddeckID) {

        Cursor cursor = database.query(DbHelper.TABLE_SELECTION, allSelectionColumns, DbHelper.COLUMN_SELECTION_CARD_DECK_ID + " = " + carddeckID
                + " AND " + DbHelper.COLUMN_SELECTION_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        long selectionDate = 0;

        if (cursor.moveToFirst()) {
            do {

                selectionDate = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_SELECTION_DATE));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return selectionDate;
    }


    /**
     * Deselects a carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param carddeckID
     */
    public void deselectCarddeck(long carddeckID) {
        database.delete(DbHelper.TABLE_SELECTION, DbHelper.COLUMN_SELECTION_CARD_DECK_ID + "=" + carddeckID
                + " AND " + DbHelper.COLUMN_SELECTION_USER_ID + "=" + loggedInUser.getId(), null);

        List<FlashCard> flashCards = getFlashCards(carddeckID);

        for (FlashCard card: flashCards) {

            deselectCard(card.getId(), carddeckID);
        }
    }


    /**
     * Deselects a card
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param cardID
     */
    public void deselectCard(long cardID, long parentID) {

        database.delete(DbHelper.TABLE_SELECTION, COLUMN_SELECTION_CARD_ID + "=" + cardID
                + " AND " + DbHelper.COLUMN_SELECTION_USER_ID + "=" + loggedInUser.getId(), null);

        //deselectCarddeck(parentID);
    }


    /**
     * Selects a carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param carddeckID
     */
    public void selectCarddeck(long carddeckID) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_SELECTION_CARD_DECK_ID, carddeckID);
        values.put(DbHelper.COLUMN_SELECTION_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_SELECTION_DATE,  System.currentTimeMillis());

        // Executes the query
        database.insert(DbHelper.TABLE_SELECTION, null, values);

        List<FlashCard> flashCards = getFlashCards(carddeckID);

        for (FlashCard card: flashCards) {

            selectCard(card.getId(), carddeckID);
        }
    }


    /**
     * Selects a card
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param cardID
     */
    public void selectCard(long cardID, long parentID) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_SELECTION_CARD_ID, cardID);
        values.put(DbHelper.COLUMN_SELECTION_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_SELECTION_DATE,  System.currentTimeMillis());

        // Executes the query
        database.insert(DbHelper.TABLE_SELECTION, null, values);

        //selectCarddeck(parentID);
    }


    /**
     * Returns the timestamp when a card was selected if 0 then card wasnt selected
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     * @param cardID
     * @return
     */
    public long getCardSelectionDate(long cardID) {

        Cursor cursor = database.query(DbHelper.TABLE_SELECTION, allSelectionColumns, COLUMN_SELECTION_CARD_ID + " = " + cardID
                        + " AND " + DbHelper.COLUMN_SELECTION_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        long selectionDate = 0;

        if (cursor.moveToFirst()) {
            do {

                selectionDate = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_SELECTION_DATE));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return selectionDate;

    }


    /**
     * Returns the voting of a card
     * if it returns 0 then the logged in user hasn't voted already
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param cardID
     * @return
     */
    public int getCardVoting(long cardID) {

        Cursor cursor = database.query(DbHelper.TABLE_VOTING, allVotingColumns, DbHelper.COLUMN_VOTING_CARD_ID + " = " + cardID
                        + " AND " + DbHelper.COLUMN_VOTING_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        int value = 0;

        if (cursor.moveToFirst()) {
            do {

                value = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_VALUE));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return value;

    }


    /**
     * Returns the votings rating id of a card
     * if it returns null then the logged in user hasn't voted already
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param cardID
     * @return
     */
    public Long getCardVotingRatingId(long cardID) {

        Cursor cursor = database.query(DbHelper.TABLE_VOTING, allVotingColumns, DbHelper.COLUMN_VOTING_CARD_ID + " = " + cardID
                        + " AND " + DbHelper.COLUMN_VOTING_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        Long value = null;

        if (cursor.moveToFirst()) {

            if (!cursor.isNull(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_RATING_ID))) {

                value = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_RATING_ID));
            }

        }

        cursor.close();

        return value;

    }


    /**
     * Returns the voting of an answer
     * if it returns 0 then the logged in user hasn't voted already
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     *
     * @param answerId
     * @return
     */
    public int getAnswerVoting(long answerId) {

        Cursor cursor = database.query(DbHelper.TABLE_VOTING, allVotingColumns, DbHelper.COLUMN_VOTING_ANSWER_ID + " = " + answerId
                        + " AND " + DbHelper.COLUMN_VOTING_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        int value = 0;

        if (cursor.moveToFirst()) {
            do {

                value = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_VALUE));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return value;

    }


    /**
     * Returns the votings rating id of an answer
     * if it returns null then the logged in user hasn't voted already
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param answerId
     * @return
     */
    public Long getAnswerVotingRatingId(long answerId) {

        Cursor cursor = database.query(DbHelper.TABLE_VOTING, allVotingColumns, DbHelper.COLUMN_VOTING_ANSWER_ID + " = " + answerId
                        + " AND " + DbHelper.COLUMN_VOTING_USER_ID + " = " + loggedInUser.getId()
                , null, null, null, null);

        Long value = null;

        if (cursor.moveToFirst()) {

            if (!cursor.isNull(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_RATING_ID))) {

                value = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_VOTING_RATING_ID));
            }
        }

        cursor.close();

        return value;

    }

    public void selectCard(long cardID) {
        selectCard(cardID, getCardParentID(cardID));
    }

    public void deselectCard(long cardID) {
        deselectCard(cardID, getCardParentID(cardID));
    }

    public long getCardParentID(long cardID) {

        Cursor cursor = database.query(TABLE_FLASHCARD, allFlashCardColumns, DbHelper.COLUMN_FLASHCARD_ID + " = " + cardID
                , null, null, null, null);

        long parentId = -1;

        if (cursor.moveToFirst()) {
            do {

                parentId = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_FLASHCARD_CARDDECK_ID));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return parentId;
    }


    /**
     * Logs out user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-05
     *
     */
    public void logoutUser() {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_IS_LOGGED_IN, 0);

        invalidateToken();

        database.update(DbHelper.TABLE_USER, values, DbHelper.COLUMN_USER_ID + " = " + loggedInUser.getId(), null);


        // reset the class var
        this.loggedInUser = null;
    }


    /**
     * tries to login user
     * sets the class var of the currently logged i user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param mEmail
     * @param mUserName
     * @param mPassword
     * @return
     */
    public boolean loginUser(String mEmail, String mUserName, String mPassword) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_IS_LOGGED_IN, 1);

        int affectedRows = database.update(DbHelper.TABLE_USER, values,
                DbHelper.COLUMN_USER_NAME + " = '" + mUserName
                + "' AND " + DbHelper.COLUMN_USER_EMAIL + " = '" + mEmail
                + "' AND " + DbHelper.COLUMN_USER_PASSWORD + " = '" + mPassword
                + "' AND " + DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1
                , null);

        // Check if it was successful
        if (affectedRows > 0) {

            this.loggedInUser = getLoggedInUser();

            return true;
        }

        return false;
    }


    /**
     * Updates a card voting to set the remote rating id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param ratingId
     * @param cardId
     */
    public void addRatingIdToCardVoting(Long ratingId, long cardId) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_VOTING_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_VOTING_CARD_ID, cardId);
        values.put(DbHelper.COLUMN_VOTING_RATING_ID, ratingId);

        database.updateWithOnConflict(DbHelper.TABLE_VOTING, values, DbHelper.COLUMN_VOTING_USER_ID + "=" + loggedInUser.getId() + " AND " + DbHelper.COLUMN_VOTING_CARD_ID + "=" + cardId, null, SQLiteDatabase.CONFLICT_ABORT);

    }


    /**
     * Updates an answer voting to set the remote rating id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param ratingId
     * @param answerId
     */
    public void addRatingIdToAnswerVoting(Long ratingId, long answerId) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_VOTING_USER_ID, loggedInUser.getId());
        values.put(DbHelper.COLUMN_VOTING_ANSWER_ID, answerId);
        values.put(DbHelper.COLUMN_VOTING_RATING_ID, ratingId);

        database.updateWithOnConflict(DbHelper.TABLE_VOTING, values, DbHelper.COLUMN_VOTING_USER_ID + "=" + loggedInUser.getId() + " AND " + DbHelper.COLUMN_VOTING_ANSWER_ID + "=" + answerId, null, SQLiteDatabase.CONFLICT_ABORT);

    }

    /**
     * Returns the name of category
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-0108
     *
     * @param id
     * @return
     */
    public String getCategoryNameById(long id) {

        Cursor cursor = database.query(DbHelper.TABLE_CATEGORY, allCategoryColumns, DbHelper.COLUMN_CATEGORY_ID + " = " + id, null, null, null, null);

        String categoryName = "";

        if (cursor.moveToFirst()) {

            categoryName = cursor.getString(1);
        }

        return categoryName;
    }


    /**
     * Gets all selected flashcards for the logged in user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     * @return
     */
    public List<FlashCard> getSelectedFlashcards() {
        List<FlashCard> flashCards = new ArrayList<FlashCard>();


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_FLASHCARD +
                " JOIN " + TABLE_SELECTION + " ON "
                + TABLE_SELECTION + "." + COLUMN_SELECTION_CARD_ID + "=" + TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_ID
                + " JOIN " + TABLE_USER + " ON "
                + TABLE_SELECTION + "." + COLUMN_SELECTION_USER_ID + "=" + TABLE_USER + "." + COLUMN_USER_ID
                );

        qb.appendWhere(COLUMN_USER_IS_LOGGED_IN + "=" + 1 + " AND " + COLUMN_SELECTION_DATE + "NOT NULL");
        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_ID));
                long carddeckId = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_CARDDECK_ID));
                int rating = cursor.getInt(cursor.getColumnIndex(COLUMN_FLASHCARD_RATING));
                long questionId = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_QUESTION_ID));
                boolean multipleChoice = cursor.getInt(cursor.getColumnIndex(COLUMN_FLASHCARD_MULTIPLE_CHOICE)) > 0;
                long created = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_CREATED));
                long lastUpdated = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_LAST_UPDATED));
                long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_USER_ID));
                User author = getUser(userId);
                List<Tag> tags = getTags(cardId);
                Question question = getQuestion(questionId);
                List<Answer> answers = getAnswers(cardId);

                FlashCard flashCard = new FlashCard(cardId, tags, rating, new Date(created), new Date(lastUpdated), question, answers, author, multipleChoice);

                flashCards.add(flashCard);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCards;
    }


    /**
     * Gets all selected flashcard ids for the logged in user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     * @return
     */
    public List<Long> getSelectedFlashcardIDs() {

        Settings settings = Settings.getSettings();
        Constants learnMode = settings.getLearnMode();

        String orderBy = null;

        switch (learnMode) {

            case SETTINGS_LEARN_MODE_KNOWLEDGE:

                orderBy = " ORDER BY " + TABLE_STATISTICS  + "." +
                        COLUMN_STATISTICS_KNOWLEDGE + " ASC,  max(statistics.endDate) ASC";
                // Order by knowledge ASC
                break;

            case SETTINGS_LEARN_MODE_DATE:

                // ORDER by dateEnd ASC
                orderBy = " ORDER BY " + TABLE_STATISTICS  + "." +
                        COLUMN_STATISTICS_END_DATE + " ASC";
                break;

            case SETTINGS_LEARN_MODE_RANDOM:

                // ORDER Random ??
                break;

            case SETTINGS_LEARN_MODE_DRAWER:

                // Order by drawer ASC

                orderBy = " ORDER BY " + TABLE_STATISTICS + "." +
                        COLUMN_STATISTICS_DRAWER + " ASC,  max(statistics.endDate) ASC";
                break;

        }

        List<Long> ids = new ArrayList<Long>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        /*
        qb.setTables(
                TABLE_SELECTION
                + " JOIN " + TABLE_USER + " ON "
                + TABLE_SELECTION + "." + COLUMN_SELECTION_USER_ID
                        + "=" + TABLE_USER + "." + COLUMN_USER_ID
                + " left JOIN " + TABLE_STATISTICS + " ON "
                + TABLE_STATISTICS+ "." + COLUMN_STATISTICS_CARD_ID
                        + "=" + TABLE_SELECTION + "." + COLUMN_SELECTION_CARD_ID
                );

        qb.appendWhere(COLUMN_USER_IS_LOGGED_IN + "=" + 1
                + " AND " + COLUMN_SELECTION_DATE + " NOT NULL"
                + " AND " + COLUMN_SELECTION_CARD_ID + " NOT NULL " + "ORDER BY " + orderBy);

        Cursor cursor = qb.query(database, null, null, null, null, null, null);
        */

        Cursor cursor = database.rawQuery("SELECT selection.cardId, * FROM selection\n" +
                "    LEFT JOIN statistics ON selection.cardId = statistics.cardId\n" +
                "    JOIN user ON selection.userId = user.userId\n" +
                "    WHERE selection.cardId NOT NULL\n" +
                "        AND user.isLoggedIn = 1\n" +
                "     GROUP BY (selection.cardId)\n" +
                "    " + orderBy, null);


        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_SELECTION_CARD_ID));

                ids.add(cardId);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return ids;
    }


    /**
     * Checks if an answer is of type Multiple choice
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     * @param id
     * @return
     */
    public boolean isAnswerOfTypeMultiplyChoice(long id) {

        boolean isMultipleChoice = false;


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(
                TABLE_FLASHCARD
                        + " JOIN " + TABLE_ANSWER + " ON "
                        + TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_ID + "=" + TABLE_ANSWER + "." + COLUMN_ANSWER_PARENT_CARD_ID
        );

        qb.appendWhere(COLUMN_ANSWER_ID+ "=" + id);

        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            isMultipleChoice = cursor.getLong(cursor.getColumnIndex(COLUMN_FLASHCARD_MULTIPLE_CHOICE)) > 0;

        }
        cursor.close();

        return isMultipleChoice;
    }


    /**
     * inits the settings for a user
     * do this after first creating the new user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param userId
     */
    public void initStandardSettings(long userId) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_SETTINGS_USER_ID, userId);

        database.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Get the writable instance of the Database
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @return
     */
    public SQLiteDatabase getSQLiteDatabase() {
        return database;
    }


    /**
     * Gets a bar chart data
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     * @return
     */
    public List<Entry> getEntriesForLineChart() {

        List<Entry> entries = new ArrayList<Entry>();

        String query = "SELECT avg(knowledge) knowledge, endDate, endDate-startDate as duration, sum(endDate-startDate) as durationSum FROM selection\n" +
                "    LEFT JOIN statistics ON selection.cardId = statistics.cardId\n" +
                "    JOIN user ON user.isLoggedIn = 1\n" +
                "    WHERE selection.cardId NOT NULL\n" +
                "GROUP BY  endDate " +
                "ORDER BY endDate ASC";


        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                long index = cursor.getPosition();
                long count = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_KNOWLEDGE));

                entries.add(new Entry(index, count));
            } while (cursor.moveToNext());
        }

        return entries;
    }


    /**
     * Generates data for a bar chart
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     * @return entries
     */
    public List<BarEntry> getEntriesForBarChart() {

        List<BarEntry> entries = new ArrayList<BarEntry>();
        String[] columns = {"count("+ COLUMN_STATISTICS_ID + ") AS count"};

        Cursor cursor = database.query(TABLE_STATISTICS
                + " JOIN " + TABLE_SELECTION + " ON " + TABLE_SELECTION + "." + COLUMN_SELECTION_CARD_ID + " = " + TABLE_STATISTICS+ "." + COLUMN_STATISTICS_CARD_ID
                ,
                columns,
                TABLE_STATISTICS + "." + COLUMN_STATISTICS_USER_ID + "=" + getLoggedInUser().getId(),
                null, TABLE_STATISTICS + "." + COLUMN_STATISTICS_CARD_ID,
                null, TABLE_STATISTICS + "." + COLUMN_STATISTICS_DRAWER + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long index = cursor.getPosition();
                long count = cursor.getLong(0);

                entries.add(new BarEntry(index, count));
            } while (cursor.moveToNext());
        }

        return entries;
    }


    /**
     * Generates data for a radar chart
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     * @return entries
     */
    public List<RadarEntry> getEntriesForRadarChart() {

        List<RadarEntry> entries = new ArrayList<RadarEntry>();
        String[] columns = {"count("+ COLUMN_STATISTICS_ID + ") AS count"};

        Cursor cursor = database.query(TABLE_STATISTICS,
                columns,
                COLUMN_STATISTICS_USER_ID + "=" + getLoggedInUser().getId(),
                null, COLUMN_STATISTICS_CARD_ID,
                null, COLUMN_STATISTICS_DRAWER + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long index = cursor.getPosition();
                long count = cursor.getLong(0);

                entries.add(new RadarEntry(index, count));
            } while (cursor.moveToNext());
        }

        return entries;
    }


    /**
     * Generates data for a pie chart
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     * @return entries
     */
    public List<PieEntry> getEntriesForPieChart() {
        List<PieEntry> entries = new ArrayList<PieEntry>();

        // TODO Uncomment this to get percent float totalCountPercent = getCountStatistics() / 100f;
        float totalCountPercent = 1;

        String[] columns = {"count("+ COLUMN_STATISTICS_ID + ") AS count"};

        Cursor cursor = database.query(TABLE_STATISTICS,
                columns,
                COLUMN_STATISTICS_USER_ID + "=" + getLoggedInUser().getId(),
                null, COLUMN_STATISTICS_CARD_ID,
                null, COLUMN_STATISTICS_DRAWER + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long index = cursor.getPosition();
                long count = cursor.getLong(0);

                PieEntry pieEntry = new PieEntry(count * totalCountPercent, "drawer " + index);
                entries.add(pieEntry);
            } while (cursor.moveToNext());
        }

        return entries;
    }


    /**
     * Counts the number of statistic rows for the user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     * @return count
     */
    public float getCountStatistics() {
        String[] columns = {COLUMN_STATISTICS_ID};

        float count = 0;

        Cursor cursor = database.query(TABLE_STATISTICS,
                columns,
                COLUMN_STATISTICS_USER_ID + "=" + getLoggedInUser().getId(),
                null, null,
                null, null);

        if (cursor.moveToFirst()) {
             count = cursor.getCount();
        }

        return count;
    }


    /**
     * Counts how many cards are selected
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     *
     * @return count
     */
    public float countSelection() {
        String[] columns = {COLUMN_SELECTION_ID};

        float count = 0;

        Cursor cursor = database.query(TABLE_SELECTION,
                columns,
                COLUMN_SELECTION_USER_ID + "=" + getLoggedInUser().getId(),
                null, null,
                null, null);

        if (cursor.moveToFirst()) {
             count = cursor.getCount();
        }

        return count;
    }


    /**
     * Saves a list of user groups to the db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-28
     *
     * @param userGroups
     */
    public void saveUserGroups(List<UserGroup> userGroups) {

        for (UserGroup group : userGroups) {
            saveUserGroup(group);
        }
    }


    /**
     * Saves a list of users
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @param users
     */
    public void saveUsers(List<User> users) {

        for (User user : users) {

            saveUser(user);
        }
    }


    /**
     * Gets all users
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @return
     */
    public List<User> getUsers() {

        List<User> users = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                try {
                    long id = cursor.getLong(0);
                    String name = cursor.getString(2);
                    String avatar = cursor.getString(1);
                    // no pwd saved
                    String email = cursor.getString(4);
                    int rating = cursor.getInt(5);

                    //long groupId = cursor.getLong(5); not needed here
                    String created = cursor.getString(7);
                    String lastLogin = cursor.getString(8);

                    users.add(new User(id, avatar, name, email, rating, created, lastLogin));

                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            } while (cursor.moveToNext());

        }

        cursor.close();

        return users;
    }


    /**
     * Collects all users of a given group
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-01
     *
     * @param userGroupId
     * @return
     */
    public List<User> getUsersOfUserGroup(Long userGroupId) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        List<User> users = new ArrayList<>();

        String join = DbHelper.TABLE_USER_GROUP_JOIN_TABLE + " JOIN "
                + DbHelper.TABLE_USER + " ON " + TABLE_USER + "." + COLUMN_USER_ID
                + "=" + DbHelper.TABLE_USER_GROUP_JOIN_TABLE + "." + DbHelper.COLUMN_USER_GROUP_JOIN_TABLE_USER_ID;


        qb.setTables(join);
        qb.appendWhere(TABLE_USER_GROUP_JOIN_TABLE + "." + COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID  + "=" + userGroupId);

        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                try {
                    long id = cursor.getLong(cursor.getColumnIndex( COLUMN_USER_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                    String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_USER_AVATAR));
                    // no pwd saved
                    String email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
                    int rating = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_RATING));

                    //long groupId = cursor.getLong(5); not needed here
                    String created = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CREATED));
                    String lastLogin = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_LOGIN));

                    users.add(new User(id, avatar, name, email, rating, created, lastLogin));

                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            } while (cursor.moveToNext());

        }

        cursor.close();

        return users;

    }


    /**
     * Saves a relation of user and group to the joining table
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-01
     *
     * @param userId
     * @param groupId
     */
    public void saveUserGroupJoinTable(Long userId, Long groupId) {


        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_GROUP_JOIN_TABLE_USER_ID, userId);
        values.put(DbHelper.COLUMN_USER_GROUP_JOIN_TABLE_GROUP_ID, groupId);

        database.insertWithOnConflict(DbHelper.TABLE_USER_GROUP_JOIN_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Saves a token for a user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-14
     *
     * @param token
     */
    public void saveToken(String token) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_AUTH_TOKEN_TOKEN, token);
        values.put(DbHelper.COLUMN_AUTH_TOKEN_USER_ID, this.getLoggedInUser().getId());
        values.put(DbHelper.COLUMN_AUTH_TOKEN_CREATED, System.currentTimeMillis());

        database.insertWithOnConflict(DbHelper.TABLE_AUTH_TOKEN, null, values, SQLiteDatabase.CONFLICT_ABORT);
    }


    /**
     * Gets the token for a given user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-15
     *
     * @param userId
     * @return
     */
    public String getToken(long userId) {

        String token = null;
        Cursor cursor = database.query(
                DbHelper.TABLE_AUTH_TOKEN,
                allAuthTokenColumns,
                COLUMN_AUTH_TOKEN_USER_ID  + "=" + userId,
                null, null, null, null);

        if (cursor.moveToFirst()) {

            try {
                token = cursor.getString(cursor.getColumnIndex(COLUMN_AUTH_TOKEN_TOKEN));

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return token;
    }


    /**
     * Invalidates a token for a user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-14
     *
     */
    public void invalidateToken() {

        database.delete(DbHelper.TABLE_AUTH_TOKEN,
                COLUMN_AUTH_TOKEN_USER_ID + " = " + this.getLoggedInUser().getId(),
                null);
    }


    /**
     * Gets the messages for the logged in user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @return
     */
    public List<Message> getMessages() {

        List<Message> messages = new ArrayList<Message>();

        Cursor cursor = database.query(
                DbHelper.TABLE_MESSAGE,
                allMessageColumns,
                COLUMN_MESSAGE_RECIPIENT + "=" + getLoggedInUser().getId(),
                null, null, null, null);


        if (cursor.moveToFirst()) {
            do {

                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_ID));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_TYPE));
                Message.MessageType messageType = Message.MessageType.convert(type);

                long recipient = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_RECIPIENT));
                User userRecipient = getUser(recipient);

                String content = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_CONTENT));
                long created = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_DATE_CREATED));

                long targetDeck = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_TARGET_DECK));
                CardDeck cardDeck = getCardDeck(targetDeck);

                long sender = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_SENDER));
                User userSender = getUser(sender);

                messages.add(new Message(id, messageType, userRecipient, content, created, cardDeck, userSender));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return messages;
    }

    public List<Challenge> getChallenges() {

        List<Challenge> challenges = new ArrayList<>();

        Cursor cursor = database.query(
                DbHelper.TABLE_CHALLENGE
                + " JOIN " + TABLE_MESSAGE + " ON " + TABLE_MESSAGE + "." + COLUMN_MESSAGE_ID + "=" + TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_MESSAGE_ID,
                allChallengeColumns,
                COLUMN_MESSAGE_RECIPIENT + "=" + getLoggedInUser().getId(),
                null, null, null, null);


        if (cursor.moveToFirst()) {
            do {

                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_CHALLENGE_ID));
                /* TODO next
                Message message = new Message();
                long messageId = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_ID));

                challenges.add(new Challenge(id, ));
                */

            } while (cursor.moveToNext());
        }

        cursor.close();

        return challenges;
    }


    /**
     * Saves messages to the local db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param messages
     */
    public void saveMessages(List<Message> messages) {

        for (Message message : messages) {

            saveMessage(message);
        }
    }


    /**
     * Saves a message to the local db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param message
     */
    private long saveMessage(Message message) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_MESSAGE_ID, message.getId());
        values.put(DbHelper.COLUMN_MESSAGE_TYPE, message.getMessageType().toString());
        values.put(DbHelper.COLUMN_MESSAGE_RECIPIENT, message.getRecipient().getId());
        values.put(DbHelper.COLUMN_MESSAGE_CONTENT, message.getContent());
        values.put(DbHelper.COLUMN_MESSAGE_DATE_CREATED, message.getCreated());
        values.put(DbHelper.COLUMN_MESSAGE_TARGET_DECK, message.getTargetCardDeck().getId());
        values.put(DbHelper.COLUMN_MESSAGE_SENDER, message.getSender().getId());

        return database.insertWithOnConflict(TABLE_MESSAGE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Returns the name of a carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param targetDeck
     * @return
     */
    public String  getCardDeckName(long targetDeck) {

        String name = "";
        Cursor cursor = database.query(
                DbHelper.TABLE_CARD_DECK,
                allCardDeckColumns,
                COLUMN_CARD_DECK_ID  + "=" + targetDeck,
                null, null, null, null);

        if (cursor.moveToFirst()) {

            try {
                name = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_DECK_NAME));

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return name;
    }


    /**
     * Counts the cards of an carddeck
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param deckIds
     * @return
     */
    public int  countCardDeckCards(long deckIds) {

        int count = 0;
        Cursor cursor = database.query(
                DbHelper.TABLE_FLASHCARD,
                allFlashCardColumns,
                COLUMN_FLASHCARD_CARDDECK_ID  + "=" + deckIds,
                null, null, null, null);



        if (cursor.moveToFirst()) {

            try {
                count = cursor.getCount();

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return count;
    }


    /**
     * Saves a challenge to the local db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param messageId
     * @param statisticId
     */
    public void saveChallenge(long messageId, long statisticId) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CHALLENGE_MESSAGE_ID, messageId);
        values.put(DbHelper.COLUMN_CHALLENGE_STATISTIC_ID, statisticId);

        database.insertWithOnConflict(TABLE_CHALLENGE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * gets a challenge by the message id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param messageId
     * @return
     */
    public Challenge getChallengeByMessageId(long messageId) {

        List<Statistic> statistics = new ArrayList<Statistic>();
        Challenge challenge = null;
        long carddeckId = -1;
        Message message = null;
        CardDeck cardDeck = null;
        long id = -1;


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // base query
        String query =
                DbHelper.TABLE_CHALLENGE
                + " JOIN " + DbHelper.TABLE_STATISTICS + " ON "
                + DbHelper.TABLE_CHALLENGE + "." + DbHelper.COLUMN_CHALLENGE_STATISTIC_ID
                        + " = " +  DbHelper.TABLE_STATISTICS + "." + DbHelper.COLUMN_STATISTICS_ID
                + " JOIN " + DbHelper.TABLE_MESSAGE + " ON "
                + DbHelper.TABLE_CHALLENGE + "." + DbHelper.COLUMN_CHALLENGE_MESSAGE_ID
                        + " = " +  DbHelper.TABLE_MESSAGE + "." + DbHelper.COLUMN_MESSAGE_ID;

        String where =
                " WHERE " + TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_MESSAGE_ID +  " = " + messageId
                + " AND " + TABLE_STATISTICS + "." + COLUMN_STATISTICS_USER_ID + " = " + getLoggedInUser().getId();

        qb.setTables(query + where);

        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                id = cursor.getLong(cursor.getColumnIndex(COLUMN_CHALLENGE_ID));

                if (message == null) {

                    messageId = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_ID));
                    String type = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_TYPE));
                    Message.MessageType messageType = Message.MessageType.convert(type);

                    long recipient = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_RECIPIENT));
                    User userRecipient = getUser(recipient);

                    String content = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_CONTENT));
                    long created = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_DATE_CREATED));

                    long targetDeck = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_TARGET_DECK));
                    cardDeck = getCardDeck(targetDeck);

                    long sender = cursor.getLong(cursor.getColumnIndex(COLUMN_MESSAGE_SENDER));
                    User userSender = getUser(sender);

                    message = new Message(messageId, messageType, userRecipient, content, created, cardDeck, userSender);
                }

                long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_CARD_ID));
                long userId = getLoggedInUser().getId();
                float knowledge = cursor.getFloat(cursor.getColumnIndex(COLUMN_STATISTICS_KNOWLEDGE));
                int drawer = cursor.getInt(cursor.getColumnIndex(COLUMN_STATISTICS_DRAWER));
                long startDate = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_START_DATE));
                long endDate = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_END_DATE));

                Statistic statistic = new Statistic(userId, cardId, knowledge, drawer, startDate, endDate);

                statistics.add(statistic);

            } while (cursor.moveToNext());
        }

        cursor.close();

        challenge = new Challenge(id, message, cardDeck, statistics, isChallengeCompleted(messageId, carddeckId));

        return challenge;
    }


    /**
     * Checks if all cards of a challenge are played
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param messageId
     * @param deckId
     * @return
     */
    public boolean isChallengeCompleted (long messageId, long deckId) {


        int countPlayed = 0;


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // base query
        String query =
                DbHelper.TABLE_FLASHCARD
                        + " JOIN " + DbHelper.TABLE_STATISTICS + " ON "
                        + DbHelper.TABLE_FLASHCARD + "." + DbHelper.COLUMN_FLASHCARD_ID
                        + " = " +  DbHelper.TABLE_STATISTICS + "." + DbHelper.COLUMN_STATISTICS_CARD_ID
                        + " JOIN " + DbHelper.TABLE_CHALLENGE + " ON "
                        + DbHelper.TABLE_CHALLENGE + "." + DbHelper.COLUMN_CHALLENGE_STATISTIC_ID
                        + " = " +  DbHelper.TABLE_STATISTICS + "." + DbHelper.COLUMN_STATISTICS_ID
                        + " JOIN " + DbHelper.TABLE_MESSAGE + " ON "
                        + DbHelper.TABLE_CHALLENGE + "." + DbHelper.COLUMN_CHALLENGE_MESSAGE_ID
                        + " = " +  DbHelper.TABLE_MESSAGE + "." + DbHelper.COLUMN_MESSAGE_ID + " ";

        String where =
                 TABLE_CHALLENGE + "." + COLUMN_CHALLENGE_MESSAGE_ID +  " = " + messageId
                        + " AND " + TABLE_STATISTICS + "." + COLUMN_STATISTICS_USER_ID + " = " + getLoggedInUser().getId()
                        + " AND " + TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_CARDDECK_ID + " = " + COLUMN_MESSAGE_TARGET_DECK ;

        qb.setTables(query);

        qb.setDistinct(true);

        String[] columns = {
                TABLE_FLASHCARD + "." + COLUMN_FLASHCARD_ID
        };

        Cursor cursor = qb.query(database, columns, where, null, null, null, null);

        if (cursor.moveToFirst()) {

            countPlayed = cursor.getCount();

        }

        return countPlayed == countCardDeckCards(deckId);
    }


    /**
     * Updates the pwd of a user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-22
     *
     * @param userId
     * @param newPwd
     */
    public void saveUserPassword(long userId, String newPwd) {

        loggedInUser.setPassword(newPwd);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_PASSWORD, newPwd);

        database.updateWithOnConflict(TABLE_USER, values, COLUMN_USER_ID + "=" + userId, null ,SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Either creates or deletes the mark of a card
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-27
     *
     * Sets a bookmark for a card
     * @param card
     */
    public void setBookmark(FlashCard card) {

        if (!card.isMarked()) {

            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_BOOKMARK_CARD_ID, card.getId());
            values.put(DbHelper.COLUMN_BOOKMARK_USER_ID, getLoggedInUser().getId());
            values.put(DbHelper.COLUMN_BOOKMARK_MARK_DATE, System.currentTimeMillis());

            database.insertWithOnConflict(TABLE_BOOKMARK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } else {

            database.delete(TABLE_BOOKMARK, COLUMN_BOOKMARK_CARD_ID + "=" + card.getId(), null);
        }
    }


    /**
     * checks if a card is marked locally - necessary to check after getting cards from server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-27
     *
     * @param flashCard
     * @return
     */
    public boolean isCardMarkedLocally(FlashCard flashCard) {

        int count = 0;
        Cursor cursor = database.query(
                DbHelper.TABLE_BOOKMARK + " JOIN " + TABLE_USER + " ON "
                        + TABLE_BOOKMARK + "." +COLUMN_BOOKMARK_USER_ID + "=" + TABLE_USER + "." + COLUMN_USER_ID,
                allBookmarkColumns,
                COLUMN_BOOKMARK_CARD_ID  + "=" + flashCard.getId(),
                null, null, null, null);

        if (cursor.moveToFirst()) {

            try {
                count = cursor.getColumnCount();

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

        }

        cursor.close();

        return count > 0;
    }


    /**
     * updates the user with the media uri for the avatar
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-02
     *
     * @param userId
     * @param mediaUri
     */
    public void saveAvatar(Long userId, String mediaUri) {

        if (DEBUG) Log.d("save avatar", userId + " " + mediaUri);
        // For updating user

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_AVATAR, mediaUri);

        database.update(DbHelper.TABLE_USER, values, DbHelper.COLUMN_USER_ID + "=" + userId, null);

    }
}
