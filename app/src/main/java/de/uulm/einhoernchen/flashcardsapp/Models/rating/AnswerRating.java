package de.uulm.einhoernchen.flashcardsapp.Models.rating;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.User;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;


/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 */
public class AnswerRating extends Rating{
    @JsonProperty(JsonKeys.ANSWER)
    protected Answer ratedAnswer;

    public AnswerRating(User author, Answer ratedAnswer, int ratingModifier ) {
            this.ratedAnswer=ratedAnswer;
            this.author=author;
            this.ratingModifier=ratingModifier;
    }


    public Answer getRatedAnswer() {
        return ratedAnswer;
    }

    /**
     * Checks if a combination of user and answer is already in the database.
     * @return false if no such combination exists.
     */
    public static boolean exists(User author, Answer answer){
        // @TODO to be implemented
        return false;
    }

    /**
     * Changes the rating to either add or substract the ratingmodifier. Updates the answer object to save those changes.
     */
    public void apply(){
        //System.out.println("Modifying rating of answer="+ ratedAnswer.getId()+": "+ratedAnswer.getRating()+" to: "+(ratedAnswer.getRating()+ratingModifier));
        // @TODO to be implemented
        //ratedAnswer.updateRating(ratingModifier);
        //ratedAnswer.update();
    }

    /**
     * Changes the rating to either add or substract the ratingmodifier. Updates the answer object to save those changes.
     */
    private void compensate(){
//        System.out.println("Compensating rating of answer="+ ratedAnswer.getId()+": "+ratedAnswer.getRating()+" to: "+(ratedAnswer.getRating()-ratingModifier));
        // @TODO to be implemented
        //ratedAnswer.updateRating(-1*ratingModifier);
        //ratedAnswer.update();
    }

    @Override
    public String toString(){
        return "[id="+id+", author="+author+", ratingModifier="+ratingModifier+", ratedAnswer="+ratedAnswer+"]";
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
