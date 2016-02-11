package com.molecode.w2k.services.impl;

import com.molecode.w2k.services.EmailService;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by YP on 2016-01-12.
 */
public class EmailServiceImpl implements EmailService {

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

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
