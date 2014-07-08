package uk.co.revsys.user.manager.client;

import java.io.IOException;
import uk.co.revsys.user.manager.model.AbstractEntity;

public interface EntityClient<E extends AbstractEntity> {

    public E create(E entity) throws IOException;
    
    public E create(String username, String password, E entity) throws IOException;
    
    public String createRaw(String json) throws IOException;
    
    public String createRaw(String username, String password, String json) throws IOException;
    
}
