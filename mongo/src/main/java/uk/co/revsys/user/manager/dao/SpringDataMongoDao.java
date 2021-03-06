package uk.co.revsys.user.manager.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.validation.Validator;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;

public class SpringDataMongoDao<E extends AbstractEntity> implements EntityDao<E> {

	private final MongoOperations mongoOps;
	private final Class<? extends E> entityType;

	public SpringDataMongoDao(MongoOperations mongoOps, Class<? extends E> entityType) {
		this.mongoOps = mongoOps;
		this.entityType = entityType;
	}

	@Override
	public E create(E entity) throws DAOException, DuplicateKeyException {
		try {
            if(entity.getId() == null){
                entity.setId(UUID.randomUUID().toString());
            }
			mongoOps.insert(entity);
			return entity;
		} catch (org.springframework.dao.DuplicateKeyException ex) {
			throw new DuplicateKeyException(ex);
		}
	}

	@Override
	public List<E> findAll() throws DAOException {
		return (List<E>) mongoOps.findAll(entityType);
	}

	@Override
	public E findById(String id) throws DAOException {
		return mongoOps.findById(id, entityType);
	}

	@Override
	public E update(E entity) throws DAOException {
		mongoOps.save(entity);
		return entity;
	}

	@Override
	public void delete(String id) throws DAOException {
		E entity = findById(id);
		if (entity != null) {
			mongoOps.remove(entity);
		}
	}

    @Override
    public List<E> find(String key, Object value) throws DAOException {
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        return (List<E>) mongoOps.find(query, entityType);
    }

	@Override
	public List<E> find(Map<String, Object> filters) throws DAOException {
		return (List<E>) mongoOps.find(convertMapToQuery(filters), entityType);
	}

    @Override
    public E findOne(String key, Object value) throws DAOException {
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        return (E) mongoOps.findOne(query, entityType);
    }

	@Override
	public E findOne(Map<String, Object> filters) throws DAOException {
		return mongoOps.findOne(convertMapToQuery(filters), entityType);
	}

	private Query convertMapToQuery(Map<String, Object> filters) {
		Query query = new Query();
		for (Entry<String, Object> filter : filters.entrySet()) {
			query.addCriteria(new Criteria(filter.getKey()).is(filter.getValue()));
		}
		return query;
	}

}
