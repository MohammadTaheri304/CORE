package io.zino.core.transaction.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

public class LockMgr {

    private ConcurrentHashMap<Long, StampedLock> lockMap = new ConcurrentHashMap<Long, StampedLock>();

    private static LockMgr instance = new LockMgr();

    public static LockMgr getInstance() {
        return instance;
    }

    public StampedLock getLock(long id) {
        return lockMap.computeIfAbsent(id, insertedKey -> new StampedLock());
    }
}
