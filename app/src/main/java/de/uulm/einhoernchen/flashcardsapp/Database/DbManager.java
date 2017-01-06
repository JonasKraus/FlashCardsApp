package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.Category;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Question;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbManager {

    private static final boolean DEBUG = false;

    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private Context context;
    private User loggedInUser;

    /**
     * All Columns of an user
     */
    private String[] allUserColumns = {
            DbHelper.COLUMN_USER_ID,                  //0
            DbHelper.COLUMN_USER_AVATAR,              //1
            DbHelper.COLUMN_USER_NAME,                //2
            DbHelper.COLUMN_USER_PASSWORD,            //3
            DbHelper.COLUMN_USER_EMAIL,               //4
            DbHelper.COLUMN_USER_RATING,              //5
            DbHelper.COLUMN_USER_GROUP,               //6
            DbHelper.COLUMN_USER_CREATED,             //7
            DbHelper.COLUMN_USER_LAST_LOGIN,          //8
            DbHelper.COLUMN_USER_LOCAL_ACCOUNT,       //9
            DbHelper.COLUMN_USER_IS_LOGGED_IN         //10
    };

    private String[] allFlashCardColumns = {
            DbHelper.COLUMN_FLASHCARD_ID,             //0
            DbHelper.COLUMN_FLASHCARD_CARDDECK_ID,    //1
            DbHelper.COLUMN_FLASHCARD_RATING,         //2
            DbHelper.COLUMN_FLASHCARD_QUESTION_ID,    //3
            DbHelper.COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
            DbHelper.COLUMN_FLASHCARD_CREATED,        //5
            DbHelper.COLUMN_FLASHCARD_LAST_UPDATED,   //6
            DbHelper.COLUMN_FLASHCARD_USER_ID         //7
    };

    private String[] allQuestionColumns = {
            DbHelper.COLUMN_QUESTION_ID,              //0
            DbHelper.COLUMN_QUESTION_TEXT,            //1
            DbHelper.COLUMN_QUESTION_MEDIA_URI,       //2
            DbHelper.COLUMN_QUESTION_AUTHOR_ID,       //3
    };

    private String[] allAnswerColumns = {
            DbHelper.COLUMN_ANSWER_ID,                //0
            DbHelper.COLUMN_ANSWER_TEXT,              //1
            DbHelper.COLUMN_ANSWER_HINT,              //2
            DbHelper.COLUMN_ANSWER_MEDIA_URI,         //3
            DbHelper.COLUMN_ANSWER_USER_ID,           //4
            DbHelper.COLUMN_ANSWER_PARENT_CARD_ID,    //5
            DbHelper.COLUMN_ANSWER_RATING,            //6
            DbHelper.COLUMN_ANSWER_CORRECT,           //7
            DbHelper.COLUMN_ANSWER_CREATED,           //8
            DbHelper.COLUMN_ANSWER_LAST_UPDATED       //9
    };

    private String[] allCardTagColumns = {
            DbHelper.COLUMN_CARD_TAG_FLASHCARD_ID,    //0
            DbHelper.COLUMN_CARD_TAG_TAG_ID,          //1
    };

    private String[] allTagColumns = {
            DbHelper.COLUMN_TAG_ID,                   //0
            DbHelper.COLUMN_TAG_NAME,                 //1
    };

    private String[] allGroupColumns = {
            DbHelper.COLUMN_GROUP_ID,                 //0
            DbHelper.COLUMN_GROUP_NAME,               //1
            DbHelper.COLUMN_GROUP_DESCRIPTION,        //2
    };

    private String[] allRatingColumns = {
            DbHelper.COLUMN_RATING_ID,                //0
            DbHelper.COLUMN_RATING_TYPE,              //1
            DbHelper.COLUMN_RATING_USER_ID,           //2
            DbHelper.COLUMN_RATING_MODIFIER,          //3
            DbHelper.COLUMN_RATING_FLASHCARD_ID,      //4
            DbHelper.COLUMN_RATING_ANSWER_ID,         //5
    };

    private String[] allAuthTokenColumns = {
            DbHelper.COLUMN_RATING_ID,                //0
            DbHelper.COLUMN_AUTH_TOKEN_ID,            //1
            DbHelper.COLUMN_AUTH_TOKEN_USER_ID,       //2
            DbHelper.COLUMN_AUTH_TOKEN_TOKEN,         //3
            DbHelper.COLUMN_AUTH_TOKEN_CREATED,       //4
    };

    /*
    private String[] allCardDeckColumns = {
            DbHelper.COLUMN_CARD_DECK_ID,             //0
            DbHelper.COLUMN_CARD_DECK_NAME,           //1
            DbHelper.COLUMN_CARD_DECK_DESCRIPTION,    //2
    };
    */

    private String[] allCategoryColumns = {
            DbHelper.COLUMN_CATEGORY_ID,             //0
            DbHelper.COLUMN_CATEGORY_NAME,           //1
            DbHelper.COLUMN_CATEGORY_PARENT,         //2
    };

    private String[] allUserGroupColumns = {
            DbHelper.COLUMN_GROUP_ID,                 //0
            DbHelper.COLUMN_GROUP_NAME,               //1
            DbHelper.COLUMN_GROUP_DESCRIPTION,        //2
    };

    private String[] allCardDeckColumns = {
            DbHelper.COLUMN_CARD_DECK_ID,             //0
            DbHelper.COLUMN_CARD_DECK_NAME,           //1
            DbHelper.COLUMN_CARD_DECK_DESCRIPTION,    //2
            DbHelper.COLUMN_CARD_DECK_VISIBLE,        //3
            DbHelper.COLUMN_CARD_DECK_GROUP,          //4
            DbHelper.COLUMN_CARD_DECK_PARENT,         //5
    };

    private String[] allSelectionColumns = {
            DbHelper.COLUMN_SELECTION_ID,             //0
            DbHelper.COLUMN_SELECTION_USER_ID,        //1
            DbHelper.COLUMN_SELECTION_CARD_DECK_ID,   //2
            DbHelper.COLUMN_SELECTION_CARD_ID,        //3
            DbHelper.COLUMN_SELECTION_DATE            //4
    };

    private String[] allVotingColumns = {
            DbHelper.COLUMN_VOTING_ID,                //0
            DbHelper.COLUMN_VOTING_USER_ID,           //1
            DbHelper.COLUMN_VOTING_CARD_ID,           //2
            DbHelper.COLUMN_VOTING_ANSWER_ID,         //3
            DbHelper.COLUMN_VOTING_VALUE,             //4
            DbHelper.COLUMN_VOTING_DATE,              //5
            DbHelper.COLUMN_VOTING_RATING_ID          //6
    };

    /**
     * Constructor
     *
     * @param context
     */
    public DbManager(Context context) {

        this.context = context;
        dbHelper = new DbHelper(context);
    }


    /**
     * Opens a database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {

        this.database = dbHelper.getWritableDatabase();

        this.loggedInUser = getLoggedInUser();
    }


    /**
     * Closes a database connection
     *
     */
    public void close() {

        dbHelper.close();
         if (DEBUG) Log.d("db", "closed");
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

        User user = null;
        Cursor cursor = database.query(DbHelper.TABLE_USER, allUserColumns,
                DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1 + " AND " + DbHelper.COLUMN_USER_IS_LOGGED_IN + " = " + 1
                , null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
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

        Cursor cursor = database.query(DbHelper.TABLE_FLASHCARD, allFlashCardColumns, DbHelper.COLUMN_FLASHCARD_CARDDECK_ID + " = " + parentId, null, null, null, null);

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


                FlashCard flashCard = new FlashCard(cardId, tags, rating, new Date(created), new Date(lastUpdated), question, answers, author, multipleChoice);

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

        Cursor cursor = database.query(DbHelper.TABLE_FLASHCARD, allFlashCardColumns, DbHelper.COLUMN_FLASHCARD_ID + " = " + flashCardId, null, null, null, null);

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


                flashCard = new FlashCard(cardId, tags, rating, new Date(created), new Date(lastUpdated), question, answers, author, multipleChoice);

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

        database.insertWithOnConflict(DbHelper.TABLE_FLASHCARD, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

    public List<CardDeck> getCardDecks(Long parentId) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DbHelper.TABLE_CARD_DECK +
                " LEFT JOIN " + DbHelper.TABLE_SELECTION + " ON "
                + DbHelper.TABLE_CARD_DECK + "." + DbHelper.COLUMN_CARD_DECK_ID + " = " +  DbHelper.TABLE_SELECTION + "." + DbHelper.COLUMN_SELECTION_CARD_DECK_ID
                + " AND " + DbHelper.TABLE_SELECTION + "." + DbHelper.COLUMN_SELECTION_USER_ID + " = " + loggedInUser.getId()
        );

        Cursor cursor = qb.query(database, null, null, null, null, null, null);

        List<CardDeck> cardDecks = new ArrayList<CardDeck>();

        //Cursor cursor = database.query(DbHelper.TABLE_CARD_DECK, allCardDeckColumns, DbHelper.COLUMN_CARD_DECK_PARENT + " = " + parentId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long cardDeckId = cursor.getLong(0);
                String cardDeckName = cursor.getString(1);
                String cardDeckDescription = cursor.getString(2);
                boolean cardDeckVisible = cursor.getInt(3) > 0;
                long cardDeckGroupId = cursor.getLong(4);
                long cardDeckParentId = cursor.getLong(5);
                long selectionDate = cursor.getLong(cursor.getColumnIndex(dbHelper.COLUMN_SELECTION_DATE));

                boolean isSelected = selectionDate > 0;

                UserGroup userGroup = getUserGroup(cardDeckGroupId);
                cardDecks.add(new CardDeck(cardDeckId,cardDeckVisible, userGroup,cardDeckName, cardDeckDescription, selectionDate));

            } while (cursor.moveToNext());
        }
        return cardDecks;
    }

    private UserGroup getUserGroup(long cardDeckGroupId) {

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
        database.updateWithOnConflict(DbHelper.TABLE_FLASHCARD, values, DbHelper.COLUMN_FLASHCARD_ID + "=" + cardId, null, SQLiteDatabase.CONFLICT_REPLACE);

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

        database.delete(DbHelper.TABLE_SELECTION, DbHelper.COLUMN_SELECTION_CARD_ID + "=" + cardID
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
        values.put(DbHelper.COLUMN_SELECTION_CARD_ID, cardID);
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

        Cursor cursor = database.query(DbHelper.TABLE_SELECTION, allSelectionColumns, DbHelper.COLUMN_SELECTION_CARD_ID + " = " + cardID
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

        Cursor cursor = database.query(DbHelper.TABLE_FLASHCARD, allFlashCardColumns, DbHelper.COLUMN_FLASHCARD_ID + " = " + cardID
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

        database.update(DbHelper.TABLE_USER, values, DbHelper.COLUMN_USER_ID + " = " + loggedInUser.getId(), null);
    }

    public boolean loginUser(String mEmail, String mUserName, String mPassword) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USER_IS_LOGGED_IN, 1);

        int affectedRows = database.update(DbHelper.TABLE_USER, values,
                DbHelper.COLUMN_USER_NAME + " = '" + mUserName
                + "' AND " + DbHelper.COLUMN_USER_EMAIL + " = '" + mEmail
                + "' AND " + DbHelper.COLUMN_USER_PASSWORD + " = '" + mPassword
                + "' AND " + DbHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1
                , null);

        return affectedRows > 0;
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
}
