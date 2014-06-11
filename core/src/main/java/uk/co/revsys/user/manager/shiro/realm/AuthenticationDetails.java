package uk.co.revsys.user.manager.shiro.realm;

import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;

public class AuthenticationDetails {

    private User user;
    private Account account;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    
}
