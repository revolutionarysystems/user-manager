package uk.co.revsys.user.manager.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SimpleLoginServlet extends LoginServlet {

    private String successUrl;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        ShiroFilterFactoryBean shiroFactoryFactoryBean = webApplicationContext.getBean(ShiroFilterFactoryBean.class);
        this.successUrl = shiroFactoryFactoryBean.getSuccessUrl();
    }

    @Override
    public void doSuccess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(200);
        resp.getOutputStream().print(successUrl);
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
    }

    @Override
    public void doFailure(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(401);
    }

}
