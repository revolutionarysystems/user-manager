package uk.co.revsys.user.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import static org.junit.Assert.fail;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;

public class EntityServiceImplTest {

    private IMocksControl mocksControl;
    private EntityDao<Account> mockEntityDao;
    private EntityServiceImpl entityService;

    public EntityServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        mocksControl = EasyMock.createControl();
        mockEntityDao = mocksControl.createMock(EntityDao.class);
        entityService = new EntityServiceImpl(validator, mockEntityDao);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class EntityServiceImpl.
     */
    @Test
    public void testCreate() throws Exception {
        Account account = new Account();
        account.setName("Test Account");
        account.setVerificationCode("abc123");
        account.setStatus(null);
        try {
            entityService.create(account);
            fail("Expected ConstraintViolationException to be thrown");
        } catch (ConstraintViolationException ex) {
            // pass
        }
        account.setStatus(Status.enabled);
        expect(mockEntityDao.create(account)).andReturn(account);
        mocksControl.replay();
        entityService.create(account);
        mocksControl.verify();
    }

    /**
     * Test of findAll method, of class EntityServiceImpl.
     */
    @Test
    public void testFindAll() throws Exception {
        expect(mockEntityDao.findAll()).andReturn(new ArrayList<Account>());
        mocksControl.replay();
        entityService.findAll();
        mocksControl.verify();
    }

    /**
     * Test of findById method, of class EntityServiceImpl.
     */
    @Test
    public void testFindById() throws Exception {
        String id = "123";
        Account account = new Account();
        expect(mockEntityDao.findById(id)).andReturn(account);
        mocksControl.replay();
        entityService.findById(id);
        mocksControl.verify();
    }

    /**
     * Test of update method, of class EntityServiceImpl.
     */
    @Test
    public void testUpdate() throws Exception {
        Account account = new Account();
        account.setName("Test Account");
        account.setVerificationCode("abc123");
        account.setStatus(null);
        try {
            entityService.create(account);
            fail("Expected ConstraintViolationException to be thrown");
        } catch (ConstraintViolationException ex) {
            // pass
        }
        account.setStatus(Status.enabled);
        expect(mockEntityDao.update(account)).andReturn(account);
        mocksControl.replay();
        entityService.update(account);
        mocksControl.verify();
    }

    /**
     * Test of delete method, of class EntityServiceImpl.
     */
    @Test
    public void testDelete() throws Exception {
        String id = "123";
        mockEntityDao.delete(id);
        mocksControl.replay();
        entityService.delete(id);
        mocksControl.verify();
    }

    /**
     * Test of find method, of class EntityServiceImpl.
     */
    @Test
    public void testFind() throws Exception {
        Map filters = new HashMap();
        expect(mockEntityDao.find(filters)).andReturn(new ArrayList());
        mocksControl.replay();
        entityService.find(filters);
        mocksControl.verify();
    }

    /**
     * Test of findOne method, of class EntityServiceImpl.
     */
    @Test
    public void testFindOne() throws Exception {
        Map filters = new HashMap();
        expect(mockEntityDao.findOne(filters)).andReturn(new Account());
        mocksControl.replay();
        entityService.findOne(filters);
        mocksControl.verify();
    }

}
