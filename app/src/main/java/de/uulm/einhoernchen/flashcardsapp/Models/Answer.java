package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;
import java.util.Date;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 17/06/16.
 */

public class Answer {

    private long id;
    private String answerText;
    private String hintText;
    private URI mediaURI;
    private User author;

    private FlashCard card;
    private Date created;
    private int rating;

    public Answer(String answerText, String hintText, User author) {
        this.answerText = answerText;
        this.hintText = hintText;
        this.author = author;
    }


    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answerText='" + answerText + '\'' +
                ", hintText='" + hintText + '\'' +
                ", mediaURI=" + mediaURI +
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

    public URI getMediaURI() {
        return mediaURI;
    }

    public void setMediaURI(URI mediaURI) {
        this.mediaURI = mediaURI;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public FlashCard getCard() {
        return card;
    }

    public Date getCreated() {
        return created;
    }

    public void setCard(FlashCard card) {
        this.card = card;
    }
}
