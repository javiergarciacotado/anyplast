package com.anyplast.portal.login.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;

import com.anyplast.portal.email.MandrillConfiguration;
import com.anyplast.portal.email.client.MandrillClient;
import com.anyplast.portal.email.template.Message;
import com.anyplast.portal.email.template.MessageTemplate;
import com.anyplast.portal.email.template.TemplateContent;
import com.anyplast.portal.email.template.To;
import com.anyplast.portal.email.template.TypeEnum;
import com.anyplast.portal.validation.Validador;
import com.anyplast.portal.validation.impl.CIFValidator;
import com.anyplast.portal.validation.impl.CPValidator;
import com.anyplast.portal.validation.impl.PhoneValidator;
import com.anyplast.portal.validation.impl.StringValidator;
import com.anyplast.portal.validation.impl.WebAdressValidator;
import com.liferay.portal.ContactFirstNameException;
import com.liferay.portal.ContactFullNameException;
import com.liferay.portal.ContactLastNameException;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserIdException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.EmailAddressException;
import com.liferay.portal.RequiredFieldException;
import com.liferay.portal.ReservedUserEmailAddressException;
import com.liferay.portal.ReservedUserScreenNameException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.UserIdException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.UserScreenNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.NoRedirectActionResponse;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.PortletActionInvoker;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;


public class CustomCreateAccountAction extends BaseStrutsPortletAction { 

	private static Logger LOG = Logger.getLogger(CustomCreateAccountAction.class);
	
	private static final String COMPANYNAME = "companyName";
	private static final String COMPANYCIF = "companyCIF";
	private static final String COMPANYADDRESS = "companyAddress";
	private static final String COMPANYADDRESSNUMBER = "companyAddressNumber";
	private static final String COMPANYCP = "companyCP";
	private static final String COMPANYCONTACTNAMEPHONE = "companyContactNamePhone";
	private static final String COMPANYCONTACTNAMEROLE = "companyContactNameRole";
	private static final String COMPANYCOUNTRY = "companyCountry";
	private static final String COMPANYCOUNTRYSELECT = "companyCountrySelect";
	private static final String COMPANYPROVINCE = "companyProvince";
	private static final String COMPANYPROVINCESELECT = "companyProvinceSelect";
	private static final String COMPANYWEB = "companyWeb";
	private List<String> errorList;

