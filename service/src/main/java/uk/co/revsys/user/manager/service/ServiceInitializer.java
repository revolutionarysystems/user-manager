package uk.co.revsys.user.manager.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;

public class ServiceInitializer implements ServletContextListener{
	
    private final Logger LOGGER = LoggerFactory.getLogger(ServiceInitializer.class);
    
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
			EntityService<Account> accountService = (EntityService<Account>) applicationContext.getBean("accountService");
			UserService userService = (UserService) applicationContext.getBean("userService");
			RoleService roleService = (RoleService) applicationContext.getBean("roleService");
			String masterAccountId = Constants.MASTER_ACCOUNT_ID;
			String masterUserRoleId = Constants.ADMINSTRATOR_ROLE_ID;
			String masterUserId = Constants.MASTER_USER_ID;
			String accountOwnerRoleId = Constants.ACCOUNT_OWNER_ROLE_ID;
			if(accountService.findById(masterAccountId)==null){
				Account account = new Account();
				account.setId(masterAccountId);
				account.setName("Master Account");
				account.setStatus(Status.enabled);
				accountService.create(account);
			}
			if(roleService.findById(masterUserRoleId)==null){
				Role role = new Role();
				role.setId(masterUserRoleId);
				role.setName(masterUserRoleId);
				role.setDescription("User Manager Administrator");
				roleService.create(role);
			}
			if(roleService.findById(accountOwnerRoleId)==null){
				Role role = new Role();
				role.setId(accountOwnerRoleId);
				role.setName(accountOwnerRoleId);
				role.setDescription("User Manager Account Owner");
				roleService.create(role);
			}
			if(userService.findById(masterUserId) == null){
				User user = new User();
                user.setId(masterUserId);
				user.setAccount(masterAccountId);
				user.setName("Master User");
				user.setUsername(masterUserId);
				user.setPassword("changeme123");
				user.setStatus(Status.enabled);
				List<String> roles = new ArrayList<String>();
				roles.add(masterUserRoleId);
                roles.add(accountOwnerRoleId);
				user.setRoles(roles);
				userService.create(user);
			}
		} catch (DAOException ex) {
			LOGGER.error("Unable to setup default data", ex);
		} catch(DuplicateKeyException ex){
			LOGGER.error("Unable to setup default data", ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
