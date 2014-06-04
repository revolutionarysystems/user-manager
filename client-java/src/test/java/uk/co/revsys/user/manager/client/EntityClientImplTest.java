
package uk.co.revsys.user.manager.client;

import java.io.ByteArrayInputStream;
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
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpRequest;
import uk.co.revsys.utils.http.HttpResponse;

public class EntityClientImplTest {

    public EntityClientImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class EntityClientImpl.
     */
    @Test
    public void testCreate() throws Exception {
        IMocksControl mocksControl = EasyMock.createControl();
        //HttpClient httpClient = new HttpClientImpl();
        HttpClient httpClient = mocksControl.createMock(HttpClient.class);
        EntityClient<Account> accountClient = new EntityClientImpl(httpClient, "http://localhost:8080/user-manager-service", "accounts", Account.class);
        String username = "master-user";
        String password = "changeme123";
        Account account = new Account();
        account.setName("Test Account");
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setInputStream(new ByteArrayInputStream("{\"id\": \"1234\", \"name\": \"Test Account\"}".getBytes()));
        expect(httpClient.invoke(isA(HttpRequest.class))).andReturn(response);
        mocksControl.replay();
        Account result = accountClient.create(username, password, account);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Account", result.getName());
        mocksControl.verify();
    }

}