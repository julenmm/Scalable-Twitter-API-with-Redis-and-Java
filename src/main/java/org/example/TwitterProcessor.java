package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes Twitter data from CSV files and interacts with a database API.
 * This class provides functionality to read tweets and follows from CSV files and save them to a database.
 */
public class TwitterProcessor {
    private final DPDatabaseAPI api;

    /**
     * Constructor for TwitterProcessor.
     *
     * @param api The database API used to interact with the database.
     */
    public TwitterProcessor(DPDatabaseAPI api) {
        this.api = api;
    }

    /**
     * Processes tweets from a CSV file and inserts them into the database.
     * Reads tweet information from a specified CSV file path and stores each tweet in the database.
     *
     * @param csvPath Path to the CSV file containing tweet data.
     */
    public void processTweets(String csvPath) {
        List<Tweet> tweets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line = br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userId = Integer.parseInt(values[0].trim());
                String tweetText = values[1];
                tweets.add(new Tweet(userId, tweetText));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading tweets: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < tweets.size(); i++) {
            try {
                api.insertTweet(tweets.get(i));
                //System.out.println("tweet inserted number" + i);
            } catch (Exception e) {
                System.err.println("Error inserting tweet: " + e.getMessage());
            }
        }
    }

    /**
     * Processes follows relationships from a CSV file and inserts them into the database.
     * Reads follow information (user follow relationships) from a specified CSV file path and stores each relationship in the database.
     *
     * @param csvPath Path to the CSV file containing follows data.
     */
    public void processFollows(String csvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line = br.readLine(); // Skip header line

            List<Follows> follows = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userId = Integer.parseInt(values[0].trim());
                int followsId = Integer.parseInt(values[1].trim());
                follows.add(new Follows(userId, followsId));
            }

            for (int i = 0; i < follows.size(); i++) {
                try {
                    api.insertFollows(follows.get(i));
                   // System.out.println("follows inserted number" + i);
                } catch (Exception e) {
                 //   System.err.println("Error inserting follows: " + e.getMessage());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing follows: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves and processes timelines for all users.
     * Retrieves a list of all user IDs from the database and processes timelines for each user.
     */
    public void retrieveAndProcessAllTimelines() {
        List<Integer> userIds = this.api.getAllUserIds();
        for(int i = 0; i < userIds.size(); i++) {
            this.api.retrieveTimeline(userIds.get(i));
          //  System.out.println("timelenine retreived: " + i);
        }
    }
}
