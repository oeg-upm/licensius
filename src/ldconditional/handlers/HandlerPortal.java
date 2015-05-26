package ldconditional.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.LDRConfig;
import static ldconditional.handlers.HandlerIndex.getImage64;
import static ldconditional.handlers.HandlerIndex.logger;
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
            logger.warn("Error sirviendo la pagina indice del portal." + e.getMessage());
        }
    }

    /**
     * Obtiene el HTML para los datsets
     */
    public static String getHTMLDatasets(String sdat)
    {
        String html="";

        //se escoge un dataset por defecto en caso de que no haya ninguno.
        if (sdat==null || sdat.isEmpty())
            sdat="geo";

        for(ConditionalDataset cd : ConditionalDatasets.datasets)
        {
            String row="";
            String name = cd.name;
            String title = "untitled";
            String comment = "not commented";
            int ngrafos = 0;

            if (cd.getDatasetDump()==null)
                continue;

            
            try{title = cd.getDatasetVoid().getTitle();}catch(Exception e){}
            try{comment = cd.getDatasetVoid().getComment();}catch(Exception e){}
            try{ngrafos = cd.getDatasetVoid().getGrafos().size();}catch(Exception e){}            
            
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
            row+=""+ngrafos;
//            row += "Triples: " + cd.getDatasetVoid().getNumTriples(row) + "<br/>";
//            row += "Datasets: " + cd.getDatasetVoid().getGrafos().size() + "<br/>";
            row +="</td>";

            row+="<td>";
//            row += "<img src=\"/img/"+ name +".png\" height=\"128\" style=\"float:right;\"/>";
            row += getLogo(name);
            row+=  "<p class=\"lead\"><a href=\""+LDRConfig.getServer()+name+  "/linkeddata.html\">"+ title+"</a></p>";
            row += comment;
            row +="</td>";


            row +="</tr>";

            html+=row;
        }

        return html;

    }
    

    /**
     * Obtiene el logo de un dataset como un churro en base 64
     */
    public static String getLogo(String name)
    {
            String img="";
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + name + "/logo.png";
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(new File(filename));
                String churro = getImage64(bi);
                img = "<img style=\"float:right;\" src=\"data:image/png;base64," + churro + "\">";
            } catch (IOException e) {}            
            return img;
    }
    
}
