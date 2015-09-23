package br.com.sigen.jdaf.business.security.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tb_usuario", schema = "security")
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idUsuario;
	private String login;

	private List<Endereco> enderecos;

	@Id
	@Column(name = "id_usuario")
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "ds_login")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
