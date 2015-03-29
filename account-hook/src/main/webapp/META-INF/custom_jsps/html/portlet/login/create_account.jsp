<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/login/init.jsp"%>

<%
	String redirect = ParamUtil.getString(request, "redirect");
%>

<portlet:actionURL secure="<%=PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS | request.isSecure()%>" var="createAccountURL">
	<portlet:param name="struts_action" value="/login/create_account" />
</portlet:actionURL>

<aui:form action="<%=createAccountURL%>" method="post" name="fm">
	<aui:input name="saveLastPath" type="hidden" value="<%=false%>" />
	<aui:input name="<%=Constants.CMD%>" type="hidden" value="<%=Constants.ADD%>" />
	<aui:input name="redirect" type="hidden" value="<%=redirect%>" />
	
	<!-- Deleted user -->
	<c:if test='<%= SessionMessages.contains(request, "deleted-user") %>'>
		<div class="alert alert-success">
			<liferay-ui:message key="deleted-user" />
		</div>
	</c:if>
	
	<!-- Custom liferay error -->
	<liferay-ui:error key="error.companyCIF" message="error.companyCIF" />
	<liferay-ui:error key="error.companyCP" message="error.companyCP" />
	<liferay-ui:error key="error.companyContactNamePhone" message="error.companyContactNamePhone" />
	<liferay-ui:error key="error.companyContactNameEmail" message="error.companyContactNameEmail" />
	<liferay-ui:error key="error.companyName" message="error.companyName" />
	<liferay-ui:error key="error.companyAddress" message="error.companyAddress" />
	<liferay-ui:error key="error.companyAddressNumber" message="error.companyAddressNumber" />
	<liferay-ui:error key="error.companyContactName" message="error.companyContactName" />
	<liferay-ui:error key="error.companyContactNameRole" message="error.companyContactNameRole" />
	<liferay-ui:error key="error.companyCountry" message="error.companyCountry" />
	<liferay-ui:error key="error.companyProvince" message="error.companyProvince" />
	<liferay-ui:error key="error.companyWeb" message="error.companyWeb" />

	<!-- Liferay Exception handler -->
	<liferay-ui:error exception="<%=ContactFirstNameException.class%>" message="please-enter-a-valid-first-name" />
	<liferay-ui:error exception="<%=ContactFullNameException.class%>" message="please-enter-a-valid-first-middle-and-last-name" />
	<liferay-ui:error exception="<%=DuplicateUserEmailAddressException.class%>" message="the-email-address-you-requested-is-already-taken" />
	<liferay-ui:error exception="<%=DuplicateUserIdException.class%>" message="the-user-id-you-requested-is-already-taken" />
	<liferay-ui:error exception="<%=DuplicateUserScreenNameException.class%>" message="the-screen-name-you-requested-is-already-taken" />
	<liferay-ui:error exception="<%=EmailAddressException.class%>" message="please-enter-a-valid-email-address" />
	<liferay-ui:error exception="<%=RequiredFieldException.class%>" message="please-fill-out-all-required-fields" />
	<liferay-ui:error exception="<%=ReservedUserEmailAddressException.class%>" message="the-email-address-you-requested-is-reserved" />
	<liferay-ui:error exception="<%=ReservedUserIdException.class%>" message="the-user-id-you-requested-is-reserved" />
	<liferay-ui:error exception="<%=ReservedUserScreenNameException.class%>" message="the-screen-name-you-requested-is-reserved" />
	<liferay-ui:error exception="<%=UserEmailAddressException.class%>" message="please-enter-a-valid-email-address" />
	<liferay-ui:error exception="<%=UserPasswordException.class%>"> 
	<%
		UserPasswordException upe = (UserPasswordException) errorException;
	%>

		<c:if test="<%=upe.getType() == UserPasswordException.PASSWORD_CONTAINS_TRIVIAL_WORDS%>">
			<liferay-ui:message
				key="that-password-uses-common-words-please-enter-in-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
		</c:if>

		<c:if test="<%=upe.getType() == UserPasswordException.PASSWORD_INVALID%>">
			<liferay-ui:message key="that-password-is-invalid-please-enter-in-a-different-password" />
		</c:if>

		<c:if test="<%=upe.getType() == UserPasswordException.PASSWORD_LENGTH%>">
			<%PasswordPolicy passwordPolicy = PasswordPolicyLocalServiceUtil.getDefaultPasswordPolicy(company.getCompanyId());%>
			<%=LanguageUtil.format(pageContext, "that-password-is-too-short-or-too-long-please-make-sure-your-password-is-between-x-and-512-characters", String.valueOf(passwordPolicy.getMinLength()), false)%>
		</c:if>

		<c:if test="<%=upe.getType() == UserPasswordException.PASSWORD_TOO_TRIVIAL%>">
			<liferay-ui:message key="that-password-is-too-trivial" />
		</c:if>

		<c:if test="<%=upe.getType() == UserPasswordException.PASSWORDS_DO_NOT_MATCH%>">
			<liferay-ui:message key="the-passwords-you-entered-do-not-match-each-other-please-re-enter-your-password" />
		</c:if>
	</liferay-ui:error>

	<aui:model-context model="<%=Contact.class%>" />

	<aui:fieldset column="<%=true%>">
		<aui:col width="<%=50%>">
			<%@ include file="create_account_user_name.jspf"%>

			<aui:input label="password" name="password1" size="30" type="password" value="" />
			<aui:input label="enter-again" name="password2" size="30" type="password" value="">
				<aui:validator name="equalTo">
					'#<portlet:namespace />password1'
				</aui:validator>
			</aui:input>

			<aui:input autoFocus="<%=false%>" model="<%=User.class%>"
				name="emailAddress">
				<c:if test="<%=PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED)%>">
					<aui:validator name="required" />
				</c:if>
			</aui:input>
			
			<!-- companyContactNamePhone -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyContactNamePhone" />

			<!-- companyContactNameRole -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyContactNameRole" />
		</aui:col>

		<aui:col width="<%=50%>">

			<!-- companyName -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyName" />

			<!-- companyCIF -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyCIF" />

			<!-- companyAddress -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyAddress" />

			<!-- companyAddressNumber -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyAddressNumber" />

			<!-- companyCP -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyCP" />

			<!-- companyCountry -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyCountrySelect" />

			<!-- companyProvince -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyProvinceSelect" />

			<!-- companyWeb -->
			<liferay-ui:custom-attribute className="<%=User.class.getName()%>"
				classPK="<%=0%>" editable="<%=true%>" label="<%=true%>"
				name="companyWeb"  />

		</aui:col>
	</aui:fieldset>
