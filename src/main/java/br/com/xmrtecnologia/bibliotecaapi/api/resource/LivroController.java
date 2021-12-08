package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import br.com.xmrtecnologia.bibliotecaapi.api.dto.LivroDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor  // cria o construtor para as propriedades final
@Api("Livro API")
@Slf4j  // para o Log
public class LivroController { 

	private final LivroService livroService;
	private final ModelMapper modelMapper;
	private final EmprestimoService emprestimoService;

	

//	public LivroController(LivroService livroService, ModelMapper modelMapper, EmprestimoService emprestimoService) {
//		// injeção através do Construtor
//		this.livroService = livroService;
//		this.modelMapper = modelMapper;
//		this.emprestimoService = emprestimoService;
//
//	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Adicionar um Livro")
	public LivroDTO adicionar( @RequestBody @Valid LivroDTO dto) {
		
		log.info("Adicionando um Livro para o Isbn: {} ", dto.getIsbn());
//		Livro livro = Livro.builder()
//				.autor(dto.getAutor())
//				.titulo(dto.getTitulo())
//				.isbn(dto.getIsbn())
//				.build();
		
		Livro livro = modelMapper.map(dto, Livro.class);
		

		livro = livroService.save(livro);
		
//		dto = LivroDTO.builder()
//				.id(livro.getId())
//				.autor(livro.getAutor())
//				.titulo(livro.getTitulo())
//				.isbn(livro.getIsbn())
//				.build();
		
		dto = modelMapper.map(livro, LivroDTO.class);
		
		return dto;
	}
	
	@GetMapping("{id}")
	@ApiOperation("Buscar um livro pelo seu Id")
	public LivroDTO buscar ( @PathVariable Long id) {
		log.info("Buscar um Livro com o Id: {} ", id);
		return livroService
				.getById(id).map(livro -> modelMapper.map(livro, LivroDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	// Passando parâmetros pela URL "/livros?titulo=%s&autor=%s&page=0&size=100"
	@GetMapping
	@ApiOperation("Buscar livros pelos filtros: Isbn, Autor ou Título")
	public Page<LivroDTO> listar ( LivroDTO livroDTO, Pageable pageRequest) {
		Livro filtro = modelMapper.map(livroDTO, Livro.class);
		
		Page<Livro> resultado = livroService.listar(filtro, pageRequest);
		
		List<LivroDTO> list = resultado.getContent()
				.stream()
				.map(livro -> modelMapper.map(livro, LivroDTO.class))
				.collect(Collectors.toList());
		
		return new PageImpl<LivroDTO>(list, pageRequest, resultado.getTotalElements());
		
	}
	
	@PutMapping("{id}")
	@ApiOperation("Atualizar as informações de um livro pelo seu Id")
	public LivroDTO atualizar ( @PathVariable Long id, @RequestBody @Valid LivroDTO dto) {
		log.info("Atualizando um Livro com o Id: {} ", id);
		
		return livroService.getById(id)
				.map( livro -> {
					livro.setAutor(dto.getAutor());
					livro.setTitulo(dto.getTitulo());
					livro = livroService.atualizar(livro);
					return modelMapper.map(livro, LivroDTO.class);
				})
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		// Faz a mesma coisa que o código acima
//		Livro livro = livroService.getById(id)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//		
//		livro.setAutor(dto.getAutor());
//		//livro.setIsbn(dto.getIsbn());
//		livro.setTitulo(dto.getTitulo());
//		
//		livro = livroService.atualizar(livro);
//
//		dto = modelMapper.map(livro, LivroDTO.class);
//		
//		return dto; 
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation("Excluir um livro pelo seu Id")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Livro excluído com sucesso."),
		@ApiResponse(code = 404, message = "Livro Não encontrado.")
	})
	public void excluir ( @PathVariable Long id) {
		log.info("Excluir um Livro com o Id: {} ", id);
		Livro livro = livroService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		livroService.excluir(livro);
		 
	}
	
	// Sub-recurso de livros
	@GetMapping("{id}/emprestimos")
	@ApiOperation("Buscar os Empréstimos de um determinado livro pelo seu Id")
	public Page<EmprestimoFiltroDTO> emprestimosPorLivro(@PathVariable Long id, Pageable pageable){
		Livro livro = livroService.getById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		Page<Emprestimo> resultado = emprestimoService.getEmprestimosPorLivro(livro, pageable);
		
		List<EmprestimoFiltroDTO> list = resultado.getContent()
				.stream()
				.map(emprestimo -> { 
					LivroDTO livroDTO = modelMapper.map(emprestimo.getLivro(), LivroDTO.class);
					EmprestimoFiltroDTO emprestimoDTO = modelMapper.map(emprestimo, EmprestimoFiltroDTO.class);
					emprestimoDTO.setLivro(livroDTO);
					return emprestimoDTO;
					})
				.collect(Collectors.toList());
		
		return new PageImpl<EmprestimoFiltroDTO>(list, pageable, resultado.getTotalElements());
		
	}
}
