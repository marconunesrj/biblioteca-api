package br.com.xmrtecnologia.bibliotecaapi.domain.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import br.com.xmrtecnologia.bibliotecaapi.model.repository.EmprestimoRepository;

@Service
public class EmprestimoServiceImpl implements EmprestimoService {

	private EmprestimoRepository emprestimoRepository;
	//private ModelMapper modelMapper;

	public EmprestimoServiceImpl(EmprestimoRepository emprestimoRepository) {
		this.emprestimoRepository = emprestimoRepository;
	}

//	public EmprestimoServiceImpl(EmprestimoRepository emprestimoRepository, ModelMapper modelMapper) {
//		this.emprestimoRepository = emprestimoRepository;
//		this.modelMapper = modelMapper;
//	}

	@Override
	public Emprestimo salvar(Emprestimo emprestimoAserSalvo) {

		// Query do Spring
		if (emprestimoRepository.existsByLivroAndNotRetornado(emprestimoAserSalvo.getLivro())) {
			throw new BusinessException("Livro está emprestado.");
		}
		// ou 
//		validarRetornado(emprestimoAserSalvo);
		
		return emprestimoRepository.save(emprestimoAserSalvo);
	}
	
	@Override
	public boolean validarRetornado (Emprestimo emprestimoAserSalvo) {

		if (emprestimoAserSalvo.getRetornado() != null && emprestimoAserSalvo.getRetornado() == true ) {
			return true;
		} else { 
			throw new BusinessException("Livro está emprestado.");
		}

	}

	@Override
	public Optional<Emprestimo> getById(Long id) {
		
		return emprestimoRepository.findById(id);
	}

	@Override
	public Emprestimo atualizar(Emprestimo emprestimo) {

		return emprestimoRepository.save(emprestimo);
	}

	@Override
	public Page<Emprestimo> listar(EmprestimoFiltroDTO filtroDTO, Pageable pageRequest) {
		
		return emprestimoRepository.findByLivroIsbnOrCliente(filtroDTO.getIsbn(),
				filtroDTO.getCliente(), pageRequest);
	}

	@Override
	public Page<Emprestimo> getEmprestimosPorLivro(Livro livro, Pageable pageable) {
		
		return emprestimoRepository.findByLivro(livro, pageable);
	}

	@Override
	public List<Emprestimo> getAllEmprestimosAtrasados() {
		// Qtd de dias de atraso
		final Integer diasEmprestimo = 4;
		// 3 Dias atrasado
		LocalDate dataAtraso = LocalDate.now().minusDays(diasEmprestimo); 
		
		
		
		return emprestimoRepository.findByDataEmprestimoLessThanAndRetornadoFalse(dataAtraso);
	}
	
	// Funciona com o teste listarEmprestimosFiltradosTest onde está comentado
//	public Page<Emprestimo> listar(EmprestimoFiltroDTO filtroDTO, Pageable pageRequest) {
//		Emprestimo filtro = modelMapper.map(filtroDTO, Emprestimo.class);
//		Example<Emprestimo> example = Example.of(filtro,
//				ExampleMatcher.matching()
//					.withIgnoreCase()
//					.withIgnoreNullValues()
//					.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//				);
//		
//		return emprestimoRepository.findAll(example, pageRequest);
//	}
	
}
