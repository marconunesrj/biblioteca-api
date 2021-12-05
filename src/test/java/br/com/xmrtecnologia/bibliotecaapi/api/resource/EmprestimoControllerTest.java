package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

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
										.Cliente("Fulano")
										.livro(livro)
										.dataEmprestimo(LocalDate.now())
										.retornado(false).build();
		BDDMockito.given( emprestimoService.save(Mockito.any(Emprestimo.class)))
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

		BDDMockito.given( emprestimoService.save(Mockito.any(Emprestimo.class)))
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
	
	
}
