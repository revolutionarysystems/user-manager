package uk.co.revsys.user.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.revsys.resource.repository.provider.handler.FilteringResourceHandler;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Application;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;

public class ApplicationConfigurationLoader implements FilteringResourceHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfigurationLoader.class);
    
    private final EntityService<Application> applicationService;
    private final RoleService roleService;
    private final EntityService<Permission> permissionService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApplicationConfigurationLoader(EntityService<Application> applicationService, RoleService roleService, EntityService<Permission> permissionService) {
        this.applicationService = applicationService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public void handle(String path, uk.co.revsys.resource.repository.model.Resource resource, InputStream contents) throws IOException {
        try {
            String jsonString = IOUtils.toString(contents);
            JSONObject json = new JSONObject(jsonString);
            createPermissions(json.getJSONArray("permissions"));
            createRoles(json.getJSONArray("roles"));
            createApplication(json);
        } catch (DAOException ex) {
            LOGGER.error("Unable to update application from configuration: " + resource.getName(), ex);
        }
    }

    @Override
    public boolean accept(uk.co.revsys.resource.repository.model.Resource resource) {
        return resource.getName().endsWith(".json");
    }

    private void createApplication(JSONObject applicationJson) throws DAOException, IOException {
        String id = applicationJson.getString("id");
        Application application = new Application();
        application.setId(id);
        application.setName(applicationJson.getString("name"));
        application.setDescription(applicationJson.getString("description"));
        List<String> permissions = new ArrayList<String>();
        JSONArray permissionsJson = applicationJson.getJSONArray("permissions");
        for (int i = 0; i < permissionsJson.length(); i++) {
            permissions.add(permissionsJson.getJSONObject(i).getString("id"));
        }
        application.setPermissions(permissions);
        List<String> roles = new ArrayList<String>();
        JSONArray rolesJson = applicationJson.getJSONArray("roles");
        for (int i = 0; i < rolesJson.length(); i++) {
            roles.add(rolesJson.getJSONObject(i).getString("id"));
        }
        application.setRoles(roles);
        JSONObject attributesJson = applicationJson.getJSONObject("attributes");
        Map attributes = objectMapper.readValue(attributesJson.toString(), Map.class);
        application.setAttributes(attributes);
        if (applicationService.findById(id) == null) {
            try {
                applicationService.create(application);
            } catch (DuplicateKeyException ex) {
                // Ignore as impossible
            }
        } else {
            applicationService.update(application);
        }
    }

    private void createRoles(JSONArray roles) throws DAOException {
        for (int i = 0; i < roles.length(); i++) {
            JSONObject roleJson = roles.getJSONObject(i);
            String id = roleJson.getString("id");
            Role role = new Role();
            role.setId(id);
            role.setName(roleJson.getString("name"));
            role.setDescription(roleJson.getString("description"));
            List<String> permissions = new ArrayList<String>();
            JSONArray permissionsJson = roleJson.getJSONArray("permissions");
            for (int j = 0; j < permissionsJson.length(); j++) {
                permissions.add(permissionsJson.getString(j));
            }
            role.setPermissions(permissions);
            if (roleService.findById(id) == null) {
                try {
                    roleService.create(role);
                } catch (DuplicateKeyException ex) {
                    // Ignore as impossible
                }
            } else {
                roleService.update(role);
            }
        }
    }

    private void createPermissions(JSONArray permissions) throws DAOException {
        for (int i = 0; i < permissions.length(); i++) {
            JSONObject permissionJson = permissions.getJSONObject(i);
            String id = permissionJson.getString("id");
            Permission permission = new Permission();
            permission.setId(id);
            permission.setName(permissionJson.getString("name"));
            permission.setDescription(permissionJson.getString("description"));
            if (permissionService.findById(id) == null) {
                try {
                    permissionService.create(permission);
                } catch (DuplicateKeyException ex) {
                    // Ignore as impossible
                }
            } else {
                permissionService.update(permission);
            }
        }
    }

}
