package uk.co.revsys.user.manager.camel;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class AddRoleToAccountProcessor extends HttpProxyProcessor {

    private String account;
    private String role;

    public AddRoleToAccountProcessor(String baseUrl) {
        super(baseUrl);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
        return "/accounts/" + getAccount()+ "/addRole";
    }

    @Override
    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("role", role);
        return params;
    }

}
