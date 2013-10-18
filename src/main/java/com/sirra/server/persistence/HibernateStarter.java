package com.sirra.server.persistence;

import java.util.*;

import javax.persistence.*;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.*;
import org.reflections.Reflections;

/**
 * Call HibernateStarter.init(...) from your bootstrap code to initialize hibernate functions. 
 * 
 * @author aris
 */
public class HibernateStarter {

	public static SessionFactory sessionFactory;
	
	/**
	 * @param entityPackage e.g. "com.sirra" The root package to search for entity classes.
	 */
	public static void init(String entityPackage) {
		Configuration configuration = new Configuration();
		
		// Process hibernate.cfg.xml
		configuration.configure();
		
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
	}
}
