package com.anyplast.portal.email.template;

public enum TypeEnum {
	TO("to"),CC("cc"), BCC("bcc");

	private String value;
	
	private TypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

};
