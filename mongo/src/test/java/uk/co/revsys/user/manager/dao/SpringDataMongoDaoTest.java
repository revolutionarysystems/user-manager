package uk.co.revsys.user.manager.dao;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.co.revsys.user.manager.model.Account;

public class SpringDataMongoDaoTest extends AbstractDaoTest{

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

    @Override
    public EntityDao<Account> getAccountDao() {
		MongoClient mongoClient = new Fongo("Test Mongo Server 1").getMongo();
		mongoClient.dropDatabase("user-manager");
		SpringDataMongoDao<Account> mongoDao = new SpringDataMongoDao<Account>(new MongoTemplate(mongoClient, "user-manager"), Account.class);
        return mongoDao;
    }

}
