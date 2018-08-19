package io.zino.core.transaction.service.account;

import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.model.account.AccountBalance;
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
       return create(account, new BigDecimal("0.0"));
    }

    public boolean create(Account account, BigDecimal initialBalance) {
        return AccountRepository.getInstance().create(account);
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

    public Account findByExtUid(String extUid) {
        return AccountRepository.getInstance().findByExtUid(extUid);
    }

    public AccountBalance getBalance(String extUid){
        return AccountRepository.getInstance().getBalance(extUid);
    }
}
