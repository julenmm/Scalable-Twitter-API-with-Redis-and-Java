package org.example;

import database.DBUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the DPDatabaseAPI interface to provide database operations using SQLite.
 * It is responsible for creating a temporary in-memory database, inserting tweets, managing follow relationships,
 * and retrieving user IDs and tweet timelines.
 */
public class DPDatabaseSqLite implements DPDatabaseAPI{

    private DBUtils dbu;

    /**
     * Constructor for DPDatabaseSqLite class. Initializes the database utilities.
     */
    public DPDatabaseSqLite() {
        this.dbu = new DBUtils();
    }

    /**
     * Inserts a Tweet record into the database.
     *
     * @param tweet The Tweet object containing user ID and tweet text to be inserted.
     */
    @Override
    public void insertTweet(Tweet tweet) {
        Long tweetId = dbu.getConnection().sync().incr("tweet:next_id");

        String tweetKey = "tweet:" + tweetId;
        Map<String, String> tweetMap = new HashMap<>();
        tweetMap.put("user_id", String.valueOf(tweet.getUserId()));
        tweetMap.put("tweet_ts", Instant.now().toString());
        tweetMap.put("tweet_text", tweet.getText());

        this.dbu.insertHset(tweetKey, tweetMap);

        this.updateTimeLines(tweet, tweetId);
    }

    /**
     * Updates timelines for followers after inserting a Tweet.
     *
     * @param tweet   The Tweet object that was inserted.
     * @param tweetId The unique ID of the inserted tweet.
     */
    @Override
    public void updateTimeLines(Tweet tweet, Long tweetId) {
        List<String> userFollowers = this.dbu.getList("user_followed_by:" + tweet.getUserId());

        for (String userKey : userFollowers) {
            this.dbu.rPushLimit("timeline:" + userKey, String.valueOf(tweetId), 10);
        }
    }

    /**
     * Inserts a Follows record into the database.
     *
     * @param follows The Follows object representing a user-follows-user relationship to be inserted.
     */
    @Override
    public void insertFollows(Follows follows) {
        String userFollowsKey = "user_follows:" + follows.getUserId();
        String followsId = String.valueOf(follows.getFollowsId());

        this.dbu.rPush(userFollowsKey, followsId);

        String userFollowedBy = "user_followed_by:" + follows.getFollowsId();

        this.dbu.rPush(userFollowedBy, userFollowsKey);
    }

    /**
     * Retrieves a list of all unique user IDs from the FOLLOWS table.
     *
     * @return A List of integer user IDs.
     */
    @Override
    public List<Integer> getAllUserIds() {
        List<Integer> userIds = new ArrayList<>();
        String matchPattern = "user_follows:*";
        List<String> userKeys = dbu.getKeysByPattern(matchPattern);

        for (String key : userKeys) {
            try {
                String idStr = key.substring(key.indexOf(':') + 1);
                userIds.add(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing user ID from key: " + key);
            }
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
        List<Tweet> timeLine = new ArrayList<>();
        List<String> tweetKeys = this.dbu.getList("timeline:" + userId);
        for(String key: tweetKeys){
            Map<String, String> tweet = this.dbu.hGet(key);
            timeLine.add(new Tweet(Integer.parseInt(tweet.get("user_id")), tweet.get("tweet_text")));
        }
        return timeLine;
    }

    /**
     * Closes the database connection.
     * This method ensures that the connection to the SQLite database is properly closed.
     */
    @Override
    public void closeConnection() {
        this.dbu.closeConnection();
    }
}
