package de.uulm.einhoernchen.flashcardsapp.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 *         on 13/06/16.
 */
@JsonPropertyOrder({ JsonKeys.GROUP_ID}) //ensure that groupID is the first element in json.
public class UserGroup {

	@JsonProperty(JsonKeys.GROUP_ID)
	private Long id;

    @JsonProperty(JsonKeys.GROUP_NAME)
    private String name;

    //TODO: Delete this attribute
    @JsonProperty(JsonKeys.GROUP_DESCRIPTION)
    private String description;

    @JsonIgnore    // to prevent endless recursion.
	private List<User> users;


	public UserGroup(String name, String description, List<User> users) {
		super();
		this.name = name;
		this.description = description;
		this.users = users;
	}

	public UserGroup(UserGroup requestGroup) {
		super();
		this.name=requestGroup.getName();
		this.description=requestGroup.getDescription();
		this.users=requestGroup.getUsers();
		for(User u:users){
//            System.out.println(">> updating user: "+u);
			u.setGroup(this);
		}
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return users;
	}

	/**
	 * Replaces the current users with the given users.
	 * @param users
     */
	public void setUsers(List<User> users) {
		this.users = users;
		for(User u: users){
			u.setGroup(this);
		}
	}

	/**
	 * Adds one user to this group, updates the user's group as well.
	 * @param user
     */
	public void addUser(User user) {
		if (!users.contains(user)) {
			users.add(user);
			user.setGroup(this);
			// @TODO to be implemented
			//this.save();
		}
	}

	@Override
	public String toString() {
		return "UserGroup [id=" + id + ", name=" + name + ", description="
				+ description+"]";
	}

    /**
     * Removes a specific user from the users of this group.
     * @param user
     */
    public void removeUser(User user) {
        if(users.contains(user)){
            users.remove(user);
			// @TODO to be implemented
			//this.update();
        }
    }
}