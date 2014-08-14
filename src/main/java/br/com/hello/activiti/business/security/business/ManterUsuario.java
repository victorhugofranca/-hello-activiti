package br.com.hello.activiti.business.security.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.sigen.jdaf.business.security.entity.Usuario;

@Stateless
public class ManterUsuario {

	@PersistenceContext
	private EntityManager entityManager;

	public List<Usuario> load(int pageIndex, int pageSize)
			throws InvalidParameterException {

		if (pageIndex < 0)
			throw new InvalidParameterException();

		if (pageSize <= 1)
			throw new InvalidParameterException();

		return entityManager
				.createQuery(
						"select usuario from Usuario usuario order by usuario.idUsuario asc",
						Usuario.class).setFirstResult(pageIndex)
				.setMaxResults(pageSize).getResultList();
	}

	public Integer count() {
		Long count = (Long) entityManager.createQuery(
				"select count(usuario) from Usuario usuario").getSingleResult();
		return Integer.valueOf(count.toString());
	}

	public void update(Usuario usuario) {
		entityManager.merge(usuario);
	}

}
