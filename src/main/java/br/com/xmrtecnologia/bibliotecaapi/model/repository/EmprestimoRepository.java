package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Livro;

public interface EmprestimoRepository  extends JpaRepository<Emprestimo, Long> {

	@Query(value = " select case when ( count(e.id) > 0 ) "
			+ "then true else false end "
			+ "from Emprestimo e where e.livro = :livro and "
			+ "( e.retornado is null or e.retornado is false ) ")
	//"select from Emprestimo where livro :livro and retornado is not true"
	boolean existsByLivroAndNotRetornado(@Param("livro") Livro livro);

}
