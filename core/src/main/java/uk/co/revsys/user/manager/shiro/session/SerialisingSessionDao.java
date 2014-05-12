package uk.co.revsys.user.manager.shiro.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

public abstract class SerialisingSessionDao extends AbstractSessionDAO {

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		if (session instanceof SimpleSession) {
			((SimpleSession) session).setId(sessionId);
		}
		SerialisedSession serialisedSession = serialiseSession(session);
		doCreate(serialisedSession);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		SerialisedSession serialisedSession = doReadSerialisedSession(sessionId);
		if(serialisedSession == null){
			return null;
		}
		return deserialiseSession(serialisedSession);
	}

	@Override
	public void update(Session sn) throws UnknownSessionException {
		doUpdate(serialiseSession(sn));
	}

	@Override
	public void delete(Session sn) {
		doDelete(sn.getId());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		Collection<SerialisedSession> activeSerialisedSessions = doGetActiveSessions();
		Collection<Session> activeSessions = new ArrayList<Session>();
		for (SerialisedSession serialisedSession : activeSerialisedSessions) {
			activeSessions.add(deserialiseSession(serialisedSession));
		}
		return activeSessions;
	}

	protected SerialisedSession serialiseSession(Session session) {
		ObjectOutputStream objectOutputStream = null;
		try {
			SerialisedSession serialisedSession = new SerialisedSession();
			serialisedSession.setId(session.getId());
			serialisedSession.setHost(session.getHost());
			serialisedSession.setCreationTime(session.getStartTimestamp());
			serialisedSession.setLastAccessTime(session.getLastAccessTime());
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(session);
			serialisedSession.setBytes(byteArrayOutputStream.toByteArray());
			return serialisedSession;
		} catch (IOException ex) {
			throw new SessionDaoException(ex);
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException ex) {
				throw new SessionDaoException(ex);
			}
		}
	}

	protected Session deserialiseSession(SerialisedSession serialisedSession) {
		try {
			InputStream inputStream = new ByteArrayInputStream(serialisedSession.getBytes());
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			Session session = (Session) objectInputStream.readObject();
			return session;
		} catch (IOException ex) {
			throw new SessionDaoException(ex);
		} catch (ClassNotFoundException ex) {
			throw new SessionDaoException(ex);
		}
	}

	protected abstract void doCreate(SerialisedSession serialisedSession);

	protected abstract SerialisedSession doReadSerialisedSession(Serializable id);

	protected abstract void doUpdate(SerialisedSession serialisedSession);

	protected abstract void doDelete(Serializable id);

	protected abstract Collection<SerialisedSession> doGetActiveSessions();

}
