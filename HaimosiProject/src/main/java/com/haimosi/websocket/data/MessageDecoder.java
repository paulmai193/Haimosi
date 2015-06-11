package com.haimosi.websocket.data;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haimosi.define.Constant;
import com.haimosi.param.ParamDefine;

/**
 * The Class MessageDecoder.
 * 
 * @author Paul Mai
 */
public class MessageDecoder implements Decoder.Text<MessageInterface> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder.Text#decode(java.lang.String)
	 */
	@Override
	public MessageInterface decode(String s) throws DecodeException {
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(s);
		int command = json.get(ParamDefine.COMMAND).getAsInt();
		MessageInterface message = null;
		switch (command) {
			case Constant.SOCKET_COMMAND_LOGIN:
				message = new MessageLogin();
				message.fromJson(json);
				break;

			case Constant.SOCKET_COMMAND_PUSH_TRANS:
				message = new MessagePushTransaction();
				message.fromJson(json);
				break;

			default:
				break;
		}
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder#destroy()
	 */
	@Override
	public void destroy() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder#init(javax.websocket.EndpointConfig)
	 */
	@Override
	public void init(EndpointConfig arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder.Text#willDecode(java.lang.String)
	 */
	@Override
	public boolean willDecode(String s) {
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(s);
		if (json.has(ParamDefine.COMMAND)) {
			return true;
		}
		else {
			return false;
		}
	}
}
