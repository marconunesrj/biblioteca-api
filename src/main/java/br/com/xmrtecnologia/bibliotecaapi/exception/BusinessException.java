package br.com.xmrtecnologia.bibliotecaapi.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessException(String s) {
		super(s);
	}

	public BusinessException() {
		super();
	}
}
