/**
 *
 */
package de.uulm.einhoernchen.flashcardsapp.Models;


import java.util.Date;
import java.util.List;



/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *
 */

public class FlashCard {

    private long id;
    private int rating;
    private Date created;
    private Date lastUpdated;
    private List<Tag> tags;

    private Question question;
    private List<Answer> answers;
    private User author;
    private boolean multipleChoice;

    private boolean isSelected;
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
                if(a!=null) {
                    a.setCard(this);
                }
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
                if(a!=null) {
                    a.setCard(this);
                }
            }
        }
        if(question!=null){
            this.question = question;
        }
        this.author = author;
        this.multipleChoice = multipleChoice;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isMarked() {
        return isMarked;
    }

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
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
