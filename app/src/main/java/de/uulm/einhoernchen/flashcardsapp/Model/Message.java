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

        /**
         * Converts string to Enum
         *
         * @author Jonas Kraus jonas.kraus@uni-ulm.de
         * @since 2017-02-17
         *
         * @param type
         * @return
         */
        public static MessageType convert(String type) {

            if (type.equals(DECK_CHALLENGE_MESSAGE.toString())) {

                return DECK_CHALLENGE_MESSAGE;
            }

            return DECK_CHALLENGE_MESSAGE;
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
    private long sender;

    // For parsing and faster access to the items
    private User receipientUser;
    private User senderUser;
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
     * @param sender
     */
    public Message(long id, MessageType messageType, long receipient, String content, long created, long targetDeck, long sender) {
        this.id = id;
        this.messageType = messageType;
        this.receipient = receipient;
        this.content = content;
        this.created = created;
        this.targetDeck = targetDeck;
        this.sender = sender;
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
     * @param sender
     */
    public Message(MessageType messageType, long receipient, String content, long created, long targetDeck, long sender) {
        this.messageType = messageType;
        this.receipient = receipient;
        this.content = content;
        this.created = created;
        this.targetDeck = targetDeck;
        this.sender = sender;
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
    public Message(long id, MessageType messageType, User recipient, String content, long created, CardDeck targetDeck, User senderUser) {
        this.id = id;

        this.messageType = messageType;
        this.receipientUser = recipient;
        this.content = content;
        this.created = created;
        this.targetCardDeck = targetDeck;
        this.senderUser = senderUser;
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

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public User getReceipientUser() {
        return receipientUser;
    }

    public void setReceipientUser(User receipientUser) {
        this.receipientUser = receipientUser;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public CardDeck getTargetCardDeck() {
        return targetCardDeck;
    }

    public void setTargetCardDeck(CardDeck targetCardDeck) {
        this.targetCardDeck = targetCardDeck;
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
