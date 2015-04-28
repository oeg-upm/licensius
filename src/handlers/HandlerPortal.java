package handlers;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import ldconditional.LDRConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Sirve el macroportal
 * @author vroddon
 */
public class HandlerPortal {
    static final Logger logger = Logger.getLogger(HandlerIndex.class);

    public void serve(HttpServletRequest request, HttpServletResponse response, String sdataset) {
        String body = "";
        try {
            String token = "";
            File f = new File("./htdocs/template_portal.html");
            token = FileUtils.readFileToString(f);
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            token = token.replace("<!--TEMPLATEFOOTER-->", footer);
            
            token = token.replace("<!--TEMPLATEDATASETS-->", getHTMLDatasets(sdataset));

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(token);


        }catch(Exception e)
        {
            logger.warn("Error sirviendo un dataset");
        }
    }

    public static String getHTMLDatasets(String sdat)
    {
        String html="";

        if (sdat==null || sdat.isEmpty())
            sdat="geo";

        for(ConditionalDataset cd : ConditionalDatasets.datasets)
        {
            String row="";
            String name = cd.name;
            String title = "";
            String comment = "";

            if (cd.getDatasetDump()==null)
                continue;

            title = cd.getDatasetVoid().getTitle();
            comment = cd.getDatasetVoid().getComment();

            row+="<tr>";


            /*
            String color= "btn-default";
            String texto="select";
            if (name.equals(sdat))
            {
                color = "btn-info";
                texto = "default";
            }
            row += "<td><a class=\"btn " + color+ " \" href=\"/service/chooseDataset?dataset="+name+"\" role=\"button\">"+texto+"</a></td>";
            */

            row+="<td>";
            row += name;
            row +="</td>";

            row+="<td>";
            row+=""+cd.getDatasetVoid().getGrafos().size();
//            row += "Triples: " + cd.getDatasetVoid().getNumTriples(row) + "<br/>";
//            row += "Datasets: " + cd.getDatasetVoid().getGrafos().size() + "<br/>";
            row +="</td>";

            row+="<td>";
            row += "<img src=\"/img/"+ name +".png\" height=\"128\" style=\"float:right;\"/>";
            row+=  "<p class=\"lead\"><a href=\""+LDRConfig.getServer()+name+  "/linkeddata.html\">"+ title+"</a></p>";
            row += comment;
            row +="</td>";


            row +="</tr>";

            html+=row;
        }

        return html;

    }
}
