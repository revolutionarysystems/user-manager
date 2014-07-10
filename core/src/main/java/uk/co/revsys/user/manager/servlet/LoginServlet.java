package uk.co.revsys.user.manager.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
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
        // Shiro uses urls that start with a / to be relative to the webapp, tomcat interprets this as relative to the application root directory (ie. webapps)
        if(this.loginUrl.startsWith("/")){
            this.loginUrl = this.loginUrl.substring(1);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(loginUrl);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet.doPost");
        Subject subject = SecurityUtils.getSubject();
        System.out.println("subject = " + subject);
        System.out.println("subject = " + subject.isAuthenticated());
        String username = req.getParameter("username");
        System.out.println("username = " + username);
        String password = req.getParameter("password");
        System.out.println("password = " + password);
        try {
            subject.login(new UsernamePasswordToken(username, password));
            System.out.println("success");
            System.out.println("subject = " + subject.isAuthenticated());
            resp.sendRedirect(successUrl); 
        } catch (UnknownAccountException ex) {
            ex.printStackTrace();
            resp.sendRedirect(loginUrl);
        } catch(IncorrectCredentialsException ex){
            ex.printStackTrace();
            resp.sendRedirect(loginUrl);
        }
    }

}
