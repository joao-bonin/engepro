CREATE TABLE user_funnel_access
(
    user_id   BIGINT(20) NOT NULL,
    funnel_id BIGINT(20) NOT NULL,
    PRIMARY KEY (user_id, funnel_id),
    CONSTRAINT fk_user_funnel_access_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT fk_user_funnel_access_funnel FOREIGN KEY (funnel_id) REFERENCES funnel (id) ON DELETE CASCADE ON UPDATE NO ACTION
);
