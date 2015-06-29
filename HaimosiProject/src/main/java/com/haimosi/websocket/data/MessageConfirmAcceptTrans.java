package com.haimosi.websocket.data;

import logia.utility.json.JsonTool;
import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;
import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Class MessageAcceptTrans.
 * 
 * @author Paul Mai
 */
@logia.utility.json.annotaion.JsonObject
public class MessageConfirmAcceptTrans implements MessageInterface {

	/** The command. */
	@JsonKey(key = ParamDefine.COMMAND)
	private int  command;

	/** The transid. */
	@JsonKey(key = ParamDefine.TRANSACTION_ID)
	private int  transid;

	/** The status. */
	@JsonKey(key = ParamDefine.STATUS)
	private byte status;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#excecuteMessage(com.haimosi.websocket.endpoint.WSEndpoint)
	 */
	@Override
	public void excecuteMessage(WSEndpoint endpoint) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#fromJson(com.google.gson.JsonObject)
	 */
	@Override
	public void fromJson(JsonObject json) {
		this.command = json.get(ParamDefine.COMMAND).getAsInt();
		this.transid = json.get(ParamDefine.TRANSACTION_ID).getAsInt();
		this.status = json.get(ParamDefine.STATUS).getAsByte();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#getContentAsObject()
	 */
	@Override
	public Object getContentAsObject() {
		return null;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public byte getStatus() {
		return this.status;
	}

	/**
	 * Gets the transid.
	 *
	 * @return the transid
	 */
	public int getTransid() {
		return this.transid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#setCommand(int)
	 */
	@Override
	public void setCommand(int command) {
		this.command = command;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(byte status) {
		this.status = status;
	}

	/**
	 * Sets the transid.
	 *
	 * @param transid the transid to set
	 */
	public void setTransid(int transid) {
		this.transid = transid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return JsonTool.toJsonObject(this);
	}

}
