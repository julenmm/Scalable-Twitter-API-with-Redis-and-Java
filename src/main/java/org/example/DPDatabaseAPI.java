package org.example;

import java.util.List;

/**
 * The DPDatabaseAPI interface defines the operations for interacting with a database.
 * It includes methods for creating a temporary database, inserting tweets and follows relationships,
 * and retrieving user IDs and tweet timelines.
 */
public interface DPDatabaseAPI {


    /**
     * Inserts a Tweet record into the database.
     *
     * @param tweet The Tweet object containing user ID and tweet text to be inserted.
     */
    void insertTweet(Tweet tweet);

    /**
     * Inserts a Follows record into the database.
     *
     * @param follows The Follows object representing a user-follows-user relationship to be inserted.
     */
    void insertFollows(Follows follows);

    /**
     * Retrieves a list of all unique user IDs from the database.
     *
     * @return A List of integer user IDs.
     */
    List<Integer> getAllUserIds();

    /**
     * Retrieves a timeline of tweets for a specified user.
     * The timeline consists of the 10 most recent tweets from users followed by the given user.
     *
     * @param userId The user ID for whom the timeline is to be retrieved.
     * @return A List of Tweet objects representing the user's timeline.
     */
    List<Tweet> retrieveTimeline(int userId);

    /**
     * Closes the database connection.
     * This method ensures that the connection to the SQLite database is properly closed.
     */
    void closeConnection();

    /**
     * Updates timelines for followers after inserting a Tweet.
     *
     * @param tweet   The Tweet object that was inserted.
     * @param tweetId The unique ID of the inserted tweet.
     */
    void updateTimeLines(Tweet tweet, Long tweetId);
}
