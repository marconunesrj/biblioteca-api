package br.com.xmrtecnologia.bibliotecaapi.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroDTO {
	
	private Long id; 
	
	@NotEmpty
	private String titulo;

	@NotEmpty
	private String autor;
	
	@NotEmpty
	private String isbn;

}
