package com.anyplast.portal.email.template;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

	private String subject;
	private String fromEmail;
	private String fromName;
	private List<To> to;
	private boolean merge;
	private List<Merge> globalMergeVars;
	
	public Message() {}

	public Message(String subject, String fromEmail, String fromName, List<To> to, boolean merge, List<Merge> globalMergeVars) {
		this.subject = subject;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.to = to; 
		this.merge = merge;
		this.globalMergeVars = globalMergeVars;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@JsonProperty("from_email")
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	@JsonProperty("from_name")
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public List<To> getTo() {
		return to;
	}

	public void setTo(List<To> to) {
		this.to = to;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	@JsonProperty("global_merge_vars")
	public List<Merge> getGlobalMergeVars() {
		return globalMergeVars;
	}

	public void setGlobalMergeVars(List<Merge> globalMergeVars) {
		this.globalMergeVars = globalMergeVars;
	}

	
}