<%

	String content = StringPool.BLANK;
	try{
		
		JournalArticleDisplay articleDisplay =  JournalContentUtil.getDisplay(themeDisplay.getScopeGroupId(), "12103" , "", themeDisplay.getLocale().getLanguage() ,themeDisplay);
		content = articleDisplay.getContent();
		content = content.replaceAll("\\r|\\n", "");
		content = content.replace("'", "\\'");
		
	}catch(Exception e){
		content = StringPool.BLANK;
	}

%>

	<!-- Terms of use -->
	<div class="control-group form-inline">
		<input type="checkbox" id="chkTerms" name="chkTerms" class="aui-field-required">
		<aui:a href="#" cssClass="" id="terms-dialog" label="terms-of-use" />
	</div>
		
	<aui:script>
		AUI().use('aui-base','aui-io-plugin-deprecated','liferay-util-window', function(A) {
			A.one('#<portlet:namespace />terms-dialog').on('click', function(event){
			var popUpWindow=Liferay.Util.Window.getWindow(
			{
				dialog: {
					centered: true,
					constrain2view: true,
					//cssClass: 'css',
					modal: true,
					resizable: false,
					width: 700
				}
			}
			).plug( A.Plugin.IO, {autoLoad: false}).render();

			popUpWindow.show();
			popUpWindow.titleNode.html('<liferay-ui:message key="terms-of-use"/>');
			/*
				Don't work: <liferay-ui:journal-article articleId="12103" groupId="<%=themeDisplay.getScopeGroupId() %>" />
				because of JS error \n, <p>, etc.
			*/
			popUpWindow.bodyNode.html('<%=content %>');
			});
		});
	</aui:script>
	
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<liferay-util:include page="/html/portlet/login/navigation.jsp" />