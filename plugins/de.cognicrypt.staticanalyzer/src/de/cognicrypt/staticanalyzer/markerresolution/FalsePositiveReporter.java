package de.cognicrypt.staticanalyzer.markerresolution;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.activation.*;
//import java.util.Properties;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

public class FalsePositiveReporter implements IMarkerResolution {

	private final String label;

	public FalsePositiveReporter(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(IMarker arg0) {

		JDialog reporterDialog = new JDialog();
		reporterDialog.setTitle("Issue reporter");
		reporterDialog.setLocationRelativeTo(null);
		reporterDialog.setSize(440, 280);
		reporterDialog.setLayout(new BorderLayout());
		reporterDialog.setModal(true);

		TitledBorder title = BorderFactory.createTitledBorder("Issue Description");
		JTextArea reportField = new JTextArea();
		reportField.setFont(reportField.getFont().deriveFont(12f));
		reportField.setBorder(title);
		reportField.setEditable(true);

		JToolBar controlBar = new JToolBar();
		controlBar.setFloatable(false);
		controlBar.setRollover(true);
		JLabel infoLabel = new JLabel(
				"<html>Additonaly, to the issue description, the concerned source code file will be send.</html>");

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		JButton candelButton = new JButton("Cancel");
		candelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				cancel();
			}
		});

		controlBar.add(infoLabel);
		controlBar.add(sendButton);
		controlBar.add(candelButton);

		reporterDialog.add(reportField, BorderLayout.CENTER);
		reporterDialog.add(controlBar, BorderLayout.SOUTH);
		reporterDialog.setVisible(true);
	}

	private void send() {

//		String to = "";// change accordingly
//		final String user = "";// change accordingly
//		final String password = "";// change accordingly
//
//		// 1) get the session object
//		Properties properties = System.getProperties();
//		properties.setProperty("mail.smtp.host", "mail.gmx.net");
//		properties.put("mail.smtp.auth", "true");
//
//		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(user, password);
//			}
//		});
//
//		// 2) compose message
//		try {
//			MimeMessage message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(user));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			message.setSubject("Message Aleart");
//
//			// 3) create MimeBodyPart object and set your message text
//			BodyPart messageBodyPart1 = new MimeBodyPart();
//			messageBodyPart1.setText("This is message body");
//
//			// 4) create new MimeBodyPart object and set DataHandler object to this object
//			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
//
//			String filename = "SuppressWarningFix.java";// change accordingly
//			DataSource source = new FileDataSource(filename);
//			messageBodyPart2.setDataHandler(new DataHandler(source));
//			messageBodyPart2.setFileName(filename);
//
//			// 5) create Multipart object and add MimeBodyPart objects to this object
//			Multipart multipart = new MimeMultipart();
//			multipart.addBodyPart(messageBodyPart1);
//			multipart.addBodyPart(messageBodyPart2);
//
//			// 6) set the multiplart object to the message object
//			message.setContent(multipart);
//
//			// 7) send message
//			Transport.send(message);
//
//			System.out.println("message sent....");
//		} catch (MessagingException ex) {
//			ex.printStackTrace();
//		}

	}

	private void cancel() {

	}

	private void clear() {

	}

}
