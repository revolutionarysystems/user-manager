package uk.co.revsys.user.manager.client;

import java.io.IOException;
import java.util.List;
import uk.co.revsys.user.manager.model.AbstractEntity;

public interface EntityClient<R extends RawEntityClient, E extends AbstractEntity> {

    public R raw();
    
    public List<E> findAll() throws IOException;
    
    public E create(E entity) throws IOException;

    public E findById(String id) throws IOException;
    
    public void delete(String id) throws IOException;

}
