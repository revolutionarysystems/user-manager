package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
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
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.AccountService;
import uk.co.revsys.user.manager.service.Constants;
import uk.co.revsys.user.manager.service.UserService;

@Path("/accounts")
public class AccountRestService extends EntityRestService<Account, AccountService> {

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
            account = getService().create(account);
            JSONObject jsonObj = new JSONObject(json);
            if (jsonObj.has("user")) {
                User user = getObjectMapper().readValue(jsonObj.get("user").toString(), User.class);
                user.setAccount(account.getId());
                user = userService.create(user);
                return Response.ok("{\"account\": " + toJSONString(account) + ", \"user\": " + toJSONString(user) + "}").build();
            } else {
                return Response.ok(toJSONString(account)).build();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DAOException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DuplicateKeyException ex) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
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
                if (isOwner(account)) {
                    List<User> users = getService().getUsers(account);
                    return Response.status(Response.Status.OK).entity(toJSONString(users)).build();
                }else{
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            }
        } catch (DAOException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (JsonProcessingException ex) {
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
