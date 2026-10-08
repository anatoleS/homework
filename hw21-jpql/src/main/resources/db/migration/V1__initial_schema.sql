-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence client_SEQ start with 1 increment by 1;
create sequence address_SEQ start with 1 increment by 1;
create sequence phone_SEQ start with 1 increment by 1;

create table client
(
    id   bigint not null primary key,
    name varchar(50)
);

create table address
(
    id      bigint not null primary key,
    street  varchar(255),
    constraint fk_address_client foreign key (id) references client (id) on delete cascade
);

create table phone
(
    id        bigint not null primary key,
    number    varchar(50),
    client_id bigint not null,
    constraint fk_phone_client foreign key (client_id) references client (id) on delete cascade
);
