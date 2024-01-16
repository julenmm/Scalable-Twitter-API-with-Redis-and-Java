package org.example;

/**
 * Represents a relationship where a user follows another user.
 * This class is part of the org.example package.
 */
public class Follows {
    private int userId;
    private int followsId;

    /**
     * Constructs a new Follows object.
     *
     * @param userId    the user ID of the follower
     * @param followsId the user ID of the user being followed
     */
    public Follows(int userId, int followsId) {
        this.userId = userId;
        this.followsId = followsId;
    }

    /**
     * Gets the user ID of the follower.
     *
     * @return the user ID of the follower
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Gets the user ID of the followed.
     *
     * @return the user ID of the followed
     */
    public int getFollowsId() {
        return this.followsId;
    }
}
