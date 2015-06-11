package com.haimosi.websocket.config;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Class ServerEndpointConfig.
 * 
 * @author Paul Mai
 */
public class ServerEndpointConfig extends Configurator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.server.ServerEndpointConfig.Configurator#getEndpointInstance(java.lang.Class)
	 */
	@Override
	public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
		T endPoint = super.getEndpointInstance(clazz);
		if (endPoint instanceof WSEndpoint) {
			return endPoint;
		}
		else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.server.ServerEndpointConfig.Configurator#getNegotiatedSubprotocol(java.util.List, java.util.List)
	 */
	@Override
	public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
		// TODO Auto-generated method stub
		if (supported.containsAll(requested)) {
			return super.getNegotiatedSubprotocol(supported, requested);
		}
		else {
			return "N/A";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.server.ServerEndpointConfig.Configurator#modifyHandshake(javax.websocket.server.ServerEndpointConfig,
	 * javax.websocket.server.HandshakeRequest, javax.websocket.HandshakeResponse)
	 */
	@Override
	public void modifyHandshake(javax.websocket.server.ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		if (httpSession != null) {
			sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
		}

		super.modifyHandshake(sec, request, response);
	}
}