package br.com.sigen.jdaf.business.security.entity;

import java.io.Serializable;

public class Endereco implements Serializable {

	private String numero;
	private String logradouro;

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
}
