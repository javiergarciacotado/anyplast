package com.anyplast.portal.login.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyplast.portal.email.MandrillConfiguration;
import com.anyplast.portal.email.client.MandrillClient;
import com.anyplast.portal.email.template.Merge;
import com.anyplast.portal.email.template.Message;
import com.anyplast.portal.email.template.MessageTemplate;
import com.anyplast.portal.email.template.TemplateContent;
import com.anyplast.portal.email.template.To;
import com.anyplast.portal.email.template.TypeEnum;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.UserActiveException;
import com.liferay.portal.UserLockoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;

public class CustomForgotPasswordAction extends BaseStrutsPortletAction {

	private static Logger LOG = Logger.getLogger(CustomForgotPasswordAction.class);

	@Override
	public String render(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		return originalStrutsPortletAction.render(portletConfig, renderRequest, renderResponse);
	}

	@Override
	/*
	 * Implement way: sendPassword --> throw Exception --> SessionErrors.add
	 * 							   --> not thrown --> sendRedirect to Login 
	 */

	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		final ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		try
		{
			sendPasswordEmail(actionRequest, themeDisplay.getCompanyId());
			sendRedirect(actionRequest, actionResponse, themeDisplay.getPlid());
		}
		catch(Exception ex) {
			if (ex instanceof UserActiveException ||
					ex instanceof UserLockoutException ||
					ex instanceof NoSuchUserException ||
					ex instanceof PortletModeException ||
					ex instanceof WindowStateException ||
					ex instanceof IOException)
				SessionErrors.add(actionRequest, ex.getClass(), ex);
			else {
				if (LOG.isDebugEnabled()) LOG.error("AP - " + CustomForgotPasswordAction.class + " - Exception not treated - " + ex.getMessage());
				ex.printStackTrace();
			}
		}

	}

	private void sendRedirect(ActionRequest actionRequest, ActionResponse actionResponse, long plid) throws PortletModeException, WindowStateException, IOException {

		//URL after login to redirect
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
		
		PortletURL portletURL = PortletURLFactoryUtil.create(request, PortletKeys.LOGIN, plid, PortletRequest.RENDER_PHASE);

		portletURL.setParameter("saveLastPath", Boolean.FALSE.toString());
		portletURL.setParameter("struts_action", "/login/login");
		portletURL.setPortletMode(PortletMode.VIEW);
		portletURL.setWindowState(getWindowState(request));
		
		actionResponse.sendRedirect(portletURL.toString());
	}
	
	private WindowState getWindowState(HttpServletRequest request) {
		
		WindowState windowState = WindowState.MAXIMIZED;
		String windowStateString = ParamUtil.getString(request, "windowState");

		if (windowStateString != null && !windowStateString.isEmpty())
			windowState = WindowStateFactory.getWindowState(windowStateString);
		
		return windowState;
	}

	private void sendPasswordEmail(ActionRequest actionRequest, long companyId) throws Exception {

		User user = getUser(actionRequest, companyId);
		
		if (user != null) {
			MandrillConfiguration mandrillConfiguration = new MandrillConfiguration();
			MandrillClient mandrillClient = new MandrillClient(mandrillConfiguration);
			Locale locale = actionRequest.getLocale();
			ResourceBundle rb = ResourceBundle.getBundle("content.Language", locale);
			
			//email config
			String fromName = mandrillConfiguration.get("fromName"); 
			String fromEmail = mandrillConfiguration.get("fromEmail");
			String emailAddress = user.getEmailAddress();
			String fullName = user.getFullName();
			String password = user.getPassword();
			String subject = rb.getString("email.subject.forgot.password");
			String templateName = "forgot-password-" + locale.getLanguage(); 

			MessageTemplate messageTemplateCall = generateMessage(templateName, subject, fromEmail, fromName, emailAddress, fullName, password);
			try {
				mandrillClient.sendMessageTemplate(messageTemplateCall);
			}
			catch(Exception ex) {
				if (LOG.isDebugEnabled()) LOG.error("AP - " + CustomForgotPasswordAction.class + " - Exception sending message - " + ex.getMessage());
			}
		}
		else throw new NoSuchUserException();

	}

	private MessageTemplate generateMessage(String templateName, String subject, 
			String fromEmail, String fromName, String toEmail, String toFullName, String password) {
		
		List<To> toList = new ArrayList<To>();
		toList.add(new To(toEmail, toFullName, TypeEnum.TO.getValue()));
		List<Merge> mergeList = new ArrayList<Merge>();
		mergeList.add(new Merge("userName",toEmail));
		mergeList.add(new Merge("password",password));
				
		Message message = new Message(subject, fromEmail, fromName, toList, true, mergeList);
		List<TemplateContent> templateContent = null;
		MessageTemplate messageTemplate = new MessageTemplate(templateName, templateContent, message);
		
		return messageTemplate;
	}

	private User getUser(ActionRequest actionRequest, long companyId) throws PortalException, SystemException {
	
		String emailAddress = ParamUtil.getString(actionRequest, "emailAddress");
		User user = null;
		if (emailAddress != null && !emailAddress.isEmpty()) {
			user = UserLocalServiceUtil.getUserByEmailAddress(companyId, emailAddress);
			if (!user.isActive()) {
				throw new UserActiveException();
			}

			if (user.isLockout()) {
				throw new UserLockoutException();
			}
		}
		return user;


	}






}
