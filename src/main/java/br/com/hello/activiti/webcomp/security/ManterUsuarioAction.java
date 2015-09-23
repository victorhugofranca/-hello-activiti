package br.com.hello.activiti.webcomp.security;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.hello.activiti.business.security.business.ManterUsuario;
import br.com.sigen.jdaf.business.security.entity.Usuario;

@Named("manterUsuarioAction")
@ConversationScoped
public class ManterUsuarioAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private ManterUsuario usuarioService;

	@Inject
	private Conversation conversation;

	private Usuario selectedUser;

	private LazyDataModel<Usuario> users;

	public ManterUsuarioAction() {
	}

	@PostConstruct
	public void init() {
		this.users = new LazyDataModel<Usuario>() {
			@Override
			public List<Usuario> load(int pageIndex, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				try {
					users.setRowCount(usuarioService.count());
					return usuarioService.load(pageIndex, pageSize);
				} catch (Exception ex) {
					String errorMessage = "Erro ao recuperar lista de usuários ";
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									errorMessage, errorMessage));
					return null;
				}
			}
		};

	}

	public String edit() {
		usuarioService.update(selectedUser);
		return "list";
	}

	public void beginConversation() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	protected Logger getLogger(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class for logger is required.");
		}
		return Logger.getLogger(clazz.getName());
	}

	public LazyDataModel<Usuario> getUsers() {
		return users;
	}

	public void setUsers(LazyDataModel<Usuario> users) {
		this.users = users;
	}

	public Usuario getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(Usuario selectedUser) {
		this.selectedUser = selectedUser;
	}
}
