package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetCarddeck extends AsyncTask<Long, Long, List<CardDeck>> {

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseCarddeck {
        List<CardDeck> processFinish(List<CardDeck> cardDecks);
    }

    public AsyncResponseCarddeck delegate = null;
    private final Long parentId;

    public AsyncGetCarddeck(Long parentId, AsyncResponseCarddeck delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<CardDeck> doInBackground(Long... params) {

        List<CardDeck> cardDecks = new ArrayList<>();

        Log.d("Beginn get Carddecks", "parent id " + this.parentId);

        String urlString = "http://192.168.0.8:9000/cardDecks"; // URL to call TODO nach parentId wählen

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));

            String decodedString;

            while ((decodedString = in.readLine()) != null) {
                Log.d("response", decodedString);

                int response = urlConnection.getResponseCode();

                if (response >= 200 && response <=399){

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(decodedString);
                    //user = new User(id,root.get("avatar").asText(), root.get("name").asText(), "", root.get("email").asText(), root.get("rating").asInt(), root.get("created").asText(), root.get("lastLogin").asText());
                    Log.d("response root", root.toString());
                   // return user;
                    return cardDecks;

                } else {

                }
            }
            in.close();

        } catch (Exception e) {

            Log.e("fehler", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<CardDeck> cardDecks) {
        super.onPostExecute(cardDecks);
        if (cardDecks != null && cardDecks.size() > 0) {

            delegate.processFinish(cardDecks);

        } else {
            // TODO just for testing purpose
            cardDecks = new ArrayList<>();
            for (int i = 0; i < 100; i ++) {
                cardDecks.add(i, new CardDeck("deck "+ i, "beschreibung " + i));
            }

            Log.d("onPostExecute num:", cardDecks.size() + "");
            delegate.processFinish(cardDecks);
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
