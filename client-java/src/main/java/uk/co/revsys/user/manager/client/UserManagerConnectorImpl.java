package uk.co.revsys.user.manager.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import uk.co.revsys.utils.http.BasicAuthCredentials;
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpMethod;
import uk.co.revsys.utils.http.HttpRequest;
import uk.co.revsys.utils.http.HttpResponse;

public class UserManagerConnectorImpl implements UserManagerConnector {

    private HttpClient httpClient;
    private String baseUrl;
    private ThreadLocal<String> authenticationToken;
    private ThreadLocal<String> username;
    private ThreadLocal<String> password;

    public UserManagerConnectorImpl(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        if(!baseUrl.endsWith("/")){
            baseUrl = baseUrl + "/";
        }
        this.baseUrl = baseUrl;
    }

    @Override
    public void setAuthenticationToken(String token) {
        this.authenticationToken = new ThreadLocal<String>();
        this.authenticationToken.set(token);
    }

    @Override
    public void setCredentials(String username, String password) {
        this.username = new ThreadLocal<String>();
        this.username.set(username);
        this.password = new ThreadLocal<String>();
        this.password.set(password);
    }

    @Override
    public String get(String path) throws IOException {
        return send(HttpMethod.GET, path, null, null);
    }

    @Override
    public String post(String path, String contentType, String body) throws IOException {
        return send(HttpMethod.POST, path, contentType, body);
    }

    @Override
    public String delete(String path) throws IOException {
        return send(HttpMethod.DELETE, path, null, null);
    }

    protected String send(HttpMethod method, String path, String contentType, String body) throws IOException {
        HttpRequest request = new HttpRequest(baseUrl + path);
        if (body != null) {
            request.setBody(new ByteArrayInputStream(body.getBytes()));
            request.setHeader("Content-Type", contentType);
        }
        if (authenticationToken != null) {
            request.setHeader("Cookie", "JSESSIONID=" + authenticationToken.get());
        } else if (username != null) {
            request.setCredentials(new BasicAuthCredentials(username.get(), password.get()));
        }
        HttpResponse response = httpClient.invoke(request);
        if (response.getStatusCode() != 200) {
            InputStream errorStream = response.getInputStream();
            if (errorStream != null) {
                String errorMessage = IOUtils.toString(errorStream);
                throw new IOException("Server returned status " + response.getStatusCode() + " - " + errorMessage);
            }
            throw new IOException("Server returned status " + response.getStatusCode());
        }
        return IOUtils.toString(response.getInputStream());
    }

}
