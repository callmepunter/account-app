insert into tb_account values (303, 'john doe', 0);
insert into tb_account values (787, 'dreamliner', 0);
insert into tb_account values (380, 'double decker', 1);

insert into tb_account_balance values (1001, 50, 'EUR', 303);
insert into tb_account_balance values (1002, 50, 'AUD', 303);

insert into tb_account_balance values (1003, 50, 'EUR', 787);
insert into tb_account_balance values (1004, 50, 'AUD', 380);

commit;