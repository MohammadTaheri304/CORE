package io.zino.core;

import io.zino.core.atomicTransaction.model.AtomicTransaction;
import io.zino.core.atomicTransaction.repository.AccountBalanceRepository;
import io.zino.core.atomicTransaction.repository.AtomicTransactionRepository;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Core {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(1116, "0.0"));
//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(2226, "0.0"));

        IntStream.rangeClosed(0, 100000)
                .boxed().collect(Collectors.toList())
                .parallelStream().forEach(i -> {
            boolean res = AtomicTransactionRepository.getInstance().create(new AtomicTransaction(
                    1116,
                    2226,
                    "1.0"
            ));
            //System.out.println(res);
        });

        long endTime = System.currentTimeMillis();
        System.out.println("Total time:"+(endTime-startTime) + " TPMili:"+((double)100000/(endTime-startTime)));


        System.out.println("from " + AccountBalanceRepository.getInstance().getAccountBalance(1116));
        System.out.println("to " + AccountBalanceRepository.getInstance().getAccountBalance(2226));
    }
}
