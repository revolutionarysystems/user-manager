package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.Role;

@Path("/roles")
public class RoleRestService extends EntityRestService<Role>{

	public RoleRestService(EntityService<Role> service) {
		super(service);
	}

	@Override
	protected Class<? extends Role> getEntityType() {
		return Role.class;
	}

}
