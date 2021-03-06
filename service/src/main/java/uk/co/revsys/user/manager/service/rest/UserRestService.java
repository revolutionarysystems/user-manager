package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.Constants;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.service.UserService;
import uk.co.revsys.user.manager.service.exception.ServiceException;

@Path("/users")
public class UserRestService extends EntityRestService<User, UserService> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UserRestService.class);
    
    public UserRestService(UserService service) {
        super(service);
    }

    @GET
    @Path("/me")
    @Produces("application/json")
    public Response me() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String userId = (String) subject.getPrincipal();
            User user = getService().findById(userId);
            return Response.ok(toJSONString(user)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to return current user", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to return current user", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/roles")
    public Response getRoles(@PathParam("id") String userId) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (!isAuthorisedToFindById(user)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            List<Role> roles = getService().getRoles(user);
            return Response.ok(toJSONString(roles)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to retrieve user roles: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to retrieve user roles: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/permissions")
    public Response getPermissions(@PathParam("id") String userId) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (!isAuthorisedToFindById(user)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            List<Permission> permissions = getService().getPermissions(user);
            return Response.ok(toJSONString(permissions)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to retrieve user permissions: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to retrieve user permissions: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to retrieve user permissions: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{id}/changePassword")
    public Response changePassword(@PathParam("id") String userId, @FormParam("password") String password) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (!isAdministrator() && !isUser(user)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().changePassword(user, password);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to change password: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to change password: " + userId, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to change password: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    // When POST was used username was double decoded - so username with a + in would come through with a space instead
    @PUT
    @Path("/changePassword")
    public Response changePasswordByUsername(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            User user = getService().findOne("username", username);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
            }
            if (!isAdministrator()) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().changePassword(user, password);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to change password: " + username, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to change password: " + username, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to change password: " + username, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Path("/resetPassword")
    public Response resetPassword(@FormParam("username") String username){
        try {
            User user = getService().findOne("username", username);
            if(user == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            getService().resetPassword(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to reset password: " + username, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to reset password: " + username, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Path("/{id}/addRole")
    public Response addRole(@PathParam("id") String id, @FormParam("role") String role){
        try {
            User user = getService().findById(id);
            if(!isAdministrator()){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().addRole(user, role);
            return Response.ok(toJSONString(user)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to add role " + role + " to user: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to add role " + role + " to user: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to add role " + role + " to user: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/profilePicture")
    public Response getProfilePicture(@PathParam("id") String userId) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            InputStream profilePicture = getService().getProfilePicture(user);
            return Response.ok(profilePicture, MediaType.IMAGE_PNG_VALUE).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to retrieve profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (FileNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to retrieve profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.IMAGE_PNG_VALUE)
    @Path("/{id}/profilePicture")
    public Response setProfilePicture(@PathParam("id") String userId, InputStream pictureStream) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            String base64Picture = IOUtils.toString(pictureStream);
            byte[] pictureBytes = Base64.decodeBase64(base64Picture);
            getService().setProfilePicture(user, new ByteArrayInputStream(pictureBytes));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to save profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to save profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA_VALUE)
    @Path("/{id}/profilePicture")
    public Response uploadProfilePicture(@PathParam("id") String userId, BufferedInMultiPart bufferedInMultiPart) {
        try {
            User user = getService().findById(userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            List<InPart> parts = bufferedInMultiPart.getParts();
            InPart part = parts.get(0);
            String contentDispositionHeaderValue = part.getHeaders().getFirst("Content-Disposition");
            InputStream pictureStream = part.getInputStream();
            getService().setProfilePicture(user, pictureStream);
            return Response.status(Response.Status.OK).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to save profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to save profile picture: " + userId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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

    protected boolean isAccountOwner(User e) {
        Subject subject = SecurityUtils.getSubject();
        User user = subject.getPrincipals().oneByType(User.class);
        return subject.hasRole(Constants.ACCOUNT_OWNER_ROLE) && user.getAccount().equals(e.getAccount());
    }

    protected boolean isUser(User e) {
        Subject subject = SecurityUtils.getSubject();
        User user = subject.getPrincipals().oneByType(User.class);
        return user.getId().equals(e.getId());
    }

    @Override
    protected JSONObject doFilter(JSONObject json, User user) {
        if (!(isAdministrator() || isAccountOwner(user))) {
            json.remove("verificationCode");
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
