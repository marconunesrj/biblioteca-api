package br.com.xmrtecnologia.bibliotecaapi.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import lombok.RequiredArgsConstructor;

@Service   // Para que o Schedule funcione no Spring
@RequiredArgsConstructor
public class ScheduleService {

	private static final String CRON_EMPRESTIMOS_ATRASADOS = "0 0 0 1/1 * ?";
	
	// Vai pegar este valor no arquivo application.properties
	//@Value("${application.mail.emprestimos.atrasados.mensagem}")  
	private String mensagem = "Atenção! Você tem um empréstimo atrasado. Favor devolver o mais rápido possível.";
	
	private final EmprestimoService emprestimoService;
	private final EmailService emailService;
	
	
	// cron = segundo minuto hora dia mes ano
	// site cronmaker.com (Obs: o Spring só utiliza os seis primeiros argumentos gerados pelo cronmaker)
	@Scheduled(cron = "0 33 10 1/1 * ?")
	public void agendamentoTarefasTeste() {
		System.out.println("Teste de tarefas");
	}

	@Scheduled(cron = CRON_EMPRESTIMOS_ATRASADOS)
	public void sendMailParaEmprestimosAtrasados() {
		List<Emprestimo> todosEmprestimosAtrasados = emprestimoService.getAllEmprestimosAtrasados();
		
		List<String> mailsList = todosEmprestimosAtrasados.stream()
									.map( emprestimo -> emprestimo.getEmailCliente() )
									.collect(Collectors.toList());
		
		
		emailService.sendMails(mensagem, mailsList);
	}
	

}
