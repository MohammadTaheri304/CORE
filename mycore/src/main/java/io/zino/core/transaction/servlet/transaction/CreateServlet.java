package io.zino.core.transaction.servlet.transaction;

import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.service.transaction.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class CreateServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String extuid= request.getParameter("extuid");
        if(extuid==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Long fromAccountId= Long.parseLong(request.getParameter("fromAccountId"));
        if(fromAccountId==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Long tempAccountId= Long.parseLong(request.getParameter("tempAccountId"));
        if(tempAccountId==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Long toAccountId= Long.parseLong(request.getParameter("toAccountId"));
        if(toAccountId==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        BigDecimal amount= new BigDecimal(request.getParameter("amount"));
        if(amount==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        String description= new String(request.getParameter("description"));

        Transaction tnx = new Transaction(extuid, fromAccountId, tempAccountId, toAccountId, amount, description);
        boolean res = TransactionService.getInstance().create(tnx);
        if(!res){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }
}
