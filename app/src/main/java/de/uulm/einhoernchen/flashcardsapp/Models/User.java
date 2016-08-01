package de.uulm.einhoernchen.flashcardsapp.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
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

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_CREATED)
    // @TODO check correct Date private Date created;
    private String created;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_LAST_LOGIN)
    // @TODO check correct Date private Date lastLogin;
    private String lastLogin;

    @JsonProperty(JsonKeys.USER_GROUP)
    private UserGroup group;

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


    public User(Long userId, String name, String password, String email, int rating, String created) {
        this.id = userId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.created = created;
    }

    public User(Long id, String avatar, String name, String password, String email, int rating, String created, String lastLogin) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.created = created;
        this.lastLogin = lastLogin;
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
                + created + ", group=" + group + "]";
    }

    public String toJsonString() {
        return "[{\"userId\":" + id + ", \"name\":\"" + name + "\", \"password\":\"" + password
                + "\", \"email\":\"" + email + "\", \"rating\":" + rating + ", \"created\":\""
                + created + "\", \"group\":" + group + "}]";
    }


    public String getCreated() {
        return created;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
//        System.out.println(">> setting usergroup from "+this.getGroup()+" to "+group);
        this.group = group;
        //update group definition as well.
        if (group!=null && !group.getUsers().contains(this)) {
            group.addUser(this);
        }
        this.update();
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
            this.update();
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
        this.update();
    }

    public void deleteToken(AuthToken authToken){
        if(authTokenList.remove(authToken))
            authToken.delete();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        avatar = avatar == null ? "" : avatar;
        this.avatar = avatar;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
    /**
     * Adds the given rating to the current rating, updates this instance.
     * @param ratingModifier
     */
    public void updateRating(int ratingModifier){
        System.out.println(new Date()+ " Modifying rating from="+rating+" by modifier="+ratingModifier+" to="+(rating+ratingModifier));
        this.rating+=ratingModifier;
        this.update();
    }

    public void delete(){
        //Get all tags and unlink them from this card. Tag still exists to this point.
        List<Answer> givenAnswers = null; // @TODO to be implemented
        System.out.println("Answers from the user has size=" + givenAnswers.size());

        for (Answer a : givenAnswers) {
            System.out.println(">> Trying to null author on answer a=" + a + " where author was: " + a.getAuthor());
            a.setAuthor(null);
            a.update();
        }


        List<FlashCard> cards = null; // @TODO to be implemented
        System.out.println("Created cards list has size=" + cards.size());

        for (FlashCard c : cards) {
            System.out.println(">> Trying to null author on card c=" + c + " where author was: " + c.getAuthor());
            c.setAuthor(null);
            c.update();
        }


        List<Question> questions = null; // @TODO to be implemented
        System.out.println("Questions from the user has size=" + questions.size());
        for (Question q : questions) {
            System.out.println(">> Trying to null author on question q=" + q + " where author was: " + q.getAuthor());
            q.setAuthor(null);
            q.update();
        }
        // @TODO to be implemented
        //super.delete();
    }

    public static void update() {
        // @TODO to be implemented
    }
}