	@Override
	public String render(StrutsPortletAction originalStrutsPortletAction,PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
					throws Exception {
		
		String ret = originalStrutsPortletAction.render(null, portletConfig, renderRequest, renderResponse);
		renderRequest.setAttribute(WebKeys.PORTLET_DECORATE, Boolean.TRUE);
		return ret;

	}

	@Override
	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		/*
		 * First way: addUser --> throw Exception --> Print JSP
		 * 					  --> not throw Exception --> valideExpando --> addExpando(userIdAdded) --> throw Exception --> deleteUserAdded --> Print errors JSP
		 * 																							--> redirect login
		 * 																--> if NOK valideExpando --> Print errors JSP
		 * 
		 * Second way(implemented): valideExpando --> addUser --> throw Exception --> Print JSP
		 * 													  --> not throw Exception --> addExpando(userIdAdded) --> throw Exception --> Print errors JSP
		 * 																										  --> redirect login
		 * 										  --> if NOK valideExpando --> Print errors JSP
		 */
		try {
			final ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
			final String companyName = ParamUtil.getString(actionRequest, "ExpandoAttribute--"+ COMPANYNAME + "--");
			final String companyCIF = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCIF + "--");
			final String companyAddress = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYADDRESS +"--");
			final String companyAddressNumber = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYADDRESSNUMBER + "--");
			final String companyCP = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCP + "--");
			final String companyContactNamePhone = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCONTACTNAMEPHONE +"--");
			final String companyContactNameRole = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCONTACTNAMEROLE + "--");
			final String companyCountry = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCOUNTRYSELECT + "--");
			final String companyProvince = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYPROVINCESELECT + "--");
			final String companyWeb = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYWEB + "--");
			
			Company company = themeDisplay.getCompany();
			long companyId = company.getCompanyId();
			
			if (valideExpandoData(companyName, companyCIF, companyAddress, companyAddressNumber,
					companyCP, companyContactNamePhone, companyContactNameRole, companyCountry, 
					companyCountry, companyProvince, companyWeb)) //valide expando values
				{ 
				final User user = addUser(actionRequest, companyId); //add user 
				addExpandoUser(companyId, user.getUserId(), companyName, companyCIF, companyAddress, companyAddressNumber,
						companyCP, companyContactNamePhone, companyContactNameRole, companyCountry, 
						companyCountry, companyProvince, companyWeb); //add expando values by userId
				sendUserRegisteredEmail(actionRequest, user); //send welcome email
				sendRedirect(actionRequest, actionResponse, portletConfig, user); //redirect logged in
			}
			else {
				if (errorList != null && errorList.size() > 0) {
					for(String error : errorList)
						SessionErrors.add(actionRequest, error);
				}
			}
		}
		catch(Exception ex) {

			if (ex instanceof DuplicateUserEmailAddressException ||
				ex instanceof DuplicateUserIdException ||
				ex instanceof DuplicateUserScreenNameException ||
				ex instanceof ContactFirstNameException ||
				ex instanceof ContactFullNameException ||
				ex instanceof ContactLastNameException ||
				ex instanceof EmailAddressException ||
				ex instanceof RequiredFieldException ||
				ex instanceof ReservedUserEmailAddressException ||
				ex instanceof ReservedUserScreenNameException ||
				ex instanceof UserEmailAddressException ||
				ex instanceof UserIdException ||
				ex instanceof UserPasswordException ||
				ex instanceof UserScreenNameException) {
				
				SessionErrors.add(actionRequest, ex.getClass(), ex);
			} 
			else { 
				if (LOG.isDebugEnabled()) LOG.error("AP - " + CustomCreateAccountAction.class + " - Exception not treated - " + ex.getMessage());
				throw ex;
			}
		
		
		}
	}
	

	private boolean valideExpandoData(String companyName,
			String companyCIF, String companyAddress,
			String companyAddressNumber, String companyCP,
			String companyContactNamePhone,
			String companyContactNameRole, String companyCountry,
			String companyCountry2, String companyProvince,
			String companyWeb) {

		errorList = new LinkedList<String>();
		
		Validador<String> validator = new CIFValidator();
		if (!validator.validate(companyCIF)) errorList.add("error.companyCIF");

		validator = new CPValidator();
		if (!validator.validate(companyCP)) errorList.add("error.companyCP");

		validator = new PhoneValidator();
		if (!validator.validate(companyContactNamePhone)) errorList.add("error.companyContactNamePhone");

		//String validator maybe not make sense --> all fields in JSP are required!
		validator = new StringValidator();
		if (!validator.validate(companyName)) errorList.add("error.companyName");
		if (!validator.validate(companyAddress)) errorList.add("error.companyAddress");

		//address number may be '16bis'
		if (!validator.validate(companyAddressNumber)) errorList.add("error.companyAddressNumber");
		if (!validator.validate(companyContactNameRole)) errorList.add("error.companyContactNameRole");
		if (!validator.validate(companyCountry)) errorList.add("error.companyCountry");
		if (!validator.validate(companyProvince)) errorList.add("error.companyProvince");

		if (companyWeb != null && !companyWeb.isEmpty()) {
			validator = new WebAdressValidator();
			if (!validator.validate(companyWeb)) errorList.add("error.companyWeb");
		}
		
		if (errorList.size() > 0) return false;
		return true;
	}
	
	private User addUser(ActionRequest actionRequest, long companyId) throws PortalException, SystemException  {

		final long creatorUserId = 0;
		final boolean autoPassword = false; //login.create.account.allow.custom.password=true
		final String password1 = ParamUtil.getString(actionRequest, "password1");
		final String password2 = ParamUtil.getString(actionRequest, "password2");
		final boolean autoScreenName = true; //screenname is emailAddress
		final String screenName = null;
		final String emailAddress = ParamUtil.getString(actionRequest, "emailAddress");
		final long facebookId = 0;
		final String openId = null;
		final Locale locale = actionRequest.getLocale();
		final String firstName = ParamUtil.getString(actionRequest, "firstName");
		final String middleName = null;//ParamUtil.getString(actionRequest, "middleName");
		final String lastName = ParamUtil.getString(actionRequest, "lastName");
		int birthdayMonth = 0;//ParamUtil.getInteger(actionRequest, "birthdayMonth");
		int birthdayDay = 1;//ParamUtil.getInteger(actionRequest, "birthdayDay");
		int birthdayYear = 1970;//ParamUtil.getInteger(actionRequest, "birthdayYear");
		int prefixId = 0;
		int suffixId = 0;
		boolean male = true;//ParamUtil.getBoolean(actionRequest, "male", true);
		final String jobTitle = null;

		//MemberShip
		final Group group = GroupLocalServiceUtil.getGroup(companyId, GroupConstants.GUEST);
		final Role role = RoleLocalServiceUtil.getRole(companyId, "BasicRole"); 
	
		long[] groupIds = {group.getGroupId()};
		long[] organizationIds = {};
		long[] roleIds = {role.getRoleId()};
		long[] userGroupIds = {};
		boolean sendEmail = false;
		final ServiceContext serviceContext = ServiceContextFactory.getInstance(User.class.getName(), actionRequest);

		//To decide if addUser --> throwing exception or validate user before inserting 
		return  UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, facebookId, openId, locale, firstName, middleName, lastName, prefixId,
				suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
				roleIds, userGroupIds, sendEmail, serviceContext);
	}

	private void addExpandoUser(long companyId, long userId, String companyName, String companyCIF, String companyAddress,
			String companyAddressNumber, String companyCP, String companyContactNamePhone, String companyContactNameRole, String companyCountry,
			String companyCountry2, String companyProvince, String companyWeb) throws PortalException, SystemException {

		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYNAME, userId, companyName);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCIF, userId, companyCIF);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESS, userId, companyAddress);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESSNUMBER, userId, companyAddressNumber);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCP, userId, companyCP);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEPHONE, userId, companyContactNamePhone);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEROLE, userId, companyContactNameRole);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCOUNTRY, userId, companyCountry);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYPROVINCE, userId, companyProvince);
		ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYWEB, userId, companyWeb);
	}
	
	private void sendRedirect(ActionRequest actionRequest, ActionResponse actionResponse, PortletConfig portletConfig, User user) throws Exception {

		String className = "com.liferay.portlet.login.action.LoginAction";
//		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
						
		NoRedirectActionResponse noRedirectActionResponse = new NoRedirectActionResponse(actionResponse);
		//URL after login to redirect
		PortletActionInvoker.processAction(className, portletConfig, actionRequest, noRedirectActionResponse);
		String redirect = PortalUtil.getPathMain() + "/portal/login?login=" + user.getEmailAddress() + "&password=" + user.getPasswordUnencrypted();
				
		//redirect response to /portal/login
		actionResponse.sendRedirect(redirect);
		
	}
	
	private void sendUserRegisteredEmail(ActionRequest actionRequest, User user) {
		
		MandrillConfiguration mandrillConfiguration = new MandrillConfiguration();
		MandrillClient mandrillClient = new MandrillClient(mandrillConfiguration);
		Locale locale = actionRequest.getLocale();
		ResourceBundle rb = ResourceBundle.getBundle("content.Language", locale);
		
		//email config
		String fromName = mandrillConfiguration.get("fromName"); 
		String fromEmail = mandrillConfiguration.get("fromEmail");
		String emailAddress = user.getEmailAddress();
		String fullName = user.getFullName();
		String subject = rb.getString("email.subject.user.registered");
		String templateName = "registered-user-" + locale.getLanguage();  
			
		MessageTemplate messageTemplateCall = generateMessage(templateName, subject, fromEmail, fromName, emailAddress, fullName);
		try {
			mandrillClient.sendMessageTemplate(messageTemplateCall);
		}
		catch(Exception ex) {
			if (LOG.isDebugEnabled()) LOG.error("AP - " + CustomCreateAccountAction.class + " - Exception sending message - " + ex.getMessage());
		}
	}
	
	private MessageTemplate generateMessage(String templateName, String subject, 
			String fromEmail, String fromName, String toEmail, String toFullName) {
		
		List<To> toList = new ArrayList<To>();
		toList.add(new To(toEmail, toFullName, TypeEnum.TO.getValue()));
		
		//Phase 0: Nothing to merge with email
		Message message = new Message(subject, fromEmail, fromName, toList, false, null);
		List<TemplateContent> templateContent = null;
		MessageTemplate messageTemplateCall = new MessageTemplate(templateName, templateContent, message);
		
		return messageTemplateCall;
	}
}


