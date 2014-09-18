package uk.co.revsys.user.manager.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.Hash;
import uk.co.revsys.resource.repository.ResourceRepository;
import uk.co.revsys.resource.repository.model.Resource;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;

public class UserService extends EntityServiceImpl<User> {

    private final RoleService roleService;
    private final DefaultPasswordService passwordService;
    private final ResourceRepository resourceRepository;

    public UserService(EntityDao dao, RoleService roleService, DefaultPasswordService passwordService, ResourceRepository resourceRepository) {
        super(dao);
        this.roleService = roleService;
        this.passwordService = passwordService;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public User create(User user) throws DAOException, DuplicateKeyException {
        User existingUser = findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new DuplicateKeyException("A user with username " + user.getUsername() + " already exists");
        }
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            password = UUID.randomUUID().toString();
        }
        Hash hash = passwordService.hashPassword(password);
        String hashedPassword = hash.toBase64();
        user.setPassword(hashedPassword);
        String salt = hash.getSalt().toBase64();
        user.setPasswordSalt(salt);
        return super.create(user);
    }

    @Override
    public User update(User user) throws DAOException, DuplicateKeyException, ConstraintViolationException {
        Map nameFilter = new HashMap();
        nameFilter.put("username", user.getUsername());
        User existingUser = findOne(nameFilter);
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new DuplicateKeyException("A user with username " + user.getUsername() + " already exists");
        }
        return super.update(user);
    }
    
    public User findByUsername(String username) throws DAOException{
        Map nameFilter = new HashMap();
        nameFilter.put("username", username);
        return findOne(nameFilter);
    }

    public User changePassword(User user, String password) throws DAOException, DuplicateKeyException {
        Hash hash = passwordService.hashPassword(password);
        String hashedPassword = hash.toBase64();
        user.setPassword(hashedPassword);
        String salt = hash.getSalt().toBase64();
        user.setPasswordSalt(salt);
        return update(user);
    }

    public List<Role> getRoles(User user) throws DAOException {
        List<Role> roles = new ArrayList<Role>();
        for (String roleId : user.getRoles()) {
            Role role = roleService.findById(roleId);
            roles.add(role);
        }
        return roles;
    }

    public List<Permission> getPermissions(User user) throws DAOException {
        List<Permission> permissions = new ArrayList<Permission>();
        for (Role role : getRoles(user)) {
            permissions.addAll(roleService.getPermissions(role));
        }
        return permissions;
    }

    public void setProfilePicture(User user, InputStream profilePicture) throws IOException {
        Resource resource = new Resource(user.getId(), "profilePicture");
        resourceRepository.write(resource, profilePicture);
    }

    public InputStream getProfilePicture(User user) throws IOException {
        Resource resource = new Resource(user.getId(), "profilePicture");
        return resourceRepository.read(resource);
    }

}
