package io.zino.core;

import io.zino.core.atomicTransaction.model.AccountBalance;
import io.zino.core.atomicTransaction.model.AtomicTransaction;
import io.zino.core.atomicTransaction.repository.AccountBalanceRepository;
import io.zino.core.atomicTransaction.repository.AtomicTransactionRepository;

public class Core {
    public static void main(String[] args) {

//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(1116, "0.0"));
//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(2226, "0.0"));

        boolean res = AtomicTransactionRepository.getInstance().create(new AtomicTransaction(
                1116,
                2226,
                "2000.0"
        ));
        System.out.println(res);

        System.out.println("from "+AccountBalanceRepository.getInstance().getAccountBalance(1116));
        System.out.println("to "+AccountBalanceRepository.getInstance().getAccountBalance(2226));
    }
}
