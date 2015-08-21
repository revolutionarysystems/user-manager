package uk.co.revsys.user.manager.client;

import java.io.IOException;

public class RawEntityClientImpl implements RawEntityClient{

    protected UserManagerConnector connector;
    protected String path;

    public RawEntityClientImpl(UserManagerConnector connector, String path) {
        this.connector = connector;
        this.path = path;
    }

    @Override
    public String findAll() throws IOException {
        return get("all");
    }
    
    @Override
    public String create(String json) throws IOException {
        return post("", "application/json", json);
    }

    @Override
    public String findById(String id) throws IOException {
        return get(id);
    }

    @Override
    public void delete(String id) throws IOException {
        connector.delete(this.path + "/" + id);
    }
    
    protected String get(String path) throws IOException{
        return connector.get(this.path + "/" + path);
    }
    
    protected String post(String path, String contentType, String body) throws IOException{
        return connector.post(this.path + "/" + path, contentType, body);
    }

}
