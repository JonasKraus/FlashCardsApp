package de.uulm.einhoernchen.flashcardsapp.Model;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;


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
    private Uri uri;

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
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @param id
     * @param questionText
     * @param uri
     * @param author
     */
    public Question(long id, String questionText, Uri uri, User author) {
        this.id = id;
        this.questionText = questionText;
        this.uri = uri;
        this.author = author;
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param questionId
     * @param questionText
     * @param mediaURI
     * @param author
     */
    public Question(long questionId, String questionText, String mediaURI, User author) {
        this.id = questionId;
        this.questionText = questionText;
        try {
            if (mediaURI != null) {
                this.uri = Uri.parse(mediaURI);
            } else {
                this.uri = Uri.parse("");
            }
        } catch (Exception e) {
            Log.e("exeption parse uri", e.getMessage());
        }
        this.author = author;
    }


    /**
     * Dummyconstructor
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     *
     * @param text
     * @param uri
     * @param loggedInUser
     */
    public Question(String text, String uri, User loggedInUser) {
        this.questionText = text;
        this.uri = Uri.parse(uri);
        this.author = loggedInUser;
    }

    /**
     * Parses a question from the given JsonNode node.
     * @param node the json node to parse
     * @return a question object containing the information
     */
    public static Question parseQuestion(JsonNode node) {
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
            question.setUri(Uri.parse(node.get(JsonKeys.URI).asText()));
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

    public String toFilterString() {
        return "" +
                "" + questionText +
                "" + uri +
                "" + author;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
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

