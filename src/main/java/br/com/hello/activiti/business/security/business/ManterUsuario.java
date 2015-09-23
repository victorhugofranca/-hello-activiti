package br.com.hello.activiti.business.security.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.sigen.jdaf.business.security.entity.Usuario;


@Stateless
public class ManterUsuario {

//	@Resource(lookup = "java:jboss/infinispan/container/singleton")
//	private CacheContainer container;
//	private org.infinispan.Cache<String, String> cache;

	@PersistenceContext(name = "werp-entity")
	private EntityManager entityManager;

//	@PostConstruct
//	public void start() {
//		this.cache = this.container.getCache();
//	}

	public List<Usuario> load(int pageIndex, int pageSize)
			throws InvalidParameterException {

		if (pageIndex < 0)
			throw new InvalidParameterException();

		if (pageSize <= 1)
			throw new InvalidParameterException();

		List<Usuario> usuarios = entityManager
				.createQuery(
						"select usuario from Usuario usuario order by usuario.idUsuario asc",
						Usuario.class).setFirstResult(pageIndex)
				.setMaxResults(pageSize).getResultList();

//		System.out.println(cache.get("teste"));
//
//		cache.put("teste", "valor teste");
//
		return usuarios;
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
