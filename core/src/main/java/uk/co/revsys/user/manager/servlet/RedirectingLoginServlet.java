package uk.co.revsys.user.manager.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class RedirectingLoginServlet extends LoginServlet {

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
        if (this.loginUrl.startsWith("/")) {
            this.loginUrl = this.loginUrl.substring(1);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(loginUrl);
    }

    @Override
    public void doSuccess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(successUrl);
    }

    @Override
    public void doFailure(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(loginUrl);
    }
}
