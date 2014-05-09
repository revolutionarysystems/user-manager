package uk.co.revsys.user.manager.shiro.realm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.shiro.realm.exception.RealmException;

public abstract class AbstractAuthorizingRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
		try {
			String userId = (String) pc.getPrimaryPrincipal();
			User user = getUser(userId);
			SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
			for (Role role : getRoles(user)) {
				authorizationInfo.addRole(role.getName());
			}
			for (Permission permission : getPermissions(user)) {
				authorizationInfo.addStringPermission(permission.getName());
			}
			return authorizationInfo;
		} catch (RealmException ex) {
			throw new AuthorizationException(ex);
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {
		try {
			if (!(at instanceof UsernamePasswordToken)) {
				return null;
			}
			UsernamePasswordToken token = (UsernamePasswordToken) at;
			User user = getUser(token);
			if (user == null || user.getStatus().equals(Status.disabled)) {
				return null;
			}
			Account account = getAccount(user.getAccount());
			if (account == null || account.getStatus().equals(Status.disabled)) {
				return null;
			}
			String password = user.getPassword();
			System.out.println("password = " + password);
			String saltBase64 = user.getPasswordSalt();
			System.out.println("saltBase64 = " + saltBase64);
			byte[] saltBytes = Base64.decode(saltBase64);
			SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
			principalCollection.add(user.getId(), getName());
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("name", user.getName());
			principalCollection.add(user, getName());
			SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principalCollection, password, ByteSource.Util.bytes(saltBytes));
			System.out.println("authenticationInfo = " + authenticationInfo);
			return authenticationInfo;
		} catch (RealmException ex) {
			throw new AuthenticationException(ex);
		}
	}

	protected abstract Account getAccount(String accountId) throws RealmException;

	protected abstract User getUser(UsernamePasswordToken token) throws RealmException;

	protected abstract User getUser(String userId) throws RealmException;

	protected abstract List<Role> getRoles(User user) throws RealmException;

	protected abstract List<Permission> getPermissions(User user) throws RealmException;

}
