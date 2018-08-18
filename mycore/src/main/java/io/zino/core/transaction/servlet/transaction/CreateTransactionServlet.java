package io.zino.core.transaction.servlet.transaction;

import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.service.account.AccountService;
import io.zino.core.transaction.service.transaction.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class CreateTransactionServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String extuid = request.getParameter("extuid");
        if (extuid == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        String fromAccountExtUid = request.getParameter("from");
        if (fromAccountExtUid == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        final Account fromAc = AccountService.getInstance().findByExtUid(fromAccountExtUid);
        if (fromAc == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Long fromAccountId = fromAc.getId();

        String toAccountExtUid = request.getParameter("to");
        if (toAccountExtUid == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        final Account toAc = AccountService.getInstance().findByExtUid(toAccountExtUid);
        if (toAc == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Long toAccountId = toAc.getId();

        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        if (amount == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        String description = new String(request.getParameter("description"));

        Transaction tnx = new Transaction(extuid, fromAccountId, toAccountId, amount, description);
        boolean res = TransactionService.getInstance().create(tnx);
        if (!res) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }
}
