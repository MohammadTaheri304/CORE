CREATE SCHEMA core;

CREATE SEQUENCE core.accounts_id_seq;
CREATE TABLE core.accounts(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.accounts_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  active BOOLEAN NOT NULL,
  balance NUMERIC(19, 2) NOT NULL,
  primary key (id));
alter table core.accounts add constraint UK_accounts_extuid  unique (extuid);

CREATE SEQUENCE core.transactions_id_seq;
CREATE TABLE core.transactions(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.transactions_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  fromAccountId BIGINT NOT NULL,
  toAccountId BIGINT NOT NULL,
  amount NUMERIC(19, 2) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  verifyDate TIMESTAMP ,
  verify BOOLEAN NOT NULL,
  description VARCHAR(255),
  primary key (id));
alter table core.transactions add constraint UK_transactions_extuid  unique (extuid);
alter table core.transactions add constraint FK_fromAccountId_account foreign key (fromAccountId) references core.accounts;
alter table core.transactions add constraint FK_toAccountId_account foreign key (toAccountId) references core.accounts;