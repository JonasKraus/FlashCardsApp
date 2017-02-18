package de.uulm.einhoernchen.flashcardsapp.Model;

import java.util.List;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.18
 */

public class Challenge {

    private long id;
    private Message message;
    private CardDeck cardDeck;
    private List<Statistic> statistics;

    // Completed is true if all cards of the deck has a corresponding statistic
    private boolean completed = false;


    /**
     * Constructs a Challenge
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-19
     *
     * @param id
     * @param message
     * @param cardDeck
     * @param statistics
     */
    public Challenge(long id, Message message, CardDeck cardDeck, List<Statistic> statistics, boolean completed) {
        this.id = id;
        this.message = message;
        this.cardDeck = cardDeck;
        this.statistics = statistics;
        this.completed = completed;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public CardDeck getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(CardDeck cardDeck) {
        this.cardDeck = cardDeck;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", message=" + message +
                ", cardDeck=" + cardDeck +
                ", statistics=" + statistics +
                '}';
    }
}
