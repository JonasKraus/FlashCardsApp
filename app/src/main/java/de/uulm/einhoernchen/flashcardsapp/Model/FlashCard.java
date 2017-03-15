/**
 *
 */
package de.uulm.einhoernchen.flashcardsapp.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *
 */
@JsonPropertyOrder({ JsonKeys.FLASHCARD_ID})
public class FlashCard {
    @JsonProperty(JsonKeys.FLASHCARD_ID)
    private long id;

    @JsonProperty(JsonKeys.RATING)
    private int rating = 0;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_CREATED)
    private Date created;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_UPDATED)
    private Date lastUpdated;

    // TODO: 11/07/16 add Catalogue(CardDeck)
    private List<Tag> tags;

    @JsonProperty(JsonKeys.FLASHCARD_QUESTION)
    private Question question;

    @JsonProperty(JsonKeys.FLASHCARD_ANSWERS)
    private List<Answer> answers;

    @JsonProperty(JsonKeys.AUTHOR)
    private User author;

    @JsonIgnore
    private CardDeck deck;

    @JsonProperty(JsonKeys.FLASHCARD_MULTIPLE_CHOICE)
    private boolean multipleChoice;

    @JsonIgnore
    private boolean isSelected;

    @JsonIgnore
    private boolean marked;
    private long selectionDate;


    /**
     * Constructor for creating a new card by the view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param author
     * @param answers
     * @param question
     * @param multipleChoice
     */
    public FlashCard(User author, List<Answer> answers, Question question, boolean multipleChoice) {
        this.author = author;
        if(answers!=null){
            this.answers = answers;
        }
        if(question!=null){
            this.question = question;
        }
        this.multipleChoice = multipleChoice;
        this.tags = new ArrayList<Tag>();
    }


    /**
     * For json parser
     *
     * @param id
     * @param tags
     * @param rating
     * @param created
     * @param lastUpdated
     * @param question
     * @param answers
     * @param author
     * @param multipleChoice
     */
    public FlashCard(long id, List<Tag> tags, int rating, Date created, Date lastUpdated, Question question, List<Answer> answers, User author, boolean multipleChoice) {
        this.id = id;

        if (tags == null) {

            tags = new ArrayList<>();
        }

        this.tags = tags;
        this.rating = rating;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.question = question;
        this.answers = answers;
        this.author = author;
        this.multipleChoice = multipleChoice;
        this.marked = false;

    }


    /**
     * Creates a card with a bookmark
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-27
     *
     * @param id
     * @param tags
     * @param rating
     * @param created
     * @param lastUpdated
     * @param question
     * @param answers
     * @param author
     * @param multipleChoice
     * @param marked
     */
    public FlashCard(long id, List<Tag> tags, int rating, Date created, Date lastUpdated, Question question, List<Answer> answers, User author, boolean multipleChoice, boolean marked) {

        this.id = id;

        if (tags == null) {

            tags = new ArrayList<>();
        }

        this.tags = tags;
        this.rating = rating;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.question = question;
        this.answers = answers;
        this.author = author;
        this.multipleChoice = multipleChoice;
        this.marked = marked;
    }


    /**
     * Sets the question of this card to a specific question object and updates the flashcard in the DB.
     * @param question
     */
    public void setQuestion(Question question) {
        System.out.println("Flashcard: setQuestionText q="+question);
        this.question = question;
    }


    @Override
    public String toString() {
        return "FlashCard{" +
                "id=" + id +
                ", rating=" + rating +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                ", author=" + author +
                ", multipleChoice=" + multipleChoice +
                '}';
    }


    public String toFilterString() {
        return "" +
                "" + rating +
                "" + created +
                "" + lastUpdated +
                "" + author +
                "" + getQuestion().toFilterString();
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLastUpdatedString() {
        // TODO to be implemented
        if (this.lastUpdated == null) {

            return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
        }

        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format( lastUpdated.getTime());
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;

    }

    public CardDeck getDeck() {
        return deck;
    }

    public void setDeck(CardDeck deck) {
        this.deck = deck;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    @JsonIgnore
    public boolean isSelected() {
        return isSelected;
    }
    @JsonIgnore
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    @JsonIgnore
    public boolean isMarked() {
        return marked;
    }
    @JsonIgnore
    public void setMarked(boolean marked) {
        this.marked = marked;
    }


    public List<Tag> getTags() {
        return tags;
    }


    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


    /**
     * Generates rating string for view usage
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

    public void setSelectionDate(long selectionDate) {
        this.selectionDate = selectionDate;
    }

    public long getSelectionDate() {
        return selectionDate;
    }
}

