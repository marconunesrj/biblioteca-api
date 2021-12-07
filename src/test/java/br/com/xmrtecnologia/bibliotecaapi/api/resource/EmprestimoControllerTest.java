package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoDTO;
import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import br.com.xmrtecnologia.bibliotecaapi.api.dto.RetornadoEmprestimoDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import br.com.xmrtecnologia.bibliotecaapi.service.EmprestimoServiceTest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//Para subir Somente o Controller de Emprestimo quando for Fazer os Testes
@WebMvcTest(controllers = EmprestimoController.class)
@AutoConfigureMockMvc
public class EmprestimoControllerTest {

	static String EMPRESTIMO_API = "/emprestimos";

	@Autowired
	MockMvc mvc;

	@MockBean
	private LivroService livroService;

	@MockBean
	private EmprestimoService emprestimoService;

	@Test
	@DisplayName("Deve realizar um empréstimo")
	public void adicionarEmprestimoTest() throws Exception {
				
		// cenário
		String isbn = "123";
		//Long id = 1l;
		EmprestimoDTO dto = EmprestimoDTO.builder().isbn(isbn).cliente("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
			
		Livro livro = Livro.builder().id(1l).isbn(isbn).build();
		BDDMockito.given(livroService.getLivroByIsbn(isbn) )
				.willReturn(Optional.of(livro));
		
		Emprestimo emprestimo = Emprestimo.builder()
										.id(1l)
										.cliente("Fulano")
										.livro(livro)
										.dataEmprestimo(LocalDate.now())
										.retornado(false).build();
		BDDMockito.given( emprestimoService.salvar(Mockito.any(Emprestimo.class)))
			.willReturn(emprestimo);
		
		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(EMPRESTIMO_API)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json);
		
		// Verificação
		mvc.perform(request)
			.andExpect( status().isCreated())
			.andExpect( content().string("1"));  // Estamos devolvendo um Long que é o id mesmo
 		  //.andExpect( jsonPath("id").value(1l));
		
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente.")
	public void invalidoIsbnTest( )throws Exception {
		
		// cenário
		String isbn = "123";
		//Long id = 1l;
		EmprestimoDTO dto = EmprestimoDTO.builder().isbn(isbn).cliente("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		//Livro livro = Livro.builder().id(1l).isbn(isbn).build();
		BDDMockito.given(livroService.getLivroByIsbn(isbn) )
				.willReturn(Optional.empty());

		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(EMPRESTIMO_API)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json);

		// Verificação
		mvc.perform(request)
			.andExpect( status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Livro não encontrado para o isbn informado: " + dto.getIsbn()));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro já emprestado.")
	public void emprestimoErroLivrojaEmprestadoTest( )throws Exception {
		
		// cenário
		String isbn = "123";
		//Long id = 1l;
		EmprestimoDTO dto = EmprestimoDTO.builder().isbn(isbn).cliente("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		Livro livro = Livro.builder().id(1l).isbn(isbn).build();
		
		BDDMockito.given(livroService.getLivroByIsbn(isbn) )
				.willReturn(Optional.of(livro));

		BDDMockito.given( emprestimoService.salvar(Mockito.any(Emprestimo.class)))
			.willThrow(new BusinessException("Livro está emprestado."));
		
		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(EMPRESTIMO_API)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json);

		// Verificação
		mvc.perform(request)
			.andExpect( status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Livro está emprestado."));
	}
	
	@Test
	@DisplayName("Deve retornar um Livro.")
	public void retornarLivroTest() throws Exception {
		
		// cenário { retornado; true }
		RetornadoEmprestimoDTO dto = RetornadoEmprestimoDTO.builder().retornado(true).build();
		Livro livro = Livro.builder()
				.id(1l)
				.isbn("123")
				.build();
		
		Emprestimo emprestimo = Emprestimo.builder()
				.id(1l)
				.cliente("Fulano")
				.livro(livro)
				.dataEmprestimo(LocalDate.now())
				.build();
		
		BDDMockito.given(emprestimoService.getById(Mockito.anyLong()))
			.willReturn(Optional.of(emprestimo));
		
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução
//		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//				.patch(EMPRESTIMO_API.concat("/1"))
//				.accept(MediaType.APPLICATION_JSON)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(json);
		
		
		// verificação
		mvc
			.perform(
					patch(EMPRESTIMO_API.concat("/1"))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
			)
			.andExpect(status().isOk());
		
		// Verificando se o método atualizar foi chamado 1 vez
		Mockito.verify(emprestimoService, Mockito.times(1)).atualizar(emprestimo);
		
	}
	
	
	@Test
	@DisplayName("Deve retornar 404 quando tentar devolver um Livro inexistente.")
	public void retornarLivroInexistenteTest() throws Exception {
		
		// cenário { retornado; true }
		RetornadoEmprestimoDTO dto = RetornadoEmprestimoDTO.builder().retornado(true).build();
		
		BDDMockito.given(emprestimoService.getById(Mockito.anyLong()))
			.willReturn(Optional.empty());
		
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução		
		
		// verificação
		mvc
			.perform(
					patch(EMPRESTIMO_API.concat("/1"))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
			)
			.andExpect(status().isNotFound());
		
		// Verificando se o método atualizar foi chamado o vez
		Mockito.verify(emprestimoService, Mockito.times(0))
			.atualizar(Emprestimo.builder().id(1l).build());
		
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos em uma busca")
	public void buscarLivrosTest() throws Exception {
		
		// cenário
		Long id = 1l;
		
		Emprestimo emprestimo = EmprestimoServiceTest.criarEmprestimo();
		emprestimo.setId(id);
		Livro livro = Livro.builder().id(id).isbn("321").build();
		emprestimo.setLivro(livro);
				
		Page<Emprestimo> paginacao = new PageImpl<Emprestimo>( Arrays.asList(emprestimo), 
				PageRequest.of(0, 10) , 1l);
		
		BDDMockito.given(emprestimoService.listar(Mockito.any(EmprestimoFiltroDTO.class), 
				Mockito.any(Pageable.class)))
					.willReturn( (Page<Emprestimo>) paginacao );
		
		
		// "/emprestimos?"
		String queryString = String.format("?isbn=%s&cliente=%s&page=0&size=10", 
				emprestimo.getLivro().getIsbn(), emprestimo.getCliente());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(EMPRESTIMO_API.concat(queryString))
													.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect( status().isOk() )
			.andExpect( jsonPath("content", Matchers.hasSize(1)))
			.andExpect( jsonPath("totalElements").value(1) )
			.andExpect( jsonPath("pageable.pageSize").value(10))
			.andExpect( jsonPath("pageable.pageNumber").value(0));
	}
	

}
