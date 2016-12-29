package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private boolean visible;

    @JsonProperty(JsonKeys.CARDDECK_GROUP)
    private UserGroup userGroup;

    @JsonProperty(JsonKeys.CARDDECK_NAME)
    private String name;

    @JsonProperty(JsonKeys.CARDDECK_DESCRIPTION)
    private String description;

    private long selectionDate;

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
        this.userGroup=otherDeck.getUserGroup();
        userGroup.addDeck(this);
        this.visible=otherDeck.isVisible();
    }

    private int rating = 0;

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param id
     * @param visible
     * @param userGroup
     * @param name
     * @param description
     * @param cards
     */
    public CardDeck(long id, boolean visible, UserGroup userGroup, String name, String description, List<FlashCard> cards) {
        this.id = id;
        this.visible = visible;
        this.userGroup = userGroup;
        this.name = name;
        this.description = description;
        this.cards = cards;
        userGroup.addDeck(this);
    }

    /**
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-01
     *
     * @param id
     * @param visible
     * @param userGroup
     * @param name
     * @param description
     */
    public CardDeck(long id, boolean visible, UserGroup userGroup, String name, String description, long selectionDate) {
        this.id = id;
        this.visible = visible;
        this.userGroup = userGroup;
        this.name = name;
        this.description = description;
        this.selectionDate = selectionDate;
    }

    public long getSelectionDate() {
        return selectionDate;
    }

    public void setSelectionDate(long selectionDate) {
        this.selectionDate = selectionDate;
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
        for (FlashCard c:this.cards) {
            c.setDeck(null);
        }
        this.cards = cards;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void delete() {
        userGroup.deleteDeck(this);
        for (FlashCard card : cards) {
/*            card.setDeck(null);
            card.update();*/
            card.delete();
        }
        String gIds="";

        StringBuilder b = new StringBuilder();

        //userGroup.getDecks().forEach(deck->b.append(deck.getId()+"; "));  TODO to be implemented

        //super.delete(); TODO to be implemented
    }

    @Override
    public String toString() {
        return "CardDeck{" +
                "id=" + id +
                ", visible=" + visible +
                ", userGroup=" + userGroup +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cards=" + cards +
                '}';
    }


    public int getRating() {
        // TODO to be implemented
        return rating;
    }

    /**
     * Method to show rating in a textView
     *
     * @return
     */
    public String getRatingForView() {
        if (this.getRating() >= 0) {
            return this.getRating() + "";
        } else {
            return this.getRating() + "";
        }
    }
}
