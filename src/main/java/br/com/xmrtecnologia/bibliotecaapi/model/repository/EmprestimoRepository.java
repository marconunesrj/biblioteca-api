package br.com.xmrtecnologia.bibliotecaapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// Sempre que extender JpaRepository e colocar o parâmetro Pageable,
	//  o método retornará uma página (Page)
	@Query ( value = " select e from Emprestimo as e join e.livro as l where "
			+ "l.isbn = :isbn or e.cliente = :cliente ")
	Page<Emprestimo> findByLivroIsbnOrCliente(
			@Param("isbn") String isbn, 
			@Param("cliente") String cliente, 
			Pageable pageRequest);

	Page<Emprestimo> findByLivro(Livro livro, Pageable pageable);

	@Query( value = " select e from Emprestimo e where e.dataEmprestimo <= :dataAtraso and "
						+ "( e.retornado is null or e.retornado is false ) " )
	List<Emprestimo> findByDataEmprestimoLessThanAndRetornadoFalse(@Param("dataAtraso") LocalDate dataAtraso);

}
