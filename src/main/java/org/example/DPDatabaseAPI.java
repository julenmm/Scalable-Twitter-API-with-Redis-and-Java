package org.example;

import java.util.List;

/**
 * The DPDatabaseAPI interface defines the operations for interacting with a database.
 * It includes methods for creating a temporary database, inserting tweets and follows relationships,
 * and retrieving user IDs and tweet timelines.
 */
public interface DPDatabaseAPI {

    /**
     * Creates a temporary in-memory database and initializes it using SQL statements from a specified file.
     *
     * @param sqlFilePath Path to the SQL file containing the database schema and initialization statements.
     */
    void createTempDataBase(String sqlFilePath);

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
}
