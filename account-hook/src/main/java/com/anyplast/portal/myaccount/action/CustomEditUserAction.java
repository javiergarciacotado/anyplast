package com.anyplast.portal.myaccount.action;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyplast.portal.validation.Validador;
import com.anyplast.portal.validation.impl.CIFValidator;
import com.anyplast.portal.validation.impl.CPValidator;
import com.anyplast.portal.validation.impl.PhoneValidator;
import com.anyplast.portal.validation.impl.StringValidator;
import com.anyplast.portal.validation.impl.WebAdressValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

public class CustomEditUserAction extends BaseStrutsPortletAction {

	private static Logger LOG = Logger.getLogger(CustomEditUserAction.class);
	
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
	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		try {
			final ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
			final String toDelete = ParamUtil.getString(actionRequest, Constants.DELETE);
			final User user = themeDisplay.getUser();
			
			if (Constants.DELETE.equals(toDelete)) {
				deleteUser(user);
				redirectToLogin(actionRequest, actionResponse, themeDisplay);
				return;
			}
			updateExpandoUser(actionRequest, actionResponse, themeDisplay.getCompanyId(), user.getUserId());
		}
		catch(PortalException | SystemException ex) {
			if (errorList != null && errorList.size() > 0) {
				for(String error : errorList)
					SessionErrors.add(actionRequest, error);
			}
			else {
				SessionErrors.add(actionRequest, ex.getClass(), ex);
			}
		}
		
