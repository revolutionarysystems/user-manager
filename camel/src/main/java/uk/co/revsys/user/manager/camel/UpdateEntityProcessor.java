package uk.co.revsys.user.manager.camel;

import uk.co.revsys.esb.component.HttpProxyProcessor;

public abstract class UpdateEntityProcessor extends HttpProxyProcessor{

    private String id;
    
    public UpdateEntityProcessor(String baseUrl) {
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
    public String getUrlPath() {
        return "/" + getEntityType() + "/" + getId();
    }
    
    public abstract String getEntityType();

}
