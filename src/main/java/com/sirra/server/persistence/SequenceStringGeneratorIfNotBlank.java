package com.sirra.server.persistence;

import java.io.*;
import java.util.*;

import org.hibernate.*;
import org.hibernate.dialect.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;
import org.hibernate.type.*;

/**
 * Allows sequence IDs for string ID fields.
 * 
 * @author aris
 */
public class SequenceStringGeneratorIfNotBlank extends SequenceGenerator {

	@Override
    public Serializable generate(SessionImplementor session, Object object)
    throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
        
        if(id != null && id instanceof Integer && ((Integer)id) == 0) {
        	// For integer sequence, id of 0 is same as null.
        	return super.generate(session, object).toString();
        }
        return id != null ? id : super.generate(session, object).toString();
    }
	
	protected IntegralDataTypeHolder buildHolder() {
        return new IdentifierGeneratorHelper.BigDecimalHolder();
    }
	
	@Override
	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		if(!params.containsKey("sequence")) {
			String tableName = params.getProperty("target_table");
			params.put("sequence", "sequence_" + tableName);
		}
		super.configure(type, params, dialect);
	}
}
