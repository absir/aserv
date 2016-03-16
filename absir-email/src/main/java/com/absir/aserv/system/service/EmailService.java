/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月27日 下午6:36:52
 */
package com.absir.aserv.system.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.system.configure.JEmailConfigure;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Properties;

@Base
@Bean
public class EmailService {

    public static final JEmailConfigure emailConfigure = JConfigureUtils.getConfigure(JEmailConfigure.class);

    private Session session;

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

    public void clearSession() {
        session = null;
    }

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

    public MimeMessage createMimeMessage() throws AddressException, MessagingException {
        MimeMessage mimeMessage = new MimeMessage(getSession());
        mimeMessage.setFrom(new InternetAddress(emailConfigure.getFrom()));
        mimeMessage.setSentDate(Calendar.getInstance().getTime());
        return mimeMessage;
    }

    public MimeMessage createMimeMessage(String to) throws AddressException, MessagingException {
        MimeMessage mimeMessage = createMimeMessage();
        mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(to));
        return mimeMessage;
    }

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

    public void sendMimeMessage(MimeMessage mimeMessage) throws MessagingException {
        Transport.send(mimeMessage, mimeMessage.getRecipients(RecipientType.TO));
    }

    public static class SimpleAuthenticator extends Authenticator {

        private PasswordAuthentication passwordAuthentication;

        public SimpleAuthenticator(String username, String password) {
            passwordAuthentication = new PasswordAuthentication(username, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return passwordAuthentication;
        }
    }
}
