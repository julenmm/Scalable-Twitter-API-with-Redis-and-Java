package org.example;

/**
 * Represents a tweet with a user ID and text content.
 */
public class Tweet {
    private int userId;
    private String text;

    /**
     * Constructs a Tweet instance.
     *
     * @param userId The ID of the user who posted the tweet.
     * @param text   The text content of the tweet.
     */
    public Tweet(int userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    /**
     * Retrieves the user ID associated with the tweet.
     *
     * @return The user ID.
     */
    public int getUserId(){
        return this.userId;
    }

    /**
     * Retrieves the text content of the tweet.
     *
     * @return The text of the tweet.
     */
    public String getText() {
        return this.text;
    }
}
