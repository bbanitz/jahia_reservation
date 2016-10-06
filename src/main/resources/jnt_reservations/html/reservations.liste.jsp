<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>


<jcr:sql var="reservations"
    sql="select * from [jnt:uneReservation]"/>
<table class="table-bordered table-striped">
  <tbody>
    <tr>
      <th>Email</th><th>Nom</th><th>Prénom</th><th>adresse</th><th>code postal</th><th>ville</th><th>téléphone</th><th>places</th><th>etat</th>
    </tr>  
<c:forEach items="${reservations.nodes}" var="uneReservation" varStatus="status">
  <tr>
    <td>
      ${uneReservation.properties.email.string}
    </td>  
    <td>
      ${uneReservation.properties.nom.string}
    </td>  
    <td>
      ${uneReservation.properties.prenom.string}
    </td>  
    <td>
      ${uneReservation.properties.adresse.string}
    </td>  
    <td>
      ${uneReservation.properties.codePostal.string}
    </td>  
    <td>
      ${uneReservation.properties.ville.string}
    </td>  
    <td>
      ${uneReservation.properties.telephone.string}
    </td>  
    <td>
      ${uneReservation.properties.places.string}
    </td>  
    <td>
      <c:if test="${uneReservation.properties.confirmation.boolean}">
             confirmé &nbsp;
        
      </c:if>            
      <c:if test="${uneReservation.properties.paye.boolean}">
             Payé
      </c:if>            
    </td>  

    <td>
      <form action="${url.base}${currentNode.path}.reservationAction.do">
         <input type="hidden" name="action" value="delete"/>
         <input type="hidden" name="path" value="${uneReservation.path}"/>
         <input type="hidden" name="pagePath" value="${currentNode.parent.parent.path}"/>
         <input class="button" type="submit" value="effacer" onclick="if(window.confirm('Voulez-vous vraiment supprimer ?')){return true;}else{return false;}"/>
      </form>
    </td>
    <td>
      <form action="${url.base}${currentNode.path}.reservationAction.do">
         <input type="hidden" name="action" value="paye"/>
         <input type="hidden" name="path" value="${uneReservation.path}"/>
         <input type="hidden" name="pagePath" value="${currentNode.parent.parent.path}"/>
         <input class="button" type="submit" value="Payé" onclick="if(window.confirm('Confirmation du paiement ?')){return true;}else{return false;}"/>
      </form>
    </td>
  </tr> 
</c:forEach></ul>
</tbody>
</table>
