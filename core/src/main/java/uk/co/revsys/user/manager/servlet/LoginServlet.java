package uk.co.revsys.user.manager.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class LoginServlet extends HttpServlet {

    private String successUrl;
    private String loginUrl;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        ShiroFilterFactoryBean shiroFactoryFactoryBean = webApplicationContext.getBean(ShiroFilterFactoryBean.class);
        this.successUrl = shiroFactoryFactoryBean.getSuccessUrl();
        this.loginUrl = shiroFactoryFactoryBean.getLoginUrl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet.doGet");
        resp.sendRedirect(loginUrl);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet.doPost");
        Subject subject = SecurityUtils.getSubject();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        subject.login(new UsernamePasswordToken(username, password));
        System.out.println("Login Success - redirecting to " + successUrl);
        resp.sendRedirect(successUrl);
    }

}
