package com.haimosi.servlet.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.haimosi.param.ParamDefine;
import com.haimosi.servlet.filter.AuthenAppsFilter;
import com.haimosi.websocket.endpoint.InBoundClient;

/**
 * The listener interface for receiving session events. The class that is interested in processing a session event implements this interface, and the
 * object created with that class is registered with a component using the component's <code>addSessionListener<code> method. When
 * the session event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SessionEvent
 * @author Paul Mai
 */
public class SessionListener implements HttpSessionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		se.getSession().setMaxInactiveInterval(10); // Default 10 seconds for unauthorize http session
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		Integer idUser = (Integer) se.getSession().getAttribute(ParamDefine.USER);
		if (idUser != null) {
			AuthenAppsFilter._clientMap.remove(idUser);
			InBoundClient._clientSessionMap.remove(idUser);
		}
		System.gc();
	}

}
