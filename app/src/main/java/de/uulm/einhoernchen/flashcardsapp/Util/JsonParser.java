package de.uulm.einhoernchen.flashcardsapp.Util;

import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.Category;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.Tag;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Models.UserGroup;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.08.21
 */
public class JsonParser {

    private static final boolean DEBUG = false;

    public static List<Category> readCategroies(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readCategoryArray(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Takes an inputstream and parses carddecks
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static List<CardDeck> readCardDecks(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readCardDeckArray(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Takes an inputstream and parses one user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.21
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static User parseUser(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readUser(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Takes an inputstream and parses flashCards
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016.08.22
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static List<FlashCard> readFlashCards(InputStream in)  throws IOException  {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readFlashCardArray(reader);
        } finally {
            reader.close();
        }
    }

    private static List<Category> readCategoryArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readCategoryArray");
        List<Category> categoryList = new ArrayList<Category>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {

                categoryList.add(readCategory(reader));

            }
            reader.endArray();
        } catch (IOException e) {
            if (DEBUG) Log.d("Parser Error", "readCategoryArray");
            e.printStackTrace();
        }
        return categoryList;
    }

    private static List<CardDeck> readCardDeckArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readCardDeckArray");
        List<CardDeck> cardDeckList = new ArrayList<CardDeck>();

        try {

            reader.beginArray();

            while (reader.hasNext()) {

                // TODO is this the right place to disable invisible Carddecks
                CardDeck cardDeck = readCarddeck(reader);
                if (cardDeck.isVisible()) {
                    cardDeckList.add(cardDeck);
                }
            }
            reader.endArray();
        } catch (IOException e) {
            if (DEBUG) Log.d("Parser Error", "readCardDeckArray");
            e.printStackTrace();
        }
        return cardDeckList;
    }

    private static Category readCategory(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readCategory");
        long id = -1;
        String name = "";
        List<CardDeck> cardDecks = new ArrayList<>();
        long parent = -1;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.CATEGORY_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.CATEGORY_NAME)) {
                    name = reader.nextString();
                } /*else if (stringName.equals(JsonKeys.CATEGORY_CARDDECKS)) {

                    JsonToken check = reader.peek();
                    Log.d("chck", check.toString());

                    if (check != JsonToken.NULL) {
                        // TODO wird hier nicht gebraucht - dekcs erst bei klich abholen
                        //cardDecks = readCardDeckArray(reader);
                        reader.beginArray();

                        if (DEBUG) Log.d("parser Method", "readCategory readCardDecks " + reader.toString());
                    } else {
                        reader.nextNull();
                    }
                }*/ else if (stringName.equals(JsonKeys.CATEGORY_PARENT)) {

                    JsonToken check = reader.peek();
                    if (DEBUG) Log.d("parser Method", "readCategory readCardDecks " + reader.toString());

                    if (check != JsonToken.NULL) {
                        parent = readCategory(reader).getId();
                    } else {
                        reader.nextNull();
                    }

                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Category(id,parent, name);
    }

    private static CardDeck readCarddeck(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readCarddeck");
        long id = -1;
        boolean visible = false;
        UserGroup userGroup = null;
        String name = "Group not found";
        String description = "No description";
        List<FlashCard> cards = new ArrayList<>();

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.CARDDECK_VISIBLE)) {
                    visible = reader.nextBoolean();
                } else if (stringName.equals(JsonKeys.CARDDECK_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.CARDDECK_GROUP)) {
                    JsonToken check = reader.peek();


                    if (check != JsonToken.NULL) {
                        userGroup = readUserGroup(reader);
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.CARDDECK_NAME)) {
                    name = reader.nextString();
                } else if (stringName.equals(JsonKeys.CARDDECK_DESCRIPTION)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        description = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                /*} else if (stringName.equals(JsonKeys.CARDDECK_CARDS)) {
                    cards = readFlashCardArray(reader);*/
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new CardDeck(id, visible, userGroup, name, description);
    }

