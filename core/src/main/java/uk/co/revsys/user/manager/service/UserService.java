package uk.co.revsys.user.manager.service;

import java.util.ArrayList;
import java.util.List;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;

public class UserService extends EntityServiceImpl<User>{

	private final RoleService roleService;
	
	public UserService(EntityDao dao, RoleService roleService) {
		super(dao);
		this.roleService = roleService;
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
