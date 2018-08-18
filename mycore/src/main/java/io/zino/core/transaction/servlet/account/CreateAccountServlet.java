package io.zino.core.transaction.servlet.account;

import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.service.account.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class CreateAccountServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {


        String extuid = request.getParameter("extuid");
        if (extuid == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        Boolean active = Boolean.parseBoolean(request.getParameter("active"));
        if (active == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String balanceString = request.getParameter("balance");
        BigDecimal balance = balanceString==null ? new BigDecimal(0) : new BigDecimal(balanceString);

        Account account = new Account(extuid, active, balance);
        boolean res = AccountService.getInstance().create(account);
        if (!res) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }


        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }
}