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
