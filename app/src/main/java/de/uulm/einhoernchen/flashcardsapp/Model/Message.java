package de.uulm.einhoernchen.flashcardsapp.Model;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.15
 */

public class Message {

    public enum MessageType {
        DECK_CHALLENGE_MESSAGE("deckChallengeMessage");

        private final String text;

        private MessageType (final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private long id;
    private MessageType messageType;
    private long receipient;
    private String content;
    private long created;
    private long targetDeck;


    // For parsing and faster access to the items
    private User receipientUser;
    private CardDeck targetCardDeck;


    /**
     * Constructor for a received message
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-15
     *
     * @param id
     * @param messageType
     * @param receipient
     * @param content
     * @param created
     * @param targetDeck
     */
    public Message(long id, MessageType messageType, long receipient, String content, long created, long targetDeck) {
        this.id = id;
        this.messageType = messageType;
        this.receipient = receipient;
        this.content = content;
        this.created = created;
        this.targetDeck = targetDeck;
    }


    /**
     * Constructor for constructing a message and send it
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-15
     *
     * @param messageType
     * @param receipient
     * @param content
     * @param created
     * @param targetDeck
     */
    public Message(MessageType messageType, long receipient, String content, long created, long targetDeck) {
        this.messageType = messageType;
        this.receipient = receipient;
        this.content = content;
        this.created = created;
        this.targetDeck = targetDeck;
    }


    /**
     * Constructor for the json parser
     * uses directly the objects of the given ids
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param id
     * @param messageType
     * @param recipient
     * @param content
     * @param created
     * @param targetDeck
     */
    public Message(long id, MessageType messageType, User recipient, String content, long created, CardDeck targetDeck) {

        this.messageType = messageType;
        this.receipientUser = recipient;
        this.content = content;
        this.created = created;
        this.targetCardDeck = targetDeck;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public long getReceipient() {
        return receipient;
    }

    public void setReceipient(long receipient) {
        this.receipient = receipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getTargetDeck() {
        return targetDeck;
    }

    public void setTargetDeck(long targetDeck) {
        this.targetDeck = targetDeck;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageType=" + messageType +
                ", receipient=" + receipient +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", targetDeck=" + targetDeck +
                '}';
    }
}
