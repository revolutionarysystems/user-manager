package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.List;
import uk.co.revsys.user.manager.model.AbstractEntity;

public class EntityClientImpl<R extends RawEntityClient, E extends AbstractEntity> implements EntityClient<R, E> {

    protected R rawClient;
    protected ObjectMapper objectMapper;
    protected Class<? extends E> entityClass;

    public EntityClientImpl(R rawClient, ObjectMapper objectMapper, Class<? extends E> entityClass) {
        this.rawClient = rawClient;
        this.objectMapper = objectMapper;
        this.entityClass = entityClass;
    }

    @Override
    public List<E> findAll() throws IOException {
        String result = rawClient.findAll();
        return objectMapper.readValue(result, TypeFactory.defaultInstance().constructCollectionType(List.class, entityClass));
    }

    @Override
    public E create(E entity) throws IOException {
        String result = rawClient.create(objectMapper.writeValueAsString(entity));
        return objectMapper.readValue(result, entityClass);
    }

    @Override
    public E findById(String id) throws IOException {
        String result = rawClient.findById(id);
        return objectMapper.readValue(result, entityClass);
    }

    @Override
    public void delete(String id) throws IOException {
        rawClient.delete(id);
    }

    @Override
    public R raw() {
        return rawClient;
    }

}
