package br.com.xmrtecnologia.bibliotecaapi.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

	// Injetando o valor vindo do arquivo application.properties
	//@Value("${application.mail.default.remetente}")  // Não funcionou
	private String remetente = "mail@biblioteca-api.com";
	
	@Autowired
	private final JavaMailSender javaMailSender;

	@Override
	public void sendMails(String mensagem, List<String> mailsList) {
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetente);
		mailMessage.setSubject("Livro com empréstimo atrasado.");
		mailMessage.setText(mensagem);
		String[] listaPara = mailsList.toArray( new String[mailsList.size()]);
		mailMessage.setTo(listaPara);
		
		javaMailSender.send(mailMessage);
	}

}
