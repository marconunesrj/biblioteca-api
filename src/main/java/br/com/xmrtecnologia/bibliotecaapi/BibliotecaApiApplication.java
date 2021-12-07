package br.com.xmrtecnologia.bibliotecaapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Utilizado para agendamento de tarefas
public class BibliotecaApiApplication { 

	///////// Para testar o envio de email - Funcionando
//	@Autowired //(required=false)
//	private EmailService emailService;
	
	// O CommandLineRunner vai ser executado assim que a aplicação subir.
//	@Bean
//	public CommandLineRunner runner() {
//		return args -> {
//			// biblioteca-api-445a1d@inbox.mailtrap.io
//			List<String> emails = Arrays.asList("biblioteca-api-445a1d@inbox.mailtrap.io");
//			emailService.sendMails("Testando serviço de emails", emails);
//			System.out.println("Emails Enviados");
//		};
//	}
	//////////////////////////////////////
		
	@Bean
	public ModelMapper modelMapper( ) {
		return new ModelMapper();  // Cria uma instância singleton para servir a toda aplicação
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApiApplication.class, args);
	}

}
