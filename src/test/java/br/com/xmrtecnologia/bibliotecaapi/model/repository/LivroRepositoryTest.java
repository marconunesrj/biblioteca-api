package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;


// Teste de Integracao com Banco de Dados
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LivroRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LivroRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
	public void deveRetornarVerdadeiroExistirIsbnInformado( ) {
		
		// cenario
		String isbn = "123";
		// Cria um livro fictício para poder testar
		Livro livro = criarNovoLivro();
		entityManager.persist(livro);
		
		// execução
		boolean exists = repository.existsByIsbn(isbn);
		
		
		// verificação
		assertThat(exists).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void deveRetornarFalseNaoExistirIsbnInformado( ) {
		
		// cenario
		String isbn = "1234";  
		
		// execução
		boolean exists = repository.existsByIsbn(isbn);
		
		
		// verificação
		assertThat(exists).isFalse();
		
	}
	
	private Livro criarNovoLivro() {
		return Livro.builder().autor("Artur").titulo("As aventuras").isbn("123").build();
	}

}
