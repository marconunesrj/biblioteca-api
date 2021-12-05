package br.com.xmrtecnologia.bibliotecaapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.impl.LivroServiceImpl;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import br.com.xmrtecnologia.bibliotecaapi.model.repository.LivroRepository;

// teste Unitário
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LivroServiceTest {

	LivroService livroService;

	@MockBean
	LivroRepository livroRepository;

	@BeforeEach
	public void setUp() {
		this.livroService = new LivroServiceImpl(livroRepository);
	}

	@Test
	@DisplayName("Deve salvar um livro")
	public void salvarLivroTeste() {

		// Cenário
		Livro livro = criarLivro();
		// Forçando o valor de retorno para false, apesar do valor default já seja false
		Mockito.when( livroRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);

		Mockito.when(livroRepository.save(livro))
				.thenReturn(Livro.builder()
						.id(11L)
						.autor("Artur")
						.titulo("As aventuras")
						.isbn("123456")
						.build());

		// Execução
		Livro livroSalvo = livroService.save(livro);

		// Verificação
		assertThat(livroSalvo.getId()).isNotNull();
		assertThat(livroSalvo.getAutor()).isEqualTo("Artur");
		assertThat(livroSalvo.getTitulo()).isEqualTo("As aventuras");
		assertThat(livroSalvo.getIsbn()).isEqualTo("123456");

	} 

	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	public void naoDeveSalvarLivroComIsbnDuplicado() {
		// cenário
		Livro livro = criarLivro();
		
		// Forçando o valor de retorno para true, pq o default é false
		Mockito.when( livroRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		// execução
		Throwable exception = Assertions.catchThrowable(() -> livroService.save(livro));
		
		// verificação
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Isbn já cadastrado.");
		
		// Verifique se o repository nunca vai executar o método save
		Mockito.verify(livroRepository, Mockito.never()).save(livro);
		
	}
	
	@Test
	@DisplayName("Deve obter um livro pelo id")
	public void pegarLivroPeloIdTest() {
		// cenário
		Long id = 1l;
		
		Livro livro = criarLivro();  // já tem o id
		
		Mockito.when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

		// execução
		Optional<Livro> livroEncontrado = livroService.getById(id);
		
		// verificação
		assertThat( livroEncontrado.isPresent() ).isTrue();
		assertThat( livroEncontrado.get().getId()).isEqualTo(id);
		assertThat( livroEncontrado.get().getAutor()).isEqualTo(livro.getAutor());
		assertThat( livroEncontrado.get().getTitulo()).isEqualTo(livro.getTitulo());
		assertThat( livroEncontrado.get().getIsbn()).isEqualTo(livro.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao tentar obter um livro pelo id quando ele não existe na base.")
	public void retornarVaziotentarPegarLivroPeloIdNaoExistenteTest() {
		// cenário
		Long id = 1l;
		
		Mockito.when(livroRepository.findById(id)).thenReturn(Optional.empty());

		// execução
		Optional<Livro> livro = livroService.getById(id);
		
		// verificação
		assertThat( livro.isPresent() ).isFalse();
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro pelo Id")
	public void excluiLivroTest() {
		
		// cenário
		Livro livro = Livro.builder().id(1l).build();
		
		// execução
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> livroService.excluir(livro));
		
		// verificação
		Mockito.verify(livroRepository, Mockito.times(1)).delete(livro);
		
	}
	
	@Test
	@DisplayName("Deve ocorrer erro ao tentar excluir um livro pelo Id inexistente.")
	public void naoExcluiLivroTest() {
		
		// cenário
		Livro livro = new Livro();
		
		// execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> livroService.excluir(livro));
		
		
		// verificação
		Mockito.verify(livroRepository, Mockito.never()).delete(livro);
		
	}
	
	@Test
	@DisplayName("Deve atualizar um livro pelo Id")
	public void atualizarLivroTest() {
		
		// cenário
		long id = 1l;
		
		// livro a atualizar
		Livro livroAAtualizar = Livro.builder().id(id).build();
		
		// simulação
		Livro livroAtualizado = criarLivro();
		livroAtualizado.setId(id);
		Mockito.when(livroRepository.save(livroAAtualizar)).thenReturn(livroAtualizado);
		
		
		// execução
		Livro livro = livroService.atualizar(livroAAtualizar);
		
		// verificação
		assertThat(livro.getId()).isEqualTo(livroAtualizado.getId());
		assertThat(livro.getAutor()).isEqualTo(livroAtualizado.getAutor());
		assertThat(livro.getTitulo()).isEqualTo(livroAtualizado.getTitulo());
		assertThat(livro.getIsbn()).isEqualTo(livroAtualizado.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve ocorrer erro ao tentar atualizar um livro pelo Id inexistente.")
	public void naoAtualizarLivroTest() {
		
		// cenário
		Livro livro = new Livro();
		
		// execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> livroService.atualizar(livro));
		
		
		// verificação
		Mockito.verify(livroRepository, Mockito.never()).save(livro);
		
	}
	
	private Livro criarLivro() {
		return Livro.builder().id(1L).autor("Artur").titulo("As aventuras").isbn("123456").build();
	}
	
}
