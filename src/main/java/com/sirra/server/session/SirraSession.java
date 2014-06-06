package com.sirra.server.session;

import java.util.*;

import javax.servlet.http.*;

import org.hibernate.*;

import com.sirra.server.persistence.*;

/**
 * Each call to the api starts a SirraSession, which is retrievable from anywhere.
 * 
 * You can retrieve convenient objects from the session, such as request and 
 * response objects, hibernate session objects, as well as store any object you want in a map.
 */
public class SirraSession {

	protected static Map<Thread, SirraSession> lookup = new HashMap();
	
	public static void start(HttpServletRequest request, HttpServletResponse response) {
		lookup.put(Thread.currentThread(), new SirraSession(request, response));
	}
	
	public static void start() {
		lookup.put(Thread.currentThread(), new SirraSession());
	}
	
	public static SirraSession get() {
		SirraSession ss = lookup.get(Thread.currentThread());
		
		if(ss == null) lookup.put(Thread.currentThread(), new SirraSession(null, null));
		
		return lookup.get(Thread.currentThread());
	}
	
	public static void end() {
		SirraSession ss = lookup.remove(Thread.currentThread());
		ss.commit();
	}
	
	public static void rollback() {
		SirraSession ss = lookup.remove(Thread.currentThread());
		ss._rollback();	
	}
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	protected Session hibernateSession;
	
	protected String accountId;
	protected String userId;
	
	protected Map<String, Object> dataMap;
	
	private SirraSession(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		
		init();
	}
	
	private SirraSession() {
		init();
	}
	
	protected void init() {
		dataMap = new HashMap();
		
		if(HibernateStarter.sessionFactory == null) {
			System.err.println("Can't initialize HibernateSession as HibernateStarter was not initialized.");
			return;
		}
		
		try {
			hibernateSession = HibernateStarter.sessionFactory.openSession();
			hibernateSession.beginTransaction();
		} catch (NullPointerException e) {
			System.out.println("Failed to initialize HibernateSession.");
			e.printStackTrace();
		}
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public boolean hasAccount() {
		return accountId != null;
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Store any data you want.
	 */
	public void setData(String key, Object value) {
		dataMap.put(key, value);
	}
	
	public Object getData(String key) {
		return dataMap.get(key);
	}

	public Session getHibernateSession() {
		return hibernateSession;
	}
	
	// Consider using hibernateSession.flush()
	public void commitButLeaveRunnning() {
		if(hibernateSession == null) {
			System.err.println("Can't commit HibernateSession as there is no HibernateSession.");
			return;
		}
		
		try {
			hibernateSession.getTransaction().commit();
			hibernateSession.beginTransaction();
		} catch (NullPointerException e) {
			System.out.println("Failed to commit");
			e.printStackTrace();
		}
	}
	
	// When you need the current transactions to "see" the SQL changes made so far. Does not commit yet.
	public void flushTransactions() {
		if(hibernateSession == null) {
			System.err.println("Can't commit HibernateSession as there is no HibernateSession.");
			return;
		}
		
		try {
			hibernateSession.flush();
		} catch (NullPointerException e) {
			System.out.println("Failed to flush");
			e.printStackTrace();
		}
	}

	protected void commit() {
		if(hibernateSession == null) {
			System.err.println("Can't commit HibernateSession as there is no HibernateSession.");
			return;
		}
		
		try {
			hibernateSession.getTransaction().commit();
			hibernateSession.close();
			hibernateSession = null;
		} catch (NullPointerException e) {
			System.out.println("Failed to commit");
			e.printStackTrace();
		}
	}
	
	protected void _rollback() {
		if(hibernateSession == null) {
			System.err.println("Can't commit HibernateSession as there is no HibernateSession.");
			return;
		}
		
		try {
			hibernateSession.getTransaction().rollback();
			hibernateSession.close();
		} catch (NullPointerException e) {
			System.out.println("Failed to commit");
			e.printStackTrace();
		}
	}
}