		originalStrutsPortletAction.processAction(portletConfig, actionRequest, actionResponse);

	}
	
	@Override
	public String render(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		try
		{
			getExpandoValues(renderRequest);
		}
		catch (SystemException sex) {
			if (LOG.isDebugEnabled()) 
				LOG.error("AP - " + CustomEditUserAction.class + " - Exception not treated - " + sex.getMessage());
		}
		
		return originalStrutsPortletAction.render(portletConfig, renderRequest, renderResponse);
	}

	

	private void deleteUser(User user) throws PortalException, SystemException {
		
		UserLocalServiceUtil.deleteUser(user);
		deleteExpandoUser(user.getCompanyId(),user.getUserId());
		
	}
	
	private void deleteExpandoUser(long companyId, long userId) throws PortalException, SystemException {
		
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYNAME, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCIF, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESS, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESSNUMBER, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCP, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEPHONE, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEROLE, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCOUNTRY, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYPROVINCE, userId);
		ExpandoValueLocalServiceUtil.deleteValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYWEB, userId);
	}



	private void redirectToLogin(ActionRequest actionRequest, ActionResponse actionResponse,
			ThemeDisplay themeDisplay) throws IOException {
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
		request.getSession().invalidate();
		SessionMessages.add(request, "deleted-user");
		actionResponse.sendRedirect(themeDisplay.getURLSignIn());
		
	}

	private void getExpandoValues(RenderRequest renderRequest) throws SystemException {
		
		final long userId = PortalUtil.getUserId(renderRequest);
		final long companyId = PortalUtil.getCompanyId(renderRequest);
		
		ExpandoValue companyValueName = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYNAME, userId);
		ExpandoValue companyValueCIF = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCIF, userId);
		ExpandoValue companyValueAddress = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESS, userId);
		ExpandoValue companyValueAddressNumber = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESSNUMBER, userId);
		ExpandoValue companyValueCP = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCP, userId);
		ExpandoValue companyValueContactNamePhone = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEPHONE, userId);
		ExpandoValue companyValueContactNameRole = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEROLE, userId);
		ExpandoValue companyValueCountry = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCOUNTRY, userId);
		ExpandoValue companyValueProvince = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYPROVINCE, userId);
		ExpandoValue companyValueWeb = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYWEB, userId);
		
		renderRequest.setAttribute(COMPANYNAME, companyValueName);
		renderRequest.setAttribute(COMPANYCIF, companyValueCIF);
		renderRequest.setAttribute(COMPANYADDRESS, companyValueAddress);
		renderRequest.setAttribute(COMPANYADDRESSNUMBER, companyValueAddressNumber);
		renderRequest.setAttribute(COMPANYCP, companyValueCP);
		renderRequest.setAttribute(COMPANYCONTACTNAMEPHONE, companyValueContactNamePhone);
		renderRequest.setAttribute(COMPANYCONTACTNAMEROLE, companyValueContactNameRole);
		renderRequest.setAttribute(COMPANYCOUNTRYSELECT, companyValueCountry);
		renderRequest.setAttribute(COMPANYPROVINCESELECT, companyValueProvince);
		renderRequest.setAttribute(COMPANYWEB, companyValueWeb);
		
	}

	private void updateExpandoUser(ActionRequest actionRequest, ActionResponse actionResponse, long companyId, long userId) throws SystemException, PortalException {

		errorList = new LinkedList<String>();
		
		final String newCompanyName = ParamUtil.getString(actionRequest, "ExpandoAttribute--"+ COMPANYNAME + "--");
		final String newCompanyCIF = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCIF + "--");
		final String newCompanyAddress = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYADDRESS +"--");
		final String newCompanyAddressNumber = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYADDRESSNUMBER + "--");
		final String newCompanyCP = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCP + "--");
		final String newCompanyContactNamePhone = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCONTACTNAMEPHONE +"--");
		final String newCompanyContactNameRole = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCONTACTNAMEROLE + "--");
		final String newCompanyCountry = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYCOUNTRYSELECT + "--");
		final String newCompanyProvince = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYPROVINCESELECT + "--");
		final String newCompanyWeb = ParamUtil.getString(actionRequest, "ExpandoAttribute--" + COMPANYWEB + "--");

		if (!valideExpandoData(newCompanyName, newCompanyCIF, newCompanyAddress, newCompanyAddressNumber,
				newCompanyCP, newCompanyContactNamePhone, newCompanyContactNameRole, newCompanyCountry, newCompanyCountry, newCompanyProvince, newCompanyWeb))
			throw new PortalException();
		
		ExpandoValue companyValueName = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYNAME, userId);
		if (companyValueName != null) {
			companyValueName.setString(newCompanyName);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueName);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYNAME, userId, newCompanyName);
			
		
		ExpandoValue companyValueCIF = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCIF, userId);
		if (companyValueCIF != null) {
			companyValueCIF.setString(newCompanyCIF);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueCIF);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCIF, userId, newCompanyCIF);
		
		ExpandoValue companyValueAddress = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESS, userId);
		if (companyValueAddress != null) {
			companyValueAddress.setString(newCompanyAddress);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueAddress);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESS, userId, newCompanyAddress);
		
		ExpandoValue companyValueAddressNumber = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESSNUMBER, userId);
		if (companyValueAddressNumber != null) {
			companyValueAddressNumber.setString(newCompanyAddressNumber);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueAddressNumber);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYADDRESSNUMBER, userId, newCompanyAddressNumber);
			
		ExpandoValue companyValueCP = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCP, userId);
		if (companyValueCP != null) {
			companyValueCP.setString(newCompanyCP);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueCP);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCP, userId, newCompanyCP);
		
		ExpandoValue companyValueContactNamePhone = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEPHONE, userId);
		if (companyValueContactNamePhone != null) {
			companyValueContactNamePhone.setString(newCompanyContactNamePhone);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueContactNamePhone);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEPHONE, userId, newCompanyContactNamePhone);
			
		ExpandoValue companyValueContactNameRole = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEROLE, userId);
		if (companyValueContactNameRole != null) {
			companyValueContactNameRole.setString(newCompanyContactNameRole);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueContactNameRole);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCONTACTNAMEROLE, userId, newCompanyContactNameRole);
			
		ExpandoValue companyValueCountry = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCOUNTRY, userId);
		if (companyValueCountry != null) {
			companyValueCountry.setString(newCompanyCountry);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueCountry);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYCOUNTRY, userId, newCompanyCountry);
			
		ExpandoValue companyValueProvince = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYPROVINCE, userId);
		if (companyValueProvince != null) {
			companyValueProvince.setString(newCompanyProvince);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueProvince);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYPROVINCE, userId, newCompanyProvince);
		
		ExpandoValue companyValueWeb = ExpandoValueLocalServiceUtil.getValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYWEB, userId);
		if (companyValueWeb != null) {
			companyValueWeb.setString(newCompanyWeb);
			ExpandoValueLocalServiceUtil.updateExpandoValue(companyValueWeb);
		}
		else ExpandoValueLocalServiceUtil.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, COMPANYWEB, userId, newCompanyWeb);
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

}
