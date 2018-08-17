CREATE SCHEMA core AUTHORIZATION core;
CREATE SEQUENCE core.atomic_transactions_id_seq;
CREATE TABLE core.atomic_transactions(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.atomic_transactions_id_seq'),
  fromAccountId BIGINT NOT NULL,
  toAccountId BIGINT NOT NULL,
  amount VARCHAR(255) NOT NULL,
  publishDate TIMESTAMP DEFAULT NOW(),
  primary key (id));

CREATE SEQUENCE core.account_balances_id_seq;
CREATE TABLE core.account_balances(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.account_balances_id_seq'),
  accountId BIGINT NOT NULL,
  balance VARCHAR(255) NOT NULL,
  primary key (id));

alter table core.account_balances add constraint UK_account_id  unique (accountId);