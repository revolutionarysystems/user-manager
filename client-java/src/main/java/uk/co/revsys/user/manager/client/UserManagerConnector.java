package uk.co.revsys.user.manager.client;

import java.io.IOException;

public interface UserManagerConnector {

    public void setAuthenticationToken(String token);

    public void setCredentials(String username, String password);

    public String get(String path) throws IOException;

    public String post(String path, String contentType, String body) throws IOException;

    public String delete(String path) throws IOException;

}
