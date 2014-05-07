package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.Permission;


@Path("/permissions")
public class PermissionRestService extends EntityRestService<Permission>{

	public PermissionRestService(EntityService<Permission> service) {
		super(service);
	}

	@Override
	protected Class<? extends Permission> getEntityType() {
		return Permission.class;
	}

}
