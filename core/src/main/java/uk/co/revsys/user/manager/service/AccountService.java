package uk.co.revsys.user.manager.service;

import java.util.Date;
import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Status;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.service.exception.ServiceException;

public class AccountService extends EntityServiceImpl<Account> {

    private final UserService userService;

    public AccountService(Validator validator, EntityDao dao, UserService userService) {
        super(validator, dao);
        this.userService = userService;
    }

    @Override
    public Account create(Account account) throws ServiceException, DAOException, DuplicateKeyException, ConstraintViolationException {
        if (account.getStatus().equals(Status.enabled)) {
            account.setActivationTime(new Date());
        }
        return super.create(account);
    }

    public List<User> getUsers(Account account) throws DAOException {
        List<User> users = userService.find("account", account.getId());
        return users;
    }
    
    public void addRole(Account account, String role) throws DAOException, ServiceException{
        for(User user: getUsers(account)){
            userService.addRole(user, role);
        }
    }
    
    public void removeRole(Account account, String role) throws DAOException, ServiceException{
        for(User user: getUsers(account)){
            userService.removeRole(user, role);
        }
    }

    public void activate(Account account) throws DAOException, ServiceException {
        if (account.getStatus().equals(Status.pending)) {
            account.setStatus(Status.enabled);
            account.setActivationTime(new Date());
            account = update(account);
            List<User> users = getUsers(account);
            for (User user : users) {
                user.setStatus(Status.enabled);
                userService.update(user);
            }
        }
    }

    public void disable(Account account) throws DAOException, ServiceException {
        if (!account.getStatus().equals(Status.disabled)) {
            account.setStatus(Status.disabled);
            account.setDisabledTime(new Date());
            account = update(account);
            List<User> users = getUsers(account);
            for (User user : users) {
                user.setStatus(Status.disabled);
                userService.update(user);
            }
        }
    }

    public void enable(Account account) throws DAOException, ServiceException {
        if (account.getStatus().equals(Status.disabled)) {
            account.setStatus(Status.enabled);
            account.setDisabledTime(new Date());
            account = update(account);
            List<User> users = getUsers(account);
            for (User user : users) {
                if (user.getStatus().equals(Status.disabled)) {
                    user.setStatus(Status.enabled);
                    userService.update(user);
                }
            }
        }
    }

}
