package uk.co.revsys.user.manager.client;

import java.io.IOException;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.utils.http.HttpClient;

public class AccountClient extends EntityClientImpl<Account>{

    EntityClient<User> userClient;
    
    public AccountClient(HttpClient httpClient, String baseUrl) {
        super(httpClient, baseUrl, "accounts", Account.class);
        userClient = new EntityClientImpl<User>(httpClient, baseUrl, "users", User.class);
    }
    
    public Account createWithUser(String username, String password, Account account, User user) throws IOException {
        account = create(username, password, account);
        user.setAccount(account.getId());
        user = userClient.create(username, password, user);
        return account;
    }

}
