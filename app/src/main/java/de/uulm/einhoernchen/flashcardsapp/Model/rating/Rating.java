package de.uulm.einhoernchen.flashcardsapp.Model.rating;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;


/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 */
public abstract class Rating {

    protected long id;
    @JsonProperty(JsonKeys.AUTHOR)
    protected User author;
    //VoteType for 5* vs. +-1 style?
    @JsonProperty(JsonKeys.RATING_MODIFIER)
    protected int ratingModifier;

    public long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public int getRatingModifier() {
        return ratingModifier;
    }


}
