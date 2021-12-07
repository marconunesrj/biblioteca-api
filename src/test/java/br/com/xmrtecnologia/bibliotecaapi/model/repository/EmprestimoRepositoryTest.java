package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import static br.com.xmrtecnologia.bibliotecaapi.model.repository.LivroRepositoryTest.criarNovoLivro;
import static org.assertj.core.api.Assertions.assertThat;

//Teste de Integracao com Banco de Dados
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class EmprestimoRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	EmprestimoRepository repository;
	
	@Test
	@DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
	public void existsByLivroAndNotRetornadoTest() {
		
		// cenário
//		Livro livro = Livro.builder()
//				.autor("Fulano")
//				.isbn("123")
//				.titulo("As aventuras")
//				.build();
		
		Emprestimo emprestimo = criarEPersistirEmprestimo(LocalDate.now());
		
		// execução
		boolean existe = repository.existsByLivroAndNotRetornado(emprestimo.getLivro());
		
		// verificação
		assertThat(existe).isTrue();
		
	}

	@Test
	@DisplayName("Deve buscar um empréstimo pelo isbn do livro ou pelo cliente do empréstimo.")
	public void findByLivroIsbnOrClienteTest () {
		
		// cenário
		Emprestimo emprestimo = criarEPersistirEmprestimo(LocalDate.now());
		
		PageRequest pageRequest = PageRequest.of(0, 10);

		// execução
		Page<Emprestimo> paginacao = repository.findByLivroIsbnOrCliente(
				emprestimo.getLivro().getIsbn(), 
				emprestimo.getCliente(), pageRequest);
		
		// verificação
		assertThat(paginacao.getContent()).contains(emprestimo);
		assertThat(paginacao.getContent()).hasSize(1);
		assertThat(paginacao.getPageable().getPageSize()).isEqualTo(10);
		assertThat(paginacao.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(paginacao.getTotalElements()).isEqualTo(1);

	}

	@Test
	@DisplayName("Deve obter empréstimos cuja data empréstimo seja menor ou igual a três dias atrás e não retornados.")
	public void findByDataEmprestimoLessThanAndRetornadoFalseTest() {
		Emprestimo emprestimo = criarEPersistirEmprestimo(LocalDate.now().minusDays(5));
		
		List<Emprestimo> resultado = repository.findByDataEmprestimoLessThanAndRetornadoFalse(LocalDate.now().minusDays(4));
		
		assertThat(resultado).hasSize(1);
		assertThat(resultado).contains(emprestimo);
	
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando a data empréstimo seja maior ou igual que a dois dias atrás e não retornados.")
	public void notfindByDataEmprestimoLessThanAndRetornadoFalseTest() {
		criarEPersistirEmprestimo(LocalDate.now().minusDays(2));
		
		List<Emprestimo> resultado = repository.findByDataEmprestimoLessThanAndRetornadoFalse(LocalDate.now().minusDays(4));
		
		assertThat(resultado).hasSize(0);
		assertThat(resultado).isEmpty();
	
	}
	
	
	public Emprestimo criarEPersistirEmprestimo(LocalDate localDate) {
		Livro livro = criarNovoLivro();
		entityManager.persist(livro);
		
		Emprestimo emprestimo = Emprestimo.builder()
				.cliente("Marco")
				.emailCliente("cliente@email.com")
				.livro(livro)
				.dataEmprestimo(localDate)
				.build();	
		entityManager.persist(emprestimo);
		
		return emprestimo;
		
	}
	
	
	
}
