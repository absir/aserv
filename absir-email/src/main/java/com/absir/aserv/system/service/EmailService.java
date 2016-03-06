/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月27日 下午6:36:52
 */
package com.absir.aserv.system.service;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.system.configure.JEmailConfigure;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;

/**
 * @author absir
 *
 */
@Base
@Bean
public class EmailService {

	/** emailConfigure */
	public static final JEmailConfigure emailConfigure = JConfigureUtils.getConfigure(JEmailConfigure.class);

	/** session */
	private Session session;

	/**
	 * @author absir
	 *
	 */
	public static class SimpleAuthenticator extends Authenticator {

		/** passwordAuthentication */
		private PasswordAuthentication passwordAuthentication;

		/**
		 * @param username
		 * @param password
		 */
		public SimpleAuthenticator(String username, String password) {
			passwordAuthentication = new PasswordAuthentication(username, password);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.mail.Authenticator#getPasswordAuthentication()
		 */
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return passwordAuthentication;
		}
	}

	/**
	 * @return
	 */
	protected Session createSession() {
		Properties props = new Properties();
		props.put("mail.smtp.host", emailConfigure.getSmtp());
		props.put("mail.smtp.port", emailConfigure.getPort());
		props.put("mail.smtp.starttls.enable", emailConfigure.isStarttls());
		if (emailConfigure.isAnyone()) {
			props.put("mail.smtp.auth", false);
			return Session.getInstance(props);

		} else {
			props.put("mail.smtp.auth", true);
			return Session.getInstance(props, new SimpleAuthenticator(emailConfigure.getUsername(), emailConfigure.getPassword()));
		}
	}

	/**
	 * 
	 */
	public void clearSession() {
		session = null;
	}

	/**
	 * @return
	 */
	public Session getSession() {
		Session session = this.session;
		if (session == null) {
			synchronized (this) {
				session = this.session;
				if (session == null) {
					session = createSession();
					this.session = session;
				}
			}
		}

		return session;
	}

	/**
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public MimeMessage createMimeMessage() throws AddressException, MessagingException {
		MimeMessage mimeMessage = new MimeMessage(getSession());
		mimeMessage.setFrom(new InternetAddress(emailConfigure.getFrom()));
		mimeMessage.setSentDate(Calendar.getInstance().getTime());
		return mimeMessage;
	}

	/**
	 * @param to
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public MimeMessage createMimeMessage(String to) throws AddressException, MessagingException {
		MimeMessage mimeMessage = createMimeMessage();
		mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(to));
		return mimeMessage;
	}

	/**
	 * @param tos
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public MimeMessage createMimeMessage(String[] tos) throws AddressException, MessagingException {
		MimeMessage mimeMessage = createMimeMessage();
		int length = tos.length;
		Address[] addresses = new Address[length];
		for (int i = 0; i < length; i++) {
			addresses[i] = new InternetAddress(tos[i]);
		}

		mimeMessage.setRecipients(RecipientType.TO, addresses);
		return mimeMessage;
	}

	/**
	 * @param mimeMessage
	 * @throws MessagingException
	 */
	public void sendMimeMessage(MimeMessage mimeMessage) throws MessagingException {
		Transport.send(mimeMessage, mimeMessage.getRecipients(RecipientType.TO));
	}
}
