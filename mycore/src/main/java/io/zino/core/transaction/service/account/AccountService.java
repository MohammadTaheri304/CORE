package io.zino.core.transaction.service.account;

import io.zino.core.transaction.atomicTransaction.model.AccountBalance;
import io.zino.core.transaction.atomicTransaction.repository.AccountBalanceRepository;
import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.repository.account.AccountRepository;

import java.math.BigDecimal;

public class AccountService {

    private static final AccountService instance = new AccountService();

    private AccountService() {
    }

    public static AccountService getInstance() {
        return instance;
    }

    public boolean create(Account account) {
        // TODO it is not atomic!
        boolean createResult = AccountRepository.getInstance().create(account);
        if (createResult) {
            Account cAccount = AccountRepository.getInstance().findByExtUid(account.getExtuid());
            AccountBalance ab = new AccountBalance(cAccount.getId(), new BigDecimal(0).toPlainString());
            boolean createBalanceResult = AccountBalanceRepository.getInstance().createAccountBalance(ab);
            return true;
        } else {
            return false;
        }
    }

    public boolean active(String extUid) {
        Account ac = AccountRepository.getInstance().findByExtUid(extUid);
        if(ac==null) return false;
        if(ac.isActive()) return false;

        return AccountRepository.getInstance().changeActive(ac.getId(), true);
    }

    public boolean deactive(String extUid) {
        Account ac = AccountRepository.getInstance().findByExtUid(extUid);
        if(ac==null) return false;
        if(!ac.isActive()) return false;

        return AccountRepository.getInstance().changeActive(ac.getId(), false);
    }

    public boolean changePassword(String extUid, String newPassword) {
        Account ac = AccountRepository.getInstance().findByExtUid(extUid);
        if(ac==null) return false;
        return AccountRepository.getInstance().changePassword(ac.getId(), newPassword);
    }

    public Account findByExtUid(String extUid) {
        return AccountRepository.getInstance().findByExtUid(extUid);
    }

    public BigDecimal getBalance(String extUid){
        Account ac = AccountRepository.getInstance().findByExtUid(extUid);
        if(ac==null) return null;
        return AccountBalanceRepository.getInstance().getAccountBalance(ac.getId());
    }
}
