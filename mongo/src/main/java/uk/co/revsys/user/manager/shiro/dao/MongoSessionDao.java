package uk.co.revsys.user.manager.shiro.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;
import uk.co.revsys.user.manager.shiro.session.SerialisedSession;
import uk.co.revsys.user.manager.shiro.session.SerialisingSessionDao;

public class MongoSessionDao extends SerialisingSessionDao{

	private final MongoOperations mongoOps;

	public MongoSessionDao(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}
	
	@Override
	protected void doCreate(SerialisedSession serialisedSession) {
        System.out.println("creating session: " + serialisedSession.getId());
		mongoOps.insert(serialisedSession);
	}

	@Override
	protected SerialisedSession doReadSerialisedSession(Serializable id) {
        System.out.println("reading session: " + id);
		SerialisedSession session = mongoOps.findById(id, SerialisedSession.class);
        System.out.println("session = " + session);
        return session;
	}

	@Override
	protected void doUpdate(SerialisedSession serialisedSession) {
        System.out.println("updating session: " + serialisedSession.getId());
		mongoOps.save(serialisedSession);
	}

	@Override
	protected void doDelete(Serializable id) {
        System.out.println("deleting session: " + id);
		SerialisedSession serialisedSession = doReadSerialisedSession(id);
		mongoOps.remove(serialisedSession);
	}

	@Override
	protected Collection<SerialisedSession> doGetActiveSessions() {
        System.out.println("retrieving active sessions");
        List<SerialisedSession> activeSessions = mongoOps.findAll(SerialisedSession.class);
        System.out.println("activeSessions = " + activeSessions);
        return activeSessions;
	}


}
