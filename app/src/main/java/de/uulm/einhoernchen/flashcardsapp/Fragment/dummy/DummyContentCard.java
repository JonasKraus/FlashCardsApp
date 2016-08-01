package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCard {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<FlashCard> ITEMS = new ArrayList<FlashCard>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, FlashCard> ITEM_MAP = new HashMap<String, FlashCard>();

    private static final int COUNT = 250;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyFlashCard(i));
        }
    }

    private static void addItem(FlashCard item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId()+"", item);
    }

    private static FlashCard createDummyFlashCard(int position) {
        Random rand = new Random();
        User author = new User((long)position,"User "+position,"","user"+position+"@flashcards.de",rand.nextInt(100), 0l, "");
        Question question = new Question("Item Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ", author);
        Answer answer = new Answer("consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ","hint ....."+position, author);
        List<String> tags = new ArrayList<>();
        for (int i = 0; i <= position; i++) {
            tags.add("tag"+i);
        }
        List<Answer>answers = new ArrayList<>();
        answers.add(answer);
        FlashCard flashCard = new FlashCard(new Date(), question, answers, author,false);

        return flashCard;
    }


}
