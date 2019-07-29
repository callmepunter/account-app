alter table tb_account_transaction if exists ALTER COLUMN requested_on SET DEFAULT CURRENT_TIMESTAMP;

insert into tb_account_transaction values ('046b6c7f-0b8a-43b9-b35d-6489e6daee91', 12, 'EUR', 'for jet fuel', 81);
insert into tb_account_transaction values ('015097a3-94dd-45bc-8d36-62e3c4d601dc', 15, 'AUD', 'tomcat food',  81);
insert into tb_account_transaction values ('b10728a4-374f-4ac8-95b9-6dab8c7b4d39', 89, 'AUD', 'for pet hornets',  81);

commit;