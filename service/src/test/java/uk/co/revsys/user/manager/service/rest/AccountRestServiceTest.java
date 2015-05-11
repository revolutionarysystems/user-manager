package uk.co.revsys.user.manager.service.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.test.util.AbstractShiroTest;
import uk.co.revsys.user.manager.service.AccountService;
import uk.co.revsys.user.manager.service.UserService;

public class AccountRestServiceTest extends AbstractShiroTest {

    IMocksControl mocksControl;
    Subject mockSubject;
    AccountService mockAccountService;
    UserService mockUserService;
    AccountRestService accountRestService;

    public AccountRestServiceTest() {
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
        mockSubject = mocksControl.createMock(Subject.class);
        setSubject(mockSubject);
        mockAccountService = mocksControl.createMock(AccountService.class);
        mockUserService = mocksControl.createMock(UserService.class);
        accountRestService = new AccountRestService(mockAccountService, mockUserService);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreate() throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", "Test Account");
        Capture<Account> accountCapture = new Capture<Account>();
        Account account = new Account();
        account.setName("Test Account");
        account.setId("1234");
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(true);
        expect(mockAccountService.create(EasyMock.capture(accountCapture))).andReturn(account);
        mocksControl.replay();
        Response response = accountRestService.create(json.toString());
        assertEquals(200, response.getStatus());
        Account capturedAccount = accountCapture.getValue();
        assertEquals("Test Account", capturedAccount.getName());
        mocksControl.verify();
    }
    
    @Test
    public void testCreateAccountAndUser() throws Exception {
        JSONObject accountJson = new JSONObject();
        accountJson.put("name", "Test Account");
        JSONObject userJson = new JSONObject();
        userJson.put("name", "Test User");
        userJson.put("username", "test@test.com");
        userJson.put("password", "testing123");
        accountJson.put("user", userJson);
        Capture<Account> accountCapture = new Capture<Account>();
        Capture<User> userCapture = new Capture<User>();
        Account account = new Account();
        account.setName("Test Account");
        account.setId("1234");
        User user = new User();
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(true);
        expect(mockUserService.findByUsername("test@test.com")).andReturn(null);
        expect(mockAccountService.create(EasyMock.capture(accountCapture))).andReturn(account);
        expect(mockUserService.create(capture(userCapture))).andReturn(user);
        mocksControl.replay();
        Response response = accountRestService.create(accountJson.toString());
        assertEquals(200, response.getStatus());
        Account capturedAccount = accountCapture.getValue();
        assertEquals("Test Account", capturedAccount.getName());
        User capturedUser = userCapture.getValue();
        assertEquals("Test User", capturedUser.getName());
        assertEquals("1234", capturedUser.getAccount());
        mocksControl.verify();
    }
    
    @Test
    public void testAddUser() throws Exception {
        JSONObject userJson = new JSONObject();
        userJson.put("name", "Test User");
        userJson.put("username", "test@test.com");
        userJson.put("password", "testing123");
        Capture<User> userCapture = new Capture<User>();
        Account account = new Account();
        account.setName("Test Account");
        account.setId("1234");
        account.setMaximumUsers(-1);
        User user = new User();
        user.setAccount("1234");
        List<User> users = new ArrayList<User>();
        users.add(user);
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(false);
        expect(mockSubject.getPrincipals()).andReturn(new SimplePrincipalCollection(user, "test"));
        expect(mockSubject.hasRole("user-manager:account-owner")).andReturn(true);
        expect(mockAccountService.findById("1234")).andReturn(account);
        //expect(mockAccountService.getUsers(account)).andReturn(users);
        expect(mockUserService.findByUsername("test@test.com")).andReturn(null);
        expect(mockUserService.create(capture(userCapture))).andReturn(user);
        mocksControl.replay();
        Response response = accountRestService.addUser("1234", userJson.toString());
        assertEquals(200, response.getStatus());
        User capturedUser = userCapture.getValue();
        assertEquals("Test User", capturedUser.getName());
        assertEquals("1234", capturedUser.getAccount());
        mocksControl.verify();
    }
    
    @Test
    public void testAddUserMaximumUsersReached() throws Exception {
        JSONObject userJson = new JSONObject();
        userJson.put("name", "Test User");
        userJson.put("username", "test@test.com");
        userJson.put("password", "testing123");
        Capture<User> userCapture = new Capture<User>();
        Account account = new Account();
        account.setName("Test Account");
        account.setId("1234");
        account.setMaximumUsers(1);
        User user = new User();
        user.setAccount("1234");
        List<User> users = new ArrayList<User>();
        users.add(user);
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(true);
        expect(mockAccountService.findById("1234")).andReturn(account);
        expect(mockAccountService.getUsers(account)).andReturn(users);
        mocksControl.replay();
        Response response = accountRestService.addUser("1234", userJson.toString());
        assertEquals(400, response.getStatus());
        mocksControl.verify();
    }
    
    @Test
    public void testUpdate() throws Exception {
        Capture<Account> accountCapture = new Capture<Account>();
        Account account = new Account();
        account.setName("Test Account");
        account.setId("1234");
        Date now = new Date();
        account.setCreationTime(now);
        account.setStatus(Status.pending);
        PrincipalCollection mockPrincipalCollection = mocksControl.createMock(PrincipalCollection.class);
        User user = new User();
        user.setAccount("1234");
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(false);
        expect(mockSubject.hasRole("user-manager:account-owner")).andReturn(true);
        expect(mockSubject.getPrincipals()).andReturn(mockPrincipalCollection);
        expect(mockPrincipalCollection.oneByType(User.class)).andReturn(user);
        expect(mockSubject.hasRole("user-manager:administrator")).andReturn(false);
        expect(mockAccountService.findById("1234")).andReturn(account);
        expect(mockAccountService.update(EasyMock.capture(accountCapture))).andReturn(account);
        mocksControl.replay();
        Response response = accountRestService.update("1234", "{'creationTime': 1418222461653, 'name': 'Test Account 2', 'attributes': {'attr1': 'value1'}}");
        assertEquals(200, response.getStatus());
        Account capturedAccount = accountCapture.getValue();
        assertEquals("Test Account 2", capturedAccount.getName());
        assertEquals(Status.pending, capturedAccount.getStatus());
        assertEquals(now.getTime(), capturedAccount.getCreationTime().getTime());
        assertEquals("value1", capturedAccount.getAttributes().get("attr1"));
        mocksControl.verify();
    }

}
