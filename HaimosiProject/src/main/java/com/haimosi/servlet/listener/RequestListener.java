package com.haimosi.servlet.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.param.ParamDefine;

/**
 * The listener interface for receiving request events. The class that is interested in processing a request event implements this interface, and the
 * object created with that class is registered with a component using the component's <code>addRequestListener<code> method. When
 * the request event occurs, that object's appropriate
 * method is invoked.
 *
 * @see RequestEvent
 * @author Paul Mai
 */
public class RequestListener implements ServletRequestListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
	 */
	@Override
	public void requestDestroyed(ServletRequestEvent requestEvent) {
		try {
			Session session = (Session) requestEvent.getServletRequest().getAttribute(ParamDefine.HIBERNATE_SESSION);
			HibernateUtil.closeSession(session);
			requestEvent.getServletRequest().removeAttribute(ParamDefine.HIBERNATE_SESSION);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
	 */
	@Override
	public void requestInitialized(ServletRequestEvent requestEvent) {
		try {
			requestEvent.getServletRequest().setAttribute(ParamDefine.HIBERNATE_SESSION, HibernateUtil.beginTransaction());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
