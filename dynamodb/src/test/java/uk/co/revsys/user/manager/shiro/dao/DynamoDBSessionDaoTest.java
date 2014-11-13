
package uk.co.revsys.user.manager.shiro.dao;

import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import uk.co.revsys.user.manager.test.AbstractSessionDaoTest;

public class DynamoDBSessionDaoTest extends AbstractSessionDaoTest{

    public DynamoDBSessionDaoTest() {
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
        return new DynamoDBSessionDao();
    }

}