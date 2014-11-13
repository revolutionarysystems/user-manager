package uk.co.revsys.user.manager.dao;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.validation.Validator;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;
import uk.co.revsys.user.manager.model.Account;

public class DynamoDBDao<E extends AbstractEntity> extends ValidatingEntityDao<E> {

    private final DynamoDB db;
    private Table table;
    private final ObjectMapper objectMapper = new ObjectMapper();
    Class<? extends AbstractEntity> entityType = Account.class;

    public DynamoDBDao(Validator validator) {
        super(validator);
        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(new BasicAWSCredentials("", ""));
        dbClient.setEndpoint("http://localhost:8000");
        db = new DynamoDB(dbClient);
        table = db.getTable("account");
        try {
            String tableStatus = table.describe().getTableStatus();
            System.out.println("tableStatus = " + tableStatus);
            table.delete();
            table.waitForDelete();
        } catch (ResourceNotFoundException ex) {
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Creating table account");
        table = db.createTable(new CreateTableRequest()
                .withTableName("account")
                .withKeySchema(new KeySchemaElement("id", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L)));
        try {
            TableDescription tableDescription = table.waitForActive();
        } catch (InterruptedException ex1) {
            throw new RuntimeException(ex1);
        }
        String tableStatus = table.describe().getTableStatus();
        System.out.println("tableStatus = " + tableStatus);
    }

    @Override
    public E doCreate(E entity) throws DAOException, DuplicateKeyException {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        try {
            System.out.println(objectMapper.writeValueAsString(entity));
            Item item = new Item().withPrimaryKey("id", entity.getId()).withString("name", "Test123").withJSON("document", objectMapper.writeValueAsString(entity));
            table.putItem(item, new Expected("id").notExist());
            return entity;
        } catch (JsonProcessingException ex) {
            throw new DAOException(ex);
        } catch (ConditionalCheckFailedException ex) {
            throw new DuplicateKeyException(ex);
        }
    }

    @Override
    public E doUpdate(E entity) throws DAOException {
        try {
            System.out.println(objectMapper.writeValueAsString(entity));
            Item item = new Item().withPrimaryKey("id", entity.getId()).withString("name", "Test123").withJSON("document", objectMapper.writeValueAsString(entity));
            table.putItem(item);
            return entity;
        } catch (JsonProcessingException ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public List<E> findAll() throws DAOException {
        return find(null);
    }

    @Override
    public E findById(String id) throws DAOException {
        try {
            Item item = table.getItem(new GetItemSpec().withPrimaryKey("id", id).withAttributesToGet("document"));
            if(item == null){
                return null;
            }
            return (E) objectMapper.readValue(item.getJSON("document"), entityType);
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public void delete(String id) throws DAOException {
        table.deleteItem("id", id);
    }

    @Override
    public List<E> find(String key, Object value) throws DAOException {
        return find(new ValueMap().with(key, value));
    }

    @Override
    public List<E> find(Map<String, Object> filters) throws DAOException {
        List<E> results = new LinkedList<E>();
        ItemCollection scanResult;
        if(filters == null || filters.isEmpty()){
            scanResult = table.scan();
        }else{
            StringBuilder filterExpression = new StringBuilder();
            Map<String, String> nameMap = new HashMap<String, String>();
            Map<String, Object> valueMap = new HashMap<String, Object>();
            for(Entry<String, Object> filter: filters.entrySet()){
                String name = filter.getKey();
                Object value = filter.getValue();
                if(!filterExpression.toString().isEmpty()){
                    filterExpression.append(" AND ");
                }
                filterExpression.append("document.#").append(name).append(" = :").append(name);
                nameMap.put("#" + name, name);
                valueMap.put(":" + name, value);
            }
            scanResult = table.scan(filterExpression.toString(), nameMap, valueMap);
        }
        Iterator<Item> iterator = scanResult.iterator();
        while (iterator.hasNext()) {
            try {
                Item item = iterator.next();
                E entity = (E) objectMapper.readValue(item.getJSON("document"), entityType);
                results.add(entity);
            } catch (IOException ex) {
                throw new DAOException(ex);
            }
        }
        return results;
    }

    @Override
    public E findOne(Map<String, Object> filters) throws DAOException {
        List<E> results = find(filters);
        if(results.isEmpty()){
            return null;
        }
        return results.get(0);
    }

    @Override
    public E findOne(String key, Object value) throws DAOException {
        return findOne(new ValueMap().with(key, value));
    }

}
