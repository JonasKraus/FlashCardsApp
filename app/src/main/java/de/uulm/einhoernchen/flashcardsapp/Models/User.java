package de.uulm.einhoernchen.flashcardsapp.Models;

/**
 * Created by jonas-uni on 02.07.2016.
 */
public class User {

    private Long userId;
    private String name;
    private String password;
    private String email;
    private int rating;
    private Long group_id;
    private String created;

    public User(Long userId, String name, String password, String email, int rating, Long group_id, String created) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.group_id = group_id;
        this.created = created;
    }

    public User(Long userId, String name, String email, int rating, Long group_id, String created) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.group_id = group_id;
        this.created = created;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                ", group_id=" + group_id +
                ", created='" + created + '\'' +
                '}';
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
