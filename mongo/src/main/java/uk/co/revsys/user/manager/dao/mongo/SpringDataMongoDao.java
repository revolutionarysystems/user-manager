package uk.co.revsys.user.manager.dao.mongo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.Validator;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.co.revsys.user.manager.dao.ValidatingEntityDao;
import uk.co.revsys.user.manager.model.AbstractEntity;

public class SpringDataMongoDao<E extends AbstractEntity> extends ValidatingEntityDao<E>{

	private final MongoOperations mongoOps;
	private final Class<? extends E> entityType;

	public SpringDataMongoDao(Validator validator, MongoOperations mongoOps, Class<? extends E> entityType) {
		super(validator);
		this.mongoOps = mongoOps;
		this.entityType = entityType;
	}
	
	@Override
	public E doCreate(E entity) throws IOException {
		mongoOps.insert(entity);
		return entity;
	}

	@Override
	public List<E> findAll() throws IOException {
		return (List<E>) mongoOps.findAll(entityType);
	}

	@Override
	public E findById(String id) throws IOException {
		return mongoOps.findById(id, entityType);
	}

	@Override
	public E doUpdate(E entity) throws IOException {
		mongoOps.save(entity);
		return entity;
	}

	@Override
	public void delete(String id) throws IOException {
		E entity = findById(id);
		if(entity!=null){
			mongoOps.remove(entity);
		}
	}

	@Override
	public List<E> find(Map<String, Object> filters) throws IOException {
		return (List<E>) mongoOps.find(convertMapToQuery(filters), entityType);
	}

	@Override
	public E findOne(Map<String, Object> filters) throws IOException {
		return mongoOps.findOne(convertMapToQuery(filters), entityType);
	}
	
	private Query convertMapToQuery(Map<String, Object> filters){
		Query query = new Query();
		for(Entry<String, Object> filter: filters.entrySet()){
			query.addCriteria(new Criteria(filter.getKey()).is(filter.getValue()));
		}
		return query;
	}

}
