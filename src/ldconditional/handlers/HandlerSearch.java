package ldconditional.handlers;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.LDRConfig;
import ldconditional.auth.Evento;
import ldconditional.model.DatasetIndex;
import ldconditional.model.DatasetVoid;
import ldconditional.model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;
import oeg.rdf.commons.NQuadRawFile;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.openjena.riot.Lang;

/**
 *
 * @author vroddon
 */
public class HandlerSearch {

    static final Logger logger = Logger.getLogger(HandlerSearch.class);

    public String serveSearch(ConditionalDataset cd, HttpServletRequest request, HttpServletResponse response) {
        String token = "";
        String sel = request.getParameter("search");
        try {
            sel = URLDecoder.decode(sel, "UTF-8");
        } catch (Exception e) {
        }
        try {
            token = FileUtils.readFileToString(new File("./htdocs/template_search.html"));


            if (sel != null) {
                String results = search(cd, sel);
                token = token.replace("<!--TEMPLATERESULT-->", results);
            }

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.warn("error " + e.getMessage());
        }
        return token;
    }

    public String search(ConditionalDataset cd, String term) {
        String html = "";
        DatasetIndex di = cd.getDatasetIndex();

        String tabla = "<table class=\"table\">";
        tabla += "<thead><th>Resource</th><th>Type</th><th>#triples</th></thead>";

        List<String> ls = di.matchSujetos(term);
        for (String s : ls) {
            List<String> triples = di.getNQuadsForSujeto(s);
            System.out.println(triples);
            int ntriples = triples.size();
            Model m = NQuadRawFile.getModel(triples);

            RDFDataMgr.write(System.out, m, Lang.TURTLE);

            String label = s;
            label = oeg.utils.StringUtils.getLocalName(label);
            try {
                label = URLDecoder.decode(label, "UTF-8");
            } catch (Exception e) {
            }


            String clase = "";
            Statement st = m.getProperty(ModelFactory.createDefaultModel().createResource(s), RDFUtils.LABEL);
            if (st != null) {
                RDFNode nodo=st.getObject();
                if (nodo!=null)
                {
                    if (nodo.isLiteral())
                    {
                        label = nodo.asLiteral().getValue().toString();
//                        label = nodo.asLiteral().toString();
                    }
                    else if (nodo.isResource())
                        label = nodo.asResource().toString();
                }
            }
            st = m.getProperty(ModelFactory.createDefaultModel().createResource(s), ModelFactory.createDefaultModel().createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
            if (st != null) {
                clase = st.getObject().asResource().toString();
                clase = oeg.utils.StringUtils.getLocalName(clase);
                try {
                    clase = URLDecoder.decode(clase, "UTF-8");
                } catch (Exception er) {
                    logger.warn("error " + er.getMessage());
                }

            }

            tabla += "<tr>";
            tabla += "<td><a href=\"" + s + "\">" + label + "</a></td>";
            tabla += "<td>" + clase + "</td>";
            tabla += "<td>" + triples.size() + "</td>";

            tabla += "</tr>";

        }
        tabla += "</table>";
        html += tabla;
        return html;
    }
}
