package com.sirra.server.persistence;

import java.sql.*;

import org.hibernate.dialect.*;

/**
 * I prefer Strings to map to "text" type, rather than have length limits. Well proven that this works fine.
 * I also prefer timestamp with time zone so it's the same behavior as I'm used to.
 * 
 * @author aris
 */
public class SirraPostgresDialect extends PostgreSQLDialect {
	
	public SirraPostgresDialect() {
        registerColumnType(Types.VARCHAR, "text");
        registerColumnType(Types.TIMESTAMP, "timestamp with time zone");
    }
}
