package br.com.hello.activiti.business.bpm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

@Startup
@Singleton
public class BpmEngineService {

	private ProcessEngine processEngine;

	@PostConstruct
	private void init() {
		processEngine = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(
						"activiti.cfg.xml").buildProcessEngine();
	}

	// ***********************************************************************************
	// Historic methods
	// ***********************************************************************************

	public List<HistoricVariableInstance> loadVariableHistoryByTaskId(
			String taskId) {
		return processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery().taskId(taskId).list();
	}

	public List<HistoricTaskInstance> loadTasksHistoryByProcessInstanceId(
			String processInstanceId) {
		return processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).list();
	}

	// ***********************************************************************************
	// Diagram Methods
	// ***********************************************************************************

	public InputStream loadDiagramByProcDefAndProcInst(
			String processDefinitionId, String processInstanceId) {

		BpmnModel bpmnModel = processEngine.getRepositoryService()
				.getBpmnModel(processDefinitionId);

		List<HistoricActivityInstance> historicActivities = processEngine
				.getHistoryService().createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).list();
		List<String> activitiesIds = new ArrayList<String>();
		for (Iterator<HistoricActivityInstance> iterator = historicActivities
				.iterator(); iterator.hasNext();) {
			HistoricActivityInstance historicActivityInstance = iterator.next();
			activitiesIds.add(historicActivityInstance.getActivityId());
		}

		return ProcessDiagramGenerator.generateDiagram(bpmnModel, "png",
				activitiesIds);
	}

	// ***********************************************************************************
	// Process Definitions Methods
	// ***********************************************************************************

	public List<ProcessDefinition> loadProcessDefinitions() {
		return processEngine.getRepositoryService()
				.createProcessDefinitionQuery().list();
	}

	// ***********************************************************************************
	// Deployment Methods
	// ***********************************************************************************

	public void deleteDeployment(String deploymentId) {
		processEngine.getRepositoryService().deleteDeployment(deploymentId);
	}

	public void deploy(ZipInputStream zipInputStream) {
		processEngine.getRepositoryService().createDeployment()
				.addZipInputStream(zipInputStream).deploy();
	}

	// ***********************************************************************************
	// Process Instance Methods
	// ***********************************************************************************

	public void deleteProcessInstance(String processInstanceId, String reason) {
		processEngine.getRuntimeService().deleteProcessInstance(
				processInstanceId, reason);
	}

	public ProcessInstance startProcessInstance(String processDefId) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("employeeName", "kermit");
		variables.put("numberOfDays", Integer.valueOf(4));
		variables.put("vacationMotivation", "I'm really tired!");

		RuntimeService runtimeService = processEngine.getRuntimeService();
		return runtimeService.startProcessInstanceById(processDefId, String
				.valueOf(Math.random()).replace("0.", ""), variables);
	}

	public List<Execution> getProcessInstancesByActivityId(String activityId) {
		return processEngine.getRuntimeService().createExecutionQuery()
				.activityId(activityId).list();
	}

	public List<Execution> getProcessInstancesByProcessDefinitionId(
			String processDefinitionId) {
		return processEngine.getRuntimeService().createExecutionQuery()
				.processDefinitionId(processDefinitionId).list();
	}

	public List<Map<ActivityImpl, Long>> getProcessInstantcesGroupedByTaskDefinitions() {

		List<Map<ActivityImpl, Long>> tasksCounterList = new ArrayList<>();

		List<ProcessDefinition> processDefinitions = processEngine
				.getRepositoryService().createProcessDefinitionQuery().active()
				.list();

		for (Iterator<ProcessDefinition> iterator = processDefinitions
				.iterator(); iterator.hasNext();) {

			ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine
					.getRepositoryService())
					.getDeployedProcessDefinition(((ProcessDefinition) iterator
							.next()).getId());

			if (processDefinition != null) {
				tasksCounterList.addAll(getTasksCounterList(processDefinition));
			}
		}

		return tasksCounterList;

	}

	// ***********************************************************************************
	// Tasks Methods
	// ***********************************************************************************

	private List<Map<ActivityImpl, Long>> getTasksCounterList(
			ProcessDefinitionEntity processDefinition) {

		List<Map<ActivityImpl, Long>> tasksCounterList = new ArrayList<>();

		for (ActivityImpl activity : processDefinition.getActivities()) {
			String type = (String) activity.getProperty("type");
			if (type.equals("userTask")) {
				long tasksCounter = processEngine.getRuntimeService()
						.createExecutionQuery().activityId(activity.getId())
						.count();

				Map<ActivityImpl, Long> taskCounterMap = new LinkedHashMap<>();
				taskCounterMap.put(activity, tasksCounter);
				tasksCounterList.add(taskCounterMap);
			}
		}
		return tasksCounterList;
	}

	public void endTask(String processInstanceId, String taskDefId,
			List<FormProperty> formProperties) {
		String taskId = processEngine.getTaskService().createTaskQuery()
				.processInstanceId(processInstanceId)
				.taskDefinitionKey(taskDefId).list().get(0).getId();

		Map<String, Object> variables = new LinkedHashMap<>();

		for (Iterator<FormProperty> iterator = formProperties.iterator(); iterator
				.hasNext();) {
			FormProperty formProperty = iterator.next();
			variables.put(formProperty.getId(), formProperty.getValue());
		}

		TaskService taskService = processEngine.getTaskService();

		taskService.setVariablesLocal(taskId, variables);
		taskService.complete(taskId, variables);
	}

	// ***********************************************************************************
	// Forms Methods
	// ***********************************************************************************

	public List<FormProperty> getFormProperties(String taskDefId) {
		String taskId = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(taskDefId).list().get(0).getId();

		TaskFormData taskFormData = processEngine.getFormService()
				.getTaskFormData(taskId);

		return taskFormData.getFormProperties();
	}

	public static void main(String[] args) {
		ProcessEngine processEngine = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(
						"activiti.cfg.xml").buildProcessEngine();

		RepositoryService repositoryService = processEngine
				.getRepositoryService();
		repositoryService.createDeployment()
				.addClasspathResource("VacationRequest.bpmn20.xml").deploy();

		System.out.println("Number of process definitions: "
				+ repositoryService.createProcessDefinitionQuery().count());

	}

}
