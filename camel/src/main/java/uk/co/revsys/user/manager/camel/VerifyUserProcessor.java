package uk.co.revsys.user.manager.camel;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class VerifyUserProcessor extends HttpProxyProcessor{

    public VerifyUserProcessor(String baseUrl) {
        super(baseUrl);
    }

    private String id;
    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String getHttpMethod() {
        return POST;
    }

    @Override
    public String getUrlPath(Exchange exchng) {
        return "/users/" + getId() + "/verify";
    }

    @Override
    public Map<String, String> getPostParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("code", code);
        return parameters;
    }

    @Override
    public String getContentType() {
        return APPLICATION_FORM_URLENCODED;
    }
    
    

}
