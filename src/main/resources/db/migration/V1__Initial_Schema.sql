CREATE TABLE users
(
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(512) NOT NULL,
    active        BOOLEAN  NOT NULL DEFAULT FALSE,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE user_details
(
    user_id        BIGINT UNSIGNED PRIMARY KEY,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255),
    age            INT UNSIGNED    NOT NULL,
    gender         ENUM('MALE', 'FEMALE') NOT NULL,
    marital_status ENUM('SINGLE', 'MARRIED', 'DIVORCED', 'UNKNOWN') NOT NULL DEFAULT 'SINGLE',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_details_age ON user_details (age);
CREATE INDEX idx_user_marital_status ON user_details (marital_status);

CREATE TABLE residential_details
(
    user_id     BIGINT UNSIGNED PRIMARY KEY,
    address     VARCHAR(255) NOT NULL,
    city        VARCHAR(255) NOT NULL,
    state       VARCHAR(255) NOT NULL,
    country     VARCHAR(255) NOT NULL,
    contact_no1 VARCHAR(255) NOT NULL,
    contact_no2 VARCHAR(255),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_residential_city ON residential_details (city);

CREATE TABLE official_details
(
    user_id               BIGINT UNSIGNED PRIMARY KEY,
    employee_code         VARCHAR(255) NOT NULL,
    address               VARCHAR(255) NOT NULL,
    city                  VARCHAR(255) NOT NULL,
    state                 VARCHAR(255) NOT NULL,
    country               VARCHAR(255) NOT NULL,
    company_contact_no    VARCHAR(255) NOT NULL,
    company_contact_email VARCHAR(255) NOT NULL,
    company_name          VARCHAR(255) NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_official_city ON official_details (city);
CREATE INDEX idx_company_name ON official_details (company_name);

CREATE TABLE followers
(
    followed_user  BIGINT UNSIGNED NOT NULL,
    following_user BIGINT UNSIGNED NOT NULL,

    FOREIGN KEY (followed_user) REFERENCES users (id),
    FOREIGN KEY (following_user) REFERENCES users (id),
    PRIMARY KEY (followed_user, following_user)
);

CREATE INDEX idx_followers_following ON followers (following_user);

