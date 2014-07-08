package uk.co.revsys.user.manager.service;

import java.util.List;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;

public class AccountService extends EntityServiceImpl<Account>{

    private final UserService userService;
    
    public AccountService(EntityDao dao, UserService userService) {
        super(dao);
        this.userService = userService;
    }
    
    public List<User> getUsers(Account account) throws DAOException{
        List<User> users = userService.find("account", account.getId());
        return users;
    }

}
