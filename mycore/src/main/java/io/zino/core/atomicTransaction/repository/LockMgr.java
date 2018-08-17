package io.zino.core.atomicTransaction.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class LockMgr {

    private Map<Long, StampedLock> lockMap = new HashMap<Long, StampedLock>();

    private static LockMgr instance = new LockMgr();

    public static LockMgr getInstance() {
        return instance;
    }

    public StampedLock getLock(long id){
        StampedLock lock = lockMap.get(id);
        if(lock==null){
            lock = new StampedLock();
            lockMap.put(id, lock);
        }
        return lock;
    }
}
