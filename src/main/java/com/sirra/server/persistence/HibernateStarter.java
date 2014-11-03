package com.sirra.server.persistence;

import java.util.*;

import javax.persistence.*;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.*;
import org.hibernate.type.*;
import org.reflections.Reflections;

import com.sirra.server.util.*;

/**
 * Call HibernateStarter.init(...) from your bootstrap code to initialize hibernate functions. 
 * 
 * @author aris
 */
public class HibernateStarter {

	public static SessionFactory sessionFactory;
	
	protected static Set<BasicType> customTypeMappings;
	
	public static void addCustomTypeMappings(BasicType...basicTypes) {
		if(customTypeMappings == null) customTypeMappings = new HashSet();
		
		for(BasicType basicType: basicTypes) {
			customTypeMappings.add(basicType);
		}
	}

	/**
	 * @param entityPackage e.g. "com.sirra" The root package to search for entity classes.
	 */
	public static void init(String entityPackage) {
		Configuration configuration = new Configuration();
		
		// Process hibernate.cfg.xml
		configuration.configure();

		Config config = Config.getInstance();

		if(config.has("db.url")) {
			configuration.setProperty("hibernate.connection.url", config.get("db.url") + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		}

		if(config.has("db.username")) {
			configuration.setProperty("hibernate.connection.username", config.get("db.username"));
		}
		
		if(config.has("db.password")) {
			configuration.setProperty("hibernate.connection.password", config.get("db.password"));
		}
	
		if(customTypeMappings != null) {
			for(BasicType customType: customTypeMappings) {
				configuration.registerTypeOverride(customType);		
			}
		}
		
		// Auto-detect all persistent entities
		Reflections reflections = new Reflections(entityPackage);
    	Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
    	
    	// App-core persistent entities
    	Reflections appCoreReflections = new Reflections("com.sirra");
    	entityClasses.addAll(appCoreReflections.getTypesAnnotatedWith(Entity.class));
    	
    	for(Class clazz: entityClasses) {
    		System.out.println("Entity class found: " + clazz.getCanonicalName());
    		configuration.addAnnotatedClass(clazz);
    	}
		
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		
		// App-wide sessionFactory
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		System.out.println("Did hibernate start: " + sessionFactory);
	}
}