package com.haimosi.websocket.data;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * The Class MessageEncoder.
 * 
 * @author Paul Mai
 */
public class MessageEncoder implements Encoder.Text<MessageInterface> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Encoder#destroy()
	 */
	@Override
	public void destroy() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Encoder.Text#encode(java.lang.Object)
	 */
	@Override
	public String encode(MessageInterface message) throws EncodeException {
		return message.toJson().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Encoder#init(javax.websocket.EndpointConfig)
	 */
	@Override
	public void init(EndpointConfig config) {

	}
}
