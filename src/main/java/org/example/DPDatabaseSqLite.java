package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the DPDatabaseAPI interface to provide database operations using SQLite.
 * It is responsible for creating a temporary in-memory database, inserting tweets, managing follow relationships,
 * and retrieving user IDs and tweet timelines.
 */
public class DPDatabaseSqLite implements DPDatabaseAPI{

    private Connection connection;

    /**
     * Creates a temporary in-memory SQLite database and initializes it using SQL statements from a specified file.
     *
     * @param sqlFilePath Path to the SQL file containing the database schema and initialization statements.
     */
    @Override
    public void createTempDataBase(String sqlFilePath) {
        try {
            // Establish a connection to the SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            System.out.println("Connection to SQLite has been established.");

            // Read SQL statements from the file and concatenate
            List<String> lines = Files.readAllLines(Paths.get(sqlFilePath));
            String sql = String.join(" ", lines);

            // Execute SQL statements to initialize the database schema
            String[] sqlStatements = sql.split(";"); // Split into individual statements
            for (String statement : sqlStatements) {
                if (!statement.trim().isEmpty()) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(statement);
                    }
                }
            }

            System.out.println("In-memory database has been initialized from SQL file.");
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading SQL file: " + e.getMessage());
        }
    }

    /**
     * Inserts a Tweet record into the database.
     *
     * @param tweet The Tweet object containing user ID and tweet text to be inserted.
     */
    @Override
    public void insertTweet(Tweet tweet) {
        String sql = "INSERT INTO TWEET (user_id, tweet_text) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tweet.getUserId());
            pstmt.setString(2, tweet.getText());
            pstmt.executeUpdate();
            //System.out.println("Tweet inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting tweet: " + e.getMessage());
        }
    }

    /**
     * Inserts a Follows record into the database.
     *
     * @param follows The Follows object representing a user-follows-user relationship to be inserted.
     */
    @Override
    public void insertFollows(Follows follows) {
        String sql = "INSERT INTO FOLLOWS (user_id, follows_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, follows.getUserId());
            pstmt.setInt(2, follows.getFollowsId());
            pstmt.executeUpdate();
            //System.out.println("Follows inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting Follows: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of all unique user IDs from the FOLLOWS table.
     *
     * @return A List of integer user IDs.
     */
    @Override
    public List<Integer> getAllUserIds() {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT DISTINCT user_id FROM FOLLOWS";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user IDs: " + e.getMessage());
        }

        return userIds;
    }

    /**
     * Retrieves a timeline of tweets for a specified user.
     * The timeline consists of the 10 most recent tweets from users followed by the given user.
     *
     * @param userId The user ID for whom the timeline is to be retrieved.
     * @return A List of Tweet objects representing the user's timeline.
     */
    @Override
    public List<Tweet> retrieveTimeline(int userId) {
        List<Tweet> timeline = new ArrayList<>();
        // SQL query to find the 10 most recent tweets from followed users
        String sql = "SELECT T.user_id, T.tweet_text "+
                "FROM TWEET T " +
                "INNER JOIN FOLLOWS F ON T.user_id = F.follows_id " +
                "WHERE F.user_id = ? " +
                "ORDER BY T.tweet_ts DESC " +
                "LIMIT 10";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int tweetUserId = rs.getInt("user_id");
                    String tweetText = rs.getString("tweet_text");
                    Tweet tweet = new Tweet(tweetUserId, tweetText);
                    timeline.add(tweet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving timeline for user " + userId + ": " + e.getMessage());
        }
        return timeline;
    }
}
