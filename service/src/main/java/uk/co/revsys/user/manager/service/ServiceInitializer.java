package uk.co.revsys.user.manager.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;

public class ServiceInitializer implements ServletContextListener{
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
			EntityDao<Account> accountDao = (EntityDao) applicationContext.getBean("accountDao");
			EntityDao<User> userDao = (EntityDao) applicationContext.getBean("userDao");
			EntityDao<Role> roleDao = (EntityDao) applicationContext.getBean("roleDao");
			String masterAccountId = Constants.MASTER_ACCOUNT_ID;
			String masterUserRoleId = Constants.ADMINSTRATOR_ROLE_ID;
			String masterUserId = Constants.MASTER_USER_ID;
			String accountOwnerRoleId = Constants.ACCOUNT_OWNER_ROLE_ID;
			if(accountDao.findById(masterAccountId)==null){
				Account account = new Account();
				account.setId(masterAccountId);
				account.setName("Master Account");
				account.setStatus(Status.enabled);
				accountDao.create(account);
			}
			if(roleDao.findById(masterUserRoleId)==null){
				Role role = new Role();
				role.setId(masterUserRoleId);
				role.setName(masterUserRoleId);
				role.setDescription("User Manager Administrator");
				roleDao.create(role);
			}
			if(roleDao.findById(accountOwnerRoleId)==null){
				Role role = new Role();
				role.setId(accountOwnerRoleId);
				role.setName(accountOwnerRoleId);
				role.setDescription("User Manager Account Owner");
				roleDao.create(role);
			}
			if(userDao.findById(masterUserId) == null){
				User user = new User();
				user.setId(masterUserId);
				user.setAccount(masterAccountId);
				user.setName("Master User");
				user.setUsername(masterUserId);
				user.setPassword("changeme123");
				user.setStatus(Status.enabled);
				List<String> roles = new ArrayList<String>();
				roles.add(masterUserRoleId);
				user.setRoles(roles);
				userDao.create(user);
			}
		} catch (DAOException ex) {
			throw new RuntimeException(ex);
		} catch(DuplicateKeyException ex){
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
