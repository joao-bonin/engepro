CREATE TABLE funnel
(
    id           BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name         VARCHAR(255)              NOT NULL,
    description  VARCHAR(1000) default NULL,
    date_created TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE step
(
    id           BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name         VARCHAR(255)              NOT NULL,
    description  VARCHAR(1000) default NULL,
    date_created TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    funnel_id    BIGINT(20)                NOT NULL,
    CONSTRAINT `FK_funnel_id` FOREIGN KEY (`funnel_id`) REFERENCES `funnel` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE user
(
    id               BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name             VARCHAR(255)              NOT NULL,
    email            VARCHAR(1000)             NOT NULL,
    password         VARCHAR(1000)             NOT NULL,
    active           BIT(1)                    NOT NULL DEFAULT 1,
    has_level_config BIT(1)                    NOT NULL DEFAULT 0,
    last_login       TIMESTAMP                          default NULL,
    date_created     TIMESTAMP                          DEFAULT CURRENT_TIMESTAMP,
    last_updated     TIMESTAMP                          DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project
(
    id           BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name         VARCHAR(255)              NOT NULL,
    customer     VARCHAR(300)              NOT NULL,
    description  VARCHAR(1000)                      default NULL,
    step_id      BIGINT(20)                NOT NULL,
    user_id      BIGINT(20)                NOT NULL,
    start_date   TIMESTAMP                          default NULL,
    end_date     TIMESTAMP                          default NULL,
    is_archived  BIT(1)                    NOT NULL DEFAULT 0,
    date_created TIMESTAMP                          DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP                          DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `FK_step_id` FOREIGN KEY (`step_id`) REFERENCES `step` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
