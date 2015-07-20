package com.haimosi.websocket.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import com.haimosi.define.Constant;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ParamDefine;
import com.haimosi.pool.DAOPool;
import com.haimosi.websocket.config.ServerEndpointConfig;
import com.haimosi.websocket.data.MessageDecoder;
import com.haimosi.websocket.data.MessageEncoder;
import com.haimosi.websocket.data.MessageInterface;

/**
 * The Class InBoundClient.
 * 
 * @author Paul Mai
 */
@ServerEndpoint(value = "/websocket/inbound", subprotocols = { "v1.0" }, encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class }, configurator = ServerEndpointConfig.class)
public class InBoundClient {

	/** The _client session map. */
	public static Map<Integer, InBoundClient> _clientSessionMap = Collections.synchronizedMap(new HashMap<Integer, InBoundClient>());

	/** The create time. */
	long                                      createTime;

	/** The id user. */
	Integer                                   idUser;

	/** The protocol. */
	String                                    protocol;

	/** The session. */
	Session                                   session;

	/**
	 * Echo message.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws EncodeException the encode exception
	 */
	public void echoMessage(MessageInterface message) throws IOException, EncodeException {
		this.session.getBasicRemote().sendObject(message);
	}

	/**
	 * On close.
	 *
	 * @param session the session
	 * @throws Throwable the throwable
	 */
	@OnClose
	public void onClose() {
		System.out.println("Client " + this.session.getRequestURI().toString() + " closed");
		InBoundClient._clientSessionMap.remove(this.idUser);

		this.session = null;
		this.protocol = null;
		this.idUser = null;
		this.createTime = 0;
	}

	/**
	 * On error.
	 *
	 * @param t the t
	 */
	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
	}

	/**
	 * On message.
	 *
	 * @param msg the msg
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws EncodeException the encode exception
	 */
	@OnMessage
	public void onMessage(String msg) throws IOException, EncodeException {
		System.out.println("Receive " + msg + " from " + this.idUser);
		// TODO do something with this message

	}

	/**
	 * On open.
	 *
	 * @param session the session
	 * @param username the username
	 * @param config the config
	 * @throws Throwable
	 */
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) throws Throwable {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if (httpSession != null) {
			Integer idUser = (Integer) httpSession.getAttribute(ParamDefine.USER);
			this.idUser = idUser;
		}

		if (this.idUser != null) {
			try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
				org.hibernate.Session hibernateSession = HibernateUtil.getSession();
				UserPOJO user = userDAO.get(hibernateSession, this.idUser);
				if (user != null && user.getRole().getIdRole().equals(Constant.USER_ROLE_ADMIN)) {
					this.session = session;
					this.protocol = session.getNegotiatedSubprotocol();
					this.createTime = System.currentTimeMillis();
					if (this.protocol.equals("v1.0")) {
						session.setMaxIdleTimeout(0); // never timeout, depend on http session
						this.session.setMaxTextMessageBufferSize(1000000); // 1MB
						session.setMaxBinaryMessageBufferSize(100000000); // 10MB

						InBoundClient._clientSessionMap.put(this.idUser, this);

						System.out.println("Client " + this.session.getRequestURI().toString() + " connected");
						this.session.getBasicRemote().sendText("Hello user " + this.idUser + " !"); // For testing

					}
					else {
						session.close(new CloseReason(CloseCodes.PROTOCOL_ERROR, "Not support this protocols"));
						System.out.println("Not support this protocols");
						this.session = null;
						this.protocol = null;
						this.idUser = null;
						this.createTime = 0;
					}
				}
				else {
					session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "User not admin, denied open websocket connect"));
					System.out.println("User not admin, denied open websocket connect");
				}
				user = null;
				HibernateUtil.closeSession(hibernateSession);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
				session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
			}
		}
		else {
			session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Not recognize this session, maybe not login or session was timeout"));
			System.out.println("Not recognize this session, maybe not login or session was timeout");
		}

	}

	/**
	 * On pong message.
	 *
	 * @param pong the pong
	 */
	@OnMessage
	public void onPongMessage(PongMessage pong) {
		System.out.println("Pong " + pong.getApplicationData().toString() + " from " + this.session.getRequestURI().toString());
	}

}
