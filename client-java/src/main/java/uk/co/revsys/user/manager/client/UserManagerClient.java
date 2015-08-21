package uk.co.revsys.user.manager.client;

public interface UserManagerClient {

    public void setAuthenticationToken(String token);
    
    public void setCredentials(String username, String password);
    
    public AccountClient accounts();
    
    public UserClient users();
    
}
