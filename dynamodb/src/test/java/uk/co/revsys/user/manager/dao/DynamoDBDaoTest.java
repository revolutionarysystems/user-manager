package uk.co.revsys.user.manager.dao;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import uk.co.revsys.user.manager.model.Account;

public class DynamoDBDaoTest extends AbstractDaoTest{

	public DynamoDBDaoTest() {
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

    @Override
    public EntityDao<Account> getAccountDao() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		return new DynamoDBDao<Account>(validator);
    }

}
