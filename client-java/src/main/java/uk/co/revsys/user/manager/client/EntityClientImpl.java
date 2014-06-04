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

    private String baseUrl;
    private String entityType;
    private Class<? extends E> entityClass;
    private ObjectMapper objectMapper;
    private HttpClient httpClient;

    public EntityClientImpl(HttpClient httpClient, String baseUrl, String entityType, Class<? extends E> entityClass) {
        this.baseUrl = baseUrl;
        this.entityType = entityType;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.httpClient = httpClient;
        this.entityClass = entityClass;
    }
    
    @Override
    public E create(String username, String password, E entity) throws IOException {
        HttpRequest request = HttpRequest.POST(baseUrl + "/" + entityType, "application/json", new ByteArrayInputStream(objectMapper.writeValueAsBytes(entity)));
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
        entity = objectMapper.readValue(response.getInputStream(), entityClass);
        return entity;
    }

}
