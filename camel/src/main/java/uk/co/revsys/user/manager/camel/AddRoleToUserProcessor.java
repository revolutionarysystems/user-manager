package uk.co.revsys.user.manager.camel;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class AddRoleToUserProcessor extends HttpProxyProcessor {

    private String user;
    private String role;

    public AddRoleToUserProcessor(String baseUrl) {
        super(baseUrl);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getHttpMethod() {
        return POST;
    }

    @Override
    public String getContentType() {
        return APPLICATION_FORM_URLENCODED;
    }

    @Override
    public String getUrlPath(Exchange exchange) {
        return "/users/" + getUser()+ "/addRole";
    }

    @Override
    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("role", role);
        return params;
    }

}
