package com.haimosi.websocket.data;

import logia.utility.json.JsonTool;
import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.haimosi.define.Result;
import com.haimosi.param.ParamDefine;
import com.haimosi.websocket.endpoint.WSEndpoint;

@logia.utility.json.annotaion.JsonObject
public class MessageResult implements MessageInterface {

	@JsonKey(key = ParamDefine.RESULT)
	Result content;

	@Override
	public void excecuteMessage(WSEndpoint endpoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fromJson(JsonObject json) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCommand() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the content
	 */
	public Result getContent() {
		return this.content;
	}

	@Override
	public Object getContentAsObject() {
		return this.content;
	}

	@Override
	public void setCommand(int command) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Result content) {
		this.content = content;
	}

	@Override
	public JsonObject toJson() {
		return JsonTool.toJsonObject(this);
	}

}
