package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
