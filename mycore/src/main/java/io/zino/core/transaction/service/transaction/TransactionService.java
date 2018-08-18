package io.zino.core.transaction.service.transaction;

import io.zino.core.transaction.atomicTransaction.model.AtomicTransaction;
import io.zino.core.transaction.atomicTransaction.repository.AtomicTransactionRepository;
import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.repository.transaction.TransactionRepository;

public class TransactionService {

    private static final String INIT_PREFIX = "init-";
    private static final String VERIFY_PREFIX = "verify-";

    private static final TransactionService instance = new TransactionService();

    private TransactionService() {
    }

    public static TransactionService getInstance() {
        return instance;
    }

    public boolean create(Transaction transaction) {
        // TODO it is not atomic!
        TransactionRepository.getInstance().create(transaction);
        Transaction tnx = TransactionRepository.getInstance().findByExtUid(transaction.getExtuid());
        AtomicTransaction atomicTransaction = new AtomicTransaction(
                INIT_PREFIX + tnx.getId(),
                tnx.getFromAccountId(),
                tnx.getTempAccountId(),
                tnx.getAmount().toPlainString(),
                "INIT:" + tnx.getFromAccountId() + ":" + tnx.getToAccountId()
        );

        boolean res = AtomicTransactionRepository.getInstance().create(atomicTransaction);
        if (!res) {
            TransactionRepository.getInstance().delete(tnx.getId());
        }
        return res;
    }

    public boolean verify(String extUid) {
        // TODO it is not atomic!
        Transaction tnx = TransactionRepository.getInstance().findByExtUid(extUid);
        if (tnx == null) return false;

        AtomicTransaction atomicTransaction = new AtomicTransaction(
                VERIFY_PREFIX + tnx.getId(),
                tnx.getTempAccountId(),
                tnx.getToAccountId(),
                tnx.getAmount().toPlainString(),
                "VERIFY:" + tnx.getFromAccountId() + ":" + tnx.getToAccountId()
        );

        boolean res = AtomicTransactionRepository.getInstance().create(atomicTransaction);
        if(res){
            TransactionRepository.getInstance().verify(tnx.getId());
        }
        return res;
    }

    public Boolean inqueryByExtUid(String extUid) {
        return TransactionRepository.getInstance().inqueryByExtUid(extUid);
    }

    public boolean delete(String extUid) {
        Transaction tnx = TransactionRepository.getInstance().findByExtUid(extUid);
        if (tnx == null) return false;
        if(tnx.isVerify()) return false;

        return TransactionRepository.getInstance().delete(tnx.getId());
    }
}
