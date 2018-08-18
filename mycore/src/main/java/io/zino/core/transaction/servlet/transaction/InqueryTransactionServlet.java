package io.zino.core.transaction.servlet.transaction;

import com.google.gson.Gson;
import io.zino.core.transaction.servlet.transaction.dto.InqueryDTO;
import io.zino.core.transaction.model.transaction.Transaction;
import io.zino.core.transaction.service.transaction.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InqueryTransactionServlet extends HttpServlet {

    private Gson gson = new Gson();

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

        Boolean verify = tnx.isVerify();
        if(verify==null){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        InqueryDTO dto = new InqueryDTO();
        dto.setExtuid(extuid);
        dto.setVerify(verify);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(dto));
    }
}