CREATE SCHEMA core;
CREATE SEQUENCE core.atomic_transactions_id_seq;
CREATE TABLE core.atomic_transactions(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.atomic_transactions_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  fromAccountId BIGINT NOT NULL,
  toAccountId BIGINT NOT NULL,
  amount VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  publishDate TIMESTAMP NOT NULL DEFAULT NOW(),
  primary key (id));
alter table core.atomic_transactions add constraint UK_atomic_transactions_extuid  unique (extuid);

CREATE SEQUENCE core.account_balances_id_seq;
CREATE TABLE core.account_balances(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.account_balances_id_seq'),
  accountId BIGINT NOT NULL,
  balance VARCHAR(255) NOT NULL,
  primary key (id));
alter table core.account_balances add constraint UK_account_id  unique (accountId);


CREATE SCHEMA tnx;
CREATE SEQUENCE tnx.accounts_id_seq;
CREATE TABLE tnx.accounts(
  id BIGINT NOT NULL DEFAULT NEXTVAL('tnx.accounts_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  active BOOLEAN NOT NULL,
  primary key (id));
alter table tnx.accounts add constraint UK_accounts_extuid  unique (extuid);

CREATE SEQUENCE tnx.transactions_id_seq;
CREATE TABLE tnx.transactions(
  id BIGINT NOT NULL DEFAULT NEXTVAL('tnx.transactions_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  fromAccountId BIGINT NOT NULL,
  tempAccountId BIGINT NOT NULL,
  toAccountId BIGINT NOT NULL,
  amount NUMERIC(19, 2) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  verifyDate TIMESTAMP ,
  verify BOOLEAN NOT NULL,
  description VARCHAR(255),
  primary key (id));
alter table tnx.transactions add constraint UK_transactions_extuid  unique (extuid);
alter table tnx.transactions add constraint FK_fromAccountId_account foreign key (fromAccountId) references tnx.accounts;
alter table tnx.transactions add constraint FK_tempAccountId_account foreign key (tempAccountId) references tnx.accounts;
alter table tnx.transactions add constraint FK_toAccountId_account foreign key (toAccountId) references tnx.accounts;