package com.csus.key.service;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailManager {
	
	private Session getSession() {
		Properties property = new Properties();
		property.put("mail.smtp.auth", "true");
		property.put("mail.smtp.port", "587");
		property.put("mail.smtp.host", "smtp.gmail.com");
		property.put("mail.smtp.starttls.enable", "true");
		property.put("mail.smtp.user", "fall2018.blockchain@gmail.com");
		property.put("mail.smtp.password", "Fall2018");
		
		Session session = Session.getInstance(property);
		return session;
	}
	
	private void sendMessage(MimeMessage message, Session session) {
		try {
			Transport transport = session.getTransport("smtp");
			transport.connect(null,"fall2018.blockchain@gmail.com","Fall2018");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException ex) {
			System.out.println("EmailService:sendMessage:: MessagingException: " + ex.getMessage());
		}
	}
	
	public void sendEmailContainingThePrivateKey(byte[] data, String email) {
		//String receiver = email;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + email + ".txt");
		try {
			// compose the message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setFrom(new InternetAddress(from));
			message.setSubject("Thank you for using Digital Will Key Manager, "+ email);
			message.setText("Please save the attached file securely on your computer.\n"
					+ "IMPORTANT NOTE:  The will uploaded can only be read with help of this file.\n"
					+ "ONCE THIS FILE IS CORRUPTED, THE WILL NO LONGER BE AVAILABLE OR RETRIEVED.");
			// Attaching the file containing encoded Private key
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("privateKey");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);
			
			// Send message
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	
	public void sendEmailAuthorizeUserToRegister(byte[] privateKey, String userEmail, int will_id) {
		//String receiver = userEmail;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		System.out.println("Before the session");

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + userEmail  + ".txt");

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Thank you for using Digital Will Key Manager, " + userEmail);
			message.setText("Please save the attached file securely on your computer.\n"
					+ "IMPORTANT NOTE:  Please register to the Digital Vault Application.\n"
					+ "YOU HAVE BEEN AUTHORIZED TO VIEW THE WILL #" + will_id);
			// Attaching the file containing encoded Private key
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("privateKey");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);

			// Send message
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
