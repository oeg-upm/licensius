package ldrauthorizer.ws;

//JAVA
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//ODRLMODEL
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.HTMLODRLManager;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.ODRLRDF;

//APACHE JENA
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


//JETTY
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Class to process RDFLicenses. 
 * Example to invoke: http://purl.org/NET/rdflicense/cc-by4.0
 * @author Victor
 */
public class CLDHandlerRDFLicense extends AbstractHandler {

    private final static String RDFLICENSE = "../data/rdflicense.ttl"; 
    
    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/rdflicense")) {
            Logger.getLogger("ldr").info("It was not starting with rdflicense: "+string);
            return;
        }
        String token = "I have been requested " + string;
        Model model = null;
        String path = new String(RDFLICENSE);
        token += ". Now reading file " + path;
        try {
            model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, path);
            token += " ok";
        } catch (Exception exc) {
            token +=" not ok";
            exc.printStackTrace();
            return;
        }
        Logger.getLogger("ldr").info(token);
        
        //If we are requested the whole dataset
        if (string.equals("/rdflicense/") || string.equals("/rdflicense")) {
            if (LDRServer.isTurtle(request)) {
                Logger.getLogger("ldr").info("Sirviendo TTL " + string);
                response.setContentType("text/turtle");
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, model, Lang.TTL);
                token = sw.toString();
            } else if (LDRServer.isHuman(request)) {
                Logger.getLogger("ldr").info("Sirviendo HTML " + string);
                Logger.getLogger("ldr").info("redirigiendo");
                response.sendRedirect("http://oeg-dev.dia.fi.upm.es/licensius/rdflicense");
                return;
            } else {
                Logger.getLogger("ldr").info("Sirviendo RDF/XML " + string);
                response.setContentType("application/rdf+xml");
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, model, Lang.RDFXML);
                token = sw.toString();
            }

        } else { 
            // ES UNA LICENCIA EN CONCRETO
            //NO HAGO NEGOCIACION DE CONTENIDO PORQUE NO TENGO TIEMPO
            //VOY A BUSCAR SOLO EL FRAGMENTO... PURAMENTE PARSEANDO CADENAS!!!!
            String archivote = CLDHandlerRDFLicense.readFile(path,StandardCharsets.UTF_8);
            String slicense = "http://purl.org/NET"+string;
            String abuscar="\n"+"<"+slicense+">";
            Logger.getLogger("ldr").info("Now searching: " + abuscar);
            int index=archivote.indexOf(abuscar);
            String extension ="ttl";
            if (abuscar.length()>3)
                extension = abuscar.substring(abuscar.length()-4, abuscar.length()-1);
            if (index==-1)
            {
                Logger.getLogger("ldr").info("License " + slicense +" not found");
                String copia = new String(slicense);
                slicense=slicense.substring(0,slicense.length()-4);
                extension = copia.substring(copia.length()-3, copia.length());
                abuscar="\n"+"<"+slicense+">";
                System.out.println("Intentando ahora " + slicense);
                index=archivote.indexOf(abuscar);
                if (index==-1)
                {
                    token="rr1";
                    return;
                }
            }
            int index2 = archivote.indexOf(". ", index);
            if (index2==-1)
            {
                token="rr2";
                System.out.println("Licencia " + slicense +" no encontrada");
                return;
            }
            index2++;
            String lici=archivote.substring(index, index2);
            token=lici;
            response.setContentType("text/turtle");
            if (LDRServer.isTurtle(request) || extension.equals("ttl") ) {
                Logger.getLogger("ldr").info("Serving TTL ");
                response.setContentType("text/turtle");
            } else if (LDRServer.isHuman(request)) {
                Logger.getLogger("ldr").info("Serving HTML ");
                Policy policy = PolicyManagerOld.load(path, slicense);
                String legalcode=policy.getLegalcode();
                if (legalcode!=null && !legalcode.isEmpty())
                    response.sendRedirect(legalcode);
            } else {
                Logger.getLogger("ldr").info("Nos gustaria servir RDF/XML ");
                response.setContentType("application/rdf+xml");
                Policy policy = PolicyManagerOld.load(path, slicense);
                if (policy!=null)
                {
                    String rdf = ODRLRDF.getRDF(policy, Lang.RDFXML);
                    token = rdf.toString();
                }
            }
            
            

        }



        response.setStatus(HttpServletResponse.SC_OK);
   //     response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(token);
        baseRequest.setHandled(true);
        return;

    }

    private static String getHTMLForDatasets(List<Policy> pPagadas) {
        String html = "";
        //Para cada dataset...
        List<Asset> assets = AssetManager.readAssets();

        html += "<div style=\"color:#AA2222 !important;\"><tr style=\"height=10px;font-weight: bold;color:#AA2222 !important;\"><td>Dataset";
        html += "<td>RDF dump</td><td>Licenses</td><td>Description</td></div>\n";


        for (Asset asset : assets) {
            if (!asset.isInOffer()) {
                continue;
            }
            String labelAsset = asset.getLabel("en");               //nombre
            String commentAsset = asset.getComment();               //descripcion
            String servicio = getServicioGetDatabaseURL(asset);     //enlace
            boolean bOpen = asset.isOpen();                         //si es o no abierto
            String sopena = "<img src=\"/ldr/img/arrowdown32green.png\">";
            String scloseda = "<img src=\"/ldr/img/arrowdown32red.png\">";



            String sopen = "<button class=\"cupid-blue\" onclick=\"location.href='linkeddata'\">Download</button>";
            String sclosed = "";

            String color = bOpen ? sopen : sclosed;
            AuthorizationResponse r = ODRLAuthorizer.AuthorizeResource(asset.getURI(), pPagadas);
            if (r.ok == true) {
                color = sopen;
            }

            String htmlpol = "<td>";
            List<Policy> policies = asset.getPolicies();
            int conta = 0;
            for (Policy policy : policies) {
                Policy policy2 = policy;
                if (policy2 == null) {
                    continue;
                }
                String uri = policy2.getURI();
                String target = "";
                try {
                    target = URLEncoder.encode(asset.getURI(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CLDHandlerDatasets.class.getName()).log(Level.SEVERE, null, ex);
                }
                uri += "?target=" + target;
                conta++;

                htmlpol += conta + "- ";
                htmlpol += "<a href=\"" + uri + "\"/>";
                if (policy2.isOpen()) {
                    htmlpol += "<span>" + policy.getLabel("en") + "<br/></span>";
//                    htmlpol += "<img src=\"/ldr/img/license32green.png\">";
                } else {
                    htmlpol += "<span>" + policy.getLabel("en") + "<br/></span>";
//                    htmlpol += "<img src=\"/ldr/img/license32red.png\">";
                }
                htmlpol += "</a>";
            }
            htmlpol += "</td>";


            html += "<tr style=\"height=20px;\">";

            html += "<td> <strong>" + labelAsset + "</strong></td>";

            html += "<td> <a href=\"" + servicio + "\">" + color + "</a></td>";

            html += htmlpol;

            html += "<td>" + commentAsset + "</td></tr>\n";


        }

        return html;
    }

    /**
     * Retrieves a descriptive HTML code to show the offers
     */
    private static String getHTMLForOffers(List<Policy> pPagadas) {
        String html = "";
        //Para cada dataset...
        List<Asset> assets = AssetManager.readAssets();
        for (Asset asset : assets) {
            String labelAsset = asset.getLabel("en");
            String commentAsset = asset.getComment();

            List<Policy> policies = asset.getPolicies();
            for (Policy policy : policies) {
                Policy policy2 = PolicyManagerOld.getPolicy(policy.getURI());
                if (policy2 == null) {
                    continue;
                }
                String datasetEncoded = asset.getURI();
                try {
                    datasetEncoded = URLEncoder.encode(asset.getURI(), "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String servicio = "/ldr/service/getDataset?dataset=" + datasetEncoded;
                if (policy2.isInOffer() && policy2.isOpen()) {
                    String offer = " <tr><td style=\"border:0px solid black;\"><a href=\"" + servicio + "\" class=\"myButtongreen\">Download RDF dump <img src=\"/ldr/img/arrowdown48.png\" align=\"right\"";
                    offer += " style=\"margin-right:0px;\"/></br><b>" + labelAsset + " (0 EUR)</b></a></td>";
                    offer += " <td style=\"border: 0px solid black;;vertical-align:top;\">" + commentAsset + " nquads dump file <br/>";
                    offer += "</td></tr>\n";
                    html += offer;
                } else {
                    if (policy2.isInOffer() && !policy2.isPerTriple()) {
                        String color = "myButtonred";
                        String slabeltmp = asset.getLabel("en");
                        if (slabeltmp.equals("Mappings")) {
                            slabeltmp = slabeltmp;
                        }

                        String uriAsset = asset.getURI();
                        AuthorizationResponse r = ODRLAuthorizer.AuthorizeResource(uriAsset, pPagadas);
                        if (r.ok == true) {
                            color = "myButtongreen";
                        }

                        String price = policy2.getPriceString();
                        String offer = " <tr><td style=\"border:0px solid black;\"><a href=\"" + servicio + "\" class=\"" + color + "\">Download RDF dump <img src=\"/ldr/img/arrowdown48.png\" align=\"right\"";
                        offer += " style=\"margin-right:0px;\"/></br><b>" + labelAsset + " (" + price + ")</b></a></td>";
                        offer += " <td style=\"border: 0px solid black;;vertical-align:top;\">" + commentAsset + " nquads dump file <br/>";
                        offer += "</td></tr>\n";
                        html += offer;
                    }
                }
            }
        }

        return html;
    }

    private static String getServicioGetDatabaseURL(Asset asset) {
        String datasetEncoded = asset.getURI();
        try {
            datasetEncoded = URLEncoder.encode(asset.getURI(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String servicio = "/ldr/service/getDataset?dataset=" + datasetEncoded;
        return servicio;

    }
}
