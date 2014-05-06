package uk.co.revsys.user.manager.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.service.model.AbstractEntity;

public abstract class EntityRestService<E extends AbstractEntity> {

	private final EntityService<E> service;
	private final ObjectMapper objectMapper;

	public EntityRestService(EntityService<E> service) {
		this.service = service;
		this.objectMapper = new ObjectMapper();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json) throws IOException {
		E entity = objectMapper.readValue(json, getEntityType());
		entity = service.create(entity);
		return toJSONString(entity);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String findById(@PathParam("id") String id) throws IOException {
		E entity = service.findById(id);
		return toJSONString(entity);
	}

	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(@PathParam("id") String id, String json) throws IOException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return value==null;
			}
		});
		E existingEntity = service.findById(id);
		JSONObject existingJSON = JSONObject.fromObject(existingEntity, jsonConfig);
		E newEntity = objectMapper.readValue(json, getEntityType());
		JSONObject newJSON = JSONObject.fromObject(newEntity, jsonConfig);
		existingJSON.putAll(newJSON);
		E entity = objectMapper.readValue(existingJSON.toString(), getEntityType());
		entity.setId(id);
		entity = service.update(entity);
		return toJSONString(entity);
	}

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) throws IOException {
		service.delete(id);
	}

	protected String toJSONString(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}

	protected abstract Class<? extends E> getEntityType();

}
