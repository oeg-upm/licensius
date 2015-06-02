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
            
            token = token.replace("<!--TEMPLATEFOOTER-->", FileUtils.readFileToString(new File("./htdocs/footer.html")));
            
            token = token.replace("<!--TEMPLATEDATASETS-->", getHTMLDatasets(sdataset));
            
            token = token.replace("<!--TEMPLATEADDITIONALPORTALBUTTONS-->", getHTMLAdditionalPortalButtons());

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
            int irecursos = 0;
            int ngrafos = 0;

            if (cd.getDatasetDump()==null)
                continue;
            try{title = cd.getDatasetVoid().getTitle();}catch(Exception e){}
            try{comment = cd.getDatasetVoid().getComment();}catch(Exception e){}
            try{ngrafos = cd.getDatasetVoid().getGrafos().size();}catch(Exception e){}            
            try{irecursos=cd.getDatasetIndex().getIndexedSujetosSize();}catch(Exception e){}
            
            row+="<tr>";
            
            row+="<td>"+ name+"</td>";
            row+="<td>"+ irecursos +"</td>"; //ngrafos
            row+="<td>"+ getLogo(name);
            row+=  "<p class=\"lead\"><a href=\""+LDRConfig.getServer()+name+  "/linkeddata.html\">"+ title+"</a></p>";
            row += comment;
            row +="</td>";

            //accion
            row+="<td>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Removes this dataset from the server\" id=\"boton1\" class=\"btn btn-primary\" href=\"javascript: submitRemove('"+name+"')\"><span class=\"glyphicon glyphicon-remove\"></span></a>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Edits this dataset in the server\" id=\"boton2\" class=\"btn btn-primary\" href=\"javascript: submitEdit('"+name+"', 'edit')\"><span class=\"glyphicon glyphicon-edit\"></span></a>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Browse dataset\" id=\"boton3\" class=\"btn btn-primary\" href=\"/"+name+"\"><span class=\"glyphicon glyphicon-play\"></span></a>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Rebases this dataset\" id=\"boton4\" class=\"btn btn-primary\" href=\"javascript: submitRebase('"+name+"', 'rebase')\"><span class=\"glyphicon glyphicon-link\"></span></a>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Indexes this dataset\" id=\"boton5\" class=\"btn btn-primary\" href=\"javascript: submitIndex('"+name+"', 'index')\"><span class=\"glyphicon glyphicon-refresh\"></span></a>"; 
            row+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Administers the dataset\" id=\"boton6\" class=\"btn btn-primary\" href=\"/"+name+"/admin\"><span class=\"glyphicon glyphicon-lock\"></span></a>"; 
            row+="</td>";
            
            
            
            //endaccion

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
                img = "<img style=\"float:right;max-width:192px;max-height:192px;\" src=\"data:image/png;base64," + churro + "\">";
            } catch (IOException e) {}            
            return img;
    }

    /**
     * Botones generales sobre los datasets
     */
    private String getHTMLAdditionalPortalButtons() {
            
        String html="";
        html+="<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Adds a new dataset to the server\" id=\"boton1\" class=\"btn btn-primary\" href=\"javascript: submitEdit('', 'new')\"><span class=\"glyphicon glyphicon-plus\"></span></a>"; 
        return html;
    }
    
}
