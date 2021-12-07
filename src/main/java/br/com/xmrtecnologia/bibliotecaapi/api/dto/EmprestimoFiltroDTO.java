package br.com.xmrtecnologia.bibliotecaapi.api.dto;


import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimoFiltroDTO {

	private Long id;
	
	@NotEmpty
	private String isbn;

	@NotEmpty
	private String cliente;

	@NotEmpty
	private String emailCliente;

	private LivroDTO livro;
	
}
