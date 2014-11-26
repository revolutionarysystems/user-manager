package uk.co.revsys.user.manager.service;

import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.service.exception.ServiceException;

public class ServiceInitializer implements ServletContextListener{
	
    private final Logger LOGGER = LoggerFactory.getLogger(ServiceInitializer.class);
    
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
			RoleService roleService = (RoleService) applicationContext.getBean("roleService");
            if(roleService.findById(Constants.ADMINISTRATOR_ROLE)==null){
				Role role = new Role();
				role.setId(Constants.ADMINISTRATOR_ROLE);
				role.setName(Constants.ADMINISTRATOR_ROLE);
				role.setDescription("User Manager Administrator");
				roleService.create(role);
			}
			if(roleService.findById(Constants.ACCOUNT_OWNER_ROLE)==null){
				Role role = new Role();
				role.setId(Constants.ACCOUNT_OWNER_ROLE);
				role.setName(Constants.ACCOUNT_OWNER_ROLE);
				role.setDescription("User Manager Account Owner");
				roleService.create(role);
			}
		} catch (DAOException ex) {
			LOGGER.error("Unable to setup default data", ex);
		} catch(DuplicateKeyException ex){
			LOGGER.error("Unable to setup default data", ex);
		} catch (ServiceException ex) {
            LOGGER.error("Unable to setup default data", ex);
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Unable to setup default data", ex);
        }
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
