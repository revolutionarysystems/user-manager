package uk.co.revsys.user.manager.shiro.realm.exception;

public class RealmException extends Exception{

	public RealmException() {
	}

	public RealmException(String message) {
		super(message);
	}

	public RealmException(String message, Throwable cause) {
		super(message, cause);
	}

	public RealmException(Throwable cause) {
		super(cause);
	}

}
