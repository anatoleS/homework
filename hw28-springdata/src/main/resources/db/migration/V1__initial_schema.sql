create sequence client_SEQ start with 1 increment by 1;
create sequence address_SEQ start with 1 increment by 1;
create sequence phone_SEQ start with 1 increment by 1;

create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table address
(
    id      bigserial not null primary key,
    client_id bigint not null,
    street  varchar(255),
    constraint fk_address_client foreign key (client_id) references client (id) on delete cascade
);

create table phone
(
    id        bigserial not null primary key,
    number    varchar(50),
    client_id bigint not null,
    constraint fk_phone_client foreign key (client_id) references client (id) on delete cascade
);
