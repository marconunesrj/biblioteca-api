package br.com.xmrtecnologia.bibliotecaapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;

public class ApiErros { 
	
	private List<String> errors;
	
	public ApiErros(BindingResult bindingResult) {
		this.errors = new ArrayList<String>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErros(BusinessException ex) {
		this.errors = Arrays.asList(ex.getLocalizedMessage());
	}

	public List<String> getErrors() {
		return this.errors;
	}
}
