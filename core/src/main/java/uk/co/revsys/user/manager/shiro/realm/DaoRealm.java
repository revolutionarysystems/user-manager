package uk.co.revsys.user.manager.shiro.realm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.UsernamePasswordToken;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.service.UserService;
import uk.co.revsys.user.manager.shiro.realm.exception.RealmException;

public class DaoRealm extends AbstractAuthorizingRealm {

	private final EntityService<Account> accountService;
	private final UserService userService;

	public DaoRealm(EntityService<Account> accountService, UserService userService) {
		this.accountService = accountService;
		this.userService = userService;
	}

	@Override
	protected Account getAccount(String accountId) throws RealmException {
		try {
			return accountService.findById(accountId);
		} catch (DAOException ex) {
			throw new RealmException(ex);
		}
	}

	@Override
	protected User getUser(UsernamePasswordToken token) throws RealmException {
		try {
			Map filters = new HashMap();
			filters.put("username", token.getUsername());
			User user = userService.findOne(filters);
			return user;
		} catch (DAOException ex) {
			throw new RealmException(ex);
		}
	}

	@Override
	protected User getUser(String userId) throws RealmException {
		try {
			return userService.findById(userId);
		} catch (DAOException ex) {
			throw new RealmException(ex);
		}
	}

	@Override
	protected List<Role> getRoles(User user) throws RealmException {
		try {
			return userService.getRoles(user);
		} catch (DAOException ex) {
			throw new RealmException(ex);
		}
	}

	@Override
	protected List<Permission> getPermissions(User user) throws RealmException {
		try {
			return userService.getPermissions(user);
		} catch (DAOException ex) {
			throw new RealmException(ex);
		}
	}

}
