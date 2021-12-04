package br.com.xmrtecnologia.bibliotecaapi.domain.service;

import java.util.Optional;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

public interface LivroService {

	Livro save(Livro livro);

	Optional<Livro> getById(Long id);

	void excluir(Livro livro);

	Livro atualizar(Livro livro);
	 
}
