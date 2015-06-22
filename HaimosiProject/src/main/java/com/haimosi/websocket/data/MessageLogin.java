package com.haimosi.websocket.data;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;
import logia.utility.json.JsonTool;
import logia.utility.json.annotaion.JsonKey;

import org.hibernate.Session;

import com.google.gson.JsonObject;
import com.haimosi.define.Constant;
import com.haimosi.define.Result;
import com.haimosi.define.StatusCode;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ContactParam;
import com.haimosi.param.ParamDefine;
import com.haimosi.param.StringNotEmptyParam;
import com.haimosi.pool.DAOPool;
import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Class MessageLogin.
 * 
 * @author Paul Mai
 */
@logia.utility.json.annotaion.JsonObject
public class MessageLogin implements MessageInterface {

	/** The command. */
	@JsonKey(key = ParamDefine.COMMAND)
	private int    command;

	/** The email. */
	@JsonKey(key = ParamDefine.EMAIL)
	private String email;

	/** The password. */
	@JsonKey(key = ParamDefine.PASSWORD)
	private String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#excecuteMessage(com.haimosi.websocket.endpoint.WSEndpoint)
	 */
	@Override
	public void excecuteMessage(WSEndpoint endpoint) {
		Result result = new Result();

		// Check user & pass
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			ContactParam contactParam = new ContactParam(this.email);
			StringNotEmptyParam passParam = new StringNotEmptyParam(this.password);
			if (contactParam.getContactType() == ContactParam.CONTACT_EMAIL) {
				Session session = HibernateUtil.getSession();
				UserPOJO user = userDAO.selectByEmail(session, contactParam.getValue());
				if (user != null && user.getPassword().equals(passParam.getValue())) {
					if (user.getStatus() == Constant.USER_STATUS_ACTIVATE) {
						WSEndpoint._clientSessionMap.put(user.getIdUser(), endpoint);
						endpoint.session.setMaxIdleTimeout(0);

						result.setCode(StatusCode.SUCCESS.getCode());
						result.setName(StatusCode.SUCCESS.getContent());
					}
					else if (user.getStatus() == Constant.USER_STATUS_INACTIVATE) {
						result.setCode(StatusCode.MUST_VERIFY.getCode());
						result.setName(StatusCode.MUST_VERIFY.getContent());
					}
					else {
						result.setCode(StatusCode.LOCKED.getCode());
						result.setName(StatusCode.LOCKED.getContent());
					}
				}
				else {
					result.setCode(StatusCode.WRONG_ACCOUNT.getCode());
					result.setName(StatusCode.WRONG_ACCOUNT.getContent());
				}
				HibernateUtil.closeSession(session);
			}
			else {
				result.setCode(StatusCode.WRONG_ACCOUNT.getCode());
				result.setName(StatusCode.WRONG_ACCOUNT.getContent());
			}
			MessageResult message = new MessageResult();
			message.setContent(result);
			endpoint.echoMessage(message);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.websocket.data.MessageInterface#fromJson(com.google.gson.JsonObject)
	 */
	@Override
	public void fromJson(JsonObject json) {
		this.command = json.get(ParamDefine.COMMAND).getAsInt();
		this.email = json.get(ParamDefine.EMAIL).getAsString();
		this.password = json.get(ParamDefine.PASSWORD).getAsString();
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
		return new Login(this.email, this.password);
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
