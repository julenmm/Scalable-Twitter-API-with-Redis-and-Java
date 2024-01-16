
CREATE TABLE IF NOT EXISTS TWEET (
    tweet_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    tweet_ts DATETIME DEFAULT CURRENT_TIMESTAMP,
    tweet_text TEXT NOT NULL CHECK(length(tweet_text) <= 140)
);

CREATE TABLE IF NOT EXISTS FOLLOWS(
    user_id INT NOT NULL,
    follows_id INT NOT NULL,
    PRIMARY KEY (user_id, follows_id)
);