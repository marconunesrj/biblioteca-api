package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
	
	@Test
	@DisplayName("Deve obter um Livro por Id.")
	public void obterLivroIdTest () {
		// cenário
		// Cria um livro fictício para poder testar, sem Id
		Livro livro = criarNovoLivro();
		entityManager.persist(livro);
		
		
		// execução
		Optional<Livro> livroEncontrado = repository.findById(livro.getId());
		
		// verificação
		assertThat(livroEncontrado.isPresent()).isTrue();
	}
	
	@Test
	@DisplayName("Deve salvar um Livro")
	public void salvarLivroTest() {
		
		// cenário
		Livro livro = criarNovoLivro();
		
		
		// execução
		Livro livroSalvo = repository.save(livro);
		
		// verificação
		assertThat(livroSalvo.getId()).isNotNull();
		
	}
	
	@Test
	@DisplayName("Deve excluir um Livro")
	public void excluirLivroTest() {
		
		// cenário
		// Cria um livro fictício para poder testar
		Livro livro = criarNovoLivro();
		entityManager.persist(livro);
		 
		// Execução
		Livro livroEncontrado = entityManager.find(Livro.class, livro.getId());
		
		repository.delete(livroEncontrado); 
		
		Livro livroExcluido = entityManager.find(Livro.class, livro.getId());
		
		// Verificação 
		assertThat(livroExcluido).isNull(); 
		
	}
	
	private Livro criarNovoLivro() {
		return Livro.builder().autor("Artur").titulo("As aventuras").isbn("123").build();
	}

}
