package uk.co.revsys.user.manager.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import uk.co.revsys.user.manager.jackson.UserJacksonMixin;
import uk.co.revsys.user.manager.model.User;

public class UserServlet extends HttpServlet{

    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = new ObjectMapper();
        objectMapper.addMixInAnnotations(User.class, UserJacksonMixin.class);
    }
    
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();
        User user = subject.getPrincipals().oneByType(User.class);
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(user));
    }

}
