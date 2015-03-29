package com.anyplast.portal.email.template;

import java.util.List;

public class MessageTemplate {
	
	public static final String URI = "/messages/send-template.json";

	private String templateName;
	private List<TemplateContent> templateContent;
	private Message message;
	
	public MessageTemplate(String templateName, List<TemplateContent> templateContent, Message message) {
		this.templateName = templateName;
		this.templateContent = templateContent;
		this.message = message;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<TemplateContent> getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(List<TemplateContent> templateContent) {
		this.templateContent = templateContent;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	

	
}
