package br.com.xmrtecnologia.bibliotecaapi.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

public interface LivroService {

	Livro save(Livro livro);

	Optional<Livro> getById(Long id);

	void excluir(Livro livro);

	Livro atualizar(Livro livro);

	Page<Livro> listar(Livro filtro, Pageable pageRequest);

	Optional<Livro> getLivroByIsbn(String isbn);
	 
}
