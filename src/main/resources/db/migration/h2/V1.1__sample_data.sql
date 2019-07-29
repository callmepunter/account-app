insert into tb_account values (81, 'mavrick', 'SAVINGS');
insert into tb_account values (82, 'goose', 'SAVINGS');


insert into tb_account_balance values (91, 1000, 'EUR', 81);
insert into tb_account_balance values (92, 1000, 'AUD', 82);


insert into tb_account_transaction (id, amount, currency, remarks, type, tb_account_id, requested_on)
values ('046b6c7f-0b8a-43b9-b35d-6489e6daee91', 12, 'EUR', 'for jet fuel', 'DEBIT', 81, current_timestamp);

insert into tb_account_transaction (id, amount, currency, remarks, type, tb_account_id, requested_on)
values ('015097a3-94dd-45bc-8d36-62e3c4d601dc', 15, 'AUD', 'tomcat food','DEBIT',  81, current_timestamp);

insert into tb_account_transaction (id, amount, currency, remarks, type, tb_account_id, requested_on)
values ('b10728a4-374f-4ac8-95b9-6dab8c7b4d39', 89, 'AUD', 'for pet hornets','DEBIT',  81, current_timestamp);

commit;

