package com.sirra.server.persistence;

import java.io.*;

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;

/**
 * The default hibernate sequence generator overrides the ID if we try to manually set it.
 * 
 * NOTE: For integer sequences, 0 is considered the same as not setting an ID. So don't try to manually set an ID of 0.
 * 
 * @author aris
 */
public class SequenceGeneratorIfNotBlank extends SequenceGenerator {

	@Override
    public Serializable generate(SessionImplementor session, Object object)
    throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
        
        if(id != null && id instanceof Integer && ((Integer)id) == 0) {
        	// For integer sequence, id of 0 is same as null.
        	return super.generate(session, object);
        }
        
        return id != null ? id : super.generate(session, object);
    }
}