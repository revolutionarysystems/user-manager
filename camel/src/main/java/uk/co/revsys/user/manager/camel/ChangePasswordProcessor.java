package uk.co.revsys.user.manager.camel;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class ChangePasswordProcessor extends HttpProxyProcessor{
    
    public ChangePasswordProcessor(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String getHttpMethod() {
        return PUT;
    }

    @Override
    public String getUrlPath(Exchange exchange) {
        String user = getParameter(exchange, "user", String.class);
        if(user != null){
            return "/users/" + user + "/changePassword";
        }else{
            return "/users/changePassword";
        }
    }

    @Override
    public Map<String, String> getPostParameters(Exchange exchange) {
        Map<String, String> params = new HashMap<String, String>();
        String username = getParameter(exchange, "username", String.class);
        System.out.println("username = " + username);
        if(username!=null){
            params.put("username", username);
        }
        params.put("password", getParameter(exchange, "password", String.class));
        return params;
    }

}
