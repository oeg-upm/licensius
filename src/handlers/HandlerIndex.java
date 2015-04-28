package handlers;

import handlers.HandlerIndex;
import java.io.File;
import javax.servlet.http.HttpServletResponse;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import model.DatasetVoid;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class HandlerIndex {

    static final Logger logger = Logger.getLogger(HandlerIndex.class);

    public void serveIndex(Request baseRequest, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            System.out.println("Se van a cargar los datasets ");
            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            System.out.println("Se han cargado los datasets ");
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
            String img = "<img src=\"../datasets/" + cd.name + "/logo.png\" style=\"vertical-align:middle;width:200px;float:left;margin:8px;\"/>";
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

}
