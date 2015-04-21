package ldrauthorizer.ws;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldrauthorizerold.GoogleAuthHelper;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;


import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Class to process the datasets page
 * 
 * @author Victor
 */
public class CLDHandlerDatasets extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/ldr/en/datasets.html")) {
            return;
        }
        String token = "";
        File f = new File("../htdocs/en/datasets.html");
        token = FileUtils.readFileToString(f);
        String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
        token = token.replace("<!--TEMPLATEFOOTER-->", footer);

        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        String htmlOffers = "";
        htmlOffers = getHTMLForDatasets(p.policies);
        token = token.replace("<!--TEMPLATEHERE1-->", htmlOffers);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=utf-8");
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
            int conta=0;
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

                htmlpol+=conta+"- ";
                htmlpol += "<a href=\"" + uri + "\"/>";
                if (policy2.isOpen()) {
                    htmlpol+="<span>"+policy.getLabel("en")+"<br/></span>";
//                    htmlpol += "<img src=\"/ldr/img/license32green.png\">";
                } else {
                    htmlpol+="<span>"+policy.getLabel("en")+"<br/></span>";
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
