package uk.co.revsys.user.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import javax.validation.ConstraintViolationException;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.revsys.resource.repository.provider.handler.FilteringResourceHandler;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Application;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;

public class ApplicationConfigurationLoader implements FilteringResourceHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfigurationLoader.class);

    private final AccountService accountService;
    private final UserService userService;
    private final EntityService<Application> applicationService;
    private final RoleService roleService;
    private final EntityService<Permission> permissionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApplicationConfigurationLoader(AccountService accountService, UserService userService, EntityService<Application> applicationService, RoleService roleService, EntityService<Permission> permissionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.applicationService = applicationService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public void handle(String path, uk.co.revsys.resource.repository.model.Resource resource, InputStream contents) throws IOException {
        try {
            String jsonString = IOUtils.toString(contents);
            JSONObject json = new JSONObject(jsonString);
            createEntities(permissionService, Permission.class, json.optJSONArray("permissions"));
            createEntities(roleService, Role.class, json.optJSONArray("roles"));
            createEntities(applicationService, Application.class, json.optJSONArray("applications"));
            createEntities(accountService, Account.class, json.optJSONArray("accounts"));
            createEntities(userService, User.class, json.optJSONArray("users"));
        } catch (DAOException ex) {
            LOGGER.error("Unable to update application from configuration: " + resource.getName(), ex);
        } catch (DuplicateKeyException ex) {
            LOGGER.error("Unable to update application from configuration: " + resource.getName(), ex);
        } catch (ConstraintViolationException ex) {
            LOGGER.error("Unable to update application from configuration: " + resource.getName(), ex);
        } catch (IOException ex) {
            LOGGER.error("Unable to update application from configuration: " + resource.getName(), ex);
        }
    }

    @Override
    public boolean accept(uk.co.revsys.resource.repository.model.Resource resource) {
        return resource.getName().endsWith(".json");
    }

    private void createEntities(EntityService service, Class<? extends AbstractEntity> entityType, JSONArray json) throws DAOException, DuplicateKeyException, IOException {
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                JSONObject entityJson = json.getJSONObject(i);
                String entityId = entityJson.optString("id");
                AbstractEntity entity = objectMapper.readValue(entityJson.toString(), entityType);
                if (entityId != null && service.findById(entityId) != null) {
                    service.update(entity);
                } else {
                    service.create(entity);
                }
            }
        }
    }

}
