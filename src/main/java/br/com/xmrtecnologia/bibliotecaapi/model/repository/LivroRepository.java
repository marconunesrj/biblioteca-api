package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

 	boolean existsByIsbn(String isbn);

	Optional<Livro> findByIsbn(String isbn);

}
