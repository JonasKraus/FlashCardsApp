package de.uulm.einhoernchen.flashcardsapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.Fragment.dummy.DummyContentCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncGetFlashCard extends AsyncTask<Long, Long, List<FlashCard>> {

    /**
     * Interface to receive the carddecks in the activity that called this async task
     */
    public interface AsyncResponseFlashCard {
        void processFinish(List<FlashCard> flashCards);
    }

    public AsyncResponseFlashCard delegate = null;
    private final Long parentId;

    public AsyncGetFlashCard(Long parentId, AsyncResponseFlashCard delegate) {
        this.parentId = parentId;
        this.delegate = delegate;
    }

    @Override
    protected List<FlashCard> doInBackground(Long... params) {

        Log.d("Beginn get FlashCard", "parent id " + this.parentId);

        String urlString = "http://192.168.0.8:9000/cards"; // URL to call TODO nach parentId wählen

        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(false); // Important for get request
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            return JsonParser.readFlashCards(urlConnection.getInputStream());

        } catch (Exception e) {

            Log.e("doInBackground Error", e.toString());
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<FlashCard> flashCards) {
        super.onPostExecute(flashCards);

        // TODO for testing only
        // Should collect data from db
        if (flashCards == null || flashCards.size() == 0) {

            List<FlashCard> cards = new ArrayList<>();

            for (int position = 0; position < 100; position++) {
                Log.d("parent id", parentId + "");
                Random rand = new Random();

                User author = new User((long) position, "avatar", "Author: User " + position, "pwd", "user" + position + "@flashcards.de", rand.nextInt(100), new Date().toString(), new Date().toString());
                Question question = new Question("Item Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ", author);
                Answer answer = new Answer("consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ", "hint ....." + position, author);
                List<String> tags = new ArrayList<>();
                for (int i = 0; i <= position; i++) {
                    tags.add("tag" + i);
                }
                List<Answer> answers = new ArrayList<>();
                answers.add(answer);
                FlashCard flashCard = new FlashCard(new Date(), question, answers, author, false);

                cards.add(position, flashCard);
            }

        }

        delegate.processFinish(flashCards);

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}