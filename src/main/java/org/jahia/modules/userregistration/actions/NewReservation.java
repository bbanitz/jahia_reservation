/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.userregistration.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.RepositoryException;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Action handler for creating new user and sending an e-mail notification.
 *
 * @Bernard BANITZ
 */
public class NewReservation extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(NewReservation.class);

	private String generateKey(String email) {
			return DigestUtils.md5Hex(email + System.currentTimeMillis());
	}

	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver)
			throws Exception {
		final HttpServletRequest requ=req;
		final String nom = getParameter(parameters, "nom");
		final String prenom = getParameter(parameters, "prenom");
		final String adresse = getParameter(parameters, "adresse");
		final String codePostal = getParameter(parameters, "codePostal");
		final String ville = getParameter(parameters, "ville");
		final String telephone = getParameter(parameters, "telephone");
		final String email = getParameter(parameters, "email");
		final String places = getParameter(parameters, "places");

		if (StringUtils.isEmpty(nom) || StringUtils.isEmpty(prenom) || StringUtils.isEmpty(adresse)
				|| StringUtils.isEmpty(email)) {
			return ActionResult.BAD_REQUEST;
		}
		/*
		 * final Properties properties = new Properties();
		 * properties.put("j:email",parameters.get("desired_email").get(0));
		 * properties.put("j:firstName",parameters.get("desired_firstname").get(
		 * 0));
		 * properties.put("j:lastName",parameters.get("desired_lastname").get(0)
		 * ); for (Map.Entry<String, List<String>> param :
		 * parameters.entrySet()) { if (param.getKey().startsWith("j:")) {
		 * String value = getParameter(parameters, param.getKey()); if (value !=
		 * null) { properties.put(param.getKey(), value); } } }
		 */
		JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
			@Override
			public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {

				// final JCRUserNode user =
				// userManagerService.createUser(username, password, properties,
				// session);
				// session.save();
				JCRNodeWrapper reservationFolder = session.getNode("/sites/LARBRE/contents/reservations");
				JCRNodeWrapper emailFolder;
				if (reservationFolder.hasNode(email)) emailFolder = reservationFolder.getNode(email);
				else emailFolder = reservationFolder.addNode(email,"jnt:contentFolder");
				session.save();
				String key=generateKey(email);
				JCRNodeWrapper uneReservation = emailFolder.addNode(key, "jnt:uneReservation");
				uneReservation.setProperty("nom", nom);
				uneReservation.setProperty("prenom", prenom);
				uneReservation.setProperty("adresse", adresse);
				uneReservation.setProperty("codePostal", codePostal);
				uneReservation.setProperty("ville", ville);
				uneReservation.setProperty("telephone", telephone);
				uneReservation.setProperty("email", email);
				uneReservation.setProperty("places", places);
				session.save();
				if (mailService.isEnabled()) {
					// Prepare mail to be sent :
					boolean toAdministratorMail = Boolean.valueOf(getParameter(parameters, "toAdministrator", "false"));
					String cc = toAdministratorMail ? mailService.getSettings().getTo()
							: getParameter(parameters, "cc");
					String from = cc;
                    logger.info("send copie to :"+cc);
					//String cc = parameters.get("cc") == null ? null : getParameter(parameters, "cc");
					String bcc = parameters.get("bcc") == null ? null : getParameter(parameters, "bcc");

					Map<String, Object> bindings = new HashMap<String, Object>();
					final JCRNodeWrapper node = resource.getNode();
					logger.info("********Node :" + node.getName());
					logger.info("Template path:" + templatePath);
					bindings.put("reservation",uneReservation);
                    bindings.put("confirmationlink", requ.getScheme() +"://" + requ.getServerName() + ":" + requ.getServerPort() +
		                    Jahia.getContextPath() + Render.getRenderServletPath() + "/live/"
		                    + node.getLanguage() + node.getPath() + ".confirmationReservation.do?email="+email+"&key="+key+"&copie="+cc+"&confirmationPage="+ parameters.get("confirmationReservationPage").get(0));

					try {
						mailService.sendMessageWithTemplate(templatePath, bindings, email, from, cc, bcc,
								resource.getLocale(), "Jahia User Registration");
					} catch (ScriptException e) {
						logger.error("Error sending e-mail notification for user creation", e);
					}
				}

				return true;
			}
		});

		return new ActionResult(HttpServletResponse.SC_ACCEPTED, parameters.get("newReservationPage").get(0),
				new JSONObject());
	}
}
