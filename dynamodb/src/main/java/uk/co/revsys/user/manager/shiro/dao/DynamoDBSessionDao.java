package uk.co.revsys.user.manager.shiro.dao;

import java.io.Serializable;
import java.util.Collection;
import uk.co.revsys.user.manager.shiro.session.SerialisedSession;
import uk.co.revsys.user.manager.shiro.session.SerialisingSessionDao;

public class DynamoDBSessionDao extends SerialisingSessionDao{

    @Override
    protected void doCreate(SerialisedSession serialisedSession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected SerialisedSession doReadSerialisedSession(Serializable id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void doUpdate(SerialisedSession serialisedSession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void doDelete(Serializable id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Collection<SerialisedSession> doGetActiveSessions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
