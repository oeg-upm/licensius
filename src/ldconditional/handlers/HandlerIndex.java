package ldconditional.handlers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import ldconditional.handlers.HandlerIndex;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import ldconditional.LDRConfig;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.model.DatasetVoid;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

//import java.util.Base64;
//import java.util.Base64.Encoder;


/**
 * THIS CLASS SHOULD BE DEPRECATED!!!
 * @author vroddon
 */
public class HandlerIndex {

    static final Logger logger = Logger.getLogger(HandlerIndex.class);

    public void serveIndex(Request baseRequest, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            logger.info("Se van a cargar los datasets ");
            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            logger.info("Se han cargado los datasets ");
            DatasetVoid dv = cd.getDatasetVoid();
            if (dv==null)
                return;
            String comment = dv.getComment();
            if (comment==null) 
                comment = "";
            System.out.println("Comentario: "+ comment);
            body = FileUtils.readFileToString(new File("./htdocs/template_index.html"));
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            body = body.replace("<!--TEMPLATEFOOTER-->", footer);
            body = body.replace("<!--TEMPLATEDATASETCOMMENT-->", comment);
//            String img = "<img src=\"../datasets/" + cd.name + "/logo.png\" style=\"vertical-align:middle;width:200px;float:left;margin:8px;\"/>";
 
            String img="";
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + cd.name + "/logo.png";
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(new File(filename));
                String churro = getImage64(bi);
                img = "<img style=\"vertical-align:middle;width:200px;float:left;margin:8px;\" src=\"data:image/png;base64," + churro + "\">";
            } catch (IOException e) {}            
            body = body.replace("<!--TEMPLATEDATASETIMAGE-->", img);
        } catch (Exception e) {
            logger.warn("Error serving index.html " + e.getMessage());
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=utf-8");
        
        try{
        response.getWriter().print(body);}catch(Exception e){logger.warn("Error escribiendo en el writer");}
        baseRequest.setHandled(true);
    }

    public static String getImage64(final BufferedImage img) {
        String encodedImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            baos.flush();
         //   encodedImage = Base64.getEncoder().encodeToString(baos.toByteArray());
            encodedImage = DatatypeConverter.printBase64Binary(baos.toByteArray());
            baos.close();
            encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return encodedImage;
    }        
}
