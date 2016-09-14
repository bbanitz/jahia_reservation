<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib"%>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib"%>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search"%>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="javascript"
	resources="jquery.min.js,jquery.validate.js" />

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$("#form1")
									.validate(
											{
												rules : {
													nom : "required",
													prenom : "required",
                                                    adresse : "required",
                                                    codePostal : "required",
                                                    ville : "required",
                                                    telephone :"required",  
                                                    places : "required",
                                                    email : {required:true,email:true}
                                                    
												},
												messages : {
													prenom : "<fmt:message key='uneReservation.label.prenomManquant'/>",
													nom : "<fmt:message key='uneReservation.label.nomManquant'/>",
													adresse : "<fmt:message key='uneReservation.label.adresseManquante'/>",
													codePostal : "<fmt:message key='uneReservation.label.codePostalManquant'/>",
													ville : "<fmt:message key='uneReservation.label.villeManquante'/>",
                                                    telephone : "<fmt:message key='uneReservation.label.telephoneManquant'/>", 
													email : "<fmt:message key='uneReservation.label.emailManquant'/>",
                                                    places : "<fmt:message key='uneReservation.label.placesManquant'/>"
												}
											});
						});
	</script>
	<div class="Form">
		<template:tokenizedForm>
		    ${url.base}${currentNode.path}.newReservation.do
			<form id="form1" method="post"
				action="<c:url value='${url.base}${currentNode.path}.newReservation.do'/>"
				name="newReservation" id="newReservation">
				<input type="hidden" name="userredirectpage"
					value="${currentNode.properties['userRedirectPage'].node.path}" />
				<c:if test="${not empty currentNode.properties['from']}">
					<input type="hidden" name="from"
						value="${currentNode.properties['from'].string}" />
				</c:if>
				<c:if test="${not empty currentNode.properties['to']}">
					<input type="hidden" name="to"
						value="${currentNode.properties['to'].string}" />
				</c:if>
				<c:if test="${not empty currentNode.properties['cc']}">
					<input type="hidden" name="cc"
						value="${currentNode.properties['cc'].string}" />
				</c:if>
				<c:if test="${not empty currentNode.properties['bcc']}">
					<input type="hidden" name="bcc"
						value="${currentNode.properties['bcc'].string}" />
				</c:if>
				<input type="hidden" name="toAdministrator"
					value="${currentNode.properties['toAdministrator'].string}" />

				<fieldset>
					<p>
						<label class="left" for="nom"><fmt:message
								key="uneReservation.label.nom" /></label> <input type="text" name="nom"
							id="nom" value="" />
					</p>


					<p>
						<label class="left" for="prenom"><fmt:message
								key="uneReservation.label.prenom" /></label><input name="prenom"
							id="prenom" />
					</p>

					<p>
						<label class="left" for="adresse"><fmt:message
								key="uneReservation.label.adresse" /></label><input name="adresse"
							id="adresse" />
					</p>
					<p>
						<label class="left" for="codePostal"><fmt:message
								key="uneReservation.label.codePostal" /></label><input
							name="codePostal" id="codePostal" />
					</p>
					<p>
						<label class="left" for="ville"><fmt:message
								key="uneReservation.label.ville" /></label><input name="ville"
							id="ville" />
					</p>

					<p>
						<label class="left" for="telephone"><fmt:message
								key="uneReservation.label.telephone" /></label><input name="telephone"
							id="telephone" />
					</p>
					<p>
						<label class="left" for="email"><fmt:message
								key="uneReservation.label.email" /></label><input name="email"
							id="email" />
					</p>
					<p>
						<label class="left" for="places"><fmt:message
								key="uneReservation.label.places" /></label><input name="places"
							id="places" />
					</p>

					<input type="submit" class="button"
						value="<fmt:message key='userregistration.label.form.create'/>" />

				</fieldset>
			</form>
		</template:tokenizedForm>
	</div>

