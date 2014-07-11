package uk.co.revsys.user.manager.service;

import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;

public interface EntityService<E extends AbstractEntity> {

	public E create(E entity) throws DAOException, DuplicateKeyException, ConstraintViolationException;
	
	public List<E> findAll() throws DAOException;
	
	public E findById(String id) throws DAOException;
	
	public E update(E entity) throws DAOException, DuplicateKeyException, ConstraintViolationException;
	
	public void delete(String id) throws DAOException;
    
    public List<E> find(String key, Object value) throws DAOException;
	
	public List<E> find(Map<String, Object> filters) throws DAOException;
    
    public E findOne(String key, Object value) throws DAOException;
	
	public E findOne(Map<String, Object> filters) throws DAOException;
	
}
