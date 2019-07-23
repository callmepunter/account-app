    create table tb_account (
       id bigint not null,
        name varchar(255),
        type integer,
        primary key (id)
    );

    create table tb_account_balance (
       id bigint not null,
        amount decimal(19,2),
        currency varchar(255) not null,
        tb_account_id bigint,
        primary key (id)
    );

    create table tb_account_transaction (
       id varchar(255) not null,
        amount decimal(19,2),
        currency varchar(255),
        remarks varchar(255),
        requested_on timestamp,
        type integer,
        tb_account_id bigint,
        primary key (id)
    );



commit;