package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.service.RoleService;

@Path("/roles")
public class RoleRestService extends EntityRestService<Role, RoleService>{

	public RoleRestService(RoleService service) {
		super(service);
	}

	@Override
	protected Class<? extends Role> getEntityType() {
		return Role.class;
	}

}
