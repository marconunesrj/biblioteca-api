package br.com.xmrtecnologia.bibliotecaapi.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoDTO {

	@NotEmpty
	private String isbn;

	@NotEmpty
	private String cliente;
	
	@NotEmpty
	private String emailCliente;
	
}
