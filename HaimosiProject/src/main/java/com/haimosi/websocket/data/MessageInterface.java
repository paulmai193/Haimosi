package com.haimosi.websocket.data;

import com.google.gson.JsonObject;
import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Interface MessageInterface.
 * 
 * @author Paul Mai
 */
public interface MessageInterface {

	/**
	 * Excecute message.
	 *
	 * @param endpoint the endpoint
	 */
	public void excecuteMessage(WSEndpoint endpoint);

	/**
	 * From json.
	 */
	public void fromJson(JsonObject json);

	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public int getCommand();

	/**
	 * Gets the content as object.
	 *
	 * @return the content as object
	 */
	public Object getContentAsObject();

	/**
	 * Sets the command.
	 *
	 * @param command the new command
	 */
	public void setCommand(int command);

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson();

}
