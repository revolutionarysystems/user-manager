package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.AbstractEntity ;
import uk.co.revsys.user.manager.service.Constants;

public abstract class EntityRestService<E extends AbstractEntity, S extends EntityService<E>> {

	private final S service;
	private final ObjectMapper objectMapper;

	public EntityRestService(S service) {
		this.service = service;
		this.objectMapper = new ObjectMapper();
	}
	
	@GET
	@Path("/all")
	public Response findAll(){
		try {
			return Response.ok(toJSONString(service.findAll())).build();
		} catch (JsonProcessingException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String json){
		try {
			E entity = objectMapper.readValue(json, getEntityType());
			if(!isAuthorisedToCreate(entity)){
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			entity = service.create(entity);
			return Response.ok(toJSONString(entity)).build();
		} catch (IOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (DuplicateKeyException ex) {
			return Response.status(Response.Status.CONFLICT).build();
		} catch (ConstraintViolationException ex) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") String id){
		try {
			E entity = service.findById(id);
			if(entity==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			if(!isAuthorisedToFindById(entity)){
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			return Response.ok(toJSONString(entity)).build();
		} catch (JsonProcessingException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") String id, String json){
		try {
			E existingEntity = service.findById(id);
			if(!isAuthorisedToUpdate(existingEntity)){
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					if(value == null){
						return true;
					}else if(value instanceof List){
						return ((List)value).isEmpty();
					}else if(value instanceof Map){
						return ((Map)value).isEmpty();
					}
					return false;
				}
			});
			if(existingEntity==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			JSONObject existingJSON = JSONObject.fromObject(existingEntity, jsonConfig);
			E newEntity = objectMapper.readValue(json, getEntityType());
			JSONObject newJSON = JSONObject.fromObject(newEntity, jsonConfig);
			existingJSON.putAll(newJSON);
			E entity = objectMapper.readValue(existingJSON.toString(), getEntityType());
			entity.setId(id);
			entity = service.update(entity);
			return Response.ok(toJSONString(entity)).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (ConstraintViolationException ex) {
			return Response.status(Response.Status.CONFLICT).build();
		}catch (IOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String id){
		try {
			E entity = service.findById(id);
			if(entity == null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			if(!isAuthorisedToDelete(entity)){
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			service.delete(id);
			return Response.status(Response.Status.NO_CONTENT).build();
		} catch (DAOException ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	protected S getService() {
		return service;
	}

	protected String toJSONString(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}
	
	protected boolean isAuthorisedToCreate(E e){
		return true;
	}
	
	protected boolean isAuthorisedToFindById(E e){
		return true;
	}
	
	protected boolean isAuthorisedToUpdate(E e){
		return true;
	}
	
	protected boolean isAuthorisedToDelete(E e){
		return true;
	}
	
	protected boolean isAdministrator(){
		return SecurityUtils.getSubject().hasRole(Constants.ADMINISTRATOR_ROLE);
	}

	protected abstract Class<? extends E> getEntityType();

}
