package org.jahia.modules.userregistration.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.bin.ActionResult;
import org.jahia.bin.Jahia;
import org.jahia.bin.Render;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPublicationService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class RechercheAction extends BaseAction {

  

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		// TODO Auto-generated method stub
		final Logger logger = LoggerFactory.getLogger(RechercheAction.class);
        String recherche = getParameter(parameters, "recherche");
        String sql;
        if (recherche==null) sql="select * from [jnt:uneReservation]";
        else sql="select * from [jnt:uneReservation] as s where lower(s.nom) = '"+recherche.toLowerCase()+"'";
        logger.info("SQL="+sql);
        //ActionResult result=new ActionResult(HttpServletResponse.SC_OK,getParameter(parameters, "pagePath"));
        req.getSession().setAttribute("sql", sql);
        return new ActionResult(HttpServletResponse.SC_OK,getParameter(parameters, "pagePath"));
	}

}
