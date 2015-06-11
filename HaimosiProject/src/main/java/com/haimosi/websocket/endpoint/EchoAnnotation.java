/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.haimosi.websocket.endpoint;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.haimosi.websocket.data.MessageDecoder;
import com.haimosi.websocket.data.MessageEncoder;

/**
 * The three annotated echo endpoints can be used to test with Autobahn and the following command "wstest -m fuzzingclient -s servers.json". See the
 * Autobahn documentation for setup and general information.
 */
@ServerEndpoint(value = "/websocket/echo", subprotocols = { "v1.0" }, encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class }/*
 * ,
 * configurator
 * =
 * ServerEndpointConfig
 * .
 * class
 */)
public class EchoAnnotation {

	@OnMessage
	public void echoBinaryMessage(Session session, ByteBuffer bb, boolean last) {
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendBinary(bb, last);
			}
		}
		catch (IOException e) {
			try {
				session.close();
			}
			catch (IOException e1) {
				// Ignore
			}
		}
	}

	/**
	 * Process a received pong. This is a NO-OP.
	 *
	 * @param pm Ignored.
	 */
	@OnMessage
	public void echoPongMessage(PongMessage pm) {
		// NO-OP
	}

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendText(msg, last);
			}
		}
		catch (IOException e) {
			try {
				session.close();
			}
			catch (IOException e1) {
				// Ignore
			}
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		System.out.println("Goodbye baby");
	}

	@OnOpen
	public void onOpen(Session session) throws IOException {
		session.getBasicRemote().sendText("Hello baby");

	}
}
