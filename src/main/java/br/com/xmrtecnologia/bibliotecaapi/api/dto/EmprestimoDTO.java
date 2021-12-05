package br.com.xmrtecnologia.bibliotecaapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoDTO {

	private String isbn;
	private String cliente;
	
}
