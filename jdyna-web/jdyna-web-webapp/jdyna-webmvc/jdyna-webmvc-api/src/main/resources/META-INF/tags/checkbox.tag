<%@ attribute name="propertyPath" required="true"%>
<%@ attribute name="label" required="false"%>
<%@ attribute name="labelKey" required="false"%>
<%@ attribute name="help" required="false"%>
<%@ attribute name="helpKey" required="false"%>
<%@ attribute name="option4row" required="false" type="java.lang.Integer" %>
<%@ attribute name="collection" required="true" type="java.util.Collection" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="ajaxValidation" required="false" description="javascript function name to make for validation ajax"%>
<%@ attribute name="validationParams" required="false" type="java.util.Collection" description="parameters of javascript function for ajax validation"%>

<%@ attribute name="onchange" required="false"%>
<%-- eventi js non gestiti 
<%@ attribute name="onclick" required="false"%>
<%@ attribute name="onblur" required="false"%> 

<%@ attribute name="ondblclick" required="false"%>
<%@ attribute name="onkeydown" required="false"%>
<%@ attribute name="onkeyup" required="false"%>
<%@ attribute name="onkeypress" required="false"%>
<%@ attribute name="onfocus" required="false"%>
<%@ attribute name="onhelp" required="false"%>
<%@ attribute name="onselect" required="false"%>
<%@ attribute name="onmouseup" required="false"%>
<%@ attribute name="onmousedown" required="false"%>
<%@ attribute name="onmouseout" required="false"%>
<%@ attribute name="onmousemove" required="false"%>
<%@ attribute name="onmouseover" required="false"%>
--%>

<%@ taglib uri="jdynatags" prefix="dyna"%>
<%@ include file="/META-INF/taglibs4dynatag.jsp"%>

<c:if test="${option4row == null || option4row == 0}">
	<c:set var="option4row" value="5" />
</c:if>

<c:if test="${label != null || labelKey != null}">
	<dyna:label label="${label}" labelKey="${labelKey}" 
		help="${help}" helpKey="${helpKey}" 
		propertyPath="${propertyPath}" required="${required}" />
</c:if>

<%-- FIXME: CODICE COMUNE A TUTTI I TAG... --%>

<c:set var="objectPath" value="${dyna:getObjectPath(propertyPath)}" />

<spring:bind path="${objectPath}">
	<c:set var="object" value="${status.value}" />
	<c:if test="${object == null}">
		<%-- Bind ignores the command object prefix, so simple properties of the command object return null above. --%>
		<c:set var="object" value="${commandObject}" />
		<%-- We depend on the controller adding this to request. --%>
	</c:if>
</spring:bind>

<c:set var="propertyName" value="${dyna:getPropertyName(propertyPath)}" />
<%-- FINE CODICE COMUNE A TUTTI I TAG... --%>

<!-- we need an indexed property -->
<spring:bind path="${propertyPath}">
	<c:set var="values" value="${status.value}" />
	<c:set var="inputName"><c:out value="${status.expression}" escapeXml="false"></c:out></c:set>		
</spring:bind>

		<spring:bind path="${propertyPath}">
			<input id="_${status.expression}" name="_${status.expression}" value="true" type="hidden" />
		</spring:bind>		


<c:forEach var="option" items="${collection}" varStatus="optionStatus">
	<c:set var="checked" value="false" />
	<spring:bind path="${propertyPath}[0].object">
			<spring:transform value="${option}" var="optionToCompare" />
	</spring:bind>
	<c:forEach var="value" items="${values}" varStatus="valueStatus">
		<spring:bind path="${propertyPath}[${valueStatus.count-1}].object">
			<c:set var="currValue" value="${status.value}" />
			<c:if test="${optionToCompare == currValue}">
				<c:set var="checked" value="true" />	
			</c:if>
		</spring:bind>		
	</c:forEach>
	
	<c:set var="parametersValidation" value="${dyna:extractParameters(validationParams)}"/>
	<c:set var="functionValidation" value="${ajaxValidation}('${inputName}',${parametersValidation})" />
	<input id="${inputName}" name="${inputName}" type="checkbox" 
		value="${optionToCompare}" <c:if test="${checked == true}">checked="checked"</c:if> onchange="${functionValidation};${onchange}">${option.displayValue}</input>
	<c:if test="${optionStatus.count mod option4row == 0}">
		<br/>
	</c:if>
</c:forEach>
	<dyna:validation propertyPath="${propertyPath}" />
