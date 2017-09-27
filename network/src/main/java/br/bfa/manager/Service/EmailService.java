package br.bfa.manager.Service;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

public interface EmailService {

	public void sendSimpleMailRecoverPassword(
	        final String username, String newPassword, final Locale locale)
	        throws MessagingException;

	public void sendSimpleMailListaJogadores(List<File> files);

	public void sendSimpleMailSumula(File generateSumula);

	public void sendMultipleFiles(String sendTo, String subject, List<File> files);
}


