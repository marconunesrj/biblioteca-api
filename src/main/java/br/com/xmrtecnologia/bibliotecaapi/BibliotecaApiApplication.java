package br.com.xmrtecnologia.bibliotecaapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BibliotecaApiApplication { 

	@Bean
	public ModelMapper modelMapper( ) {
		return new ModelMapper();  // Cria uma instância singleton para servir a toda aplicação
	}

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApiApplication.class, args);
	}

}
