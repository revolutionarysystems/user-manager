package uk.co.revsys.user.manager.camel;

import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class DisableAccountProcessor extends HttpProxyProcessor{

    private String id;
    
    public DisableAccountProcessor(String baseUrl) {
        super(baseUrl);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getHttpMethod() {
        return POST;
    }

    @Override
    public String getUrlPath(Exchange exchange) {
       return "/accounts/" + getId() + "/disable";
    }

}
