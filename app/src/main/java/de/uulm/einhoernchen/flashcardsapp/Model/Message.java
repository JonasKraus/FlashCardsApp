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
    private String content;
    private long created;
    private User recipient;
    private User sender;
    private CardDeck targetCardDeck;


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
        this.recipient = recipient;
        this.content = content;
        this.created = created;
        this.targetCardDeck = targetDeck;
        this.sender = senderUser;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
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
                ", receipient=" + recipient.getName() +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", targetDeck=" + targetCardDeck.getName() +
                '}';
    }
}
