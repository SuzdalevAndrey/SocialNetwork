CREATE TABLE IF NOT EXISTS posts.t_comments (
    id SERIAL PRIMARY KEY,
    c_content VARCHAR(1000) NOT NULL CHECK (LENGTH(TRIM(c_content)) > 0),
    c_date_create TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_user_id BIGINT NOT NULL,
    c_post_id BIGINT NOT NULL,
    FOREIGN KEY (c_post_id) REFERENCES posts.t_posts(id) ON DELETE CASCADE
);
