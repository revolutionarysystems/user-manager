package uk.co.revsys.user.manager.service;

import java.util.ArrayList;
import java.util.List;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;

public class RoleService extends EntityServiceImpl<Role>{

	private final EntityService<Permission> permissionService;
	
	public RoleService(EntityDao dao, EntityService<Permission> permissionService) {
		super(dao);
		this.permissionService = permissionService;
	}
	
	public List<Permission> getPermissions(Role role) throws DAOException{
		List<Permission> permissions = new ArrayList<Permission>();
		for(String permissionId: role.getPermissions()){
			permissions.add(permissionService.findById(permissionId));
		}
		return permissions;
	}

}
