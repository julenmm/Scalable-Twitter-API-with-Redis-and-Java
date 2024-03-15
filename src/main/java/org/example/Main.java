package org.example;

/**
 * The Main class for the Twitter-like application.
 * It handles the initialization of the database and processing of tweets and follows data.
 */
public class Main {

    /**
     * The main method to run the application.
     * It accepts file paths as command line arguments and processes the data.
     * The application requires three command line arguments: the path to the SQL file
     * for database initialization, and the paths to the tweets and follows CSV files.
     *
     * @param args Command line arguments containing file paths for SQL file,
     *             tweets CSV file, and follows CSV file.
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Not enough arguments. Please provide paths for tweets CSV, and follows CSV.");
            }
            String tweetFilePath = args[0];
            String followsFilePath = args[1];

            DPDatabaseAPI api = new DPDatabaseSqLite();

            TwitterProcessor processor = new TwitterProcessor(api);

            processor.processFollows(followsFilePath);

            long startTime, endTime;

            startTime = System.nanoTime();
            processor.processTweets(tweetFilePath);
            endTime = System.nanoTime();
            printDuration("Saving tweets", startTime, endTime);

            startTime = System.nanoTime();
            processor.retrieveAndProcessAllTimelines();
            endTime = System.nanoTime();
            printDuration("Retrieving timelines", startTime, endTime);
            api.closeConnection();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prints the duration of a task in milliseconds.
     *
     * @param task The name of the task.
     * @param startTime The start time of the task in nanoseconds.
     * @param endTime The end time of the task in nanoseconds.
     */
    private static void printDuration(String task, long startTime, long endTime) {
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println(task + " took " + duration + " milliseconds.");
    }
}