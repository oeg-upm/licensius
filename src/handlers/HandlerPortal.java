package handlers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import handlers.HandlerIndex;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import ldrauthorizer.ws.AuthorizationResponse;
import ldrauthorizer.ws.LDRServer;
import ldrauthorizer.ws.LicensedTriple;
import ldrauthorizer.ws.ODRLAuthorizer;
import ldrauthorizer.ws.Portfolio;
import ldrauthorizerold.GoogleAuthHelper;
import languages.Multilingual;
import ldrauthorizer.ws.CLDHandlerManager;
import ldrauthorizer.ws.WebPolicyManager;
import model.DatasetVoid;
import model.Grafo;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Sirve el macroportal
 * @author vroddon
 */
public class HandlerPortal {
    static final Logger logger = Logger.getLogger(HandlerIndex.class);

    public void serve(HttpServletRequest request, HttpServletResponse response) {
        String body = "";
        try {
            String token = "";
            File f = new File("./htdocs/template_portal.html");
            token = FileUtils.readFileToString(f);
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            token = token.replace("<!--TEMPLATEFOOTER-->", footer);
            token = token.replace("<!--TEMPLATEDATASETS-->", getHTMLDatasets());

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(token);


        }catch(Exception e)
        {

        }
    }

    public static String getHTMLDatasets()
    {
        String html="";

        for(ConditionalDataset cd : ConditionalDatasets.datasets)
        {
            String name = cd.name;
            String comment = cd.getDatasetVoid().getComment();

        }

        return html;

    }
}
