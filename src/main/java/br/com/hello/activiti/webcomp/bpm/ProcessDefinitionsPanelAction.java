package br.com.hello.activiti.webcomp.bpm;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.hello.activiti.business.bpm.BpmEngineService;

@Named("processDefinitionsPanelAction")
@ConversationScoped
public class ProcessDefinitionsPanelAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Conversation conversation;

	@EJB
	private BpmEngineService processService;

	private List<ProcessDefinition> processDefinitions;

	private List<Execution> processInstances;

	private List<HistoricTaskInstance> historicTaskList;

	private List<HistoricVariableInstance> historicVariableList;

	public ProcessDefinitionsPanelAction() {

	}

	@PostConstruct
	private void init() {
		updateProcessDefinitions();
	}

	public void loadVariableHistory(String taskId) {
		setHistoricVariableList(processService
				.loadVariableHistoryByTaskId(taskId));
	}

	public void loadTaskHistory(String processInstanceId) {
		setHistoricTaskList(processService
				.loadTasksHistoryByProcessInstanceId(processInstanceId));
		historicVariableList = new ArrayList<HistoricVariableInstance>();
	}

	public void loadProcessInstances(String processDefinitionId) {
		setProcessInstances(processService
				.getProcessInstancesByProcessDefinitionId(processDefinitionId));

		historicTaskList = new ArrayList<HistoricTaskInstance>();
		historicVariableList = new ArrayList<HistoricVariableInstance>();
	}

	public void handleFileUpload(FileUploadEvent event) {
		System.out.println("aqui");
		UploadedFile file = event.getFile();
		try {
			processService.deploy(new ZipInputStream(file.getInputstream()));
			updateProcessDefinitions();
			historicTaskList = new ArrayList<HistoricTaskInstance>();
			historicVariableList = new ArrayList<HistoricVariableInstance>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteDeployment(String deploymentId) {
		processService.deleteDeployment(deploymentId);
		updateProcessDefinitions();
		historicTaskList = new ArrayList<HistoricTaskInstance>();
		historicVariableList = new ArrayList<HistoricVariableInstance>();
	}

	public void startProcess(String processDefinitionId) {
		ProcessInstance processInstance = processService
				.startProcessInstance(processDefinitionId);
		loadProcessInstances(processDefinitionId);
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("processStartMessage", new FacesMessage(
				"Processo iniciado: " + processInstance.getBusinessKey()));
		historicTaskList = new ArrayList<HistoricTaskInstance>();
		historicVariableList = new ArrayList<HistoricVariableInstance>();
	}

	private void updateProcessDefinitions() {
		processDefinitions = processService.loadProcessDefinitions();
		processInstances = new ArrayList<>();
		historicTaskList = new ArrayList<>();
		historicVariableList = new ArrayList<HistoricVariableInstance>();
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

	public List<ProcessDefinition> getProcessDefinitions() {
		return processDefinitions;
	}

	public void setProcessDefinitions(List<ProcessDefinition> processDefinitions) {
		this.processDefinitions = processDefinitions;
	}

	public List<Execution> getProcessInstances() {
		return processInstances;
	}

	public void setProcessInstances(List<Execution> processInstances) {
		this.processInstances = processInstances;
	}

	public List<HistoricTaskInstance> getHistoricTaskList() {
		return historicTaskList;
	}

	public void setHistoricTaskList(List<HistoricTaskInstance> historicTaskList) {
		this.historicTaskList = historicTaskList;
	}

	public List<HistoricVariableInstance> getHistoricVariableList() {
		return historicVariableList;
	}

	public void setHistoricVariableList(
			List<HistoricVariableInstance> historicVariableList) {
		this.historicVariableList = historicVariableList;
	}

}
