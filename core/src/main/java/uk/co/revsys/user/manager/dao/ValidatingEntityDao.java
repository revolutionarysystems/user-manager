package uk.co.revsys.user.manager.dao;

import java.io.IOException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import uk.co.revsys.user.manager.dao.exception.DAOException;
import uk.co.revsys.user.manager.dao.exception.DuplicateKeyException;
import uk.co.revsys.user.manager.model.AbstractEntity;

public abstract class ValidatingEntityDao<E extends AbstractEntity> implements EntityDao<E>{

	private final Validator validator;

	public ValidatingEntityDao(Validator validator) {
		this.validator = validator;
	}
	
	@Override
	public E create(E entity) throws DAOException, DuplicateKeyException, ConstraintViolationException{
		validate(entity);
		return doCreate(entity);
	}

	@Override
	public E update(E entity) throws DAOException, ConstraintViolationException {
		validate(entity);
		return doUpdate(entity);
	}
	
	protected void validate(E entity) throws ConstraintViolationException{
		Set<ConstraintViolation<E>> violations = validator.validate(entity);
		if(!violations.isEmpty()){
			throw new ConstraintViolationException(violations);
		}
	}
	
	public abstract E doCreate(E entity) throws DAOException, DuplicateKeyException;
	
	public abstract E doUpdate(E entity) throws DAOException;

}
