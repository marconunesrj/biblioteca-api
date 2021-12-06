package br.com.xmrtecnologia.bibliotecaapi.api.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RetornadoEmprestimoDTO {
	
	@NotNull
	private Boolean retornado;

}
