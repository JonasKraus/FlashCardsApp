package de.uulm.einhoernchen.flashcardsapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 27/06/16.
 */
public class Tag {

    @JsonProperty(JsonKeys.TAG_ID)
    private long id;

    @JsonProperty(JsonKeys.TAG_NAME)
    private String name;
    //this cascade from the "tag" to "join_cards_tag" - e.g. tag.delete -> delete evey entry with tag.id

    @JsonProperty(JsonKeys.TAG_CARDS)
    @JsonIgnore
    private List<FlashCard> cards;


    public Tag(String name) {
        this.name = name;
    }


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @param id
     * @param name
     */
    public Tag(long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cards=" + cards +
                '}';
    }

    public String toStringForView() {
        return "#" + name;
    }

}

