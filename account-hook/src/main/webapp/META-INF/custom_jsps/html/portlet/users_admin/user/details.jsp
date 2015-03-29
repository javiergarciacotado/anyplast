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

<%@ include file="/html/portlet/users_admin/init.jsp" %>

<%
User selUser = (User)request.getAttribute("user.selUser");
Contact selContact = (Contact)request.getAttribute("user.selContact");
%>

<liferay-ui:error-marker key="errorSection" value="details" />

<aui:model-context bean="<%= selUser %>" model="<%= User.class %>" />

<h3><liferay-ui:message key="details" /></h3>

<div class="row-fluid">
	<aui:fieldset cssClass="span6">
		<liferay-ui:error exception="<%= DuplicateUserScreenNameException.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= GroupFriendlyURLException.class %>" focusField="screenName">

			<%
			GroupFriendlyURLException gfurle = (GroupFriendlyURLException)errorException;
			%>

			<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DUPLICATE %>">
				<liferay-ui:message key="the-screen-name-you-requested-is-associated-with-an-existing-friendly-url" />
			</c:if>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= ReservedUserScreenNameException.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved" />

		<liferay-ui:error exception="<%= UserFieldException.class %>">

			<%
			UserFieldException ufe = (UserFieldException)errorException;

			StringBundler sb = new StringBundler();

			List<String> fields = ufe.getFields();

			for (int i = 0; i < fields.size(); i++) {
				String field = fields.get(i);

				sb.append(LanguageUtil.get(pageContext, TextFormatter.format(field, TextFormatter.K)));

				if ((i + 1) < fields.size()) {
					sb.append(StringPool.COMMA_AND_SPACE);
				}
			}
			%>

			<liferay-ui:message arguments="<%= sb.toString() %>" key="your-portal-administrator-has-disabled-the-ability-to-modify-the-following-fields" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= UserScreenNameException.class %>" focusField="screenName" message="please-enter-a-valid-screen-name" />

		<aui:input disabled="<%= true %>" name="screenName" />
		
		<liferay-ui:error exception="<%= DuplicateUserEmailAddressException.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= ReservedUserEmailAddressException.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= UserEmailAddressException.class %>" focusField="emailAddress" message="please-enter-a-valid-email-address" />

		<%
			User displayEmailAddressUser = null;
			if (selUser != null) {
				displayEmailAddressUser = (User)selUser.clone();
				displayEmailAddressUser.setEmailAddress(displayEmailAddressUser.getDisplayEmailAddress());
			}
		%>

		<aui:input bean="<%= displayEmailAddressUser %>" model="<%= User.class %>" name="emailAddress">
			<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) %>">
				<aui:validator name="required" />
			</c:if>
		</aui:input>

		<%@ include file="details_user_name.jspf" %>
	</aui:fieldset>

	<aui:fieldset cssClass="span5">
		<div>
			<c:if test="<%= selUser != null %>">
				<c:choose>
					<c:when test='<%= UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, "portrait") %>'>
						<portlet:renderURL var="editUserPortraitURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
							<portlet:param name="struts_action" value="/users_admin/edit_user_portrait" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="p_u_i_d" value="<%= String.valueOf(selUser.getUserId()) %>" />
							<portlet:param name="portrait_id" value="<%= String.valueOf(selUser.getPortraitId()) %>" />
						</portlet:renderURL>

						<liferay-ui:logo-selector
							currentLogoURL="<%= selUser.getPortraitURL(themeDisplay) %>"
							defaultLogoURL="<%= UserConstants.getPortraitURL(themeDisplay.getPathImage(), selUser.isMale(), 0) %>"
							editLogoURL="<%= editUserPortraitURL %>"
							imageId="<%= selUser.getPortraitId() %>"
							logoDisplaySelector=".user-logo"
						/>
					</c:when>
					<c:otherwise>
						<img src="<%= selUser.getPortraitURL(themeDisplay) %>" />
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>

		<c:if test="<%= selUser != null %>">
			<liferay-ui:error exception="<%= DuplicateUserIdException.class %>" message="the-user-id-you-requested-is-already-taken" />
			<liferay-ui:error exception="<%= ReservedUserIdException.class %>" message="the-user-id-you-requested-is-reserved" />
			<liferay-ui:error exception="<%= UserIdException.class %>" message="please-enter-a-valid-user-id" />

			<aui:field-wrapper name="userId">
				<liferay-ui:input-resource url="<%= String.valueOf(selUser.getUserId()) %>" />

				<aui:input name="userId" type="hidden" value="<%= selUser.getUserId() %>" />
			</aui:field-wrapper>
		</c:if>
	</aui:fieldset>
</div>