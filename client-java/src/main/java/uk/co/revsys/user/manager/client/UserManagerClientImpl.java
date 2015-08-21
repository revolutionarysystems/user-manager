package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.revsys.utils.http.HttpClientImpl;

public class UserManagerClientImpl implements UserManagerClient{
    
    private UserManagerConnector connector;
    private AccountClient accountClient;
    private UserClient userClient;

    public UserManagerClientImpl(String url) {
        connector = new UserManagerConnectorImpl(new HttpClientImpl(), url);
        ObjectMapper objectMapper = new ObjectMapper();
        accountClient = new AccountClient(connector, objectMapper);
        userClient = new UserClient(connector, objectMapper);
    }
    
    @Override
    public void setAuthenticationToken(String token) {
        connector.setAuthenticationToken(token);
    }

    @Override
    public void setCredentials(String username, String password) {
        connector.setCredentials(username, password);
    }

    @Override
    public AccountClient accounts() {
        return accountClient;
    }

    @Override
    public UserClient users() {
        return userClient;
    }

}
