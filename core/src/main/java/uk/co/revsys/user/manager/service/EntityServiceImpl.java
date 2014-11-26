package uk.co.revsys.user.manager.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import uk.co.revsys.user.manager.dao.EntityDao;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;
import uk.co.revsys.user.manager.service.exception.ServiceException;

public class EntityServiceImpl<E extends AbstractEntity> extends ValidatingEntityService<E>{

	private final EntityDao<E> dao;

	public EntityServiceImpl(Validator validator, EntityDao dao) {
        super(validator);
		this.dao = dao;
	}

    @Override
    public E create(E entity) throws ServiceException, DAOException, DuplicateKeyException, ConstraintViolationException {
        entity.setCreationTime(new Date());
        entity.setLastModifiedTime(new Date());
        return super.create(entity);
    }
	
	@Override
	public E doCreate(E entity) throws ServiceException, DAOException, DuplicateKeyException{
		return dao.create(entity);
	}

	@Override
	public List<E> findAll() throws DAOException {
		return dao.findAll();
	}

	@Override
	public E findById(String id) throws DAOException {
		return dao.findById(id);
	}

    @Override
    public E update(E entity) throws ServiceException, DAOException, ConstraintViolationException {
        entity.setLastModifiedTime(new Date());
        return super.update(entity);
    }

	@Override
	public E doUpdate(E entity) throws DAOException {
		return dao.update(entity);
	}

	@Override
	public void delete(String id) throws DAOException {
		dao.delete(id);
	}

    @Override
    public List<E> find(String key, Object value) throws DAOException {
        return dao.find(key, value);
    }

	@Override
	public List<E> find(Map<String, Object> filters) throws DAOException {
		return dao.find(filters);
	}

    @Override
    public E findOne(String key, Object value) throws DAOException {
        return dao.findOne(key, value);
    }

	@Override
	public E findOne(Map<String, Object> filters) throws DAOException {
		return dao.findOne(filters);
	}

	protected EntityDao<E> getDao() {
		return dao;
	}

}
