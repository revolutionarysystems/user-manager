package uk.co.revsys.user.manager.shiro.realm;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.UsernamePasswordToken;
import uk.co.revsys.user.manager.model.Account;
import uk.co.revsys.user.manager.model.Permission;
import uk.co.revsys.user.manager.model.Role;
import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.user.manager.shiro.realm.exception.RealmException;
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpRequest;
import uk.co.revsys.utils.http.HttpResponse;

public class UserManagerRealm extends AbstractAuthorizingRealm{

	private final HttpClient httpClient;
	private final String url;
	private final String accountId;
	private final ObjectMapper objectMapper;

	public UserManagerRealm(HttpClient httpClient, String url, String accountId) {
		this.httpClient = httpClient;
		this.url = url;
		this.accountId = accountId;
		this.objectMapper = new ObjectMapper();
	}
	
	@Override
	protected Account getAccount(String accountId) throws RealmException {
		return getObjectFromUrl(url + "/accounts/" + accountId, Account.class);
	}

	@Override
	protected User getUser(UsernamePasswordToken token) throws RealmException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("username", token.getUsername());
		parameters.put("password", new String(token.getPassword()));
		return getObjectFromRequest(HttpRequest.POST(url + "/login", parameters), User.class);
	}

	@Override
	protected User getUser(String userId) throws RealmException {
		return getObjectFromUrl(url + "/users/" + userId, User.class);
	}

	@Override
	protected List<Role> getRoles(User user) throws RealmException {
		return getObjectFromUrl(url + "/users/" + user.getId() + "/roles" , List.class);
	}

	@Override
	protected List<Permission> getPermissions(User user) throws RealmException {
		return getObjectFromUrl(url + "/users/" + user.getId() + "/permissions" , List.class);
	}
	
	private <T extends Object>T getObjectFromUrl(String url, Class<T> type) throws RealmException{
		return getObjectFromRequest(new HttpRequest(url), type);
	}
	
	private <T extends Object>T getObjectFromRequest(HttpRequest request, Class<T> type) throws RealmException{
		try {
			HttpResponse response = httpClient.invoke(request);
			return objectMapper.readValue(response.getInputStream(), type);
		} catch (IOException ex) {
			throw new RealmException(ex);
		}
	}

}
