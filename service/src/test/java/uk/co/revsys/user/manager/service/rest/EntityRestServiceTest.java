
package uk.co.revsys.user.manager.service.rest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.shiro.subject.Subject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.test.util.AbstractShiroTest;
import uk.co.revsys.user.manager.service.EntityService;

public class EntityRestServiceTest extends AbstractShiroTest{

	private IMocksControl mocksControl;
	private EntityService mockEntityService;
	private EntityRestService entityRestService;
	private Subject mockSubject;
	private Permission permission1;
	
    public EntityRestServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
		mocksControl = EasyMock.createControl();
		mockEntityService = mocksControl.createMock(EntityService.class);
		entityRestService = new EntityRestServiceImpl(mockEntityService);
		mockSubject = mocksControl.createMock(Subject.class);
		setSubject(mockSubject);
		permission1 = new Permission();
		permission1.setId("123");
		permission1.setName("Test Permission");
		permission1.setDescription("This is a test permission");
    }

    @After
    public void tearDown() {
		clearSubject();
    }

	@Test
	public void testFindAll() throws Exception {
		List<Permission> permissions = new LinkedList<Permission>();
		permissions.add(permission1);
		expect(mockEntityService.findAll()).andReturn(permissions);
		mocksControl.replay();
		Response response = entityRestService.findAll();
		assertEquals(200, response.getStatus());
		assertEquals("[" + toJSONString(permission1) + "]", response.getEntity());
		mocksControl.verify();
	}
	
	@Test
	public void testFindAll_ReturnEmptyList() throws Exception {
		expect(mockEntityService.findAll()).andReturn(new ArrayList());
		mocksControl.replay();
		Response response = entityRestService.findAll();
		assertEquals(200, response.getStatus());
		assertEquals("[]", response.getEntity());
		mocksControl.verify();
	}

	@Test
	public void testCreate() throws Exception {
		Capture<Permission> permissionCapture = new Capture<Permission>();
		expect(mockEntityService.create(capture(permissionCapture))).andReturn(permission1);
		mocksControl.replay();
		Response response = entityRestService.create(toJSONString(permission1));
		assertEquals(200, response.getStatus());
		assertEquals(toJSONString(permission1), response.getEntity());
		assertEquals(permission1.getId(), permissionCapture.getValue().getId());
		assertEquals(permission1.getName(), permissionCapture.getValue().getName());
		assertEquals(permission1.getDescription(), permissionCapture.getValue().getDescription());
		mocksControl.verify();
	}

	@Test
	public void testFindById() throws Exception {
		String id = "123";
		expect(mockEntityService.findById(id)).andReturn(permission1);
		mocksControl.replay();
		Response response = entityRestService.findById(id);
		assertEquals(200, response.getStatus());
		assertEquals(toJSONString(permission1), response.getEntity());
		mocksControl.verify();
	}
	
	@Test
	public void testFindById_NotFound() throws Exception {
		String id = "123";
		expect(mockEntityService.findById(id)).andReturn(null);
		mocksControl.replay();
		Response response = entityRestService.findById(id);
		assertEquals(404, response.getStatus());
		mocksControl.verify();
	}

	@Test
	public void testUpdate() throws Exception {
		String id = permission1.getId();
		Capture<Permission> permissionCapture = new Capture<Permission>();
		expect(mockEntityService.findById(id)).andReturn(permission1);
		expect(mockEntityService.update(capture(permissionCapture))).andReturn(permission1);
		mocksControl.replay();
		Response response = entityRestService.update(id, "{'description': 'This is a modified test permission'}");
		assertEquals(200, response.getStatus());
		assertEquals(toJSONString(permission1), response.getEntity());
		assertEquals(permission1.getId(), permissionCapture.getValue().getId());
		assertEquals(permission1.getName(), permissionCapture.getValue().getName());
		assertEquals("This is a modified test permission", permissionCapture.getValue().getDescription());
		mocksControl.verify();
	}
	
	@Test
	public void testUpdate_NotFound() throws Exception {
		String id = permission1.getId();
		Capture<Permission> permissionCapture = new Capture<Permission>();
		expect(mockEntityService.findById(id)).andReturn(null);
		mocksControl.replay();
		Response response = entityRestService.update(id, toJSONString(permission1));
		assertEquals(404, response.getStatus());
		mocksControl.verify();
	}

	@Test
	public void testDelete() throws Exception {
		String id = "123";
		expect(mockEntityService.findById(id)).andReturn(new User());
		mockEntityService.delete(id);
		mocksControl.replay();
		Response response = entityRestService.delete(id);
		assertEquals(204, response.getStatus());
		mocksControl.verify();
	}

	@Test
	public void testToJSONString() throws Exception {
		assertEquals(toJSONString(permission1), entityRestService.toJSONString(permission1));
	}

	public class EntityRestServiceImpl extends EntityRestService<Permission, EntityService<Permission>> {

		public EntityRestServiceImpl(EntityService<Permission> entityService) {
			super(entityService);
		}

		@Override
		public Class<? extends Permission> getEntityType() {
			return Permission.class;
		}
	}
	
	private String toJSONString(Permission permission){
		return "{\"id\":\"" + permission.getId() + "\",\"name\":\"" + permission.getName() + "\",\"description\":\"" + permission.getDescription() + "\"}";
	}

}