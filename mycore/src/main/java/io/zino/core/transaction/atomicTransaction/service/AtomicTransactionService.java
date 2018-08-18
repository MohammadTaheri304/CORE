package io.zino.core.transaction.atomicTransaction.service;

import io.zino.core.transaction.atomicTransaction.model.AccountBalance;
import io.zino.core.transaction.atomicTransaction.model.AtomicTransaction;
import io.zino.core.transaction.atomicTransaction.repository.AccountBalanceRepository;
import io.zino.core.transaction.atomicTransaction.repository.AtomicTransactionRepository;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class AtomicTransactionService {
    private static AtomicTransactionService instance = new AtomicTransactionService();

    public static AtomicTransactionService getInstance() {
        return instance;
    }

    public boolean create(AtomicTransaction atomicTransaction) {
        StampedLock fromLock = LockMgr.getInstance().getLock(atomicTransaction.getFromAccountId());
        StampedLock toLock = LockMgr.getInstance().getLock(atomicTransaction.getToAccountId());

        long fromLockStamp = 0l;
        long toLockStamp = 0l;

        try {
            while (fromLockStamp == 0 || toLockStamp == 0) {
                if (fromLockStamp != 0)
                    fromLock.unlock(fromLockStamp);
                if (toLockStamp != 0)
                    toLock.unlock(toLockStamp);

                fromLockStamp = fromLock.tryWriteLock(500, TimeUnit.MILLISECONDS);
                toLockStamp = toLock.tryWriteLock(500, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        try {
            return AtomicTransactionRepository.getInstance().create(atomicTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            fromLock.unlock(fromLockStamp);
            toLock.unlock(toLockStamp);
        }
    }

    public boolean create(AccountBalance accountBalance) {
        return AccountBalanceRepository.getInstance().createAccountBalance(accountBalance);
    }

    public BigDecimal getAccountBalance(long accountId) {
        StampedLock lock = LockMgr.getInstance().getLock(accountId);
        long lockStamp = lock.tryWriteLock();
        try {
            return AccountBalanceRepository.getInstance().getAccountBalance(accountId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock(lockStamp);
        }
    }
}
