package de.uulm.einhoernchen.flashcardsapp.Util;

import android.util.JsonReader;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.User;

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
                //cardDeckLis.add(readMessage(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardDeckLis;
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
