package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 09/08/16.
 */
public class CardDeck {

    @JsonProperty(JsonKeys.CARDDECK_ID)
    private long id;

    @JsonProperty(JsonKeys.CARDDECK_NAME)
    private String name;
    @JsonProperty(JsonKeys.CARDDECK_DESCRIPTION)
    private String description;

    //this cascades from the "tag" to "join_cards_tag" - e.g. tag.delete -> delete evey entry with tag.id
    @JsonProperty(JsonKeys.CARDDECK_CARDS)
    private List<FlashCard> cards;


    public CardDeck(String name) {
        this.name = name;
    }

    public CardDeck(String name, String description, List<FlashCard> cards) {
        this.name = name;
        this.description = description;
        this.cards = cards;
    }

    public CardDeck(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CardDeck(CardDeck otherDeck) {
        this.name = otherDeck.getName();
        this.description = otherDeck.getDescription();
        this.cards = otherDeck.getCards();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public List<FlashCard> getCards() {
        return cards;
    }

    public void setCards(List<FlashCard> cards) {
        this.cards = cards;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void delete() {
        for (FlashCard card : cards) {
/*            card.setDeck(null);
            card.update();*/
            card.delete();
        }
        // TODO to be implemented
    }

    @Override
    public String toString() {
        return "CardDeck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cards=" + cards +
                '}';
    }
}
