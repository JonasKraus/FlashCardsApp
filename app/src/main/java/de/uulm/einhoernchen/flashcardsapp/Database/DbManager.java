package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.Tag;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Util.DateProcessor;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbManager {

    private static final boolean DEBUG = false;

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
            MySQLiteHelper.COLUMN_USER_LAST_LOGIN,          //8
            MySQLiteHelper.COLUMN_USER_LOCAL_ACCOUNT        //9
    };

    private String[] allFlashCardColumns = {
            MySQLiteHelper.COLUMN_FLASHCARD_ID,             //0
            MySQLiteHelper.COLUMN_FLASHCARD_CARDDECK_ID,    //1
            MySQLiteHelper.COLUMN_FLASHCARD_RATING,         //2
            MySQLiteHelper.COLUMN_FLASHCARD_QUESTION_ID,    //3
            MySQLiteHelper.COLUMN_FLASHCARD_MULTIPLE_CHOICE,//4
            MySQLiteHelper.COLUMN_FLASHCARD_CREATED,        //5
            MySQLiteHelper.COLUMN_FLASHCARD_LAST_UPDATED,   //6
            MySQLiteHelper.COLUMN_FLASHCARD_USER_ID         //7
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
        softDeleteUser(user.getId());

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
        values.put(MySQLiteHelper.COLUMN_USER_LOCAL_ACCOUNT, localAccount);

        // Executes the query
        Long id = database.insert(MySQLiteHelper.TABLE_USER, null, values);
         if (DEBUG) Log.d("local user created", id+" "+ localAccount);
    }

    /**
     * Deletes a user by its id
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     * 
     * @param id
     */
    private void softDeleteUser(long id) {
         if (DEBUG) Log.d("delete user", id+"");
        database.delete(MySQLiteHelper.TABLE_USER, MySQLiteHelper.COLUMN_USER_ID + "=" + id, null);
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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, allUserColumns, MySQLiteHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1, null, null, null, null);
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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, allUserColumns,  MySQLiteHelper.COLUMN_USER_LOCAL_ACCOUNT + " = " + 1, null, null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FLASHCARD, allFlashCardColumns, MySQLiteHelper.COLUMN_FLASHCARD_CARDDECK_ID + " = " + parentId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(0);
                long carddeckId = cursor.getLong(1);
                int rating = cursor.getInt(2);
                long questionId = cursor.getLong(3);
                boolean multipleChoice = cursor.getInt(4) > 0;
                String created = cursor.getString(5);
                String lastUpdated = cursor.getString(6);
                long userId = cursor.getLong(7);

                User author = getUser(userId);
                List<Tag> tags = getTags(cardId);
                Question question = getQuestion(questionId);
                List<Answer> answers = getAnswers(cardId);

                flashCards.add(new FlashCard(cardId, tags, rating, created, lastUpdated, question, answers, author, multipleChoice));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCards;
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
    private List<Answer> getAnswers(long parentCardId) {

        List<Answer> answers = new ArrayList<Answer>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ANSWER, allAnswerColumns, MySQLiteHelper.COLUMN_ANSWER_PARENT_CARD_ID + " = " + parentCardId, null, null, null, null);

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
                String created = cursor.getString(8);
                String lastupdated = cursor.getString(9);

                User author = getUser(userId);

                answers.add(new Answer(answerId, correct, answerText, answerHint, mediaURI, author, created, lastupdated, rating, correct));
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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTION, allQuestionColumns, MySQLiteHelper.COLUMN_QUESTION_ID + " = " + questionId, null, null, null, null);

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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CARD_TAG, allCardTagColumns, MySQLiteHelper.COLUMN_CARD_TAG_FLASHCARD_ID + " = " + cardId, null, null, null, null);

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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TAG, allCardTagColumns, MySQLiteHelper.COLUMN_TAG_ID + " = " + tagId, null, null, null, null);

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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, allUserColumns, MySQLiteHelper.COLUMN_USER_ID+ " = " + userId, null, null, null, null);

        if (cursor.moveToFirst()) {

            long Id = cursor.getLong(0);
            String name = cursor.getString(1);
            String avatar = cursor.getString(2);
            // no pwd saved
            String email = cursor.getString(4);
            int rating = cursor.getInt(5);
            //long groupId = cursor.getLong(5); not needed here
            String created = cursor.getString(6);
            String lastLogin = cursor.getString(7);

            user = new User(userId, avatar, name, "", email, rating, created, lastLogin);
        }

        cursor.close();

        return user;
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

        softDeleteFlashCard(card.getId());

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_FLASHCARD_ID, card.getId());
        values.put(MySQLiteHelper.COLUMN_FLASHCARD_CARDDECK_ID , parentId);
        values.put(MySQLiteHelper.COLUMN_FLASHCARD_RATING , card.getRating());
        values.put(MySQLiteHelper.COLUMN_FLASHCARD_QUESTION_ID , card.getQuestion().getId());
        values.put(MySQLiteHelper.COLUMN_FLASHCARD_MULTIPLE_CHOICE , card.isMultipleChoice());

        if (card.getCreated() != null) {
            values.put(MySQLiteHelper.COLUMN_FLASHCARD_CREATED, card.getCreated().toString());
        }

        if (card.getLastUpdated() != null) {
            values.put(MySQLiteHelper.COLUMN_FLASHCARD_LAST_UPDATED , card.getLastUpdated().toString());
        }

        database.insert(MySQLiteHelper.TABLE_FLASHCARD, null, values);
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

        softDeleteAnswer(answer.getId());

        saveUser(answer.getAuthor(), false);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ANSWER_ID, answer.getId());
        values.put(MySQLiteHelper.COLUMN_ANSWER_TEXT, answer.getAnswerText());
        values.put(MySQLiteHelper.COLUMN_ANSWER_HINT, answer.getHintText());
        values.put(MySQLiteHelper.COLUMN_ANSWER_MEDIA_URI, answer.getUri() != null ? answer.getUri().toString() : null);
        values.put(MySQLiteHelper.COLUMN_ANSWER_USER_ID, answer.getAuthor().getId());
        values.put(MySQLiteHelper.COLUMN_ANSWER_PARENT_CARD_ID, cardId);
        values.put(MySQLiteHelper.COLUMN_ANSWER_RATING, answer.getRating());
        values.put(MySQLiteHelper.COLUMN_ANSWER_CORRECT, answer.isCorrect());

        if (answer.getCreated() != null) {
            values.put(MySQLiteHelper.COLUMN_ANSWER_CREATED, answer.getCreated().toString());
        }

        if (answer.getLastUpdated() != null) {
            values.put(MySQLiteHelper.COLUMN_ANSWER_LAST_UPDATED, answer.getLastUpdated().toString());
        }

        database.insert(MySQLiteHelper.TABLE_ANSWER, null, values);
    }

    /**
     * Deletes an answer, but not its dependencies
     * use this for updates
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param answerId
     */
    private void softDeleteAnswer(long answerId) {
        database.delete(MySQLiteHelper.TABLE_ANSWER, MySQLiteHelper.COLUMN_ANSWER_ID + "=" + answerId, null);
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

        softDeleteQuestion(question.getId());

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_QUESTION_ID, question.getId());
        values.put(MySQLiteHelper.COLUMN_QUESTION_TEXT, question.getQuestionText());
        values.put(MySQLiteHelper.COLUMN_QUESTION_MEDIA_URI, question.getUri() != null ? question.getUri().toString() : null);
        values.put(MySQLiteHelper.COLUMN_QUESTION_AUTHOR_ID, question.getAuthor().getId());

        database.insert(MySQLiteHelper.TABLE_QUESTION, null, values);
    }

    /**
     * Deletes a question, but not its dependencies
     * use this for updates
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param questionId
     */
    private void softDeleteQuestion(long questionId) {
        database.delete(MySQLiteHelper.TABLE_QUESTION, MySQLiteHelper.COLUMN_QUESTION_ID + "=" + questionId, null);
    }


    /**
     * Deletes a flashcard by its id
     * use this for updates
     * no dependencies are deleted
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param cardId
     */
    private void softDeleteFlashCard(long cardId) {
        database.delete(MySQLiteHelper.TABLE_FLASHCARD, MySQLiteHelper.COLUMN_FLASHCARD_ID + "=" + cardId, null);
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

        softDeleteTag(tag);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TAG_ID, tag.getId());
        values.put(MySQLiteHelper.COLUMN_TAG_NAME, tag.getName());

        // Executes the query
        database.insert(MySQLiteHelper.TABLE_TAG, null, values);

        saveCardTag(tag, cardId);

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
        database.delete(MySQLiteHelper.TABLE_CARD_TAG, MySQLiteHelper.COLUMN_CARD_TAG_TAG_ID + "=" + tag.getId(), null);
        database.delete(MySQLiteHelper.TABLE_TAG, MySQLiteHelper.COLUMN_TAG_ID + "=" + tag.getId(), null);
    }

    /**
     * deletes a tag but not the link between Card and Tag
     * use this for updating a tag
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.30
     *
     * @param tag
     */
    private void softDeleteTag(Tag tag) {
        database.delete(MySQLiteHelper.TABLE_CARD_TAG, MySQLiteHelper.COLUMN_CARD_TAG_TAG_ID + "=" + tag.getId(), null);
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
        values.put(MySQLiteHelper.COLUMN_CARD_TAG_TAG_ID, tag.getId());
        values.put(MySQLiteHelper.COLUMN_CARD_TAG_FLASHCARD_ID, cardId);

        // Executes the query
        database.insert(MySQLiteHelper.TABLE_CARD_TAG, null, values);
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
        database.delete(MySQLiteHelper.TABLE_CARD_TAG, MySQLiteHelper.COLUMN_CARD_TAG_TAG_ID + "=" + tagId
                + " AND " + MySQLiteHelper.COLUMN_CARD_TAG_FLASHCARD_ID + "=" + cardId, null);
    }
}
