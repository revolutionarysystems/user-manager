package uk.co.revsys.user.manager.service;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.Hash;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;

public class UserService extends EntityServiceImpl<User>{

	private final RoleService roleService;
	private final DefaultPasswordService passwordService;
	
	public UserService(EntityDao dao, RoleService roleService, DefaultPasswordService passwordService) {
		super(dao);
		this.roleService = roleService;
		this.passwordService = passwordService;
	}

	@Override
	public User create(User entity) throws DAOException, DuplicateKeyException, ConstraintViolationException {
		String password = entity.getPassword();
		if(password == null){
			ConstraintViolation<User> violation = ConstraintViolationImpl.forBeanValidation("{javax.validation.constraints.NotNull.message}", "may not be null", User.class, entity, null, null, PathImpl.createPathFromString("password"), null, ElementType.TYPE);
			Set<ConstraintViolation<User>> violations = new HashSet<ConstraintViolation<User>>();
			violations.add(violation);
			throw new ConstraintViolationException(violations);
		}
		Hash hash = passwordService.hashPassword(password);
		String hashedPassword = hash.toBase64();
		entity.setPassword(hashedPassword);
		String salt = hash.getSalt().toBase64();
		entity.setPasswordSalt(salt);
		return super.create(entity);
	}

	public List<Role> getRoles(User user) throws DAOException{
		List<Role> roles = new ArrayList<Role>();
		for(String roleId: user.getRoles()){
			Role role = roleService.findById(roleId);
			roles.add(role);
		}
		return roles;
	}
	
	public List<Permission> getPermissions(User user) throws DAOException{
		List<Permission> permissions = new ArrayList<Permission>();
		for(Role role: getRoles(user)){
			permissions.addAll(roleService.getPermissions(role));
		}
		return permissions;
	}
	
}
