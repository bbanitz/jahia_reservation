package org.jahia.modules.userregistration.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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


public class ReservationAction extends BaseAction {

  
	private static BitMatrix generateMatrix(String data, int size) {
        BitMatrix bitMatrix;
		try {
			bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
			return bitMatrix;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    private static ByteArrayOutputStream writeImage(String imageFormat, BitMatrix bitMatrix)  {
        
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, out);
			
			out.close();
			return out;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
 }

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		// TODO Auto-generated method stub
		final Logger logger = LoggerFactory.getLogger(ReservationAction.class);
		final JCRPublicationService publication=JCRPublicationService.getInstance();
		logger.info("canonical path :"+renderContext.getSite().getCanonicalPath());
		JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
            
        	@Override
            public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
                String path = getParameter(parameters, "path");
                String action = getParameter(parameters, "action");
                String cc="webmestre@larbre-bischheim.org";
                JCRNodeWrapper node = session.getNode(path);
                if (action.equals("delete")) node.remove();
                if (action.equals("paye")) {
                	node.getProperty("paye").setValue(true);
                	final JCRNodeWrapper resourceNode = resource.getNode();
                    String data = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() +
		                    Jahia.getContextPath() + Render.getRenderServletPath() +"/default/fr"+resourceNode.getPath()+".enregistreBillet.do?uuid="+node.getIdentifier();
                    logger.info("++++++++++++DATA++++++++++:"+data);
                    int size = 400;
                    BitMatrix bitMatrix = generateMatrix(data, size);
                    String imageFormat = "png";
                    //String outputFileName = "c:/code/qrcode-01." + imageFormat;
                    ByteArrayOutputStream out=writeImage(imageFormat, bitMatrix); 
                    JCRNodeWrapper qrcodeFolder = session.getNode("/sites/LARBRE/files/Images/qrcode");
                    JCRNodeWrapper qrcode=qrcodeFolder.uploadFile(node.getIdentifier(),new ByteArrayInputStream(out.toByteArray()), "png");
                    session.save();
                    publication.publishByMainId(qrcodeFolder.getIdentifier());
                    String qrcodePath=qrcode.getAbsoluteUrl(req).replaceFirst("default","live");
                    String placesStr=(node.getProperty("places").getDouble()>1)?" places":" place";
					String html="<body>Bonjour "+node.getPropertyAsString("nom")+" "+node.getPropertyAsString("prenom")+
							"<br/>Email:"+node.getPropertyAsString("email")+
							"<br/>Nous avons bien reçu votre règlement pour "+node.getPropertyAsString("places")+placesStr+"<br/>"+
							"Le présent mail vous servira de billet.<br/> Veuillez le présenter à l'entrée de la salle de concert.<br/>"+
							"le qr code (l'image ci dessous) doit être apparent<br/><br/>"+
							"Ce billet ne peut servir qu'une seule fois ne le transmettez donc pas à d'autres personnes<br/>"+
							"Toutes les personnes doivent se présenter en même temps à l'entrée du spectacle.<br/>"+
							"Nous vous remercions de votre compréhension et vous souhaitons un bon concert."+
							"<br><img src=\""+qrcodePath+"\"/></body>";
					mailService.sendHtmlMessage("reservations@larbre-bischheim.org",node.getProperty("email").getString(), cc, null,"concert sheerdoor", html);
                }
                logger.info("action:"+action);
                session.save();
        		return null;
        	}
       });	
	 return new ActionResult(HttpServletResponse.SC_OK,getParameter(parameters, "pagePath"));
	}

}
