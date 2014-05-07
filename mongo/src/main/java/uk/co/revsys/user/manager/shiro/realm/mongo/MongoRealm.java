package uk.co.revsys.user.manager.shiro.realm.mongo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.EntityService;

public class MongoRealm extends AuthorizingRealm{

	private final EntityService<Account> accountService;
	private final EntityService<User> userService;
	private final String acccountId;

	public MongoRealm(EntityService<Account> accountService, EntityService<User> userService, String acccountId) {
		this.accountService = accountService;
		this.userService = userService;
		this.acccountId = acccountId;
	}
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {
		try {
			if(!(at instanceof UsernamePasswordToken)){
				return null;
			}
			UsernamePasswordToken token = (UsernamePasswordToken)at;
			Account account = accountService.findById(acccountId);
			if(account.getStatus().equals(Status.disabled)){
				return null;
			}
			Map filters = new HashMap();
			filters.put("accountId", acccountId);
			filters.put("username", token.getUsername());
			User user = userService.findOne(filters);
			if(user == null || !user.getPassword().equals(token.getPassword())){
				return null;
			}
			SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
			principalCollection.add("userId", user.getId());
			principalCollection.add("displayName", user.getName());
			SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo();
			authenticationInfo.setPrincipals(principalCollection);
			return authenticationInfo;
		} catch (IOException ex) {
			throw new AuthenticationException(ex);
		}
	}

}
