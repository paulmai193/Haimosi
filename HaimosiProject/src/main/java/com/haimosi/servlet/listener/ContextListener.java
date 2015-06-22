package com.haimosi.servlet.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;
import logia.utility.pool.ThreadPoolFactory;

import org.hibernate.Session;

import com.haimosi.define.Config;
import com.haimosi.define.Constant;
import com.haimosi.hibernate.dao.RoleDAO;
import com.haimosi.hibernate.pojo.RolePOJO;
import com.haimosi.pool.DAOPool;
import com.haimosi.pool.ThreadPool;

/**
 * The listener interface for receiving context events. The class that is interested in processing a context event implements this interface, and the
 * object created with that class is registered with a component using the component's <code>addContextListener<code> method. When
 * the context event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ContextEvent
 * @author Paul Mai
 */
public class ContextListener implements ServletContextListener {

	/** The Constant FILE_PATH_CONFIG. */
	private static final String FILE_PATH_CONFIG = ContextListener.class.getClassLoader().getResource("haimosi.cfg.xml").getPath();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		ThreadPool._threadPool.release();
		HibernateUtil.releaseFactory();
		DAOPool.release();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		/* Read configurator xml */
		ContextListener.readServerConfig();
		try {
			/****************************************************/
			/** Load everything of this apps context from here **/

			/* Thread pool */
			ThreadPool._threadPool = new ThreadPoolFactory(Config.num_core_thread_in_pool, Config.num_max_thread_in_pool, Config.thread_priority,
			        true);

			/* Hibernate */
			HibernateUtil.setConfigPath("hibernate.cfg.xml");
			// Check role exist, if not create default value
			Session session = HibernateUtil.beginTransaction();
			RoleDAO roleDAO = AbstractDAO.borrowFromPool(DAOPool.rolePool);
			if (roleDAO.getList(session).size() == 0) {
				RolePOJO admin = new RolePOJO(null, "ADMIN", null);
				RolePOJO member = new RolePOJO(null, "MEMBER", null);
				try {
					roleDAO.saveOrUpdate(session, admin);
					roleDAO.saveOrUpdate(session, member);
					HibernateUtil.commitTransaction(session);
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					HibernateUtil.rollbackTransaction(session);
				}
				admin = null;
				member = null;
			}
			DAOPool.rolePool.returnObject(roleDAO);
			HibernateUtil.closeSession(session);

			/* Resource path */
			Config.resource_avatar_path = contextEvent.getServletContext().getRealPath("/resource/avatar/") + Constant.SEPERATOR;
			Config.resource_item_path = contextEvent.getServletContext().getRealPath("/resource/item/") + Constant.SEPERATOR;
			Config.resource_template_path = contextEvent.getServletContext().getRealPath("/resource/template/") + Constant.SEPERATOR;
			Config.resource_trans_path = contextEvent.getServletContext().getRealPath("/resource/transaction/") + Constant.SEPERATOR;

			/********************* Ending ***********************/
			/****************************************************/
		}
		catch (Throwable e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			e.printStackTrace();
		}

	}

	/**
	 * Read server config.
	 */
	public static synchronized void readServerConfig() {
		InputStream i = null;
		Properties p = new Properties();
		File f = new File(ContextListener.FILE_PATH_CONFIG);
		try {
			if (!f.exists()) {
				ContextListener.createDefaultConfig();
			}
			i = new FileInputStream(f);
			p.loadFromXML(i);
			ContextListener.readServerConfig(p);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				i.close();
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write server config.
	 * 
	 * @param newConfig the new config
	 * @return the properties
	 */
	public static synchronized Properties writeServerConfig(Map<String, String> newConfig) {
		OutputStream o = null;
		Properties p = new Properties();
		File f = new File(ContextListener.FILE_PATH_CONFIG);

		for (String key : newConfig.keySet()) {
			p.setProperty(key, newConfig.get(key));
		}

		try {
			o = new FileOutputStream(f);
			p.storeToXML(o, "Set new configure at " + new Date());
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				o.close();
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return p;
	}

	/**
	 * Creates the default config.
	 */
	private static void createDefaultConfig() {
		Map<String, String> newconfig = new HashMap<String, String>();

		/****************************************************/
		/******* Create all default config from here *********/
		/* Thread pool */
		newconfig.put("num_core_thread_in_pool", "1");
		newconfig.put("num_max_thread_in_pool", "5");
		newconfig.put("thread_priority", "1");

		/* DAO pool */
		newconfig.put("num_core_dao_in_pool", "1");
		newconfig.put("num_max_dao_in_pool", "5");
		newconfig.put("interval_validate_pool", "10");

		/* Data encrypt password */
		newconfig.put("encrypt_password", "haimosiv1.0");

		/********************* Ending ***********************/
		/****************************************************/

		ContextListener.writeServerConfig(newconfig);
	}

	/**
	 * Read server config.
	 * 
	 * @param p the p
	 */
	private static synchronized void readServerConfig(Properties p) {
		/****************************************************/
		/************ Init all config from here *************/
		/* Thread pool */
		Config.num_core_thread_in_pool = Integer.parseInt(p.getProperty("num_core_thread_in_pool"));
		Config.num_max_thread_in_pool = Integer.parseInt(p.getProperty("num_max_thread_in_pool"));
		Config.thread_priority = Integer.parseInt(p.getProperty("thread_priority"));

		/* DAO pool */
		Config.num_core_dao_in_pool = Integer.parseInt(p.getProperty("num_core_dao_in_pool"));
		Config.num_max_dao_in_pool = Integer.parseInt(p.getProperty("num_max_dao_in_pool"));
		Config.interval_validate_pool = Integer.parseInt(p.getProperty("interval_validate_pool"));

		/* Data encrypt password */
		Config.encrypt_password = p.getProperty("encrypt_password");

		/********************* Ending ***********************/
		/****************************************************/
	}
}
