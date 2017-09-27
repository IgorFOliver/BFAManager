package br.bfa.manager.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailServiceImpl implements EmailService {

	private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "/mail/recoverPasswordTemplate";
	private static final String EMAIL_WITHATTACHMENT_TEMPLATE_NAME = "html/email-withattachment";
	private static final String EMAIL_INLINEIMAGE_TEMPLATE_NAME = "html/email-inlineimage";

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine htmlTemplateEngine;

	@Autowired
	private MessageSource messageSource;

	/*
	 * Send HTML mail (simple)
	 */
	public void sendSimpleMailListaJogadores(List<File> attachedFiles) {
		try {

			// Prepare message using a Spring helper
			final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

			mimeMessage.setSubject("Sumulas");
			mimeMessage.setFrom(new InternetAddress("igorfoliver@gmail.com"));
			InternetAddress[] toAddresses = { new InternetAddress("igorfoliver@gmail.com") };
			mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
			mimeMessage.setContentID("teste");

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Sumulas");

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// adds attachments
			if (attachedFiles != null && attachedFiles.size() > 0) {
				for (File aFile : attachedFiles) {

					DataSource source = new FileDataSource(aFile.getAbsolutePath());
					BodyPart fileBody = new MimeBodyPart();
					fileBody.setDataHandler(new DataHandler(source));
					fileBody.setFileName(aFile.getName());
					multipart.addBodyPart(fileBody);
				}
			}

			// sets the multi-part as e-mail's content
			mimeMessage.setContent(multipart);

			// Send email
			this.mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public void sendSimpleMailSumula(File attachedFiles) {
		try {

			// Prepare message using a Spring helper
			final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

			mimeMessage.setSubject("Sumulas");
			mimeMessage.setFrom(new InternetAddress("igorfoliver@gmail.com"));
			InternetAddress[] toAddresses = { new InternetAddress("igorfoliver@gmail.com") };
			mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
			mimeMessage.setContentID("teste");

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Sumulas");

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// adds attachments
			DataSource source = new FileDataSource(attachedFiles.getAbsolutePath());
			BodyPart fileBody = new MimeBodyPart();
			fileBody.setDataHandler(new DataHandler(source));
			fileBody.setFileName(attachedFiles.getName());
			multipart.addBodyPart(fileBody);
			mimeMessage.setContent(multipart);

			// Send email
			this.mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	/*
	 * Send HTML mail (simple)
	 */
	public void sendSimpleMailRecoverPassword(final String username, final String newPassword, final Locale locale)
			throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(locale);
		ctx.setVariable("username", username);
		ctx.setVariable("password", newPassword);
		ctx.setVariable("imageResourceName", "/images/bfalogo.png");

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

		message.setSubject(
				messageSource.getMessage("email.recoverPassword.subject", null, LocaleContextHolder.getLocale()));
		message.setFrom("igorfoliver@gmail.com");
		message.setTo(username);

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.htmlTemplateEngine.process(EMAIL_SIMPLE_TEMPLATE_NAME, ctx);
		message.setText(htmlContent, true /* isHtml */);

		// Send email
		this.mailSender.send(mimeMessage);
	}

	/*
	 * Send HTML mail with attachment.
	 */
	public void sendMailWithAttachment(final String recipientName, final String recipientEmail,
			final String attachmentFileName, final byte[] attachmentBytes, final String attachmentContentType,
			final Locale locale) throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(locale);
		ctx.setVariable("name", recipientName);
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
		message.setSubject("Example HTML email with attachment");
		message.setFrom("thymeleaf@example.com");
		message.setTo(recipientEmail);

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.htmlTemplateEngine.process(EMAIL_WITHATTACHMENT_TEMPLATE_NAME, ctx);
		message.setText(htmlContent, true /* isHtml */);

		// Add the attachment
		final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
		message.addAttachment(attachmentFileName, attachmentSource, attachmentContentType);

		// Send mail
		this.mailSender.send(mimeMessage);
	}

	/*
	 * Send HTML mail with inline image
	 */
	public void sendMailWithInline(final String recipientName, final String recipientEmail,
			final String imageResourceName, final byte[] imageBytes, final String imageContentType, final Locale locale)
			throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(locale);
		ctx.setVariable("name", recipientName);
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
		ctx.setVariable("imageResourceName", imageResourceName); // so that we
																	// can
																	// reference
																	// it from
																	// HTML

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
		message.setSubject("Example HTML email with inline image");
		message.setFrom("thymeleaf@example.com");
		message.setTo(recipientEmail);

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.htmlTemplateEngine.process(EMAIL_INLINEIMAGE_TEMPLATE_NAME, ctx);
		message.setText(htmlContent, true /* isHtml */);

		// Add the inline image, referenced from the HTML code as
		// "cid:${imageResourceName}"
		final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
		message.addInline(imageResourceName, imageSource, imageContentType);

		// Send mail
		this.mailSender.send(mimeMessage);
	}

	@Override
	public void sendMultipleFiles(String sendTo, String subject, List<File> files) {
		try {
			final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress("igorfoliver@gmail.com"));
			InternetAddress[] toAddresses = { new InternetAddress(sendTo) };
			mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(subject);

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// adds attachments
			if (files != null && files.size() > 0) {
				for (File aFile : files) {

					DataSource source = new FileDataSource(aFile.getAbsolutePath());
					BodyPart fileBody = new MimeBodyPart();
					fileBody.setDataHandler(new DataHandler(source));
					fileBody.setFileName(aFile.getName());
					multipart.addBodyPart(fileBody);
				}
			}
			mimeMessage.setContent(multipart);

			this.mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}