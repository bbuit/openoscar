<%@page import="org.apache.commons.lang.time.DateFormatUtils"%>
<%@page import="oscar.util.StringUtils"%>
<%@page import="oscar.oscarReport.data.DemographicSets" %>
<%@page import="org.apache.struts.validator.DynaValidatorForm" %>
<%@page import="java.util.ArrayList, java.util.List" %>
<%@page import="org.oscarehr.common.model.DataExport" %>
<%@include file="/casemgmt/taglibs.jsp"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%
if(session.getAttribute("user") == null) response.sendRedirect("../logout.jsp");
String demographic_no = request.getParameter("demographic_no"); 
String roleName = (String)session.getAttribute("userrole") + "," + (String)session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName%>" objectName="_admin" rights="r" reverse="true">
<%response.sendRedirect("../logout.jsp");%>
</security:oscarSec>

<%
DemographicSets  ds = new DemographicSets();
ArrayList setsList = ds.getDemographicSets();

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CIHI Export</title>
<style type="text/css">

.container {
border: 1px solid black;
margin-top:5px;
}
div.vendor {
font-size: 8px;
width:48%;
margin-top:5px;
}
input.right {
float:right;
}
input.setright {
	position:relative;
	left: 25%;
}
</style>
</head>
<body>
<html:form action="/demographic/cihiExport.do" method="get">
<h3>Vender Information</h3>
<div class="container" style="width:100%;">
<div class="vendor" style="float:right">	
		<html:text styleClass="right" property="vendorBusinessName"></html:text>Vendor Business Name<br clear="right">
		<html:text styleClass="right" readonly="true" property="vendorId"></html:text>Vendor ID<br clear="right">		
		<html:text styleClass="right" readonly="true" property="vendorCommonName"></html:text>Vendor Common Name<br clear="right">
		<html:text styleClass="right" readonly="true" property="vendorSoftware"></html:text>Vendor Software<br clear="right">
		<html:text styleClass="right" readonly="true" property="vendorSoftwareCommonName"></html:text>Vendor Software Common Name<br clear="right">
		<html:text styleClass="right" readonly="true" property="vendorSoftwareVer"></html:text>Vendor Software Ver<br clear="right">
		<html:text styleClass="right" readonly="true" property="installDate"></html:text>Vendor Install Date
</div>
				
<div class="vendor" style="float:left;height:100%;">	
	Organization Name<html:text styleClass="right" property="orgName"></html:text><br clear="right">
	Contact Last Name<html:text styleClass="right" property="contactLName"></html:text><br clear="right">	
	Contact First Name<html:text styleClass="right" property="contactFName"></html:text><br clear="right">	
	Contact Phone<html:text  styleClass="right" property="contactPhone"></html:text><br clear="right">
	Contact Email<html:text styleClass="right" property="contactEmail"></html:text><br clear="right">
	Contact Username<html:text styleClass="right" property="contactUserName"></html:text><br clear="right">
	&nbsp;
</div>
</div>	
<div>
			Extract Type<html:select property="extractType">
				<html:option value="full">Full</html:option>
			</html:select><br>
		
	
	
<html:select property="patientSet">
	<html:option value="-1">--Select Set--</html:option>
<% 
String setName;
for( int idx = 0; idx < setsList.size(); ++idx ) {
	setName = (String)setsList.get(idx);
%>
	<html:option value="<%=setName%>"><%=setName%></html:option>
<%
}
%>
</html:select>
	
<input type="submit" value="Run Report"/>
</div>
<h3>Previous Reports</h3>
<table width="100%">
<tr>
	<th>Run Date</th>
	<th>File</th>
	<th>User</th>
	<th>Type</th>
</tr>
<%
	List<DataExport> dataExportList = (List<DataExport>)request.getAttribute("dataExportList");
	for( int idx = dataExportList.size()-1; idx >= 0; --idx) {
		DataExport dataExport = dataExportList.get(idx);
		String file = dataExport.getFile();
		%>
		<tr>
			<td><%=DateFormatUtils.format(dataExport.getDaterun().getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()) %></td>
			<td><a href='<c:out value="${ctx}/demographic/cihiExport.do"></c:out>?method=getFile&zipFile=<%=file%>'><%=file %></a></td>
			<td><%=dataExport.getUser()%>
			<td><%=dataExport.getType()%></td>
		</tr>
<%
	}
%>
</table>
</html:form>
</body>
</html>