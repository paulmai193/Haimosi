package com.haimosi.websocket.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.websocket.config.ServerEndpointConfig;
import com.haimosi.websocket.data.MessageDecoder;
import com.haimosi.websocket.data.MessageEncoder;
import com.haimosi.websocket.data.MessageInterface;

/**
 * The Class WSEndpoint.
 * 
 * @author Paul Mai
 */
@ServerEndpoint(value = "/websocket", subprotocols = { "v1.0" }, encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class }, configurator = ServerEndpointConfig.class)
public class WSEndpoint {

	/** The _client session map. */
	public static Map<Integer, WSEndpoint> _clientSessionMap = Collections.synchronizedMap(new HashMap<Integer, WSEndpoint>());

	/** The session. */
	public Session                         session;

	/** The protocol. */
	String                                 protocol;

	/** The id user. */
	public UserPOJO                        user;

	/** The create time. */
	long                                   createTime;

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
		WSEndpoint._clientSessionMap.remove(this.user.getIdUser());

		this.session = null;
		this.protocol = null;
		this.user = null;
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
	 */
	@OnMessage
	public void OnMessage(MessageInterface msg) {
		msg.excecuteMessage(this);
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
		this.session = session;
		this.protocol = session.getNegotiatedSubprotocol();
		this.createTime = System.currentTimeMillis();
		if (this.protocol.equals("v1.0")) {
			this.session.setMaxIdleTimeout(600000); // default timeout for 60 seconds
			this.session.setMaxTextMessageBufferSize(1000000); // 1MB
			this.session.setMaxBinaryMessageBufferSize(100000000); // 10MB

			System.out.println("Client " + this.session.getRequestURI().toString() + " connected");
			this.session.getBasicRemote().sendText("Welcome!!"); // For testing

		}
		else {
			session.close(new CloseReason(CloseCodes.PROTOCOL_ERROR, "Not support this protocols"));
			System.out.println("Not support this protocols");
			this.session = null;
			this.protocol = null;
			this.createTime = 0;

			this.finalize();
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
