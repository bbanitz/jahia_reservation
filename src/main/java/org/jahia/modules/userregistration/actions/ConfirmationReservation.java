package org.jahia.modules.userregistration.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jahia.bin.ActionResult;
import org.jahia.bin.Jahia;
import org.jahia.bin.Render;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmationReservation extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(NewUser.class);
	@Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, final Resource resource,
            JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		// TODO Auto-generated method stub
        JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
            
        	@Override
            public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
            	
                if (mailService.isEnabled()) {
                    // Prepare mail to be sent :
                    boolean toAdministratorMail = Boolean.valueOf(getParameter(parameters, "toAdministrator", "false"));
                    String to = toAdministratorMail ? mailService.getSettings().getTo():getParameter(parameters, "to");
                    String from = parameters.get("from")==null?mailService.getSettings().getFrom():getParameter(parameters, "from");
                    String cc = parameters.get("cc")==null?null:getParameter(parameters, "cc");
                    String bcc = parameters.get("bcc")==null?null:getParameter(parameters, "bcc");
                    String email = getParameter(parameters, "email");
                    String key = getParameter(parameters, "key");
                    
                    Map<String,Object> bindings = new HashMap<String,Object>();
                    JCRNodeWrapper node = session.getNode("/sites/LARBRE/contents/reservations/"+email+"/"+key);
					bindings.put("reservation", node);
					/*
		            bindings.put("confirmationlink", requ.getScheme() +"://" + requ.getServerName() + ":" + requ.getServerPort() +
		                    Jahia.getContextPath() + Render.getRenderServletPath() + "/live/"
		                    + node.getLanguage() + node.getPath() + ".confirmationReservation.do?email="+email+"key=add");
                    */
                    try {
                        mailService.sendMessageWithTemplate(templatePath,bindings,to,from,cc,bcc,resource.getLocale(),"Jahia User Registration");
                    } catch (ScriptException e) {
                        logger.error("Error sending e-mail notification for user confirmation", e);
                    }
                }
                
        		logger.info("confirmationReservation");
                return true;
            }
        });
        return new ActionResult(HttpServletResponse.SC_ACCEPTED, parameters.get("userredirectpage").get(0),
				new JSONObject());
		
	}

}
