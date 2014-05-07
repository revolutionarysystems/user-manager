package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.Application;

@Path("/applications")
public class ApplicationRestService extends EntityRestService<Application>{

	public ApplicationRestService(EntityService<Application> service) {
		super(service);
	}

	@Override
	protected Class<? extends Application> getEntityType() {
		return Application.class;
	}

}
