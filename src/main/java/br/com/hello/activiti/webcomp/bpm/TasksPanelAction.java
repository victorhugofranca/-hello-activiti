package br.com.hello.activiti.webcomp.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import br.com.hello.activiti.business.bpm.BpmEngineService;

@Named("tasksPanelAction")
@ConversationScoped
public class TasksPanelAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Conversation conversation;

	@EJB
	private BpmEngineService processService;

	private MenuModel tasksMenuModel;

	private List<Execution> processInstances;

	private boolean taskEditionMode;

	private List<FormProperty> formProperties;

	private String taskDefId;
	private String processInstanceId;

	public TasksPanelAction() {

	}

	@PostConstruct
	public void init() {
		loadMenuModel();
		processInstances = new ArrayList<>();
		setTaskEditionMode(false);
	}

	public void loadProcessInstances(String taskId) {
		setProcessInstances(processService
				.getProcessInstancesByActivityId(taskId));
	}

	public void openTask(String processInstanceId, String taskDefId) {
		setProcessInstanceId(processInstanceId);
		setTaskDefId(taskDefId);

		setTaskEditionMode(true);

		List<FormProperty> taskProperties = processService
				.getFormProperties(taskDefId);

		formProperties = new ArrayList<FormProperty>();
		formProperties.addAll(taskProperties);
	}

	public String endTask() {

		processService.endTask(getProcessInstanceId(), getTaskDefId(),
				formProperties);

		loadProcessInstances(getTaskDefId());
		loadMenuModel();
		setTaskDefId(null);
		setProcessInstanceId(null);
		formProperties.clear();
		setTaskEditionMode(false);
		return "/index.faces";
	}

	private void loadMenuModel() {
		tasksMenuModel = new DefaultMenuModel();

		DefaultSubMenu submenu = new DefaultSubMenu();
		submenu.setLabel("Tarefas");

		List<Map<ActivityImpl, Long>> tasks = processService
				.getProcessInstantcesGroupedByTaskDefinitions();

		for (Iterator<Map<ActivityImpl, Long>> iterator = tasks.iterator(); iterator
				.hasNext();) {
			Map<ActivityImpl, Long> taskGroup = (Map<ActivityImpl, Long>) iterator
					.next();

			DefaultMenuItem menuItem = new DefaultMenuItem();
			ActivityImpl activity = taskGroup.keySet().iterator().next();
			Long taskCount = taskGroup.values().iterator().next();
			menuItem.setValue(activity.getProperty("name") + "(" + taskCount
					+ ")");
			menuItem.setCommand(String.format(
					"#{tasksPanelAction.loadProcessInstances('%s')}",
					activity.getId()));
			menuItem.setAjax(false);
			submenu.addElement(menuItem);
		}

		tasksMenuModel.addElement(submenu);
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

	public MenuModel getTasksMenuModel() {
		return tasksMenuModel;
	}

	public void setTasksMenuModel(MenuModel tasksMenuModel) {
		this.tasksMenuModel = tasksMenuModel;
	}

	public List<Execution> getProcessInstances() {
		return processInstances;
	}

	public void setProcessInstances(List<Execution> processInstances) {
		this.processInstances = processInstances;
	}

	public boolean isTaskEditionMode() {
		return taskEditionMode;
	}

	public void setTaskEditionMode(boolean taskEditionMode) {
		this.taskEditionMode = taskEditionMode;
	}

	public List<FormProperty> getFormProperties() {
		return formProperties;
	}

	public void setFormProperties(List<FormProperty> formProperties) {
		this.formProperties = formProperties;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

}
