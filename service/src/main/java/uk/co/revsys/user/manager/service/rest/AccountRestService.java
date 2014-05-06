package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.service.model.Account;

@Path("/accounts")
public class AccountRestService extends EntityRestService<Account>{

	public AccountRestService(EntityService<Account> service) {
		super(service);
	}

	@Override
	protected Class<? extends Account> getEntityType() {
		return Account.class;
	}
}
