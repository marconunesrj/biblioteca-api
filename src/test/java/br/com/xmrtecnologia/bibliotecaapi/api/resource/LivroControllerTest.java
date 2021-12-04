package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import br.com.xmrtecnologia.bibliotecaapi.api.dto.LivroDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

// Teste de integração

//@RunWith(SpringRunner.class)// JUnit4 
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class LivroControllerTest {

	static String LIVRO_API = "/livros;";

	@Autowired
	MockMvc mvc;

	@MockBean
	LivroService service;

	@Test
	@DisplayName("Deve adicionar um novo livro com sucesso.") // Anotação do JUnit5
	public void adicionarLivroTest() throws Exception {

		LivroDTO dto = criarNovoLivro();

		Livro livroSalvo = Livro.builder().id(1L).autor("Artur").titulo("As aventuras").isbn("123456").build();

		BDDMockito.given(service.save(Mockito.any(Livro.class))).willReturn(livroSalvo);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LIVRO_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isCreated()).andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("id").value(1L)).andExpect(jsonPath("titulo").value(dto.getTitulo()))
				.andExpect(jsonPath("autor").value(dto.getAutor())).andExpect(jsonPath("isbn").value(dto.getIsbn()));

		// Igual ao de cima , sem o static import
//		.andExpect( MockMvcResultMatchers.status().isCreated())
//		.andExpect( MockMvcResultMatchers.jsonPath("id").isNotEmpty())
//		.andExpect( MockMvcResultMatchers.jsonPath("titulo").value("Meu Livro"))
//		.andExpect( MockMvcResultMatchers.jsonPath("autor").value("Autou"))
//		.andExpect( MockMvcResultMatchers.jsonPath("isbn").value("1234567890"));

	}

	private LivroDTO criarNovoLivro() {
		return LivroDTO.builder().autor("Artur").titulo("As aventuras").isbn("123456").build();
	}

	// Validação de Integridade de Objeto
	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes para adicionar um novo livro.") // Anotação
																													// do
																													// JUnit5
	public void adicionarInvalidoLivroTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new LivroDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LIVRO_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(3)));

	}

	// Validação de Regra de Negócio
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já cadastrado por outro.")
	public void adicionarLivroComIsbnDuplicado() throws Exception {

		LivroDTO dto = criarNovoLivro();
        String menssagemErro = "Isbn já cadastrado."; 
		String json = new ObjectMapper().writeValueAsString(dto);

		BDDMockito.given(service.save(Mockito.any(Livro.class)))
			.willThrow(new BusinessException(menssagemErro));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LIVRO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value(menssagemErro));
	}

}
