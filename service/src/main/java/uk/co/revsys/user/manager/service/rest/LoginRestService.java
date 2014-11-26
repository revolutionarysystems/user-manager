package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.service.UserService;
import uk.co.revsys.user.manager.service.exception.ServiceException;

@Path("/login")
public class LoginRestService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoginRestService.class);
    
	private final UserService userService;
    private final EntityService accountService;
	private final ObjectMapper objectMapper;

	public LoginRestService(UserService userService, EntityService accountService) {
		this.userService = userService;
        this.accountService = accountService;
		this.objectMapper = new ObjectMapper();
	}
	
	@POST
	@Produces("application/json")
	public Response login(){
		try {
			Subject subject = SecurityUtils.getSubject();
			if(!subject.isAuthenticated()){
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			String userId = (String) subject.getPrincipal();
			User user = userService.findById(userId);
            JSONObject json = new JSONObject(objectMapper.writeValueAsString(user));
            json.put("account", new JSONObject(objectMapper.writeValueAsString(accountService.findById(user.getAccount()))));
			return Response.ok(json.toString()).build();
		} catch (DAOException ex) {
            LOGGER.error("Failed to login", ex);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (JsonProcessingException ex) {
            LOGGER.error("Failed to login", ex);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (ServiceException ex) {
            LOGGER.error("Failed to login", ex);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
	}
	
}
