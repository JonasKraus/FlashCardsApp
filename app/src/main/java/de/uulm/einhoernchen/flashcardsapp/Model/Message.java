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
}
