package uk.co.revsys.user.manager.shiro.session;

public class SessionDaoException extends RuntimeException{

	public SessionDaoException() {
	}

	public SessionDaoException(String message) {
		super(message);
	}

	public SessionDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionDaoException(Throwable cause) {
		super(cause);
	}

}
