package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.List;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpRequest;

public class AccountClient extends EntityClientImpl<Account>{

    EntityClient<User> userClient;
    
    public AccountClient(HttpClient httpClient, String baseUrl, UserClient userClient) {
        super(httpClient, baseUrl, Account.class);
        this.userClient = userClient;
    }
    
    public Account createWithUser(Account account, User user) throws IOException {
        return createWithUser(UserManager.getUsername(), UserManager.getPassword(), account, user);
    }
    
    public Account createWithUser(String username, String password, Account account, User user) throws IOException {
        account = create(username, password, account);
        user.setAccount(account.getId());
        user = userClient.create(username, password, user);
        return account;
    }
    
    public List<User> getUsers(Account account) throws IOException{
        return getUsers(UserManager.getUsername(), UserManager.getPassword(), account);
    }
    
    public List<User> getUsers(String username, String password, Account account) throws IOException{
        String result = getUsersRaw(username, password, account.getId());
        ObjectMapper objectMapper = getObjectMapper();
        CollectionType returnType = objectMapper.getTypeFactory().constructCollectionType(List.class, User.class);
        return objectMapper.readValue(result, returnType);
    }
    
    public String getUsersRaw(String accountId) throws IOException{
        return getUsersRaw(UserManager.getUsername(), UserManager.getPassword(), accountId);
    }
    
    public String getUsersRaw(String username, String password, String accountId) throws IOException{
        HttpRequest request = HttpRequest.GET(constructUrl(accountId, "users"));
        return sendRequest(username, password, request);
    }

}
