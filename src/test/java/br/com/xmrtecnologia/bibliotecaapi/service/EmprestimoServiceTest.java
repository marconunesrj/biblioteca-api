package br.com.xmrtecnologia.bibliotecaapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.impl.EmprestimoServiceImpl;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import br.com.xmrtecnologia.bibliotecaapi.model.repository.EmprestimoRepository;

//teste Unitário
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EmprestimoServiceTest {

	EmprestimoService emprestimoService;

	@InjectMocks
	ModelMapper modelMapper;
	
	@MockBean
	EmprestimoRepository emprestimoRepository;

	@BeforeEach
	public void setUp() {
		this.emprestimoService = new EmprestimoServiceImpl(emprestimoRepository, modelMapper);
	}

	@Test
	@DisplayName("deve salvar um empréstimo.")
	public void salvarEmprestimoTest() {
		
		// cenário
		Livro livro = Livro.builder().id(1l).build();
		String cliente = "Fulano";
		Emprestimo emprestimoAserSalvo = Emprestimo.builder()
				.livro(livro)
				.cliente(cliente)
				.dataEmprestimo(LocalDate.now())  // obrigatório
				.retornado(true)  // atributo obrigatório para poder salvar Regra de negócio
				.build();
		
		Emprestimo emprestimoSalvo = Emprestimo.builder()
				.id(1l)
				.livro(livro)
				.cliente(cliente)
				.dataEmprestimo(LocalDate.now())
				.retornado(false)
				.build();
		
		Mockito
			.when(emprestimoRepository.existsByLivroAndNotRetornado(livro))
			.thenReturn(false);
		Mockito.when(emprestimoRepository.save(emprestimoAserSalvo)).thenReturn(emprestimoSalvo);
		
		// execução
		Emprestimo emprestimo = emprestimoService.salvar(emprestimoAserSalvo);
		
		
		// verificação
		assertThat(emprestimo.getId()).isEqualTo(emprestimoSalvo.getId());
		assertThat(emprestimo.getLivro().getId()).isEqualTo(emprestimoSalvo.getLivro().getId());
		assertThat(emprestimo.getCliente()).isEqualTo(emprestimoSalvo.getCliente());
		assertThat(emprestimo.getDataEmprestimo()).isEqualTo(emprestimoSalvo.getDataEmprestimo());
		
	}
	
	@Test
	@DisplayName("deve lançar erro de negócio ao tentar salvar um empréstimo com livro já emprestado.")
	public void lancarErroLivroJaEmpretadoTest() {
		
		// cenário
		Livro livro = Livro.builder().id(1l).build();
		Emprestimo emprestimoAserSalvo = criarEmprestimo();
		
		// execução
		
		Mockito
			.when(emprestimoRepository.existsByLivroAndNotRetornado(livro))
			.thenReturn(true);
		// ou
//		assertThrows(BusinessException.class, () -> emprestimoService.validarRetornado(emprestimoAserSalvo) );
		
		Throwable exception = catchThrowable(() -> emprestimoService.salvar(emprestimoAserSalvo));
		
		// verificação
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Livro está emprestado."); // Mensagem vem da Classe EmprestimoControllerTest
		Mockito.verify(emprestimoRepository, Mockito.never()).save(emprestimoAserSalvo);
		
	}

	public static Emprestimo criarEmprestimo() {
		Livro livro = Livro.builder().id(1l).build();
		Emprestimo emprestimoAserSalvo = Emprestimo.builder()
				.livro(livro)
				.cliente("Fulano")
				.dataEmprestimo(LocalDate.now()) // obrigatório
				.retornado(false)  // obrigatório
				.build();
		return emprestimoAserSalvo;
	}

	@Test
	@DisplayName("Deve obter um empréstimo pelo Id.")
	public void getByIdTest() {
	
		// cenário
		Long id = 1l;
		Emprestimo emprestimo = criarEmprestimo();
		emprestimo.setId(id);
		
		Mockito.when(emprestimoRepository.findById(id))
		.thenReturn(Optional.of(emprestimo));

		// execução
		Optional<Emprestimo> resultado = emprestimoService.getById(id);
		
		// verificação
		assertThat(resultado.isPresent()).isTrue();
		assertThat(resultado.get().getId()).isEqualTo(id);
		assertThat(resultado.get().getCliente()).isEqualTo(emprestimo.getCliente());
		assertThat(resultado.get().getLivro()).isEqualTo(emprestimo.getLivro());
		assertThat(resultado.get().getDataEmprestimo()).isEqualTo(emprestimo.getDataEmprestimo());

		Mockito.verify(emprestimoRepository).findById(id);

	}

	@Test
	@DisplayName("Deve atualizar um empréstimo.")
	public void atualizarEmprestimoTest() {
	
		// cenário
		Long id = 1l;
		Emprestimo emprestimo = criarEmprestimo();
		emprestimo.setId(id);
		emprestimo.setRetornado(true);
		
		// execução
		Mockito.when(emprestimoRepository.save(emprestimo))
			.thenReturn(emprestimo);
		Emprestimo emprestimoAtualizado = emprestimoService.atualizar(emprestimo);
		
		// verificação
		assertThat(emprestimoAtualizado.getRetornado()).isTrue();
		verify(emprestimoRepository).save(emprestimo);
	
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos pelas propriedades")
	public void listarEmprestimosFiltradosTest() {

		// cenário
		EmprestimoFiltroDTO emprestimoFiltroDTO = EmprestimoFiltroDTO.builder().cliente("Fulano").isbn("321").build();
		Emprestimo emprestimo = criarEmprestimo();
		emprestimo.setId(1l);
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Emprestimo> lista = Arrays.asList(emprestimo);
		
		Page<Emprestimo> paginacao = new PageImpl<Emprestimo>(lista, pageRequest, lista.size());
		
		// Funciona
//		Mockito
//		.when(emprestimoRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
//			.thenReturn(paginacao);
		// ou
		Mockito
		.when(emprestimoRepository.findByLivroIsbnOrCliente(Mockito.anyString(), 
				Mockito.anyString(),
				Mockito.any(PageRequest.class)))
					.thenReturn(paginacao);
	
		// Execução
		Page<Emprestimo> resultado = emprestimoService.listar(emprestimoFiltroDTO, pageRequest);
		
		// verificação
		assertThat(resultado.getTotalElements()).isEqualTo(1);
		assertThat(resultado.getContent()).isEqualTo(lista);
		assertThat(resultado.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(resultado.getPageable().getPageSize()).isEqualTo(10);
		
		
	}

//	@Test
//	@DisplayName()
//	public void  {
//	
//		// cenário
//		
//		
//		// execução
//		
//		
//		// verificação
//	}
	
}
