package br.com.xmrtecnologia.bibliotecaapi.domain.service;

import java.util.List;

public interface EmailService {

	void sendMails(String mensagem, List<String> mailsList);

}
