create table address
(
    id           BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    city         varchar(255),
    number       varchar(255),
    quarter      varchar(255),
    state        varchar(255),
    street       varchar(255),
    zip_code     varchar(255),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table contact
(
    id           BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    address_id   bigint                    not null,
    cnpj         varchar(255),
    email        varchar(255),
    name         varchar(255),
    observations varchar(255),
    phone        varchar(255),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `FK_address_id` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION

);

alter table project
    add column contact_id bigint not null;
alter table project
    add constraint FK_project_contact foreign key (contact_id) references contact (id);