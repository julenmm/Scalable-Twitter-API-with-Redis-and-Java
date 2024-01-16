package org.example;

import java.util.List;

import static org.example.TwitterProcessor.*;

/**
 * The Main class for the Twitter-like application.
 * It handles the initialization of the database and processing of tweets and follows data.
 */
public class Main {

    private static DPDatabaseAPI api = new DPDatabaseSqLite();

    /**
     * Main method to run the application.
     * It accepts file paths as command line arguments and processes the data.
     *
     * @param args Command line arguments containing file paths for SQL, tweets, and follows data.
     */
    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException("Not enough arguments. Please provide paths for SQL file, "
                        + "tweets CSV, and follows CSV.");
            }

            String sqlFilePath = args[0];
            String tweetFilePath = args[1];
            String followsFilePath = args[2];

            api.createTempDataBase(sqlFilePath);

            long startTime = System.nanoTime();
            readAndSaveTweets(tweetFilePath, api);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            System.out.println("System took: " + duration + " milliseconds to save tweets");
            // Calculate tweets per second
            long tweetsPerSecond = (duration > 0) ? (1000000 / (duration / 1000)) : 0;
            System.out.println("System called postTweet method an average of " + tweetsPerSecond + " times per second");

            readAndSaveFollows(followsFilePath, api);

            List<Integer> userIds = api.getAllUserIds();
            startTime = System.nanoTime();
            retrieveAllHomeTimelines(userIds, api);
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1_000_000;
            long averageTimePerTimeline = (userIds.size() > 0) ? (duration / userIds.size()) : 0;
            System.out.println("The average time in milliseconds to retrieve timelines is: " + averageTimePerTimeline);

            // Calculate home timelines per second
            long timelinesPerSecond = (duration > 0) ? (userIds.size() / (duration / 1000)) : 0;
            System.out.println("System called getHomeTimeline Method an average of " + timelinesPerSecond
                    + " times per second");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}