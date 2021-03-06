package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.AccountService;
import uk.co.revsys.user.manager.Constants;
import uk.co.revsys.user.manager.service.UserService;
import uk.co.revsys.user.manager.service.exception.ServiceException;

@Path("/accounts")
public class AccountRestService extends EntityRestService<Account, AccountService> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AccountRestService.class);

    private final UserService userService;

    public AccountRestService(AccountService service, UserService userService) {
        super(service);
        this.userService = userService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response create(String json) {
        try {
            Account account = getObjectMapper().readValue(json, getEntityType());
            if (!isAuthorisedToCreate(account)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            JSONObject jsonObj = new JSONObject(json);
            if (jsonObj.has("user")) {
                User user = getObjectMapper().readValue(jsonObj.get("user").toString(), User.class);
                User existingUser = userService.findByUsername(user.getUsername());
                if (existingUser != null) {
                    if (existingUser.getStatus().equals(Status.pending)) {
                        getService().delete(existingUser.getAccount());
                        userService.delete(existingUser.getId());
                    } else {
                        throw new DuplicateKeyException("A user with username " + user.getUsername() + " already exists");
                    }
                }
                account = getService().create(account);
                user.setAccount(account.getId());
                user = userService.create(user);
                JSONObject userJSON = new JSONObject(toJSONString(user));
                JSONObject accountJSON = new JSONObject(toJSONString(account));
                accountJSON.put("verificationCode", account.getVerificationCode());
                return Response.ok("{\"account\": " + accountJSON.toString() + ", \"user\": " + userJSON.toString() + "}").build();
            } else {
                account = getService().create(account);
                JSONObject accountJSON = new JSONObject(toJSONString(account));
                accountJSON.put("verificationCode", account.getVerificationCode());
                return Response.ok(accountJSON.toString()).build();
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to create account: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to create account: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DuplicateKeyException ex) {
            LOGGER.error("Failed to create account: " + json, ex);
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to create account: " + json, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to create account: " + json, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Path("/{id}/verify")
    public Response verify(@PathParam("id") String accountId, @FormParam("code") String code) {
        try {
            Account account = getService().findById(accountId);
            if (account == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            getService().verify(account, code);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to verify account: " + accountId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to verify account: " + accountId, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to verify account: " + accountId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/addUser")
    public Response addUser(@PathParam("id") String id, String json) {
        try {
            Account account = getService().findById(id);
            if(!isAuthorisedToUpdate(account)){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if (account.getMaximumUsers() > 0 && getService().getUsers(account).size() >= account.getMaximumUsers()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Maximum number of users reached").build();
            }
            User user = getObjectMapper().readValue(json, User.class);
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                if (existingUser.getStatus().equals(Status.pending)) {
                    getService().delete(existingUser.getAccount());
                    userService.delete(existingUser.getId());
                } else {
                    throw new DuplicateKeyException("A user with username " + user.getUsername() + " already exists");
                }
            }
            user.setAccount(account.getId());
            user = userService.create(user);
            return Response.ok(toJSONString(user)).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to create user: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to create user: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DuplicateKeyException ex) {
            LOGGER.error("Failed to create user: " + json, ex);
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to create user: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/addRole")
    public Response addRole(@PathParam("id") String id, @FormParam("role") String role){
        try {
            Account account = getService().findById(id);
            if(!isAdministrator()){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().addRole(account, role);
            return Response.ok(toJSONString(account)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to add role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to add role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to add role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/removeRole")
    public Response removeRole(@PathParam("id") String id, @FormParam("role") String role){
        try {
            Account account = getService().findById(id);
            if(!isAdministrator()){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().removeRole(account, role);
            return Response.ok(toJSONString(account)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to remove role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to remove role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to remove role " + role + " to account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    public Response activate(@PathParam("id") String id) {
        try {
            Account account = getService().findById(id);
            if (!isAuthorisedToCreate(account)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().activate(account);
            return Response.ok(toJSONString(account)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to activate account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to activate account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to activate account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to activate account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{id}/disable")
    public Response disable(@PathParam("id") String id) {
        try {
            Account account = getService().findById(id);
            if (!isAdministrator() && !isOwner(account)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().disable(account);
            return Response.ok(toJSONString(account)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to disable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to disable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to disable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to disable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @POST
    @Path("/{id}/enable")
    public Response enable(@PathParam("id") String id) {
        try {
            Account account = getService().findById(id);
            if (!isAdministrator() && !isOwner(account)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            getService().enable(account);
            return Response.ok(toJSONString(account)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to enable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to enable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to enable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to enable account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@PathParam("id") String id) {
        try {
            Account account = getService().findById(id);
            if (account == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                if (isOwner(account) || isAdministrator()) {
                    List<User> users = getService().getUsers(account);
                    return Response.status(Response.Status.OK).entity(toJSONString(users)).build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            }
        } catch (DAOException ex) {
            LOGGER.error("Failed to retrieve users for account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to retrieve users for account: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Override
    protected boolean isAuthorisedToCreate(Account e) {
        return isAdministrator();
    }

    @Override
    protected boolean isAuthorisedToFindById(Account e) {
        return isAdministrator() || isOwner(e);
    }

    @Override
    protected boolean isAuthorisedToDelete(Account e) {
        return isAdministrator();
    }

    @Override
    protected boolean isAuthorisedToUpdate(Account e) {
        return isAdministrator() || isOwner(e);
    }

    protected boolean isOwner(Account e) {
        Subject subject = SecurityUtils.getSubject();
        User user = subject.getPrincipals().oneByType(User.class);
        return subject.hasRole(Constants.ACCOUNT_OWNER_ROLE) && e.getId().equals(user.getAccount());
    }

    @Override
    protected Class<? extends Account> getEntityType() {
        return Account.class;
    }
}
