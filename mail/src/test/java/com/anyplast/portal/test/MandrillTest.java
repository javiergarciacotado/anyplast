package com.anyplast.portal.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.anyplast.portal.email.MandrillConfiguration;
import com.anyplast.portal.email.client.MandrillClient;
import com.anyplast.portal.email.template.Merge;
import com.anyplast.portal.email.template.Message;
import com.anyplast.portal.email.template.MessageTemplate;
import com.anyplast.portal.email.template.TemplateContent;
import com.anyplast.portal.email.template.To;
import com.anyplast.portal.email.template.TypeEnum;


public class MandrillTest {

//	TO DO: Mock objects when simulate operations in services
//	@Mock public LiferayService liferayService;
//	@Mock public MandrillService mandrillService;
	
	public static final String USERREGISTEREDTEMPLATENAME = "registered-user-en";
	public static final String USERFORGOTPASSTEMPLATENAME = "forgot-password-en";
	
	MandrillClient mandrillClient;
	MandrillConfiguration mandrillConfiguration;
	
	@Before public void setUp() throws IOException {
//		MockitoAnnotations.initMocks(this);
		mandrillConfiguration = new MandrillConfiguration();
		mandrillClient = new MandrillClient(new MandrillConfiguration());
	}
	
//	@Test
//	public void whenMockIsInit() throws Exception {
//		assertNotNull("@Mock could not create Liferay Service", liferayService);
//		assertNotNull("@Mock could not create Liferay Service", mandrillService);
//	}
	
	@Test(expected=Exception.class)
	public void whenParamNotIncludedInvokeMandrillApiThenThrowException() throws Exception {
		mandrillClient.sendMessageTemplate(generateWithoutToMessage("", "", USERREGISTEREDTEMPLATENAME));
	}
	
	@Test public void whenMergeButNoMergeGlobalThenEmailSent() throws Exception {
		mandrillClient.sendMessageTemplate(generateWithoutGlobalMergeMessage("jgarciacotado@gmail.com", "Javi", USERFORGOTPASSTEMPLATENAME));
	}
	
	@Test public void whenParamIncludedInvokeMandrillApiThenEmailSent() throws Exception {
		mandrillClient.sendMessageTemplate(generateMessage("jgarciacotado@gmail.com", "Javi", USERREGISTEREDTEMPLATENAME));
	}
	
	
	private MessageTemplate generateWithoutToMessage(String userEmail, String userName, String templateName) {
		List<To> toList = new ArrayList<To>();
		List<TemplateContent> templateContent = null;
		String subject = "User registered";
		
		//Will cause Exception at send method
		toList.add(new To("","", TypeEnum.TO.getValue()));
				
		Message message = new Message(subject, mandrillConfiguration.get("fromEmail"), mandrillConfiguration.get("fromName"), toList, false, null);
		MessageTemplate messageTemplate = new MessageTemplate(templateName, templateContent, message);
		
		return messageTemplate;
	}
	
	private MessageTemplate generateWithoutGlobalMergeMessage(String userEmail,
			String userName, String templateName) {
		
		List<To> toList = new ArrayList<To>();
		List<TemplateContent> templateContent = null;
		List<Merge> mergeList = new ArrayList<Merge>();
		String subject = "User registered";

		toList.add(new To(userEmail,userName, TypeEnum.TO.getValue()));
				
		Message message = new Message(subject, mandrillConfiguration.get("fromEmail"), mandrillConfiguration.get("fromName"), toList, true, mergeList);
		MessageTemplate messageTemplate = new MessageTemplate(templateName, templateContent, message);
		
		return messageTemplate;
	}

	
	public MessageTemplate generateMessage(String userEmail, String userName, String templateName) {
		
		List<To> toList = new ArrayList<To>();
		List<TemplateContent> templateContent = null;
		List<Merge> mergeList = new ArrayList<Merge>();
		String subject = "User registered";

		toList.add(new To(userEmail,userName, TypeEnum.TO.getValue()));
				
		Message message = new Message(subject, mandrillConfiguration.get("fromEmail"), mandrillConfiguration.get("fromName"), toList, false, mergeList);
		MessageTemplate messageTemplate = new MessageTemplate(templateName, templateContent, message);
		
		return messageTemplate;
	}
}
