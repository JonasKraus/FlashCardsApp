package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.uulm.einhoernchen.flashcardsapp.Util.DateProcessor;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 13/06/16.
 */

@JsonPropertyOrder({ JsonKeys.USER_ID})
public class User {

    @JsonProperty(JsonKeys.USER_ID)
    private Long id;

    @JsonProperty(JsonKeys.USER_AVATAR)
    private String avatar;

    @JsonProperty(JsonKeys.USER_NAME)
    private String name;

    @JsonProperty(JsonKeys.USER_PASSWORD)
    @JsonIgnore
    private String password;

    @JsonProperty(JsonKeys.USER_EMAIL)
    private String email;

    @JsonProperty(JsonKeys.RATING)
    private int rating;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_CREATED)
    private Date created;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_LAST_LOGIN)
    private Date lastLogin;

    @JsonProperty(JsonKeys.USER_GROUPS)
    private List<UserGroup> userGroups;

    @JsonIgnore	// to prevent endless recursion.
    private List<AuthToken> authTokenList;


    public User(String name, String email, String password, int rating) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.rating = rating;
        authTokenList=new ArrayList<>();
    }

    public User(User u) {
        super();
        this.name = u.getName();
        this.email = u.getEmail();
        this.password = u.getPassword();
        this.rating = u.getRating();
        authTokenList = new ArrayList<>();
    }

    public User(Long userId, String name, String password, String email, int rating, String created) {
        this.id = userId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.created = DateProcessor.stringToDate(created);
        authTokenList=new ArrayList<>();
    }

    public User(Long id, String avatar, String name, String password, String email, int rating, String created, String lastLogin) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.created = DateProcessor.stringToDate(created);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateInString = "31-08-1982 10:20:56";
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.lastLogin = date;
        authTokenList=new ArrayList<>();
    }

    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param id
     * @param avatar
     * @param name
     * @param email
     * @param rating
     * @param created
     * @param lastLogin
     * @param groups
     */
    public User(long id, String avatar, String name, String email, int rating, Date created, Date lastLogin, List<UserGroup> groups) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.rating = rating;
        // this.created = DateProcessor.stringToDate(created); TODO passendes Format
        // this.lastLogin = DateProcessor.stringToDate(lastLogin); TODO passendes Format
        this.userGroups = groups;
        authTokenList=new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", password=" + password
                + ", email=" + email + ", rating=" + rating + ", created="
                + created + ", userGroups=" + userGroups + "]";
    }

    public Date getCreated() {
        return created;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
//        System.out.println(">> setting usergroup from "+this.getUserGroups()+" to "+userGroups);
        this.userGroups = userGroups;
        //update userGroups definition as well.
/*		if (userGroups !=null && !userGroups.getUsers().contains(this)) {
			userGroups.addUser(this);
		}*/
        // this.update(); TODO to be implemented
    }

    public List<AuthToken> getAuthTokenList() {
        return authTokenList;
    }

    public void setAuthTokenList(List<AuthToken> authTokenList) {
        this.authTokenList = authTokenList;
    }

    /**
     * Adds one token to the tokenlist, updates this entity.
     * @param token
     */
    public void addAuthToken(AuthToken token){
        if(!authTokenList.contains(token)){
            authTokenList.add(token);
            // this.update();  TODO to be implemented
        }
    }

    /**
     * Deletes all Tokens associated with this entity.
     */
    public void deleteTokens(){
        for (int i=0; i<authTokenList.size(); i++){
            authTokenList.get(i).delete();
        }
        authTokenList=new ArrayList<>();
        //this.update(); TODO to be implemented
    }

    public void deleteToken(AuthToken authToken){
        if(authTokenList.remove(authToken))
            authToken.delete();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    /**
     * Adds the given rating to the current rating, updates this instance.
     * @param ratingModifier
     */
    public void updateRating(int ratingModifier){
        System.out.println(new Date()+ " Modifying rating from="+rating+" by modifier="+ratingModifier+" to="+(rating+ratingModifier));
        this.rating+=ratingModifier;
        //this.update(); TODO to be implemented
    }

    public void delete(){
        //Get all tags and unlink them from this card. Tag still exists to this point.
        /*  TODO to be implemented
        List<Answer> givenAnswers = Answer.find.where().eq(JsonKeys.USER_ID, id).findList();
        System.out.println("Answers from the user has size=" + givenAnswers.size());

        for (Answer a : givenAnswers) {
            System.out.println(">> Trying to null author on answer a=" + a + " where author was: " + a.getUser());
            a.setAuthor(null);
            a.update();
        }


        List<FlashCard> cards = FlashCard.find.where().eq(JsonKeys.USER_ID, id).findList();
        System.out.println("Created cards list has size=" + cards.size());

        for (FlashCard c : cards) {
            System.out.println(">> Trying to null author on card c=" + c + " where author was: " + c.getUser());
            c.setAuthor(null);
            c.update();
        }


        List<Question> questions = Question.find.where().eq(JsonKeys.USER_ID, id).findList();
        System.out.println("Questions from the user has size=" + questions.size());
        for (Question q : questions) {
            System.out.println(">> Trying to null author on question q=" + q + " where author was: " + q.getUser());
            q.setAuthor(null);
            q.update();
        }
        super.delete();
        */
    }

    public void removeGroup(UserGroup userGroup) {
        if (userGroups.contains( userGroup)){
            userGroups.remove(userGroup);
            //this.update(); TODO to be implemented
        }
    }
}
