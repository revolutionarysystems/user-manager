package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.UserService;

@Path("/login")
public class LoginRestService {

	private final UserService userService;
	private final ObjectMapper objectMapper;

	public LoginRestService(UserService userService) {
		this.userService = userService;
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
			return Response.ok(objectMapper.writeValueAsString(user)).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (JsonProcessingException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
