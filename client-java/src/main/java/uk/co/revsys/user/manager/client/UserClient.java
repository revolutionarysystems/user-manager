package uk.co.revsys.user.manager.client;

import uk.co.revsys.user.manager.model.User;
import uk.co.revsys.utils.http.HttpClient;

public class UserClient extends EntityClientImpl<User>{

    public UserClient(HttpClient httpClient, String baseUrl) {
        super(httpClient, baseUrl, User.class);
    }

}
