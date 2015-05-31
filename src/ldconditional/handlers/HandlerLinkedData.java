package ldconditional.handlers;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
/**
 *
 * @author vroddon
 */
public class HandlerLinkedData {
    static final Logger logger = Logger.getLogger(HandlerLinkedData.class);


    public void serveLinkeddata(Request baseRequest, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            ConditionalDatasets.setSelectedDataset(dataset);
            String comment = cd.getDatasetVoid().getComment();
            body = FileUtils.readFileToString(new File("./htdocs/template_linkeddata.html"));
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            body = body.replace("<!--TEMPLATEFOOTER-->", footer);
            body = body.replace("<!--TEMPLATEDATASETCOMMENT-->", comment);

//            String mainres = cd.getDatasetVoid().getHTMLMainResources();
//            String mainres = getHTMLResources(cd);
            
            
 //           body = body.replace("<!--TEMPLATEMAINRESOURCES-->", mainres);


            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            logger.error("Error serving index.html " + e.getMessage());
        }
    }

    private String getHTMLResources(ConditionalDataset cd) {
        String html="";
        List<String> ls = cd.getDatasetIndex().getIndexedSujetos();      
        for(String s : ls)
        {
            html += "<a href=\"" + s + "\">" + s + "</a>";
        }
        return html;
    }
}
