package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.Constants;
import uk.co.revsys.user.manager.service.UserService;

@Path("/users")
public class UserRestService extends EntityRestService<User, UserService>{

	public UserRestService(UserService service) {
		super(service);
	}
	
	@GET
	@Path("/{id}/roles")
	public Response getRoles(@PathParam("id") String userId){
        System.out.println("get roles");
		try {
			User user = getService().findById(userId);
			if(user==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
            if(!isAuthorisedToFindById(user)){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
			List<Role> roles = getService().getRoles(user);
			return Response.ok(toJSONString(roles)).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (JsonProcessingException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path("/{id}/permissions")
	public Response getPermissions(@PathParam("id") String userId){
        System.out.println("get permissions");
		try {
			User user = getService().findById(userId);
			if(user==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
            if(!isAuthorisedToFindById(user)){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
			List<Permission> permissions = getService().getPermissions(user);
			return Response.ok(toJSONString(permissions)).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (JsonProcessingException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
    
    @POST
    @Path("/{id}/changePassword")
    public Response changePassword(@PathParam("id") String userId, @FormParam("password") String password){
        System.out.println("change password for " + userId + " to " + password);
        try {
            User user = getService().findById(userId);
            if(user==null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if(!isUser(user)){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().changePassword(user, password);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DuplicateKeyException ex) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }

	@Override
	protected boolean isAuthorisedToDelete(User e) {
		return isAdministrator() || (isAccountOwner(e) && !e.getRoles().contains(Constants.ACCOUNT_OWNER_ROLE));
	}

	@Override
	protected boolean isAuthorisedToUpdate(User e) {
		return isAdministrator() || isAccountOwner(e) || isUser(e);
	}

	@Override
	protected boolean isAuthorisedToFindById(User e) {
		return isAdministrator() || isAccountOwner(e) || isUser(e);
	}

	@Override
	protected boolean isAuthorisedToCreate(User e) {
		return isAdministrator() || isAccountOwner(e);
	}
	
	protected boolean isAccountOwner(User e){
		Subject subject = SecurityUtils.getSubject();
		User user = subject.getPrincipals().oneByType(User.class);
		return subject.hasRole(Constants.ACCOUNT_OWNER_ROLE) && user.getAccount().equals(e.getAccount());
	}
	
	protected boolean isUser(User e){
		Subject subject = SecurityUtils.getSubject();
		User user = subject.getPrincipals().oneByType(User.class);
		return user.getId().equals(e.getId());
	}

    @Override
    protected JSONObject filter(JSONObject json, User user) {
        if(!(isAdministrator() || isAccountOwner(user))){
            json.remove("password");
            json.remove("passwordSalt");
            json.remove("account");
            json.remove("roles");
            json.remove("status");
            json.remove("attributes");
        }
        return json;
    }

	@Override
	protected Class<? extends User> getEntityType() {
		return User.class;
	}

}
