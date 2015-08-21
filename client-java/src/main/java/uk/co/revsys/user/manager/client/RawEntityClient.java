package uk.co.revsys.user.manager.client;

import java.io.IOException;

public interface RawEntityClient {

    public String findAll() throws IOException;
    
    public String create(String json) throws IOException;
    
    public String findById(String id) throws IOException;
    
    public void delete(String id) throws IOException;
    
}
