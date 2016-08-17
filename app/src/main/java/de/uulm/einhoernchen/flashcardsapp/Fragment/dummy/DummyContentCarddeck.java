package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCarddeck {

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
            //addItem(createDummyFlashCard(i));
        }
    }

    private static void addItem(FlashCard item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId()+"", item);
    }

    public static List<CardDeck> collectItemsFromServer(long parentId) {

        // TODO List
        List<CardDeck> cardDecks = new ArrayList<>();
        for (int i = 0; i < 100; i ++) {
            cardDecks.add(i, new CardDeck("deck "+ i, "beschreibung " + i));
        }

        return cardDecks;
    }

}
