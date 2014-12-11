package uk.co.revsys.user.manager.camel;

import org.apache.camel.Exchange;
import uk.co.revsys.esb.component.HttpProxyProcessor;

public class GetUserProcessor extends HttpProxyProcessor{

    private String id;
    
    public GetUserProcessor(String string) {
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
        return "/users/" + getId();
    }

}
