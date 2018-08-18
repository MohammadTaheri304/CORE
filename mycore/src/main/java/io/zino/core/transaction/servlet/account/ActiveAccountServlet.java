package io.zino.core.transaction.servlet.account;

import com.google.gson.Gson;
import io.zino.core.transaction.service.account.AccountService;
import io.zino.core.transaction.servlet.account.dto.ChangeAccountStatusDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ActiveAccountServlet extends HttpServlet {

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

        boolean res = AccountService.getInstance().active(accountExtUid);

        if(!res){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        ChangeAccountStatusDTO dto = new ChangeAccountStatusDTO();
        dto.setActive(true);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(dto));
    }
}