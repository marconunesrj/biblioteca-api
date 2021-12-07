package br.com.xmrtecnologia.bibliotecaapi.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.xmrtecnologia.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import br.com.xmrtecnologia.bibliotecaapi.model.entity.Emprestimo;

public interface EmprestimoService {

	Emprestimo salvar(Emprestimo emprestimoAserSalvo);

	boolean validarRetornado(Emprestimo emprestimoAserSalvo);

	Optional<Emprestimo> getById(Long id);

	Emprestimo atualizar(Emprestimo emprestimo);

	Page<Emprestimo> listar(EmprestimoFiltroDTO filtroDTO, Pageable pageRequest);

}
