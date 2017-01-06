package de.uulm.einhoernchen.flashcardsapp.Model.rating;


import com.fasterxml.jackson.annotation.JsonProperty;

import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 */
public class CardRating extends Rating{

    @JsonProperty(JsonKeys.FLASHCARD)
    protected FlashCard ratedFlashCard;

    public CardRating(User author, FlashCard ratedFlashCard, int ratingModifier ) {
        this.ratedFlashCard = ratedFlashCard;
        this.author=author;
        this.ratingModifier=ratingModifier;
    }

    public FlashCard getRatedFlashCard() {
        return ratedFlashCard;
    }

    /**
     * Changes the rating to either add or substract the ratingmodifier. Updates the answer object to save those changes.
     */
    public void apply(){
//        System.out.println("Modifying rating of ratedFlashCard="+ ratedFlashCard.getId()+": "+ratedFlashCard.getRating()+" to: "+(ratedFlashCard.getRating()+ratingModifier));
        // @TODO to be implemented
        //ratedFlashCard.updateRating(ratingModifier);
        //ratedFlashCard.update();
    }

    /**
     * Changes the rating to either add or substract the ratingmodifier. Updates the answer object to save those changes.
     */
    private void compensate(){
//        System.out.println("Compensating rating of answer="+ ratedFlashCard.getId()+": "+ratedFlashCard.getRating()+" to: "+(ratedFlashCard.getRating()-ratingModifier));
        // @TODO to be implemented
        //ratedFlashCard.updateRating(-1*ratingModifier);
        //ratedFlashCard.update();
    }
    /**
     * Checks if a combination of user and card is already in the database.
     * @return false if no such combination exists.
     */
    public static boolean exists(User author, FlashCard card){
        // @TODO to be implemented
        return true;
    }

    @Override
    public String toString(){
        return "[id="+id+", author="+author+", ratingModifier="+ratingModifier+", ratedFlashCard="+ ratedFlashCard +"]";
    }

    public void save() {
        // @TODO to be implemented
        //super.save();
        apply();
    }

    public void delete() {
        compensate();
        // @TODO to be implemented
        //super.delete();
    }
}
