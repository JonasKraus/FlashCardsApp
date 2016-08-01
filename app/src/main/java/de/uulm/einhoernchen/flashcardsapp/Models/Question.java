package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.URISyntaxException;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;


/**
 * Created by fabianwidmann on 17/06/16.
 */
@JsonPropertyOrder({ JsonKeys.QUESTION_ID})
public class Question {

    @JsonProperty(JsonKeys.QUESTION_ID)
    private long id;

    @JsonProperty(JsonKeys.QUESTION_TEXT)
    private String questionText;

    @JsonProperty(JsonKeys.URI)
    private URI uri;

    @JsonProperty(JsonKeys.AUTHOR)
    private User author;
//    @OneToOne(fetch= FetchType.LAZY)
//    @JoinColumn(name="parent_card_id")
//    @JsonIgnore
//    private FlashCard card;

    public Question(String questionText, User author) {
        this.questionText = questionText;
        this.author = author;
    }

    /**
     * Parses a question from the given JsonNode node.
     * @param node the json node to parse
     * @return a question object containing the information
     * @throws URISyntaxException
     */
    public static Question parseQuestion(JsonNode node) throws URISyntaxException {
        User author=null;
        String questionText = null;
        if(node.has(JsonKeys.AUTHOR)){
            if(node.get(JsonKeys.AUTHOR).has(JsonKeys.USER_ID)){
                long uid = node.get(JsonKeys.AUTHOR).get(JsonKeys.USER_ID).asLong();
                author = null; // @TODO to be implemented User.find.byId(uid);
                System.out.println("Search for user with id="+uid+" details="+author);
            }
        }
        if(node.has(JsonKeys.QUESTION_TEXT)){
            questionText=node.get(JsonKeys.QUESTION_TEXT).asText();
        }
        Question question=new Question(questionText, author);

        if(node.has(JsonKeys.URI)){
            question.setUri(new URI(node.get(JsonKeys.URI).asText()));
        }
        return question;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", uri=" + uri +
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

    public static void update() {
        // @TODO to be implemented
    }
}

