package br.com.hello.activiti.business.bpm;

import org.activiti.engine.form.AbstractFormType;

public class TextEditorFormType extends AbstractFormType {
	public static final String TYPE_NAME = "textEditor";

	public String getName() {
		return TYPE_NAME;
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return (String) modelValue;
	}
}
