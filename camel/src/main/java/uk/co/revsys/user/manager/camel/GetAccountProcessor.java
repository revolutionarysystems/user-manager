package uk.co.revsys.user.manager.camel;

import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class GetAccountProcessor extends HttpProxyProcessor{

    private String id;
    
    public GetAccountProcessor(String string) {
        super(string);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getHttpMethod() {
        return GET;
    }

    @Override
    public String getUrlPath(Exchange exchng) {
        return "/accounts/" + getId();
    }

}
