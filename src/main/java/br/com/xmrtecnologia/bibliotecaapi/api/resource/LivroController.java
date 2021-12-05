package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.LivroDTO;
import br.com.xmrtecnologia.bibliotecaapi.api.exception.ApiErros;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

@RestController
@RequestMapping("/livros")
public class LivroController { 

	private LivroService livroService;
	private ModelMapper modelMapper;
	

	public LivroController(LivroService livroService, ModelMapper modelMapper) {
		// injeção através do Construtor
		this.livroService = livroService;
		this.modelMapper = modelMapper;

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LivroDTO adicionar( @RequestBody @Valid LivroDTO dto) {
		
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
	public LivroDTO buscar ( @PathVariable Long id) {
		return livroService
				.getById(id).map(livro -> modelMapper.map(livro, LivroDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	// Passando parâmetros pela URL "/livros?titulo=%s&autor=%s&page=0&size=100"
	@GetMapping
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
	public LivroDTO atualizar ( @PathVariable Long id, @RequestBody @Valid LivroDTO dto) {
		
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
	public void excluir ( @PathVariable Long id) {
		Livro livro = livroService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		livroService.excluir(livro);
		 
	}
	
	// Esta exception é lançada toda vez que o @Valid falhar
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		
		//List<ObjectError> allErrors = bindingResult.getAllErrors();
		
		return new ApiErros(bindingResult);
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleBusinessExceptions(BusinessException ex) {
		return new ApiErros(ex);
	}

}
