/**
 *
 */
package de.uulm.einhoernchen.flashcardsapp.Models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private int rating;

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

    @JsonProperty(JsonKeys.FLASHCARD_MULTIPLE_CHOICE)
    private boolean multipleChoice;

    @JsonIgnore
    private boolean isSelected;

    @JsonIgnore
    private boolean isMarked;

    public FlashCard(User author, boolean multipleChoice, List<String> tags) {
        this.author = author;
        this.multipleChoice = multipleChoice;
    }

    public FlashCard(User author, List<Answer> answers, Question question, boolean multipleChoice) {
        this.author = author;
        if(answers!=null){
            this.answers = answers;
            for (Answer a: answers) {
                if(a!=null){
                    a.setCard(this);
                    a.update();}
            }
        }
        if(question!=null){
            this.question = question;
        }
        this.multipleChoice = multipleChoice;
    }

    public FlashCard(Date created, Question question, List<Answer> answers, User author, boolean multipleChoice) {
        this.created = created;
        if(answers!=null){
            this.answers = answers;
            for (Answer a: answers) {
                if(a!=null){
                    a.setCard(this);
                    a.update();}
            }
        }
        if(question!=null){
            this.question = question;
        }
        this.author = author;
        this.multipleChoice = multipleChoice;
    }

    public FlashCard(FlashCard requestObject) {
        this.author=requestObject.getAuthor();
        this.answers=requestObject.getAnswers();
        this.question=requestObject.getQuestion();
        this.multipleChoice =requestObject.isMultipleChoice();
        this.tags=requestObject.getTags();
    }

    /**
     * Adds one answer to this specific flashcard, updates the flashcards in the DB.
     * @param answer
     */
    public void addAnswer(Answer answer){
        System.out.println("Flashcard: addAnswer a="+answer);
        if(answer!=null && !this.answers.contains(answer)){
            this.answers.add(answer);
            answer.setCard(this);
            this.update();
        }
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

    public Date getLastUpdated() {
        return lastUpdated;
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
        return isMarked;
    }
    @JsonIgnore
    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public void addTag(Tag tag){
        if(!tags.contains(tag)){
            tags.add(tag);
            tag.addFlashCard(this);
        }
    }

    public List<Tag> getTags() {
//        for (Tag t: tags) {
//            System.out.print(t.getName());
//        }
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds the given rating to the current rating, updates this instance and calls the function on the corresponding user.
     * @param ratingModifier
     */
    public void updateRating(int ratingModifier){
        this.rating+=ratingModifier;
        this.update();
        //update user as well, work on the newest data from the db, not our local reference.
        // User.find.byId(author.getId()).updateRating(ratingModifier);
    }


    public void delete(){
        //Get all tags and unlink them from this card. Tag still exists to this point.
        for (Tag tmptag : tags) {
            tmptag.removeFlashCard(this);
            if (tmptag.getCards().size() == 0) {
                // TODO: 01/07/16 do we want to delete if no reference to the tag exists?
            }
            System.out.println("Removing link to tag=" + tmptag);
        }
        // @TODO to be implemented super.delete();
    }

    public static void update() {
        // @TODO to be implemented
    }
}

