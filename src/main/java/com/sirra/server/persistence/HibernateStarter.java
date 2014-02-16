package com.sirra.server.persistence;

import java.util.*;

import javax.persistence.*;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.*;
import org.hibernate.type.*;
import org.reflections.Reflections;

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
	
	public static void init(String entityPackage) {
		init(entityPackage, null);
	}
	
	/**
	 * @param entityPackage e.g. "com.sirra" The root package to search for entity classes.
	 */
	public static void init(String entityPackage, String password) {
		Configuration configuration = new Configuration();
		
		// Process hibernate.cfg.xml
		configuration.configure();

		if(password != null) {
			configuration.setProperty("hibernate.connection.password", password);
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