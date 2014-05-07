package uk.co.revsys.user.manager.dao.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Validator;
import uk.co.revsys.user.manager.dao.ValidatingEntityDao;
import uk.co.revsys.user.manager.model.AbstractEntity;

public class MongoDao<E extends AbstractEntity> extends ValidatingEntityDao<E> {

	private final DBCollection dbCollection;
	private final ObjectMapper objectMapper;
	private final Class<? extends E> entityType;

	public MongoDao(Validator validator, MongoClient mongo, String database, String collection, Class<? extends E> entityType) {
		super(validator);
		this.dbCollection = mongo.getDB(database).getCollection(collection);
		this.objectMapper = new ObjectMapper();
		objectMapper.addMixInAnnotations(entityType, MongoEntityJacksonMixin.class);
		this.entityType = entityType;
	}

	@Override
	public E doCreate(E entity) throws IOException {
		entity.setId(UUID.randomUUID().toString());
		WriteResult writeResult = dbCollection.insert((DBObject) JSON.parse(objectMapper.writeValueAsString(entity)));
		return entity;
	}

	@Override
	public List<E> findAll() throws IOException {
		return find(new HashMap());
	}

	@Override
	public E findById(String id) throws IOException {
		DBObject result = dbCollection.findOne(new BasicDBObject("_id", id));
		if (result == null) {
			return null;
		}
		return objectMapper.readValue(result.toString(), entityType);
	}

	@Override
	public E doUpdate(E entity) throws IOException {
		WriteResult writeResult = dbCollection.save((DBObject) JSON.parse(objectMapper.writeValueAsString(entity)));
		return entity;
	}

	@Override
	public void delete(String id) throws IOException{
		dbCollection.remove(new BasicDBObject("_id", id));
	}

	@Override
	public List<E> find(Map<String, Object> filters) throws IOException{
		DBCursor cursor = dbCollection.find((DBObject) JSON.parse(objectMapper.writeValueAsString(filters)));
		List<E> results = new LinkedList<E>();
		while (cursor.hasNext()) {
			DBObject next = cursor.next();
			results.add(objectMapper.readValue(next.toString(), entityType));
		}
		return results;
	}

	@Override
	public E findOne(Map<String, Object> filters) throws IOException {
		DBObject result = dbCollection.findOne((DBObject) JSON.parse(objectMapper.writeValueAsString(filters)));
		if (result == null) {
			return null;
		}
		return objectMapper.readValue(result.toString(), entityType);
	}

}
