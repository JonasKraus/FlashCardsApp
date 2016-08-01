package de.uulm.einhoernchen.flashcardsapp.Models;

import java.net.URI;

/**
 * Created by fabianwidmann on 17/06/16.
 */
public class Question {
    private long id;
    private String questionText;
    private URI mediaURI;
    private User author;
//    @OneToOne(fetch= FetchType.LAZY)
//    @JoinColumn(name="parent_card_id")
//    @JsonIgnore
//    private FlashCard card;


    public Question(String questionText, User author) {
        this.questionText = questionText;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", mediaURI=" + mediaURI +
                ", author=" + author +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
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
}
