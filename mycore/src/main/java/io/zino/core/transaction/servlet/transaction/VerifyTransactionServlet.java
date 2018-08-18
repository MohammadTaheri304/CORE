package io.zino.core.transaction.servlet.transaction;

import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.service.transaction.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class VerifyTransactionServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String extuid= request.getParameter("extuid");
        if(extuid==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        Transaction tnx = TransactionService.getInstance().findByExtUid(extuid);
        if(tnx==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        boolean res = TransactionService.getInstance().verify(tnx);
        if(!res){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ \"status\": \"ok\"}");
    }

}