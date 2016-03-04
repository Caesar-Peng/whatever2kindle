package com.molecode.w2k.services.impl;

import com.molecode.w2k.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by YP on 2016-01-12.
 */
public class EmailServiceImpl implements EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Override
	public void deliverArticle(String kindleEmail, File kindleFile) {

		try {
			Properties properties = new Properties();
			Session session = Session.getDefaultInstance(properties);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("w2k@molecode.com", "Whatever2Kindle"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(kindleEmail));

			Multipart multipart = new MimeMultipart();

			BodyPart mobiPart = new MimeBodyPart();
			DataSource source = new FileDataSource(kindleFile);
			mobiPart.setDataHandler(new DataHandler(source));
			mobiPart.setDisposition(Part.ATTACHMENT);
			mobiPart.setFileName(kindleFile.getName());
			multipart.addBodyPart(mobiPart);

			message.setContent(multipart);

			Transport.send(message);

			LOG.info("Mobi file({}) has been sent to the kindle device account {}.", kindleFile.getName(), kindleEmail);

		} catch (MessagingException e) {
			LOG.warn("Failed to send email message.", e);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Unsupported encoding", e);
		}

	}
}
