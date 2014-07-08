package uk.co.revsys.user.manager.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import uk.co.revsys.user.manager.model.AbstractEntity;
import uk.co.revsys.utils.http.BasicAuthCredentials;
import uk.co.revsys.utils.http.HttpClient;
import uk.co.revsys.utils.http.HttpRequest;
import uk.co.revsys.utils.http.HttpResponse;

public class EntityClientImpl<E extends AbstractEntity> implements EntityClient<E>{

    private final String baseUrl;
    private final String entityType;
    private final Class<? extends E> entityClass;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public EntityClientImpl(HttpClient httpClient, String baseUrl, String entityType, Class<? extends E> entityClass) {
        this.baseUrl = baseUrl;
        this.entityType = entityType;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.httpClient = httpClient;
        this.entityClass = entityClass;
    }

    @Override
    public E create(E entity) throws IOException {
        return create(UserManager.getUsername(), UserManager.getPassword(), entity);
    }
    
    @Override
    public E create(String username, String password, E entity) throws IOException {
        String json = objectMapper.writeValueAsString(entity);
        String result = createRaw(username, password, json);
        entity = objectMapper.readValue(result, entityClass);
        return entity;
    }

    @Override
    public String createRaw(String json) throws IOException {
        return createRaw(UserManager.getUsername(), UserManager.getPassword(), json);
    }

    @Override
    public String createRaw(String username, String password, String json) throws IOException {
        HttpRequest request = HttpRequest.POST(constructUrl(entityType), "application/json", new ByteArrayInputStream(json.getBytes()));
        return sendRequest(username, password, request);
    }
    
    protected String constructUrl(String type, String id, String path){
        return constructUrl(type, id) + "/" + path;
    }
    
    protected String constructUrl(String type, String id){
        return constructUrl(type) + "/" + id;
    }
    
    protected String constructUrl(String type){
        return baseUrl + "/" + type;
    }
    
    protected String sendRequest(String username, String password, HttpRequest request) throws IOException{
        request.setCredentials(new BasicAuthCredentials(username, password));
        HttpResponse response = httpClient.invoke(request);
        if(response.getStatusCode()!=200){
            InputStream errorStream = response.getInputStream();
            if(errorStream!=null){
                String errorMessage = IOUtils.toString(errorStream);
                throw new IOException("Server returned status " + response.getStatusCode() + " - " + errorMessage);
            }
            throw new IOException("Server returned status " + response.getStatusCode());
        }
        return IOUtils.toString(response.getInputStream());
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
