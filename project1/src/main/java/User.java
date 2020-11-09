package main.java;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final Integer id;
    private final String username;

    public User(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public Integer getId() {return id;}

    public String toString() {
        return "userId = " + id + ", email = " + username;
    }

}
