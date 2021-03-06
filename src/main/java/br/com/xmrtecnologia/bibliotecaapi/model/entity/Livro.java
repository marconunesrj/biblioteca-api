package br.com.xmrtecnologia.bibliotecaapi.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Livro { 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String titulo;
	
	private String autor;
	
	private String isbn;
	
	@OneToMany(mappedBy = "livro", fetch = FetchType.LAZY )
	private List<Emprestimo> emprestimos;
	

}
