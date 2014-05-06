package uk.co.revsys.user.manager.service.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import uk.co.revsys.user.manager.service.model.AbstractEntity;

public interface EntityDao<E extends AbstractEntity> {
	
	public E create(E entity) throws IOException;
	
	public List<E> findAll() throws IOException;
	
	public E findById(String id) throws IOException;
	
	public E update(E entity) throws IOException;
	
	public void delete(String id) throws IOException;
	
	public List<E> find(Map<String, Object> filters) throws IOException;
	
	public E findOne(Map<String, Object> filters) throws IOException;
	
}
