package br.com.xmrtecnologia.bibliotecaapi.domain.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.xmrtecnologia.bibliotecaapi.domain.service.EmprestimoService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.repository.EmprestimoRepository;

@Service
public class EmprestimoServiceImpl implements EmprestimoService {

	private EmprestimoRepository emprestimoRepository;

	public EmprestimoServiceImpl(EmprestimoRepository emprestimoRepository) {
		this.emprestimoRepository = emprestimoRepository;
	}

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
	
}
