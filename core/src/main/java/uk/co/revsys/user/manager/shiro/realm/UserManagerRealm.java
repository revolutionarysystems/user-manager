package uk.co.revsys.user.manager.shiro.realm;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.json.JSONObject;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.shiro.realm.exception.RealmException;
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpRequest;
import uk.co.revsys.utils.http.HttpResponse;

public class UserManagerRealm extends AbstractAuthorizingRealm {

    private final HttpClient httpClient;
    private final String url;
    private final ObjectMapper objectMapper;

    public UserManagerRealm(HttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected AuthenticationDetails getAuthenticationDetails(UsernamePasswordToken token) throws RealmException {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails();
        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("username", token.getUsername());
            parameters.put("password", new String(token.getPassword()));
            HttpRequest request = HttpRequest.POST(url + "/login", parameters);
            HttpResponse response = httpClient.invoke(request);
            if (response.getStatusCode() == 200) {
                JSONObject json = new JSONObject(IOUtils.toString(response.getInputStream()));
                JSONObject accountJson = json.getJSONObject("account");
                Account account = objectMapper.readValue(accountJson.toString(), Account.class);
                json.put("account", account.getId());
                User user = objectMapper.readValue(json.toString(), User.class);
                authenticationDetails.setUser(user);
                authenticationDetails.setAccount(account);
            } else if (response.getStatusCode() == 401) {
                authenticationDetails.setUser(null);
            } else {
                throw new RealmException("Server returned response code: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            throw new RealmException(ex);
        }
        return authenticationDetails;
    }

    @Override
    protected User getUser(String userId) throws RealmException {
        return getObjectFromUrl(url + "/users/" + userId, User.class);
    }

    @Override
    protected List<Role> getRoles(User user) throws RealmException {
        return getObjectFromUrl(url + "/users/" + user.getId() + "/roles", List.class);
    }

    @Override
    protected List<Permission> getPermissions(User user) throws RealmException {
        return getObjectFromUrl(url + "/users/" + user.getId() + "/permissions", List.class);
    }

    private <T extends Object> T getObjectFromUrl(String url, Class<T> type) throws RealmException {
        return getObjectFromRequest(new HttpRequest(url), type);
    }

    private <T extends Object> T getObjectFromRequest(HttpRequest request, Class<T> type) throws RealmException {
        try {
            HttpResponse response = httpClient.invoke(request);
            if (response.getStatusCode() == 200) {
                return objectMapper.readValue(response.getInputStream(), type);
            } else {
                throw new RealmException("Server returned response code: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            throw new RealmException(ex);
        }
    }

}
