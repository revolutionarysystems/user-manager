package uk.co.revsys.user.manager.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;

public abstract class AbstractDaoTest {

	public AbstractDaoTest() {
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

	@Test
	public void test() throws Exception {
		EntityDao<Account> dao = getAccountDao();
		Account account1 = new Account();
		account1.setName("Test Account");
		account1.setStatus(Status.pending);
		Account result = dao.create(account1);
		String account1Id = result.getId();
		System.out.println("account1Id = " + account1Id);
		assertNotNull(account1Id);
		assertEquals(36, account1Id.length());
        assertEquals(Status.pending, account1.getStatus());
		try {
			dao.create(account1);
			fail();
		} catch (DuplicateKeyException ex) {
			// Pass
		}
		result = dao.findById(account1Id);
		assertEquals(account1Id, result.getId());
		assertEquals("Test Account", result.getName());
        assertEquals(Status.pending, result.getStatus());
		List<Account> results = dao.findAll();
		assertEquals(1, results.size());
		assertEquals(account1Id, results.get(0).getId());
		account1.setName("Renamed Account");
		dao.update(account1);
		result = dao.findById(account1Id);
		assertEquals("Renamed Account", result.getName());
		Account account2 = new Account();
		account2.setName("Test Account 2");
		account2.setStatus(Status.enabled);
		dao.create(account2);
		results = dao.findAll();
		assertEquals(2, results.size());
		Map filters = new HashMap();
		results = dao.find(filters);
		assertEquals(2, results.size());
		filters.put("name", "Renamed Account");
		results = dao.find(filters);
		assertEquals(1, results.size());
		assertEquals(account1Id, results.get(0).getId());
		result = dao.findOne(filters);
		assertEquals(account1Id, result.getId());
		dao.delete(account1Id);
		result = dao.findById(account1Id);
		assertNull(result);
		results = dao.findAll();
		assertEquals(1, results.size());
	}
    
    public abstract EntityDao<Account> getAccountDao();

}
