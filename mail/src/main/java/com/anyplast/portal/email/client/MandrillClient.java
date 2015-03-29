package com.anyplast.portal.email.client;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.anyplast.portal.email.MandrillConfiguration;
import com.anyplast.portal.email.template.MessageTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MandrillClient {
	
	private MandrillConfiguration mandrillConfiguration;
	
	public MandrillClient(MandrillConfiguration mandrillConfiguration) {
		this.mandrillConfiguration = mandrillConfiguration;
	}
	
	public Client createClient() {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		return client;
	}

	public void sendMessageTemplate(MessageTemplate messageTemplateCall) throws Exception {
		
		final String url = mandrillConfiguration.get("url") + MessageTemplate.URI;
		final Client client = createClient();
		
		WebTarget target = client.target(url);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", mandrillConfiguration.get("apiKey"));
		map.put("template_name", messageTemplateCall.getTemplateName());
		map.put("template_content", messageTemplateCall.getTemplateContent());
		map.put("message", messageTemplateCall.getMessage());
		
		send(target, map);
		
	}
	
	public void send(WebTarget target, Map<String, Object> map) throws Exception {
		
		final ObjectMapper mapper = new ObjectMapper(); 
		
		Response response = target.request().post(
				Entity.entity(mapper.writeValueAsString(map), MediaType.APPLICATION_JSON));
		
		if (response.getStatus() != 200) 
			throw new Exception("AP - " + MandrillClient.class + " - Exception not treated - There was an error sending the email! - send_status: " + response.getStatus());
		
	}
	
	public MandrillConfiguration getMandrillConfiguration() {
		return mandrillConfiguration;
	}

	public void setMandrillConfiguration(MandrillConfiguration mandrillConfiguration) {
		this.mandrillConfiguration = mandrillConfiguration;
	}
	
}
