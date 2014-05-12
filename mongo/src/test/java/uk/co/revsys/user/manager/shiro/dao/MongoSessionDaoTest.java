
package uk.co.revsys.user.manager.shiro.dao;

import com.github.fakemongo.Fongo;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.Assert.*;
import uk.co.revsys.user.manager.shiro.session.SerialisedSession;

public class MongoSessionDaoTest {

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

	@Test
	public void test() throws UnknownHostException {
		MongoClient mongoClient = new Fongo("Test Server 1").getMongo();
		mongoClient.dropDatabase("sessions");
		MongoOperations mongoOps = new MongoTemplate(mongoClient, "sessions");
		MongoSessionDao sessionDao = new MongoSessionDao(mongoOps);
		SimpleSession session = new SimpleSession();
		session.setHost("test-host");
		Date now = new Date();
		session.setStartTimestamp(now);
		session.setLastAccessTime(now);
		session.setAttribute("attr1", "value1");
		Serializable sessionId = sessionDao.create(session);
		System.out.println("sessionId = " + sessionId);
		assertNotNull(sessionId);
		DBCollection dbCollection = mongoClient.getDB("sessions").getCollection("serialisedSession");
		DBObject dbResult = dbCollection.findOne();
		assertEquals(sessionId, dbResult.get("_id"));
		assertEquals("test-host", dbResult.get("host"));
		assertEquals(now, dbResult.get("creationTime"));
		assertEquals(now, dbResult.get("lastAccessTime"));
		assertNotNull(dbResult);
		Session result = sessionDao.readSession(sessionId);
		System.out.println("result = " + result);
		assertNotNull(result);
		assertEquals("test-host", result.getHost());
		assertEquals(now, result.getStartTimestamp());
		assertEquals(now, result.getLastAccessTime());
		assertEquals("value1", result.getAttribute("attr1"));
		assertNull(result.getAttribute("attr2"));
		session.setAttribute("attr2", "value2");
		sessionDao.update(session);
		result = sessionDao.readSession(sessionId);
		assertEquals("value1", result.getAttribute("attr1"));
		assertEquals("value2", result.getAttribute("attr2"));
		Collection<Session> activeSessions = sessionDao.getActiveSessions();
		assertEquals(1, activeSessions.size());
		assertEquals(sessionId, activeSessions.iterator().next().getId());
		SimpleSession session2 = new SimpleSession();
		sessionDao.create(session2);
		activeSessions = sessionDao.getActiveSessions();
		assertEquals(2, activeSessions.size());
		sessionDao.delete(session);
		sessionDao.delete(session2);
		activeSessions = sessionDao.getActiveSessions();
		assertEquals(0, activeSessions.size());
	}

}