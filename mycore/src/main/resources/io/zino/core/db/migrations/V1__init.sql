CREATE SEQUENCE core.accounts_id_seq;
CREATE TABLE core.accounts(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.accounts_id_seq'),
  extuid VARCHAR(255) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  active BOOLEAN NOT NULL,
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

CREATE SEQUENCE core.account_balances_id_seq;
CREATE TABLE core.account_balances(
  id BIGINT NOT NULL DEFAULT NEXTVAL('core.account_balances_id_seq'),
  creationDate TIMESTAMP NOT NULL DEFAULT NOW(),
  accountId BIGINT,
  lastBalanceId BIGINT,
  transactionExtUid VARCHAR(255) NOT NULL,
  balance NUMERIC(19, 2) NOT NULL,
  primary key (id));
alter table core.account_balances add constraint FK_accountId_account foreign key (accountId) references core.accounts;
alter table core.account_balances add constraint FK_lastBalanceId_account_balances foreign key (lastBalanceId) references core.account_balances;