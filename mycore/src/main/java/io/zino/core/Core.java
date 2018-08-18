package io.zino.core;

import io.zino.core.transaction.servlet.account.*;
import io.zino.core.transaction.servlet.transaction.InqueryServlet;
import io.zino.core.transaction.servlet.transaction.CreateServlet;
import io.zino.core.transaction.servlet.transaction.VerifyServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class Core {

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();

        // transaction Api
        servletHandler.addServletWithMapping(CreateServlet.class, "/transaction/tnx");
        servletHandler.addServletWithMapping(InqueryServlet.class, "/transaction/inquery");
        servletHandler.addServletWithMapping(VerifyServlet.class, "/transaction/verify");

        //account Api
        servletHandler.addServletWithMapping(AccountBalanceServlet.class, "/account/balance");
        servletHandler.addServletWithMapping(ActiveAccountServlet.class, "/account/active");
        servletHandler.addServletWithMapping(DeactiveAccountServlet.class, "/account/deactive");
        servletHandler.addServletWithMapping(ChangePasswordAccountServlet.class, "/account/chpswd");
        servletHandler.addServletWithMapping(CreateAccountServlet.class, "/account/create");

        server.setHandler(servletHandler);
        server.start();
    }

    private void f() {

//        long startTime = System.currentTimeMillis();

//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(1116, "0.0"));
//        AccountBalanceRepository.getInstance().createAccountBalance(new AccountBalance(2226, "0.0"));

//        int count = 10000;
//        IntStream.rangeClosed(0, count)
//                .boxed().collect(Collectors.toList())
//                .parallelStream().forEach(i -> {
//            boolean res = AtomicTransactionRepository.getInstance().create(new AtomicTransaction(
//                    1116,
//                    2226,
//                    "1.0"
//            ));
//            //System.out.println(res);
//        });
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("Total time:" + (endTime - startTime) + " TPS:" + (1000 * (double) count / (endTime - startTime)));
//
//
//        System.out.println("from " + AccountBalanceRepository.getInstance().getAccountBalance(1116));
//        System.out.println("to " + AccountBalanceRepository.getInstance().getAccountBalance(2226));
    }
}
