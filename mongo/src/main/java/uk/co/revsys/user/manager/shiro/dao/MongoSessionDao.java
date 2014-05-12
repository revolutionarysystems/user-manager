package uk.co.revsys.user.manager.shiro.dao;

import java.io.Serializable;
import java.util.Collection;
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
		mongoOps.insert(serialisedSession);
	}

	@Override
	protected SerialisedSession doReadSerialisedSession(Serializable id) {
		return mongoOps.findById(id, SerialisedSession.class);
	}

	@Override
	protected void doUpdate(SerialisedSession serialisedSession) {
		mongoOps.save(serialisedSession);
	}

	@Override
	protected void doDelete(Serializable id) {
		SerialisedSession serialisedSession = doReadSerialisedSession(id);
		mongoOps.remove(serialisedSession);
	}

	@Override
	protected Collection<SerialisedSession> doGetActiveSessions() {
		return mongoOps.findAll(SerialisedSession.class);
	}


}
