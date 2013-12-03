package com.sirra.server.persistence;

import java.io.*;

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;

/**
 * See SequenceGeneratorIfNotBlank for notes.
 * 
 * @author aris
 *
 */
public class UuidGeneratorIfNotBlank extends UUIDGenerator {
	
	@Override
    public Serializable generate(SessionImplementor session, Object object)
    throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
        
        return id != null ? id : super.generate(session, object);
    }
}