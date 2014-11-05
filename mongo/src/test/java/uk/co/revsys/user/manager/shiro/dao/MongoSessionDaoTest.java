
package uk.co.revsys.user.manager.shiro.dao;

import com.github.fakemongo.Fongo;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.Assert.*;
import uk.co.revsys.user.manager.test.AbstractSessionDaoTest;

public class MongoSessionDaoTest extends AbstractSessionDaoTest{

    public MongoSessionDaoTest() {
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
    public SessionDAO getSessionDao() {
        MongoClient mongoClient = new Fongo("Test Server 1").getMongo();
		mongoClient.dropDatabase("sessions");
		MongoOperations mongoOps = new MongoTemplate(mongoClient, "sessions");
		MongoSessionDao sessionDao = new MongoSessionDao(mongoOps);
        return sessionDao;
    }

}