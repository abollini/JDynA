<%@ attribute name="propertyPath" required="true"%>
<%@ attribute name="controllerURL" required="false" description="change method or controller call from ajaxtag autocomplete, use this for specific search"%>
<%@ attribute name="label" required="false"%>
<%@ attribute name="labelKey" required="false"%>
<%@ attribute name="help" required="false"%>
<%@ attribute name="helpKey" required="false"%>
<%@ attribute name="size" required="false" type="java.lang.Integer" %>
<%@ attribute name="target" required="true"%>
<%@ attribute name="filtro" required="false"%>
<%@ attribute name="display" required="true"%>
<%@ attribute name="repeatable" required="false" type="java.lang.Boolean" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="ajaxValidation" required="false" description="javascript function name to make for validation ajax"%>
<%@ attribute name="validationParams" required="false" type="java.util.Collection" description="parameters of javascript function for ajax validation"%>
<%-- eventi js non gestiti 
<%@ attribute name="onclick" required="false"%>
<%@ attribute name="onblur" required="false"%> 
--%>
<%@ attribute name="onchange" required="false"%>
<%-- eventi js non gestiti
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

<spring:bind path="${propertyPath}">
	<c:set var="values" value="${status.value}" />
</spring:bind>

<c:if test="${empty controllerURL}">
	<c:set var="controllerURL" value="ajaxFrontPuntatore.htm"></c:set>
</c:if>		   
<c:catch var="exNoIndexedValue">
<c:forEach var="value" items="${values}" varStatus="iterationStatus">	
	<spring:bind path="${propertyPath}[${iterationStatus.count - 1}]">
		<c:if test="${iterationStatus.count > 1}">
		<br/>
		</c:if>
		<%-- Se sono riuscito a fare il bind allora è una proprietà indicizzata --%>
		<c:set var="inputShowed" value="true" />
		<c:set var="inputValue"><c:out value="${status.value}" escapeXml="true"></c:out></c:set>
		<c:set var="inputName"><c:out value="${status.expression}" escapeXml="false"></c:out></c:set>
		
		<c:set var="parametersValidation" value="${dyna:extractParameters(validationParams)}"/>
		<c:set var="functionValidation" value="" />
		<c:if test="${!empty ajaxValidation}">
			<c:set var="functionValidation" value="${ajaxValidation}('${inputName}'${!empty parametersValidation?',':''}${!empty parametersValidation?parametersValidation:''});" />
		</c:if>
		<c:set var="onchangeJS" value="${functionValidation}disabilitaTextBox('suggestbox${inputName}');${onchange}"/>
					
		<input type="hidden" name="${inputName}" 
				id="${inputName}" value="${inputValue}" />			
		<input name="_${inputName}" id="_${inputName}" value="true" type="hidden" />
		
 		<input type="text" name="suggestbox${inputName}" id="suggestbox${inputName}" size="${size}" 
 				value="${dyna:getDisplayValue(value,display)}" <dyna:javascriptEvents onchange="${onchangeJS}"/>/>
 				
 	  	<input type="hidden" id="display${inputName}" name="display${inputName}" 
 	  			value="${dyna:replaceApiciDoppi(display)}"/>
		
		<c:if test="${not empty inputValue}">
		  	<script type="text/javascript">				  	 
		  		disabilitaTextBox('suggestbox${inputName}');
		  	</script>
		</c:if>
		
		<%-- Svuota il campo hidden e la box--%>
		<input id="button${inputName}" type="button" onclick="document.getElementById('suggestbox${inputName}').readOnly = false;document.getElementById('${inputName}').value='';document.getElementById('suggestbox${inputName}').value='';document.getElementById('suggestbox${inputName}').className = '${cssClass}'" value="X" title="Azzera"/>	   
		<span id="indicator${inputName}" style="display:none;"><img src="<c:url value="/images/indicator.gif" />"/></span>				  
		<span id="message${inputName}"></span>
		

		<ajax:autocomplete
        		baseUrl="${pageContext.request.contextPath}/${controllerURL}"
        		source="suggestbox${inputName}"
        		target="${inputName}"
        		className="autocomplete"
        		indicator="indicator${inputName}"        				
        		minimumCharacters="3"       				        				
		       	parameters="filtro=${filtro},query={suggestbox${inputName}},model=${target},display={display${inputName}}"			        		        	
        		parser="new ResponseXmlToHtmlListParser()">
        </ajax:autocomplete> 				
	</spring:bind>
	
	<c:if test="${repeatable}">
	<c:if test="${iterationStatus.count == 1}">
	<c:set var="dynajs_var" value="_dyna_${dyna:md5(propertyPath)}" />
	<script type="text/javascript">
		var ${dynajs_var} = new DynaPointerInput('${root}','${dynajs_var}','${filtro}','${target}','${dyna:escapeApici(display)}',
									'${fn:replace(propertyPath,'anagraficadto.','')}',${fn:length(values)},
									'${dyna:escapeApici(functionValidation)};${dyna:escapeApici(onchange)}',
									 ${size},'${cssClass}');
	</script>
	</c:if>

	<c:choose>
	<c:when test="${iterationStatus.count == fn:length(values)}">
	<img src="${root}/images/main_plus.gif" class="addButton"
		onclick="${dynajs_var}.create()" />
	</c:when>
	<c:otherwise>
	<img src="${root}/images/icons/delete_icon.gif" class="deleteButton"
		onclick="${dynajs_var}.remove(${iterationStatus.count - 1},this)" />
	</c:otherwise>
	</c:choose>
	</c:if>
	
	<dyna:validation propertyPath="${propertyPath}[${iterationStatus.count - 1}]" />
