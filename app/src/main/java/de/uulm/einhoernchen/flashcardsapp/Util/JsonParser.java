package de.uulm.einhoernchen.flashcardsapp.Util;

import android.util.JsonReader;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.Tag;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Models.UserGroup;

/**
 * Created by jonas-uni on 21.08.2016.
 */
public class JsonParser {

    public List<CardDeck> readCardDecks(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readCardDeckArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<CardDeck> readCardDeckArray(JsonReader reader) {

        List<CardDeck> cardDeckLis = new ArrayList<CardDeck>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                cardDeckLis.add(readCarddeck(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardDeckLis;
    }

    public CardDeck readCarddeck(JsonReader reader) {

        long id = -1;
        boolean visible = false;
        UserGroup userGroup = null;
        String name = "Group not found";
        String description = "No description";
        List<FlashCard> cards = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals("visible")) {
                    visible = reader.nextBoolean();
                } else if (stringName.equals("cardDeckId")) {
                    id = reader.nextLong();
                } else if (stringName.equals("userGroup")) {
                    userGroup = readUserGroup(reader);
                } else if (reader.equals("cardDeckName")) {
                    name = reader.nextString();
                } else if (reader.equals("cardDeckDescpription")) {
                    description = reader.nextString();
                } else if (reader.equals("cards")) {
                    cards = readCardArray(reader);
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CardDeck(id, visible, userGroup, name, description, cards);
    }

    public List<FlashCard> readCardArray(JsonReader reader) {
        List<FlashCard> cards = new ArrayList<FlashCard>();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                cards.add(readCard(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public FlashCard readCard(JsonReader reader) {
        long id = -1;
        List<Tag> tags = null;
        int rating = 0;
        Date created = null;
        Date lastUpdated = null;
        Question question = null;
        List<Answer> answers = null;
        User author = null; // TODO wird hier eine id oder der name oder Objekt mitgegeben?
        boolean multipleChoice = false;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.FLASHCARD_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.FLASHCARD_TAGS)) {
                    tags = readTagArray(reader);
                } else if (reader.equals(JsonKeys.RATING)) {
                    rating = reader.nextInt();
                } else if (stringName.equals(created)) {
                    created = DateProcessor.stringToDate(reader.nextString());
                } else if (stringName.equals(lastUpdated)) {
                    lastUpdated = DateProcessor.stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.FLASHCARD_QUESTION)) {
                    question = readQuestion(reader);
                } else if (stringName.equals(JsonKeys.FLASHCARD_ANSWERS)) {
                    answers = readAnswerArray(reader);
                } else if (stringName.equals(JsonKeys.AUTHOR)) {
                    author = readUser(reader);
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

    public List<Answer> readAnswerArray(JsonReader reader) {

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

    public Answer readAnswer(JsonReader reader) {
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
                    hint = reader.nextString();
                } else if (stringName.equals(JsonKeys.URI)) {
                    uriString = reader.nextString();
                    uri = new URI(uriString); // TODO check what happens when string is empty
                } else if (stringName.equals(JsonKeys.AUTHOR)) {
                    author = readUser(reader);
                } else if (stringName.equals(JsonKeys.DATE_CREATED)) {
                    created = DateProcessor.stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.DATE_UPDATED)) {
                    lastUpdated = DateProcessor.stringToDate(reader.nextString());
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

    public User readUser(JsonReader reader) {
        long id = -1;
        String avatar = "";
        String name = "";
        String email = "";
        int rating = 0;
        Date created = null;
        Date lastLogin = null;
        List<UserGroup> groups = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String stringName = reader.nextName();

                if (stringName.equals(JsonKeys.USER_ID)) {
                    id = reader.nextLong();
                } else if (stringName.equals(JsonKeys.USER_AVATAR)) {
                    avatar = reader.nextString();
                } else if (stringName.equals(JsonKeys.USER_NAME)) {
                    name = reader.nextString();
                } else if (stringName.equals(JsonKeys.USER_EMAIL)) {
                    email = reader.nextString();
                } else if (stringName.equals(JsonKeys.RATING)) {
                    rating = reader.nextInt();
                } else if (stringName.equals(JsonKeys.DATE_CREATED)) {
                    created = DateProcessor.stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.DATE_LAST_LOGIN)) {
                    lastLogin = DateProcessor.stringToDate(reader.nextString());
                } else if (stringName.equals(JsonKeys.USER_GROUPS)) {
                    groups = readUserGroupArray(reader);
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(id,avatar, name, email, rating, created, lastLogin, groups);
    }

    public List<UserGroup> readUserGroupArray(JsonReader reader) {

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

    public Question readQuestion(JsonReader reader) {
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
                    uriString = reader.nextString();
                    uri = new URI(uriString); // TODO check what happens when string is empty
                }else if (stringName.equals(JsonKeys.AUTHOR)) {
                    author = readUser(reader);
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

    public List<Tag> readTagArray(JsonReader reader) {
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

    public Tag readTag(JsonReader reader) {
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

    public UserGroup readUserGroup(JsonReader reader) {
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
                    name = reader.nextString();
                } else if (reader.equals(JsonKeys.GROUP_DESCRIPTION)) {
                    description = reader.nextString();
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
                Log.d("response", decodedString);

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
                Log.d("user instanz", user.toString());
                return user;

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("fehler", e.toString());
            System.out.println(e.getMessage());
        }

        return user;
    }
}
