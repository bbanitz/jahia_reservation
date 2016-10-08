package org.jahia.modules.userregistration.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnregistreBillet extends Action {
	private static Logger logger = LoggerFactory.getLogger(EnregistreBillet.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type
	}

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		// TODO Auto-generated method stub
		logger.info("session"+session);
		final String uuid = getParameter(parameters, "uuid");
		renderContext.getResponse().setContentType("text/html");
		final PrintWriter out = renderContext.getResponse().getWriter();
		JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
			@Override
			public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
				JCRNodeWrapper node=session.getNodeByIdentifier(uuid);
				if (node.getProperty("paye").getBoolean()) {
					out.println("<h1 style='color:green'>Payé</h1>");
				}
				else {
					out.println("<h1 style='color:red'>pas payé</h1>");
					
				}
				if (node.getProperty("remiseBillets").getBoolean()) {
					out.println("<h1 style='color:red'>Attention billets déjà remis</h1>");
					
				}
				else {
					node.setProperty("remiseBillets", true);
					session.save();
				}
				out.println("<h1>" +node.getPropertyAsString("nom")+" "+node.getPropertyAsString("prenom")+ "<br/>");
				out.println(node.getPropertyAsString("adresse")+"<br/>");
				out.println(node.getPropertyAsString("codePostal")+" ");
				out.println(node.getPropertyAsString("ville")+"<br/>");
				out.println("téléphone : "+node.getPropertyAsString("telephone")+"<br/>");
				out.println("email : "+node.getPropertyAsString("email")+"<br/>");
				out.println("Nombre de places : "+node.getPropertyAsString("places")+"</h1>");
				return null;
			}});
		
		
		return ActionResult.OK;
	}

}
