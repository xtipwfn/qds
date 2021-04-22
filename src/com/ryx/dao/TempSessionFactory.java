package com.ryx.dao;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TempSessionFactory {

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
    private static Configuration configuration; 
    private static SessionFactory sessionFactory;
    
	private TempSessionFactory(){
    };
    
	public static void setConfiguration(String connectstring) throws Exception {
		String classname="";
    	String Dialect = "";
		if(connectstring.startsWith("jdbc:sqlserver")){
			classname="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			Dialect = "org.hibernate.dialect.SQLServerDialect";
		}
		if(connectstring.startsWith("jdbc:mysql")){
			classname="com.mysql.jdbc.Driver";
			Dialect = "org.hibernate.dialect.MySQLDialect";
		}
		if(connectstring.startsWith("jdbc:oracle")){
			classname="oracle.jdbc.driver.OracleDriver";
			Dialect = "org.hibernate.dialect.OracleDialect";
		}
		if("".equals(classname)){
			throw new Exception("不支持的数据库。");
		}
		String dbusername=connectstring.substring(connectstring.indexOf("?dbusername=")+12,connectstring.indexOf("&dbpassword="));
		String dbpassword=connectstring.substring(connectstring.indexOf("&dbpassword=")+12,connectstring.length());
		connectstring=connectstring.substring(0,connectstring.indexOf("?dbusername="));
		configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", Dialect);
		configuration.setProperty("hibernate.connection.url", connectstring);
		configuration.setProperty("hibernate.connection.driver_class", classname);
		configuration.setProperty("hibernate.connection.username", dbusername);
		configuration.setProperty("hibernate.connection.password", dbpassword);
		configuration.setProperty("hibernate.connection.autocommit", "true");
		rebuildSessionFactory();
	}
	/**
     * Returns the ThreadLocal Session instance.  Lazy initialize
     * the <code>SessionFactory</code> if needed.
     *
     *  @return Session
     *  @throws HibernateException
     *  
     */
    public static Session getSession() throws HibernateException {
        Session session = (Session) threadLocal.get();
		if (session == null || !session.isOpen()) {
			if (sessionFactory == null) {
				rebuildSessionFactory();
			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			threadLocal.set(session);
		}

        return session;
    }

	/**
     *  Rebuild hibernate session factory
     *
     */
	public static void rebuildSessionFactory() {
		try {
			sessionFactory = configuration.buildSessionFactory();
		} catch (Exception e) {
			System.err
					.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/**
     *  Close the single hibernate session instance.
     *
     *  @throws HibernateException
     */
    public static void closeSession() throws HibernateException {
        Session session = (Session) threadLocal.get();
        threadLocal.set(null);

        if (session != null) {
            session.close();
        }
    }

	/**
     *  return session factory
     *
     */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	/**
     *  return hibernate configuration
     *
     */
	public Configuration getConfiguration() {
		return configuration;
	}
}