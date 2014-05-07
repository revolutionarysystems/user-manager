package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.User;

@Path("/users")
public class UserRestService extends EntityRestService<User>{

	public UserRestService(EntityService<User> service) {
		super(service);
	}

	@Override
	protected Class<? extends User> getEntityType() {
		return User.class;
	}

}
