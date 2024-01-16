package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * This class handles the processing of Twitter data.
 * It includes methods for reading and saving tweet data and follow relationships from CSV files.
 */
public class TwitterProcessor {

    /**
     * Reads tweets from a CSV file and saves them using the provided API.
     *
     * @param csvPath The file path of the CSV file containing tweet data.
     * @param api     The data processing API to use for saving tweets.
     */
    public static void readAndSaveTweets(String csvPath, DPDatabaseAPI api) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            // Reading first line (header line) so that the loop doesn't read it
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userId = Integer.parseInt(values[0].trim());
                String tweetText = values[1];
                Tweet currentTweet = new Tweet(userId, tweetText);
                api.insertTweet(currentTweet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing integer from CSV: " + e.getMessage());
        }
    }

    /**
     * Reads follow relationships from a CSV file and saves them using the provided API.
     *
     * @param csvPath The file path of the CSV file containing follow data.
     * @param api     The data processing API to use for saving follow relationships.
     */
    public static void readAndSaveFollows(String csvPath, DPDatabaseAPI api){
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            // Reading first line (header line) so that the loop doesn't read it
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userId = Integer.parseInt(values[0].trim());
                int followsId = Integer.parseInt(values[1].trim());
                Follows currentFollows = new Follows(userId, followsId);
                api.insertFollows(currentFollows);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing integer from CSV: " + e.getMessage());
        }
    }

    /**
     * Retrieves and processes the home timelines for a list of user IDs.
     *
     * @param userIds The list of user IDs for which to retrieve home timelines.
     * @param api The data processing API to use for retrieving timelines.
     */
    public static void retrieveAllHomeTimelines(List<Integer> userIds, DPDatabaseAPI api) {
        for(Integer userId: userIds){
            api.retrieveTimeline(userId);
        }
    }
}
