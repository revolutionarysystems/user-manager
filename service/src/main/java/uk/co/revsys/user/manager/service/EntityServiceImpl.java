package uk.co.revsys.user.manager.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import uk.co.revsys.user.manager.service.dao.EntityDao;
import uk.co.revsys.user.manager.service.model.AbstractEntity;

public class EntityServiceImpl<E extends AbstractEntity> implements EntityService<E>{

	private final EntityDao<E> dao;

	public EntityServiceImpl(EntityDao dao) {
		this.dao = dao;
	}
	
	@Override
	public E create(E entity) throws IOException {
		return dao.create(entity);
	}

	@Override
	public List<E> findAll() throws IOException {
		return dao.findAll();
	}

	@Override
	public E findById(String id) throws IOException {
		return dao.findById(id);
	}

	@Override
	public E update(E entity) throws IOException {
		return dao.update(entity);
	}

	@Override
	public void delete(String id) throws IOException {
		dao.delete(id);
	}

	@Override
	public List<E> find(Map<String, Object> filters) throws IOException {
		return dao.find(filters);
	}

	@Override
	public E findOne(Map<String, Object> filters) throws IOException {
		return dao.findOne(filters);
	}

	protected EntityDao<E> getDao() {
		return dao;
	}

}
