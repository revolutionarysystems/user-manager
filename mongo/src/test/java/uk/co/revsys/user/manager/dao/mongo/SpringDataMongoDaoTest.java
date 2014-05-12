package uk.co.revsys.user.manager.dao.mongo;

import uk.co.revsys.user.manager.dao.SpringDataMongoDao;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;

public class SpringDataMongoDaoTest {

	public SpringDataMongoDaoTest() {
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
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		MongoClient mongoClient = new Fongo("Test Mongo Server 1").getMongo();
		mongoClient.dropDatabase("user-manager");
		SpringDataMongoDao<Account> mongoDao = new SpringDataMongoDao<Account>(validator, new MongoTemplate(mongoClient, "user-manager"), Account.class);
		Account account1 = new Account();
		account1.setName("Test Account");
		try {
			mongoDao.create(account1);
			fail();
		} catch (ConstraintViolationException ex) {
			System.out.println(ex.getConstraintViolations().iterator().next());
			System.out.println(ex.getConstraintViolations().iterator().next().getMessageTemplate());
			System.out.println(ex.getConstraintViolations().iterator().next().getPropertyPath().getClass());
			// Pass
		}
		account1.setStatus(Status.enabled);
		Account result = mongoDao.create(account1);
		String account1Id = result.getId();
		System.out.println("account1Id = " + account1Id);
		assertNotNull(account1Id);
		assertEquals(24, account1Id.length());
		try {
			mongoDao.create(account1);
			fail();
		} catch (DuplicateKeyException ex) {
			// Pass
		}
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
