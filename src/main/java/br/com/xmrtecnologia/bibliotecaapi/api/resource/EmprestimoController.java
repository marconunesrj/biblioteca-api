package br.com.xmrtecnologia.bibliotecaapi.api.resource;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

@RestController
@RequestMapping("/emprestimos")
//@RequiredArgsConstructor
public class EmprestimoController {

	private EmprestimoService emprestimoService;
	private LivroService livroService;
	@SuppressWarnings("unused")
	private ModelMapper modelMapper;
	
	public EmprestimoController(EmprestimoService emprestimoService, 
			LivroService livroService, ModelMapper modelMapper) {
		// injeção através do Construtor
		this.livroService = livroService;
		this.emprestimoService = emprestimoService;
		this.modelMapper = modelMapper;

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long adicionar( @RequestBody EmprestimoDTO dto) {
		
//		Livro livro = livroService.getLivroByIsbn(dto.getIsbn())
//				  .get();
		Livro livro = livroService.getLivroByIsbn(dto.getIsbn())
									.orElseThrow(() -> 
									    new ResponseStatusException(HttpStatus.BAD_REQUEST, 
									    		"Livro não encontrado para o isbn informado: " + dto.getIsbn()));
		Emprestimo emprestimo = Emprestimo.builder()
									.livro(livro)
									.Cliente(dto.getCliente())
									.dataEmprestimo(LocalDate.now())
									.retornado(false)
									.build();
		
		emprestimo = emprestimoService.save(emprestimo);
		
		return emprestimo.getId();
	}

}