</c:forEach>
</c:catch>
<c:if test="${!inputShowed}">
	<c:if test="${exNoIndexedValue == null}">
		<c:catch var="exNoIndexedValue">
			<spring:bind path="${propertyPath}[0]">
				<c:set var="inputValue"><c:out value="${status.value}" escapeXml="true"></c:out></c:set>
				<c:set var="inputName"><c:out value="${status.expression}" escapeXml="false"></c:out></c:set>
				<c:set var="parametersValidation" value="${dyna:extractParameters(validationParams)}"/>
				<c:set var="functionValidation" value="" />
				<c:if test="${!empty ajaxValidation}">
					<c:set var="functionValidation" value="${ajaxValidation}('${inputName}'${!empty parametersValidation?',':''}${!empty parametersValidation?parametersValidation:''});" />
				</c:if>
				<c:set var="onchangeJS" value="${functionValidation}disabilitaTextBox('suggestbox${status.expression}');${onchange}"/>			
			</spring:bind>
		</c:catch>
		<c:set var="validation" value="${propertyPath}[0]"/>	
	</c:if>
	<c:if test="${exNoIndexedValue != null}">
		<spring:bind path="${propertyPath}">
			<c:set var="inputValue"><c:out value="${status.value}" escapeXml="true"></c:out></c:set>
			<c:set var="inputName"><c:out value="${status.expression}" escapeXml="false"></c:out></c:set>
			<c:set var="parametersValidation" value="${dyna:extractParameters(validationParams)}"/>
			<c:set var="functionValidation" value="" />
			<c:if test="${!empty ajaxValidation}">
				<c:set var="functionValidation" value="${ajaxValidation}('${inputName}'${!empty parametersValidation?',':''}${!empty parametersValidation?parametersValidation:''});" />
			</c:if>

			<c:set var="onchangeJS" value="${functionValidation}disabilitaTextBox('suggestbox${status.expression}');${onchange}"/>			
		</spring:bind>
		<c:set var="validation" value="${propertyPath}"/>	
	</c:if>

	<c:set var="objectBehindPropertyPath" value="${dyna:getReferencedObject(commandObject,propertyPath)}" />
	<input type="hidden" name="${inputName}" 
		id="${inputName}" value="${inputValue}" />			
	
	<input name="_${inputName}" id="_${inputName}" value="true" type="hidden" />
	
	<input type="text" name="suggestbox${inputName}" id="suggestbox${inputName}" size="${size}"
		value="${dyna:getDisplayValue(objectBehindPropertyPath,display)}" <dyna:javascriptEvents onchange="${onchangeJS}" />/>
				
  	<input type="hidden" id="display${inputName}" name="display${inputName}" 
		value="${dyna:replaceApiciDoppi(display)}"/>
	
	<c:if test="${not empty inputValue}">
	  	<script type="text/javascript">				  	 
	  		disabilitaTextBox('suggestbox${inputName}');
	  	</script>
	</c:if>
	
	<%-- Svuota il campo hidden e la box--%>
	<input id="button${inputName}" type="button" onclick="document.getElementById('suggestbox${inputName}').readOnly = false;document.getElementById('${inputName}').value='';document.getElementById('suggestbox${inputName}').value='';document.getElementById('suggestbox${inputName}').className = '${cssClass}'" value="X" title="Azzera"/>	   
	<span id="indicator${inputName}" style="display:none;"><img src="<c:url value="/images/indicator.gif"/>" alt=""/></span>				  
	<span id="message${inputName}"></span>
			   
	<ajax:autocomplete
       		baseUrl="${pageContext.request.contextPath}/${controllerURL}"
       		source="suggestbox${inputName}"
       		target="${inputName}"
       		className="autocomplete"
       		indicator="indicator${inputName}"        				
       		minimumCharacters="3"       				        				
	       	parameters="filtro=${filtro},query={suggestbox${inputName}},model=${target},display={display${inputName}}"			        		        	
       		parser="new ResponseXmlToHtmlListParser()">
       </ajax:autocomplete>
       
	<c:if test="${repeatable}">
	<c:set var="dynajs_var" value="_dyna_${dyna:md5(propertyPath)}" />
	<script type="text/javascript">
		var ${dynajs_var} = new DynaPointerInput('${root}','${dynajs_var}','${filtro}','${target}','${dyna:escapeApici(display)}',
									'${fn:replace(propertyPath,'anagraficadto.','')}',${fn:length(values)},
									'${dyna:escapeApici(functionValidation)};${dyna:escapeApici(onchange)}',
									 ${size},'${cssClass}');
	</script>

	<img src="${root}/images/main_plus.gif" class="addButton"
		onclick="${dynajs_var}.create()" />
	</c:if>
       
       <dyna:validation propertyPath="${validation}" />
</c:if>
