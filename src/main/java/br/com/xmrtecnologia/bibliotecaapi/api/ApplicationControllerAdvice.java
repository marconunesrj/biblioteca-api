package br.com.xmrtecnologia.bibliotecaapi.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.xmrtecnologia.bibliotecaapi.api.exception.ApiErros;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;

@RestControllerAdvice // Vai ter configurações globais
public class ApplicationControllerAdvice {

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity handleResponseStatusExceptions(ResponseStatusException ex) {
		return new ResponseEntity( new ApiErros(ex), ex.getStatus() );
	}

}
