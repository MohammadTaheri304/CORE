package io.zino.core.transaction.servlet.account;

import com.google.gson.Gson;
import io.zino.core.transaction.model.account.AccountBalance;
import io.zino.core.transaction.servlet.account.dto.AccountBalanceDTO;
import io.zino.core.transaction.service.account.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class AccountBalanceServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String accountExtUid = request.getParameter("extuid");
        if(accountExtUid==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        AccountBalance acBalance = AccountService.getInstance().getBalance(accountExtUid);
        if(acBalance==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        AccountBalanceDTO dto = new AccountBalanceDTO();
        dto.setBalance(acBalance.getBalance());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(dto));
    }
}