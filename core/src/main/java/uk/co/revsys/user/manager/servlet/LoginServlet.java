package uk.co.revsys.user.manager.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

public abstract class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            subject.login(new UsernamePasswordToken(username, password));
            doSuccess(req, resp);
        } catch (UnknownAccountException ex) {
            doFailure(req, resp);
        } catch (IncorrectCredentialsException ex) {
            doFailure(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
    
    public abstract void doSuccess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    
    public abstract void doFailure(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

}
