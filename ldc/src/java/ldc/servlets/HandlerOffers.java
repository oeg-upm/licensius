package ldc.servlets;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.Ldc;
import ldc.auth.AuthorizationResponse;
import ldc.auth.GoogleAuthHelper;
import ldc.auth.Portfolio;
import ldc.model.ConditionalDataset;
import ldc.model.DatasetVoid;
import ldc.model.Grafo;
import ldc.model.Oferta;
import ldc.model.Recurso;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class HandlerOffers {

    static final Logger logger = Logger.getLogger(HandlerOffers.class);

    public void serveOffers(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            String token = "";
            File f = new File("./htdocs/template_offers.html");
            token = FileUtils.readFileToString(f);
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            token = token.replace("<!--TEMPLATEFOOTER-->", footer);
            Portfolio por = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
            String htmlOffers = "";
            ConditionalDataset cd = Ldc.getDataset(dataset);
            htmlOffers = getHTMLForOffers(cd, por.policies);
            token = token.replace("<!--TEMPLATEHERE1-->", htmlOffers);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(token);
            baseRequest.setHandled(true);

            List<Grafo> grafos = cd.getGrafos();
            for (Grafo grafo : grafos) {
                Recurso recurso = cd.getDatasetVoid().getRecurso(grafo.getURI());
                if (recurso == null) {
                    continue;
                }
                List<Policy> policies = cd.getPoliciesForGraph(grafo.getURI());
                for (Policy p : policies) {
                    String nombre = p.getTitle();
                    if (nombre.isEmpty()) {
                        nombre = p.getLabel("en");
                    }
                    if (!p.inOffer) {
                        continue;
                    }
                    Oferta offer = new Oferta();
                    offer.setLicense_title(nombre);
                    offer.setLicense_link(p.uri);
                    offer.setLicense_price("OpenData");

                }
                // LO DEJO POR AQUI PERO ES QUE ESTOY MUERTO DE SUEÑO
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            logger.error("Error serving dataset.html " + e.getMessage());
        }
    }

    private static String getServicioGetDatabaseURL(String asset) {
        String datasetEncoded = asset;
        try {
            datasetEncoded = URLEncoder.encode(asset, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "service/getDataset?dataset=" + datasetEncoded;
    }

    private static String getHTMLForOffers(ConditionalDataset cd, List<Policy> pPagadas) {
        String html = "";
        html += "<div style=\"color:#AA2222 !important;\"><tr style=\"height=10px;font-weight: bold;color:#AA2222 !important;\"><td>Dataset";
        html += "<td>RDF dump</td><td>Licenses</td><td>Description</td></div>\n";

        DatasetVoid dp = cd.getDatasetVoid();
        List<Grafo> grafos = cd.getGrafos();
        for (Grafo grafo : grafos) {
            String commentAsset = grafo.getComment();
            String labelAsset = grafo.getLabel();
            String servicio = getServicioGetDatabaseURL(grafo.getURI());     //enlace
            boolean bOpen = !grafo.getOpenPolicies().isEmpty();
            boolean bPolicies = !grafo.getPoliciesForMoney().isEmpty();
            String sopena = "<img src=\"/img/arrowdown32green.png\">";
            String scloseda = "<img src=\"/img/arrowdown32red.png\">";
//            String sopen = "<button class=\"cupid-green\" onclick=\"location.href='linkeddata'\">Download</button>"; //style=\"color:black\"
            String sopen = "<button  class=\"btn btn-success btn-block\"><span class=\"glyphicon glyphicon-download-alt\" ></span> Download</button>";

            String sclosed = "<button type=\"button\" class=\"btn btn-danger btn-block\"><span class=\"glyphicon glyphicon-shopping-cart\"></span> Buy</button>";

//            String sclosed = "<button class=\"cupid-blue\" onclick=\"location.href='linkeddata'\">Buy</button>";
//            String sclosed = "<button class=\"cupid-red\" onclick=\"location.href='linkeddata'\">Buy</button>";
            String shidden = "";
            String color = bOpen ? sopen : sclosed;
            if (!bPolicies && !bOpen) {
                color = shidden;
            }
            AuthorizationResponse r =HandlerOffers.AuthorizeResource(grafo, pPagadas);
            if (r.ok == true) {
                color = sopen;
            }

            List<Policy> policies = cd.getPoliciesForGraph(grafo.getURI());
            int conta = 0;
            String htmlpol = "<td>";

            String licencias = "";
            int contador = 0;
            for (Policy p : policies) {
                String nombre = p.getTitle();
                if (nombre.isEmpty()) {
                    nombre = p.getLabel("en");
                }
                if (!p.hasPlay()) {
                    continue;
                }
                Policy policy2 = p;
                if (policy2 == null) {
                    continue;
                }
                contador++;

                String slicencia = getPolicyHTMLTag(policy2, grafo);

                //
                String uri = policy2.getURI();
                String target = "";
                try {
                    target = URLEncoder.encode(grafo.getURI(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                uri += "?target=" + target;
                conta++;
//                htmlpol += conta + "- ";
                licencias += "<a href=\"" + uri + "\"/>";
                String slav = p.getLabel("en");
                if (policy2.isOpen()) {
                    licencias += "<span class=\"label label-success\">" + slav + " </span><br/>";
//                    htmlpol += "<img src=\"/ldr/img/license32green.png\">";
                } else {
                    licencias += "<span class=\"label label-danger\">" + slav + " </span><br/>";
//                    htmlpol += "<img src=\"/ldr/img/license32red.png\">";
                }
                licencias += "</a>";
                //
            }

            if (licencias.isEmpty()) {
                licencias = "<span class=\"label label-default\">Not offered</span>";
            }
            htmlpol += licencias;
            htmlpol += "</td>";
            html += "<tr style=\"height=20px;\">";
            html += "<td> <strong>" + labelAsset + "</strong></td>";

            html += "<td> <a href=\"" + servicio + "\">" + color + "</a></td>";

            html += htmlpol;

            html += "<td>" + commentAsset + "</td></tr>\n";
        }
        return html;
    }

    public static String getPolicyHTMLTag(Policy policy2, Grafo grafo) {
        String str = "";
        String uri = policy2.getURI();
        String target = "";
        if (grafo!=null)
        {
            try {
                target = URLEncoder.encode(grafo.getURI(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            uri += "?target=" + target;
        }
        str += "<a style=\"padding:.5em;\" href=\"" + uri + "\"/>";
        String slav = policy2.getLabel("en");
        if (policy2.isOpen()) {
            str += "<span class=\"label label-success\">" + slav + " </span><br/>";
        } else {
            str += "<span class=\"label label-danger\">" + slav + " </span><br/>";
        }
        str += "</a>";
        return str;
    }
    /**
     * Autoriza un recurso, dado un conjunto de políticas compradas en el portfolio
     */
    public static AuthorizationResponse AuthorizeResource(Grafo grafo, List<Policy> portfolioPolicies) {
        AuthorizationResponse ar = new AuthorizationResponse();
        ar.ok = false;
        ar.policies.clear();
        //y si no, miramos ALGUNA DEL PORTFOLIO
        for (Policy policy : portfolioPolicies) {
            if (policy.hasPlay() && policy.hasFirstTarget(grafo.getURI())) {
                ar.ok = true;
                ar.policies.add(policy);
                return ar;
            }
        }

        List<Policy> policies = grafo.getPolicies();
        if (policies.isEmpty()) {
            return ar;
        }

        //SI HAY ALGUNA POLITICA ABIRTA, PERFECTO
        for (Policy policy : policies) {
            Policy policyFromStore = PolicyManagerOld.getPolicy(policy.getURI());
            if (policyFromStore == null) {
                continue;
            }
            if (policyFromStore.hasPlay() && policyFromStore.isOpen()) {
                ar.ok = true;
                ar.policies.add(policyFromStore);
                return ar;
            } else if (policyFromStore.hasPlay()) {
                ar.policies.add(policyFromStore);
            }
        }



        return ar;

    }    
}
