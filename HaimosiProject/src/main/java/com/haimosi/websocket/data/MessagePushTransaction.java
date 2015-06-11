package com.haimosi.websocket.data;

import java.text.ParseException;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;
import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Class MessagePushTransaction.
 * 
 * @author Paul Mai
 */
public class MessagePushTransaction implements MessageInterface {

	/** The command. */
	private int                command;

	/** The content. */
	private TransactionContent content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#excecuteMessage(com.haimosi.websocket.endpoint.WSEndpoint)
	 */
	@Override
	public void excecuteMessage(WSEndpoint endpoint) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#fromJson(com.google.gson.JsonObject)
	 */
	@Override
	public void fromJson(JsonObject json) {
		this.command = json.get(ParamDefine.COMMAND).getAsInt();
		try {
			this.content = new TransactionContent(json.get(ParamDefine.TRANSACTIONS).getAsJsonObject());
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#getCommand()
	 */
	@Override
	public int getCommand() {
		return this.command;
	}

	/**
	 * @return the content
	 */
	public TransactionContent getContent() {
		return this.content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#getContentAsObject()
	 */
	@Override
	public Object getContentAsObject() {
		return this.content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#setCommand()
	 */
	@Override
	public void setCommand(int command) {
		this.command = command;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(TransactionContent content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.COMMAND, this.command);
		json.add(ParamDefine.TRANSACTIONS, this.content.toJson());

		return json;
	}

}
