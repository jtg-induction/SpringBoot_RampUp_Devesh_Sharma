CREATE TABLE users
(
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(512) NOT NULL,
    active        BOOLEAN  DEFAULT FALSE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_details
(
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(255)    NOT NULL,
    last_name      VARCHAR(255),
    age            INT UNSIGNED    NOT NULL,
    gender         ENUM('male', 'female', 'other') NOT NULL,
    marital_status ENUM('single', 'married', 'divorced', 'unknown') NOT NULL DEFAULT 'single',
    user_id        BIGINT UNSIGNED NOT NULL UNIQUE,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_details_gender ON user_details (gender);
CREATE INDEX idx_user_details_age ON user_details (age);
CREATE INDEX idx_user_marital_status ON user_details (marital_status);

CREATE TABLE residential_details
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    address      VARCHAR(255)    NOT NULL,
    city         VARCHAR(255)    NOT NULL,
    state        VARCHAR(255)    NOT NULL,
    country      VARCHAR(255)    NOT NULL,
    contact_no_1 VARCHAR(255)    NOT NULL,
    contact_no_2 VARCHAR(255),
    user_id      BIGINT UNSIGNED NOT NULL UNIQUE,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_residential_city ON residential_details (city);

CREATE TABLE official_details
(
    id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    employee_code         VARCHAR(255)    NOT NULL,
    address               VARCHAR(255)    NOT NULL,
    city                  VARCHAR(255)    NOT NULL,
    state                 VARCHAR(255)    NOT NULL,
    country               VARCHAR(255)    NOT NULL,
    company_contact_no    VARCHAR(255)    NOT NULL,
    company_contact_email VARCHAR(255)    NOT NULL,
    company_name          VARCHAR(255)    NOT NULL,
    user_id               BIGINT UNSIGNED NOT NULL UNIQUE,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_official_city ON official_details (city);
CREATE INDEX idx_company_name ON official_details (company_name);

CREATE TABLE followers
(
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    followed_user  BIGINT UNSIGNED NOT NULL,
    following_user BIGINT UNSIGNED NOT NULL,

    FOREIGN KEY (followed_user) REFERENCES users (id),
    FOREIGN KEY (following_user) REFERENCES users (id),
    UNIQUE (followed_user, following_user)
);

CREATE INDEX idx_followers_following ON followers (following_user);
CREATE INDEX idx_followers_count ON followers (followed_user);

