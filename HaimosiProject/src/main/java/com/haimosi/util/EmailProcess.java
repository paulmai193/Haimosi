package com.haimosi.util;

import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * The Class EmailProcess.
 * 
 * @author Paul Mai
 */
public final class EmailProcess {

	/** The password. */
	private static String  password;

	/** The session. */
	private static Session session;

	/** The username. */
	private static String  username;

	/**
	 * Send email.
	 *
	 * @param recipients the recipients
	 * @param nameRecipients the name recipients
	 * @param subject the subject
	 * @param content the content
	 * @return true, if successful
	 */
	public boolean sendEmail(String recipients, String nameRecipients, String subject, String content) {
		if (EmailProcess.session == null || EmailProcess.username == null || EmailProcess.password == null) {
			this.initialized();
		}
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(EmailProcess.session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(EmailProcess.username));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));

			// Set Subject: header field
			message.setSubject(subject, "UTF-8");

			// Now set the actual message
			// message.setText(content, "UTF-8");
			message.setContent(content, "text/html; charset=utf8");

			// Send message
			Transport.send(message);

			return true;
		}
		catch (MessagingException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Initialized mail session.
	 */
	private void initialized() {
		Properties props = new Properties();
		try {
			String propFile = EmailProcess.class.getClassLoader().getResource("email.properties").getPath();
			props.load(new FileInputStream(propFile));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		EmailProcess.username = props.getProperty("email.username");
		EmailProcess.password = props.getProperty("email.password");

		EmailProcess.session = Session.getInstance(props, new javax.mail.Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EmailProcess.username, EmailProcess.password);
			}
		});
	}
}
