package uk.co.revsys.user.manager.service.rest;

import javax.ws.rs.Path;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.Constants;

@Path("/accounts")
public class AccountRestService extends EntityRestService<Account, EntityService<Account>>{

	public AccountRestService(EntityService<Account> service) {
		super(service);
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
	
	protected boolean isOwner(Account e){
		Subject subject = SecurityUtils.getSubject();
		User user = subject.getPrincipals().oneByType(User.class);
		return subject.hasRole(Constants.ACCOUNT_OWNER_ROLE) && e.getId().equals(user.getAccount());
	}
	
	@Override
	protected Class<? extends Account> getEntityType() {
		return Account.class;
	}
}
