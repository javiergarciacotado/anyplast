<%@ include file="/html/portlet/users_admin/init.jsp" %>

<%
Contact selContact = (Contact)request.getAttribute("user.selContact");
final long userId = PortalUtil.getUserId(renderRequest);
%>

<h3><liferay-ui:message key="account.company.details" /></h3>

<aui:fieldset column="<%=true%>">
	<aui:col width="<%=50%>">
		<!-- companyName -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyName"/>
		 		
		<!-- companyCIF -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyCIF" />
				
		<!-- companyAddress -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyAddress" />
		 		
		<!-- companyAddressNumber -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyAddressNumber" />
				
		<!-- companyCP -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyCP" />
		 	
		<!-- companyContactNamePhone -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyContactNamePhone" /> 
	</aui:col>
	<aui:col width="<%=50%>">
		<!-- companyContactNameRole -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyContactNameRole" />
		
		<!-- companyCountry -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyCountrySelect" />
		
		<!-- companyProvince -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyProvinceSelect" />
		
		<!-- companyWeb -->
		<liferay-ui:custom-attribute className="<%= User.class.getName() %>" classPK="<%= userId %>" editable="<%= true %>" label="<%= true %>" name="companyWeb" />
	</aui:col>
</aui:fieldset>	