package de.uulm.einhoernchen.flashcardsapp.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private String uri;

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

    // if the answer belongs to a card with multiple choice
    // relevant for creating a card
    private Boolean multipleChoiceAnswer = null;

    public Answer(String answerText, String hintText, User author) {
        this.answerText = answerText;
        this.hintText = hintText;
        this.author = author;
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-16
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
    public Answer(long id, boolean correct, String text, String hint, String uri, User author, Date created, Date lastUpdated, int rating, boolean answerCorrect) {
        this.id = id;
        this.isCorrect = correct; // TODO redundant??
        this.answerText = text;
        this.hintText = hint;
        this.uri = uri;
        this.author = author;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.rating = rating;
        this.isCorrect = answerCorrect;
    }


    /**
     * Constructor for creating a new answer and to send it to an asynctask to save it on the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param text
     * @param hint
     * @param uri
     * @param isCorrect
     * @param user
     */
    public Answer(String text, String hint, String uri, boolean isCorrect, User user, boolean multipleChoiceAnswer) {
        this.answerText = text;
        this.hintText = hint;
        this.uri = uri;
        this.isCorrect = isCorrect;
        this.author = user;
        this.multipleChoiceAnswer = multipleChoiceAnswer;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
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

    public String getLastUpdatedString() {
        // TODO to be implemented

        if (this.lastUpdated == null) {

            return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
        }

        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format( lastUpdated.getTime());
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setCard(FlashCard card) {
        this.card = card;
    }

    public Boolean isMultipleChoiceAnswer() {
        return multipleChoiceAnswer;
    }
}
