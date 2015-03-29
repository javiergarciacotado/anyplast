package com.anyplast.portal.email.template;

public class To {

	private String email; //required
	private String name;
	public String type; 
	
	public To(String email, String name, String type) {
		this.email = email;
		this.name = name;
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
	
}
