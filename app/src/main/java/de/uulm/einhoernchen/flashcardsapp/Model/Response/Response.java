package de.uulm.einhoernchen.flashcardsapp.Model.Response;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.03.16
 */

public class Response {

    private int statuscode;
    private String description;
    private String token;
    private Long userId;
    // Should be ratingId
    private Long id;



    /**
     * Constructor for token response
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-16
     *
     * @param statuscode
     * @param description
     * @param id
     * @param token
     * @param userId
     */
    public Response(int statuscode, String description, String token, Long id, Long userId) {
        this.statuscode = statuscode;
        this.description = description;
        this.id = id;
        this.token = token;
        this.userId = userId;
    }



    /**
     * Standard constructor
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-16
     *
     * @param statuscode
     * @param description
     */
    public Response(int statuscode, String description) {
        this.statuscode = statuscode;
        this.description = description;
    }



    public int getStatuscode() {
        return statuscode;
    }



    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }



    public String getDescription() {
        return description;
    }



    public void setDescription(String description) {
        this.description = description;
    }



    public String getToken() {
        return token;
    }



    public void setToken(String token) {
        this.token = token;
    }



    public Long getUserId() {
        return userId;
    }



    public void setUserId(Long userId) {
        this.userId = userId;
    }



    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }



    /**
     * Custom toString to only display the set values
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-16
     *
     * @return
     */
    @Override
    public String toString() {

        String str = "Response{";

        str += "statuscode=" + statuscode;
        str += ", description='" + description + '\'';
        str += token != null ? ", token='" + token + '\'' : "";
        str += userId != null ? ", userId=" + userId : "";
        str += id != null ? ", id=" + id : "";


        return str;
    }

}
