<%@ include file="/html/portlet/users_admin/init.jsp" %>

<portlet:actionURL var="editUserActionURL">
	<portlet:param name="struts_action" value="/users_admin/edit_user" />
</portlet:actionURL>

<h2><liferay-ui:message key="account.delete.title" /></h2>
<h3><liferay-ui:message key="account.delete.description" /></h3>
<p><liferay-ui:message key="account.delete.summary" /></p>
<aui:form action="<%= editUserActionURL %>" method="post" name="fm">
	<aui:input name="<%=Constants.DELETE%>" type="hidden" value="<%=Constants.DELETE%>" />
	<aui:button-row>
		<aui:button name="yes" type="submit" value="yes">
		</aui:button>
		<aui:button name="no" type="submit" value="no">
		</aui:button>
	</aui:button-row>
</aui:form>
