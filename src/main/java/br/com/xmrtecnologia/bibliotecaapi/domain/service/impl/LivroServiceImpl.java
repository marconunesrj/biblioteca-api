package br.com.xmrtecnologia.bibliotecaapi.domain.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.xmrtecnologia.bibliotecaapi.domain.service.LivroService;
import br.com.xmrtecnologia.bibliotecaapi.exception.BusinessException;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;
import br.com.xmrtecnologia.bibliotecaapi.model.repository.LivroRepository;

@Service
public class LivroServiceImpl implements LivroService{

	private LivroRepository livroRepository;

	public LivroServiceImpl(LivroRepository livroRepository) {
		this.livroRepository = livroRepository;
	}
	 
	@Override
	public Livro save(Livro livro) {
		if ( livroRepository.existsByIsbn(livro.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		return livroRepository.save(livro);
	}

	@Override
	public Optional<Livro> getById(Long id) {		
		return livroRepository.findById(id);
	}

	@Override
	public void excluir(Livro livro) {
		if(livro == null || livro.getId() == null ) {
			throw new IllegalArgumentException("Livro Id não pode ser nulo.");
		}
		this.livroRepository.delete(livro);
		
	}

	@Override
	public Livro atualizar(Livro livro) {
		if(livro == null || livro.getId() == null ) {
			throw new IllegalArgumentException("Livro Id não pode ser nulo.");
		}
		
		return this.livroRepository.save(livro);
	}

}
