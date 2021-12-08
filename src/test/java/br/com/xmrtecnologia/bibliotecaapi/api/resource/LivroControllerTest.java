package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import br.com.xmrtecnologia.bibliotecaapi.api.dto.LivroDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

// Teste de integração

//@RunWith(SpringRunner.class)// JUnit4 
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Para subir Somente o Controller de Livro quando for Fazer os Testes
@WebMvcTest(controllers = (LivroController.class))
@AutoConfigureMockMvc
public class LivroControllerTest {

	static String LIVRO_API = "/livros";

	@Autowired
	MockMvc mvc;

	@MockBean
	LivroService service;

	@MockBean
	EmprestimoService emprestimoService;

	@Test
	@DisplayName("Deve adicionar um novo livro com sucesso.") // Anotação do JUnit5
	public void adicionarLivroTest() throws Exception {

		LivroDTO dto = criarNovoLivro();

		Livro livroSalvo = Livro.builder().id(1L).autor("Artur").titulo("As aventuras").isbn("123456").build();

		BDDMockito.given(service.save(Mockito.any(Livro.class))).willReturn(livroSalvo);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LIVRO_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc
			.perform(request)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("id").value(1L))
				.andExpect(jsonPath("titulo").value(dto.getTitulo()))
				.andExpect(jsonPath("autor").value(dto.getAutor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()));

		// Igual ao de cima , sem o static import
//		.andExpect( MockMvcResultMatchers.status().isCreated())
//		.andExpect( MockMvcResultMatchers.jsonPath("id").isNotEmpty())
//		.andExpect( MockMvcResultMatchers.jsonPath("titulo").value("Meu Livro"))
//		.andExpect( MockMvcResultMatchers.jsonPath("autor").value("Autou"))
//		.andExpect( MockMvcResultMatchers.jsonPath("isbn").value("1234567890"));

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
	
	@Test
	@DisplayName("Deve obter informações de um livro.")
	public void pegarDetalhesLivroTest() throws Exception {
		
		// cenario (given)
		Long id = 1l;
		
		Livro livro = Livro.builder().id(id).autor("Artur").titulo("As aventuras").isbn("123456").build();
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(livro));
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(LIVRO_API.concat("/" +id))
													.accept(MediaType.APPLICATION_JSON);
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("titulo").value(livro.getTitulo()))
			.andExpect(jsonPath("autor").value(livro.getAutor()))
			.andExpect(jsonPath("isbn").value(livro.getIsbn()));
			
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado não existir.")
	public void LivroNaoEncontradoTest() throws Exception {
		// cenario (given)
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(LIVRO_API.concat("/" +1L))
													.accept(MediaType.APPLICATION_JSON);
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve excluir um livro.")
	public void excluirLivroTest() throws Exception {
		
		// cenario (given)
		BDDMockito.given(service.getById(Mockito.anyLong()))
					.willReturn(Optional.of(Livro.builder().id(1L).build()));
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.delete(LIVRO_API.concat("/" +1L));
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando não encontrar o livro para excluir.")
	public void excluirLivroInexistenteTest() throws Exception {
		
		// cenario (given)
		BDDMockito.given(service.getById(Mockito.anyLong()))
					.willReturn(Optional.empty());
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.delete(LIVRO_API.concat("/" +1L));
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve atualizar um livro.")
	public void atualizarLivroTest() throws Exception {
		
		// cenario (given)
		Long id = 1L;
		String json = new ObjectMapper().writeValueAsString(criarNovoLivro());
		Livro livro = Livro.builder()
				.id(id)
				.autor("Marco")
				.titulo("Eu vou conseguir")
				.isbn("10")
				.build();
		BDDMockito.given(service.getById(Mockito.anyLong()))
					.willReturn(Optional.of(livro));
		Livro livroAtualizado = Livro.builder().id(id).autor("Artur").titulo("As aventuras").isbn("123456").build();
		BDDMockito.given(service.atualizar(livro)).willReturn(livroAtualizado);
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(LIVRO_API.concat("/" +id))
													.content(json)
													.accept(MediaType.APPLICATION_JSON)
													.contentType(MediaType.APPLICATION_JSON);
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isOk())			
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("titulo").value(criarLivro().getTitulo()))
			.andExpect(jsonPath("autor").value(criarLivro().getAutor()))
			.andExpect(jsonPath("isbn").value(criarLivro().getIsbn()));

	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando não encontrar o livro para atualizar.")
	public void atualizarLivroInexistenteTest() throws Exception {
		
		// cenario (given)
		Long id = 1L;
		String json = new ObjectMapper().writeValueAsString(criarNovoLivro());
		BDDMockito.given(service.getById(Mockito.anyLong()))
					.willReturn(Optional.empty());
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(LIVRO_API.concat("/" +id))
													.content(json)
													.accept(MediaType.APPLICATION_JSON)
													.contentType(MediaType.APPLICATION_JSON);
		
		// verificação
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar livros em uma busca")
	public void buscarLivrosTest() throws Exception {
		
		// cenário
		Long id = 1l;
		Livro livro = Livro.builder()
				.id(id)
				.autor(criarLivro().getAutor())
				.titulo(criarLivro().getTitulo())
				.isbn(criarLivro().getIsbn())
				.build();
		
		//Livro[] arrayLivros = {livro};
		
		//Pageable pageRequest = PageRequest.of(0, 100);
				
		Page<Livro> paginacao = new PageImpl<Livro>( Arrays.asList(livro), PageRequest.of(0, 100) , 1l);
		
		BDDMockito.given(service.listar(Mockito.any(Livro.class), Mockito.any(Pageable.class)))
			.willReturn( (Page<Livro>) paginacao );
		
		
		// "/livros?"
		String queryString = String.format("?titulo=%s&autor=%s&page=0&size=100", 
				livro.getTitulo(), livro.getAutor());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(LIVRO_API.concat(queryString))
													.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect( status().isOk() )
			.andExpect( jsonPath("content", Matchers.hasSize(1)))
			.andExpect( jsonPath("totalElements").value(1) )
			.andExpect( jsonPath("pageable.pageSize").value(100))
			.andExpect( jsonPath("pageable.pageNumber").value(0));
	}
	
	private LivroDTO criarNovoLivro() {
		return LivroDTO.builder().autor("Artur").titulo("As aventuras").isbn("123456").build();
	}

	private Livro criarLivro() {
		return Livro.builder().autor("Artur").titulo("As aventuras").isbn("123456").build();
	}


}
