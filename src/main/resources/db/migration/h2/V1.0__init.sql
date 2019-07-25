
    drop table tb_account if exists;

    drop table tb_account_balance if exists;

    drop table tb_account_transaction if exists;

    drop sequence if exists hibernate_sequence;

    create sequence hibernate_sequence start with 1000 increment by 1;

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

    alter table tb_account_balance
       add constraint fk_ttb_account_balance_tb_account_id
       foreign key (tb_account_id)
       references tb_account;

    alter table tb_account_transaction
       add constraint fk_tb_account_transaction_tb_account_id
       foreign key (tb_account_id)
       references tb_account;