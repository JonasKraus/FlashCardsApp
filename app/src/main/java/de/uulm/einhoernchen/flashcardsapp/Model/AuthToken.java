package de.uulm.einhoernchen.flashcardsapp.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 */
public class AuthToken {
    @JsonProperty(JsonKeys.TOKEN_ID)
    private Long id;

    @JsonProperty(JsonKeys.TOKEN_USER)
    User user;

    @JsonProperty(JsonKeys.TOKEN)
    String token;


    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    @JsonProperty(JsonKeys.DATE_CREATED)
    private Date created;

    /**
     * Create a new auth token, make sure it is unique.
     * @param user
     */
    public AuthToken(User user) {
        this.user = user;
        //create new tokens while we find that it is already in use. Should not happen theoretically.
        do{
//            token = UUID.randomUUID().toString();
            try {
                token=nextBase64String(32);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }while(true /*@TODO to be implemented*/);
    }

    /**
     * Returns one random sequence of characters of length n in UTF-8.
     * @param n length of the returned String
     * @return random sequence of characters
     * @throws UnsupportedEncodingException
     */
    private String nextBase64String(int n) throws UnsupportedEncodingException {
        SecureRandom csprng = new SecureRandom();
        // NIST SP800-90A recommends a seed length of 440 bits (i.e. 55 bytes)
        csprng.setSeed(csprng.generateSeed(55));
        byte[] bytes = new byte[n];
        csprng.nextBytes(bytes);
        byte[] encoded = null;// @TODO to be implemented Base64.getUrlEncoder().encode(bytes);
        return new String(encoded,"UTF-8");
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", created=" + created +
                '}';
    }

    public static void delete() {
        // @TODO to be implemented
    }
}
