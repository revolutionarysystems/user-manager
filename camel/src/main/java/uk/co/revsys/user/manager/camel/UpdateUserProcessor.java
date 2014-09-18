package uk.co.revsys.user.manager.camel;

public class UpdateUserProcessor extends UpdateEntityProcessor{

    public UpdateUserProcessor(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String getEntityType() {
        return "users";
    }

}
