
package uk.co.revsys.user.manager.service.dao.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import uk.co.revsys.user.manager.service.model.Account;
import uk.co.revsys.user.manager.service.model.Status;

public class MongoDaoTest {
	
    public MongoDaoTest() {
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

	@org.junit.Test
	public void test() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		MongoClient mongoClient = new Fongo("Test Mongo Server 1").getMongo();
		mongoClient.dropDatabase("security");
		MongoDao<Account> mongoDao = new MongoDao<Account>(validator, mongoClient, "security", "accounts", Account.class);
		Account account1 = new Account();
		account1.setName("Test Account");
		try{
			mongoDao.create(account1);
			fail();
		}catch(ConstraintViolationException ex){
			// Pass
		}
		account1.setStatus(Status.enabled);
		Account result = mongoDao.create(account1);
		String account1Id = result.getId();
		assertNotNull(account1Id);
		assertEquals(36, account1Id.length());
		result = mongoDao.findById(account1Id);
		assertEquals(account1Id, result.getId());
		assertEquals("Test Account", result.getName());
		List<Account> results = mongoDao.findAll();
		assertEquals(1, results.size());
		assertEquals(account1Id, results.get(0).getId());
		account1.setName("Renamed Account");
		mongoDao.update(account1);
		result = mongoDao.findById(account1Id);
		assertEquals("Renamed Account", result.getName());
		Account account2 = new Account();
		account2.setName("Test Account 2");
		account2.setStatus(Status.enabled);
		mongoDao.create(account2);
		results = mongoDao.findAll();
		assertEquals(2, results.size());
		Map filters = new HashMap();
		results = mongoDao.find(filters);
		assertEquals(2, results.size());
		filters.put("name", "Renamed Account");
		results = mongoDao.find(filters);
		assertEquals(1, results.size());
		assertEquals(account1Id, results.get(0).getId());
		result = mongoDao.findOne(filters);
		assertEquals(account1Id, result.getId());
		mongoDao.delete(account1Id);
		result = mongoDao.findById(account1Id);
		assertNull(result);
		results = mongoDao.findAll();
		assertEquals(1, results.size());
	}

}