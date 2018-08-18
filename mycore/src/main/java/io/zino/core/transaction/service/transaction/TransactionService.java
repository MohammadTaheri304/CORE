package io.zino.core.transaction.service.transaction;

import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.repository.transaction.TransactionRepository;
import io.zino.core.transaction.service.LockMgr;

import java.util.concurrent.locks.StampedLock;

public class TransactionService {
    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }

    public boolean create(Transaction transaction) {
        StampedLock fromLock = LockMgr.getInstance().getLock(transaction.getFromAccountId());
        long fromLockStamp = fromLock.writeLock();
        try {
            return TransactionRepository.getInstance().create(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            fromLock.unlock(fromLockStamp);
        }
    }

    public boolean verify(Transaction transaction) {
        StampedLock toLock = LockMgr.getInstance().getLock(transaction.getToAccountId());
        long toLockStamp = toLock.writeLock();
        try {
            return TransactionRepository.getInstance().verify(transaction.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            toLock.unlock(toLockStamp);
        }
    }

    public Transaction findByExtUid(String extUid) {
        return TransactionRepository.getInstance().findByExtUid(extUid);
    }

    public boolean delete(Transaction transaction) {
        StampedLock toLock = LockMgr.getInstance().getLock(transaction.getToAccountId());
        long toLockStamp = toLock.writeLock();
        try {
            return TransactionRepository.getInstance().delete(transaction.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            toLock.unlock(toLockStamp);
        }
    }

}
