package uk.co.revsys.user.manager.shiro.realm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.EntityService;
import uk.co.revsys.user.manager.service.UserService;

public class DaoRealmTest {

	IMocksControl mocksControl;
	EntityService<Account> mockAccountService;
	UserService mockUserService;
	String accountId = "123";
	DaoRealm daoRealm;

	public DaoRealmTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		mocksControl = EasyMock.createControl();
		mockAccountService = mocksControl.createMock(EntityService.class);
		mockUserService = mocksControl.createMock(UserService.class);
		daoRealm = new DaoRealm(mockAccountService, mockUserService);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testDoGetAuthenticationInfo() throws Exception {
		User user = new User();
		user.setId("user1");
		user.setAccount(accountId);
		user.setStatus(Status.enabled);
		user.setPassword("passwordhash");
		user.setPasswordSalt("passwordsalt");
		expect(mockUserService.findOne(isA(Map.class))).andReturn(user);
		Account account = new Account();
		account.setStatus(Status.enabled);
		expect(mockAccountService.findById(accountId)).andReturn(account);
		mocksControl.replay();
		AuthenticationInfo result = daoRealm.doGetAuthenticationInfo(new UsernamePasswordToken("testuser", "password123"));
		System.out.println("result = " + result);
		assertEquals("user1", result.getPrincipals().getPrimaryPrincipal());
		mocksControl.verify();
	}
	
	@Test
	public void testDoGetAuthenticationInfo_AccountNotFound() throws Exception {
		User user = new User();
		user.setId("user1");
		user.setAccount(accountId);
		user.setStatus(Status.enabled);
		user.setPassword("password123");
		expect(mockUserService.findOne(isA(Map.class))).andReturn(user);
		expect(mockAccountService.findById(accountId)).andReturn(null);
		mocksControl.replay();
		AuthenticationInfo result = daoRealm.doGetAuthenticationInfo(new UsernamePasswordToken("testuser", "password123"));
		assertNull(result);
		mocksControl.verify();
	}
	
	@Test
	public void testDoGetAuthenticationInfo_AccountDisabled() throws Exception {
		User user = new User();
		user.setId("user1");
		user.setAccount(accountId);
		user.setStatus(Status.enabled);
		user.setPassword("password123");
		expect(mockUserService.findOne(isA(Map.class))).andReturn(user);
		Account account = new Account();
		account.setStatus(Status.disabled);
		expect(mockAccountService.findById(accountId)).andReturn(account);
		mocksControl.replay();
		AuthenticationInfo result = daoRealm.doGetAuthenticationInfo(new UsernamePasswordToken("testuser", "password123"));
		assertNull(result);
		mocksControl.verify();
	}
	
	@Test
	public void testDoGetAuthenticationInfo_UserNotFound() throws Exception {
		expect(mockUserService.findOne(isA(Map.class))).andReturn(null);
		mocksControl.replay();
		AuthenticationInfo result = daoRealm.doGetAuthenticationInfo(new UsernamePasswordToken("testuser", "password123"));
		assertNull(result);
		mocksControl.verify();
	}
	
	@Test
	public void testDoGetAuthenticationInfo_UserDisabled() throws Exception {
		User user = new User();
		user.setId("user1");
		user.setStatus(Status.disabled);
		user.setPassword("password123");
		expect(mockUserService.findOne(isA(Map.class))).andReturn(user);
		mocksControl.replay();
		AuthenticationInfo result = daoRealm.doGetAuthenticationInfo(new UsernamePasswordToken("testuser", "password123"));
		assertNull(result);
		mocksControl.verify();
	}
	
	@Test
	public void testDoGetAuthorizationInfo() throws Exception{
		PrincipalCollection principalCollection = new SimplePrincipalCollection("userId", "test");
		User user = new User();
		expect(mockUserService.findById("userId")).andReturn(user);
		List<Role> roles = new ArrayList<Role>();
		Role role = new Role();
		role.setName("role1");
		roles.add(role);
		expect(mockUserService.getRoles(user)).andReturn(roles);
		List<Permission> permissions = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.setName("permission1");
		permissions.add(permission);
		expect(mockUserService.getPermissions(user)).andReturn(permissions);
		mocksControl.replay();
		AuthorizationInfo doGetAuthorizationInfo = daoRealm.doGetAuthorizationInfo(principalCollection);
		assertEquals(1, doGetAuthorizationInfo.getRoles().size());
		assertEquals("role1", doGetAuthorizationInfo.getRoles().iterator().next());
		assertEquals(1, doGetAuthorizationInfo.getStringPermissions().size());
		assertEquals("permission1", doGetAuthorizationInfo.getStringPermissions().iterator().next());
		mocksControl.verify();
	}

}
