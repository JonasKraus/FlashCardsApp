package de.uulm.einhoernchen.flashcardsapp.Models;


import android.util.Log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 17/06/16.
 */
@JsonPropertyOrder({ JsonKeys.ANSWER_ID})
public class Answer {
    @JsonProperty(JsonKeys.ANSWER_ID)
    private long id;

    @JsonProperty(JsonKeys.ANSWER_TEXT)
    private String answerText;

    @JsonProperty(JsonKeys.ANSWER_HINT)
    private String hintText;

    @JsonProperty(JsonKeys.URI)
    private URI uri;

    // TODO: 11/07/16  Ist die Antwort richtig oder falsch?
    @JsonProperty(JsonKeys.AUTHOR)
    private User author;

    @JsonIgnore
    private FlashCard card;

    @JsonProperty(JsonKeys.DATE_CREATED)
    private Date created;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_UPDATED)
    private Date lastUpdated;

    @JsonProperty(JsonKeys.RATING)
    private int rating;

    @JsonProperty(JsonKeys.ANSWER_CORRECT)
    private boolean isCorrect;

    public Answer(String answerText, String hintText, User author) {
        this.answerText = answerText;
        this.hintText = hintText;
        this.author = author;
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param id
     * @param correct
     * @param text
     * @param hint
     * @param uri
     * @param author
     * @param created
     * @param lastUpdated
     * @param rating
     * @param answerCorrect
     */
    public Answer(long id, boolean correct, String text, String hint, URI uri, User author, java.util.Date created, java.util.Date lastUpdated, int rating, boolean answerCorrect) {
        this.id = id;
        this.isCorrect = correct; // TODO redundant??
        this.answerText = text;
        this.hintText = hint;
        this.uri = uri;
        this.author = author;
        // this.created = created; TODO Welches Date und welches Format
        // this.lastUpdated = lastUpdated;
        this.rating = rating;
        this.isCorrect = answerCorrect;
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param answerId
     * @param correct
     * @param answerText
     * @param answerHint
     * @param mediaURI
     * @param author
     * @param created
     * @param lastupdated
     * @param rating
     * @param correct1
     */
    public Answer(long answerId, boolean correct, String answerText, String answerHint, String mediaURI, User author, String created, String lastupdated, int rating, boolean correct1) {
        this.id = answerId;
        this.isCorrect = correct; // TODO redundant??
        this.answerText = answerText;
        this.hintText = answerHint;
        try {
            if (mediaURI != null) {
                this.uri = new URI(mediaURI);
            } else {
                this.uri = new URI("");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("construct Answer", "faild to generate uri from string");
        }
        this.author = author;
        // this.created = created; TODO Welches Date und welches Format
        // this.lastUpdated = lastUpdated;
        this.rating = rating;
        this.isCorrect = correct1;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answerText='" + answerText + '\'' +
                ", hintText='" + hintText + '\'' +
                ", uri=" + uri +
                ", author=" + author +
                ", card=" + card +
                ", created=" + created +
                ", rating=" + rating +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    @JsonIgnore
    public FlashCard getCard() {
        return card;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setCard(FlashCard card) {
        this.card = card;
        // @TODO to be implemented
        //this.update();
    }
    /**
     * Adds the given rating to the current rating, updates this instance and calls the function on the corresponding user.
     * @param ratingModifier
     */
    public void updateRating(int ratingModifier){
        this.rating+=ratingModifier;
        // @TODO to be implemented
        //this.update();
        //update user as well, work on the newest data from the db, not our local reference.
        //User.find.byId(author.getId()).updateRating(ratingModifier);
    }

    public static void update() {
        // @TODO to be implemented
    }

}
