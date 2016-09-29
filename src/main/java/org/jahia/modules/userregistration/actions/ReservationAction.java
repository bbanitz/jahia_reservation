package org.jahia.modules.userregistration.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
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


public class ReservationAction extends Action{

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		// TODO Auto-generated method stub
		final Logger logger = LoggerFactory.getLogger(ReservationAction.class);   
		JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
            
        	@Override
            public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
                String path = getParameter(parameters, "path");
                String action = getParameter(parameters, "action");
                JCRNodeWrapper node = session.getNode(path);
                if (action.equals("delete")) node.remove();
                if (action.equals("paye")) node.getProperty("paye").setValue(true);
                logger.info("action:"+action);
                session.save();
        		return null;
        	}
       });	
	 return new ActionResult(HttpServletResponse.SC_OK,getParameter(parameters, "pagePath"));
	}

}
