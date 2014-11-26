package uk.co.revsys.user.manager.service.rest;

import uk.co.revsys.user.manager.jackson.UserJacksonMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.AbstractEntity;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.Constants;
import uk.co.revsys.user.manager.service.exception.ServiceException;
import uk.co.revsys.user.manager.service.exception.UserAlreadyExistsException;

public abstract class EntityRestService<E extends AbstractEntity, S extends EntityService<E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRestService.class);

    private final S service;
    private final ObjectMapper objectMapper;

    public EntityRestService(S service) {
        this.service = service;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.addMixInAnnotations(User.class, UserJacksonMixin.class);
    }

    @GET
    @Path("/all")
    public Response findAll() {
        try {
            return Response.ok(toJSONString(service.findAll())).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to find all entities", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to find all entities", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to find all entities", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String json) {
        try {
            E entity = objectMapper.readValue(json, getEntityType());
            if (!isAuthorisedToCreate(entity)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            entity = service.create(entity);
            return Response.ok(toJSONString(entity)).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to create entity: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to create entity: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DuplicateKeyException ex) {
            LOGGER.error("Failed to create entity: " + json, ex);
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to create entity: " + json, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to create entity: " + json, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") String id) {
        try {
            E entity = service.findById(id);
            if (entity == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (!isAuthorisedToFindById(entity)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            return Response.ok(toJSONString(entity)).build();
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to find entity: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to find entity: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to find entity: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String id, String json) {
        LOGGER.info("Updating " + id + ": " + json);
        try {
            E existingEntity = service.findById(id);
            if (existingEntity == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (!isAuthorisedToUpdate(existingEntity)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            final org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                @Override
                public boolean apply(Object source, String name, Object value) {
                    if (!getEntityType().isAssignableFrom(source.getClass())) {
                        return false;
                    }
                    return !jsonObject.has(name);
                }
            });
            JSONObject existingJSON = JSONObject.fromObject(existingEntity);
            json = filter(new org.json.JSONObject(json), existingEntity).toString();
            E newEntity = objectMapper.readValue(json, getEntityType());
            JSONObject newJSON = JSONObject.fromObject(newEntity, jsonConfig);
            existingJSON.putAll(newJSON);
            E entity = objectMapper.readValue(existingJSON.toString(), getEntityType());
            entity.setId(id);
            LOGGER.info("Updating " + id + ": " + existingJSON.toString());
            entity = service.update(entity);
            return Response.ok(toJSONString(entity)).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to update entity " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Failed to update entity " + id, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (IOException ex) {
            LOGGER.error("Failed to update entity " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (UserAlreadyExistsException ex) {
            LOGGER.error("Failed to update entity " + id, ex);
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to update entity " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        try {
            E entity = service.findById(id);
            if (entity == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (!isAuthorisedToDelete(entity)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            service.delete(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (DAOException ex) {
            LOGGER.error("Failed to delete entity: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ServiceException ex) {
            LOGGER.error("Failed to delete entity: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    protected S getService() {
        return service;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected String toJSONString(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    protected boolean isAuthorisedToCreate(E e) {
        return true;
    }

    protected boolean isAuthorisedToFindById(E e) {
        return true;
    }

    protected boolean isAuthorisedToUpdate(E e) {
        return true;
    }

    protected boolean isAuthorisedToDelete(E e) {
        return true;
    }

    protected org.json.JSONObject filter(org.json.JSONObject json, E entity) {
        return json;
    }

    protected boolean isAdministrator() {
        return SecurityUtils.getSubject().hasRole(Constants.ADMINISTRATOR_ROLE);
    }

    protected abstract Class<? extends E> getEntityType();

}
