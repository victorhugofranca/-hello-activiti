package br.com.hello.activiti.webcomp.bpm;

import java.io.InputStream;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.hello.activiti.business.bpm.BpmEngineService;

@Named("processImageAction")
@SessionScoped
public class ProcessImageAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private BpmEngineService processService;

	private StreamedContent image;

	public ProcessImageAction() {

	}

	public void getActiveActivitiesDiagram(String processDefinitionId,
			String processInstanceId) {
		InputStream inputStream = processService
				.loadDiagramByProcDefAndProcInst(processDefinitionId,
						processInstanceId);
		image = new DefaultStreamedContent(inputStream, "image/png");
	}

	public StreamedContent getImage() {
		return image;
	}

	public void setImage(StreamedContent image) {
		this.image = image;
	}

}
