INSERT INTO core.accounts( extuid, creationDate, active) VALUES ( 'core', NOW(), true);
INSERT INTO core.transactions( extuid, fromAccountId, toAccountId, amount, creationDate, verifyDate, verify, description)
  VALUES ( 'core', 1, 1, 1000000000000000.00, NOW(), NOW(), true, 'initial transaction');

INSERT INTO core.account_balances( creationDate ,accountId ,lastBalanceId ,transactionExtUid ,balance)
  VALUES ( NOW(), 1, null, 'core', 1000000000000000.00);
