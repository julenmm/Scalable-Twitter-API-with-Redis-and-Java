package database;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;

import java.util.*;


/**
 * Utility class for database operations using Redis.
 * Provides functionality for connecting to a Redis database, closing the connection,
 * inserting records, retrieving data, and handling various database operations.
 */
public class DBUtils {

    private StatefulRedisConnection<String, String> con = null;


    /**
     * Constructs a DBUtils object and initializes a database connection.
     */
    public DBUtils() {
        this.con = this.getConnection();
    }

    /**
     * Retrieves a database connection. If a connection does not already exist,
     * it attempts to establish a new one.
     *
     * @return A StatefulRedisConnection to the database.
     */
    public StatefulRedisConnection<String, String> getConnection() {
        if (con == null) {
            try {
                RedisClient redisClient = RedisClient.create("redis://localhost:6379");
                StatefulRedisConnection<String, String> connection = redisClient.connect();
                System.out.println("Connected to Memurai");
                return connection;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        return this.con;
    }

    /**
     * Closes the current database connection.
     * This method is crucial for preventing resource leaks.
     */
    public void closeConnection() {
        try {
            this.con.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts data into a Redis hash.
     *
     * @param key The Redis key under which the data will be stored.
     * @param data A map of the data to be inserted (field-value pairs).
     */
    public void insertHset(String key, Map<String, String> data) {
        try {
            con.sync().hset(key, data);
        } catch (Exception e) {
            System.err.println("Insert error: " + e.getMessage());
        }
    }

    /**
     * Retrieves all field-value pairs of the hash stored at a specific key.
     *
     * @param key The key of the hash to retrieve.
     * @return A Map of field-value pairs from the hash.
     */
    public Map<String, String> hGet(String key) {
        Map<String, String> map = new HashMap<>();
        try {
            map = this.con.sync().hgetall(key);
            return map;
        } catch (Exception e) {
            System.err.println("Insert error: " + e.getMessage());
        }
        return map;
    }

    /**
     * Appends a value to the end of the list stored at a key.
     *
     * @param key    The key at which the list is stored.
     * @param toPush The value to append to the list.
     */
    public void rPush (String key, String toPush) {
        try {
            this.con.sync().rpush(key, toPush);
        } catch (Exception e) {
            System.err.println("Insert error: " + e.getMessage());
        }
    }

    /**
     * Appends a value to a list and trims its length to a specified limit.
     * This ensures the list does not exceed the maximum number of elements allowed.
     *
     * @param key   The key of the list to modify.
     * @param toPush The value to append to the list.
     * @param limit The maximum length of the list after the operation.
     */
    public void rPushLimit (String key, String toPush, int limit) {
        try {
            this.con.sync().multi();
            this.con.sync().lpush(key, toPush);
            this.con.sync().ltrim(key, 0, limit - 1);
            this.con.sync().exec();
        } catch (Exception e) {
            System.err.println("Insert error: " + e.getMessage());
        }
    }


    /**
     * Retrieves the entire list stored at a specific key.
     *
     * @param key The key of the list to retrieve.
     * @return A List of strings representing the list elements.
     */
    public List<String> getList(String key){
        return this.con.sync().lrange(key, 0, -1);
    }

    /**
     * Retrieves a list of keys matching the specified pattern using Redis SCAN.
     *
     * This method scans through all keys in the Redis database matching the given pattern
     * and returns a list of keys that match the pattern.
     *
     * @param pattern A pattern to match keys in the Redis database. Use '*' to match all keys or
     *                a specific pattern, e.g., 'user:*' to match all keys starting with 'user:'.
     * @return A List of Strings containing the keys that match the specified pattern.
     * @throws RedisException If there is an issue with the Redis connection or if an error occurs during the scan.
     * @see ScanArgs.Builder#matches(String) ScanArgs.Builder.matches(String)
     * @see ScanCursor ScanCursor
     */
    public List<String> getKeysByPattern(String pattern) {
        List<String> keys = new ArrayList<>();
        try {
            var scanArgs = ScanArgs.Builder.matches(pattern);
            var scanCursor = ScanCursor.INITIAL;
            do {
                var keysScan = con.sync().scan(scanCursor, scanArgs);
                keys.addAll(keysScan.getKeys());
                scanCursor = keysScan;
            } while (!scanCursor.isFinished());
        } catch (RedisException e) {
            System.err.println("Error retrieving keys: " + e.getMessage());
        }
        return keys;
    }
}

