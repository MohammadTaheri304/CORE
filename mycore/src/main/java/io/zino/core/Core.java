package io.zino.core;

import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.service.account.AccountService;
import io.zino.core.transaction.service.transaction.TransactionService;
import io.zino.core.transaction.servlet.account.AccountBalanceServlet;
import io.zino.core.transaction.servlet.account.ActiveAccountServlet;
import io.zino.core.transaction.servlet.account.CreateAccountServlet;
import io.zino.core.transaction.servlet.account.DeactiveAccountServlet;
import io.zino.core.transaction.servlet.transaction.CreateTransactionServlet;
import io.zino.core.transaction.servlet.transaction.InqueryTransactionServlet;
import io.zino.core.transaction.servlet.transaction.VerifyTransactionServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.flywaydb.core.Flyway;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Core {

    public static void main(String[] args) throws Exception {
        Core core = new Core();
        core.runFlyWay();
        //core.runJetty();
        core.f0();
    }

    private void runJetty() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();

        // transaction Api
        servletHandler.addServletWithMapping(CreateTransactionServlet.class, "/transaction/tnx");
        servletHandler.addServletWithMapping(InqueryTransactionServlet.class, "/transaction/inquery");
        servletHandler.addServletWithMapping(VerifyTransactionServlet.class, "/transaction/verify");

        //account Api
        servletHandler.addServletWithMapping(AccountBalanceServlet.class, "/account/balance");
        servletHandler.addServletWithMapping(ActiveAccountServlet.class, "/account/active");
        servletHandler.addServletWithMapping(DeactiveAccountServlet.class, "/account/deactive");
        servletHandler.addServletWithMapping(CreateAccountServlet.class, "/account/create");

        server.setHandler(servletHandler);
        server.start();
    }

    private void runFlyWay() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(
                "jdbc:postgresql://localhost:5432/CORE",
                "core",
                "core");
        flyway.setSchemas("core");
        flyway.setLocations("io/zino/core/db/migrations/");
        flyway.migrate();

    }

    private void f0() throws Exception {

        int count = 100000;

        Account ac1 = new Account("ac1", true);
        Account ac2 = new Account("ac2", true);
        AccountService.getInstance().create(ac1);
        AccountService.getInstance().create(ac2);

        Account coreAc = AccountService.getInstance().findByExtUid(Account.CORE_ACCOUNT_EXTUID);
        Account ac1w = AccountService.getInstance().findByExtUid("ac1");
        Account ac2w = AccountService.getInstance().findByExtUid("ac2");

        final String TNX_PREF = "ex-1-";

        boolean resI = TransactionService.getInstance().create(
                new Transaction(
                        TNX_PREF+"init",
                        coreAc.getId(),
                        ac1w.getId(),
                        new BigDecimal(100000),
                        ""
                )
        );
        if (resI) {
            Transaction init = TransactionService.getInstance().findByExtUid(TNX_PREF+"init");
            if(!TransactionService.getInstance().verify(init)){
                throw new Exception("!!!!!!!");
            }
        } else throw new Exception("!!!!!!!");

        long startTime = System.currentTimeMillis();

        IntStream.rangeClosed(1, count).boxed().collect(Collectors.toList())
                .parallelStream().forEach(i -> {
            boolean res = TransactionService.getInstance().create(
                    new Transaction(
                            TNX_PREF + i,
                            ac1w.getId(),
                            ac2w.getId(),
                            new BigDecimal(1),
                            ""
                    )
            );
            if (res) {
                Transaction tnx = TransactionService.getInstance().findByExtUid(TNX_PREF + i);
                if (!TransactionService.getInstance().verify(tnx)){
                    System.out.println("@@@");
                }
            }else System.out.println("???");
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Total time:" + (endTime - startTime) + " TPS:" + (1000 * (double) count / (endTime - startTime)));

        System.out.println("from " + AccountService.getInstance().getBalance("ac1").getBalance());
        System.out.println("to " + AccountService.getInstance().getBalance("ac2").getBalance());
    }


}
