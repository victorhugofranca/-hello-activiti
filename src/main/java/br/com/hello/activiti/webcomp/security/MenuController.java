package br.com.hello.activiti.webcomp.security;

import static br.com.hello.activiti.webcomp.epp.AttributeLabelConstantes.MENU_ITEM;
import static br.com.hello.activiti.webcomp.epp.AttributeLabelConstantes.SUB_MENU;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Submenu;

@ManagedBean
@SessionScoped
public class MenuController implements Serializable {

	private static final long serialVersionUID = 1L;

	private MenuModel menuModel;

	public MenuController() {
		menuModel = new DefaultMenuModel();

		menuModel.addElement(gerarSubmenuProcessos());
		menuModel.addElement(gerarSubmenuPainel());
	}

	private Submenu gerarSubmenuProcessos() {
		DefaultSubMenu submenuTarefas = createSubMenu(
				MENU_ITEM + "ProcessosId", "Processos");

		DefaultMenuItem miProcessDefinitions = createMenuItem(SUB_MENU
				+ "ProcessosId", "Definições", "/processo_def.faces");
		submenuTarefas.addElement(miProcessDefinitions);

		return submenuTarefas;
	}

	private Submenu gerarSubmenuPainel() {
		DefaultSubMenu submenuTarefas = createSubMenu(SUB_MENU + "PainelId",
				"Painel");

		DefaultMenuItem mi = createMenuItem(MENU_ITEM + "TarefasId",
				"Minhas Tarefas", "/index.faces");

		submenuTarefas.addElement(mi);
		return submenuTarefas;
	}

	public MenuModel getMenuModel() {
		return menuModel;
	}

	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

	private DefaultMenuItem createMenuItem(String id, String value, String url) {
		DefaultMenuItem miProcessDefinitions = new DefaultMenuItem();
		miProcessDefinitions.setId(id);
		miProcessDefinitions.setValue(value);
		miProcessDefinitions.setUrl(url);
		miProcessDefinitions.setIcon(null);
		return miProcessDefinitions;
	}

	private DefaultSubMenu createSubMenu(String id, String label) {
		DefaultSubMenu submenuTarefas = new DefaultSubMenu();
		submenuTarefas.setId(id);
		submenuTarefas.setLabel(label);
		return submenuTarefas;
	}

}