    private static List<FlashCard> readFlashCardArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readFlashCardArray");
        List<FlashCard> cards = new ArrayList<FlashCard>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                cards.add(readFlashCard(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cards;
    }

    private static FlashCard readFlashCard(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readFlashCard");
        long id = -1;
        List<Tag> tags = new ArrayList<>();
        int rating = 0;
        Date created = null;
        Date lastUpdated = null;
        Question question = null;
        List<Answer> answers = new ArrayList<>();
        User author = null; // TODO wird hier eine id oder der name oder Objekt mitgegeben?
        boolean multipleChoice = false;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.FLASHCARD_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.FLASHCARD_TAGS)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        tags = readTagArray(reader);
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.RATING)) {
                    rating = reader.nextInt();
                } else if (stringName.equals(JsonKeys.DATE_CREATED)) {
                    created = stringToDate(reader.nextString());

                } else if (stringName.equals(JsonKeys.DATE_UPDATED)) {

                    lastUpdated = stringToDate(reader.nextString());

                } else if (stringName.equals(JsonKeys.FLASHCARD_QUESTION)) {
                    question = readQuestion(reader);
                } else if (stringName.equals(JsonKeys.FLASHCARD_ANSWERS)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        answers = readAnswerArray(reader);
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.AUTHOR)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        author = readUser(reader);
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.FLASHCARD_MULTIPLE_CHOICE)) {
                    multipleChoice = reader.nextBoolean();
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FlashCard(id, tags, rating, created, lastUpdated, question, answers, author, multipleChoice);
    }

    private static List<Answer> readAnswerArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readAnswerArray");

        List<Answer> answers = new ArrayList<Answer>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                answers.add(readAnswer(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answers;
    }

    private static Answer readAnswer(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readAnswer");

        long id = -1;
        boolean correct = true;
        String text = "No answer text found";
        String hint = "";
        String uriString = "";
        URI uri = null;
        User author = null;
        Date created = null;
        Date lastUpdated = null;
        int rating = 0;
        boolean answerCorrect = false;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.ANSWER_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.ANSWER_IS_CORRECT)) {
                    correct = reader.nextBoolean();
                } else if (stringName.equals(JsonKeys.ANSWER_TEXT)) {
                    text = reader.nextString();
                } else if (stringName.equals(JsonKeys.ANSWER_HINT)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        hint = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.URI)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        uriString = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                    uri = new URI(uriString); // TODO check what happens when string is empty
                } else if (stringName.equals(JsonKeys.AUTHOR)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        author = readUser(reader);
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.DATE_CREATED)) {
                    created = stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.DATE_UPDATED)) {
                    lastUpdated = stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.RATING)) {
                    rating = reader.nextInt();
                } else if (stringName.equals(JsonKeys.ANSWER_CORRECT)) {
                    answerCorrect = reader.nextBoolean();
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new Answer(id, correct, text, hint, uri, author, created, lastUpdated, rating, answerCorrect);
    }

    private static User readUser(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readUser");

        long id = -1;
        String avatar = "";
        String name = "";
        String email = "";
        int rating = 0;
        Date created = null;
        Date lastLogin = null;
        List<UserGroup> groups = new ArrayList<>();

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.USER_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.USER_AVATAR)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        avatar = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.USER_NAME)) {
                    name = reader.nextString();
                } else if (stringName.equals(JsonKeys.USER_EMAIL)) {
                    email = reader.nextString();
                } else if (stringName.equals(JsonKeys.RATING)) {
                    rating = reader.nextInt();
                } else if (stringName.equals(JsonKeys.DATE_CREATED)) {
                    created = stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.DATE_LAST_LOGIN)) {
                    lastLogin = stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.USER_GROUPS)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        groups = readUserGroupArray(reader);
                    } else {
                        reader.nextNull();
                    }

                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(id, avatar, name, email, rating, created, lastLogin, groups);
    }

    private static List<UserGroup> readUserGroupArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readUserGroupArray");

        List<UserGroup> groups = new ArrayList<UserGroup>();

        try {
            reader.beginArray();

            while (reader.hasNext()) {

                groups.add(readUserGroup(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private static Question readQuestion(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readQuestion");

        long id = -1;
        String text = "";
        String uriString = "";
        URI uri = null;
        User author = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.QUESTION_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.QUESTION_TEXT)) {
                    text = reader.nextString();
                }else if (stringName.equals(JsonKeys.URI)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        uriString = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                    uri = new URI(uriString); // TODO check what happens when string is empty
                }else if (stringName.equals(JsonKeys.AUTHOR)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        author = readUser(reader);
                    } else {
                        reader.nextNull();
                    }

                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new Question(id, text, uri, author);
    }

    private static List<Tag> readTagArray(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readTagArray");

        List<Tag> tags = new ArrayList<Tag>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                tags.add(readTag(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    private static Tag readTag(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readTag");

        long id = -1;
        String name = "";

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.TAG_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.TAG_NAME)) {
                    name = reader.nextString();
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Tag(id, name);
    }

    private static UserGroup readUserGroup(JsonReader reader) {
        if (DEBUG) Log.d("parser Method", "readUserGroup");

        long id = -1;
        String name = "No Group name found";
        String description = "";

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.GROUP_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.GROUP_NAME)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        name = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                } else if (stringName.equals(JsonKeys.GROUP_DESCRIPTION)) {
                    JsonToken check = reader.peek();

                    if (check != JsonToken.NULL) {
                        description = reader.nextString();
                    } else {
                        reader.nextNull();
                    }

                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UserGroup(id, name, description);
    }


    /**
     *
     * @deprecated use {@link #parseUser(InputStream)}  instead.
     * Takes an Buffered reader and Parses a user
     *
     * @param in
     * @param userId
     * @return
     */
    public static User parseUser (BufferedReader in, long userId) {
        String decodedString;

        User user = null;

        try {
            while ((decodedString = in.readLine()) != null) {
                if (DEBUG) Log.d("response", decodedString);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(decodedString);
                user = new User(
                        userId,
                        root.get("avatar").asText(),
                        root.get("name").asText(),
                        "", // TODO
                        root.get("email").asText(),
                        root.get("rating").asInt(),
                        root.get("created").asText(),
                        root.get("lastLogin").asText()
                );
                if (DEBUG) Log.d("user instanz", user.toString());
                return user;

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("parseUser Fehler", e.toString());
            System.out.println(e.getMessage());
        }

        return user;
    }

    @Nullable
    public static Date stringToDate(String dateString) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
        try {
            Date date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Error", e.toString());
        }


        return null;
    }

}
