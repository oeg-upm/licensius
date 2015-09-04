package ldc.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.Ldc;
import ldc.LdcConfig;
import ldc.Main;
import ldc.auth.Evento;
import ldc.auth.GoogleAuthHelper;
import ldc.auth.Portfolio;
import ldc.auth.SQLite;
import ldc.model.ConditionalDataset;
import ldc.model.DatasetIndex;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.HTMLODRLManager;
import odrlmodel.managers.PolicyManagerOld;
import oeg.utils.rdf.Transcoder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author vroddon
 */
public class Api {
    static final Logger logger = Logger.getLogger("ldc");

    /**
     * Obtiene una descripción JSON de los datasets
     */
    public static String getDatasets(String google) {
        JSONArray array = new JSONArray();
        Ldc.loadDatasets();
        for (ConditionalDataset cd : Ldc.datasets) {
            if (cd.verify()==false)
                continue;
            JSONObject json = cd.dsVoid.getJSON();
            String owner = "false";
            String realowner= SQLite.getOwner(cd.name);
            if (realowner.isEmpty()) {
                owner = "true";
            }
            if (realowner.equals(google))
                owner="true";
            json.put("owner", owner);
            json.put("triples", cd.getDatasetVoid().getProperty("http://rdfs.org/ns/void#triples"));
            array.put(json);
        }
        JSONObject contenedor = new JSONObject();
        contenedor.put("datasets",array);
        return contenedor.toString();
    }
    public static String getDataset(String id) {
        Ldc.loadDatasets();
        ConditionalDataset cd = Ldc.getDataset(id);
        JSONObject json = cd.dsVoid.getJSON();
        return json.toString();
    }

    public static void main(String[] argx) throws IOException {

        Main.standardInit();
        System.out.println(getDatasets(""));

    }
    
    
    /**
     * Uploads a file
     */
    static boolean uploadFile(HttpServletRequest request, String namedest) {
            try {
                List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                ConditionalDataset ds = null;
                for (FileItem fi : items) {
                    if (fi.isFormField()) {
                        String name = fi.getFieldName();
                        String value = fi.getString();
                        if (name.equals("dataset")) {
                            ds = Ldc.getDataset(value);
                            if (ds==null)
                            {
                                ds = Ldc.addDataset(value);
                            }
                        }
                    } else {
                        String fileName = fi.getName();
                        long sizeInBytes = fi.getSize();
                        logger.info("Uploading file of " + sizeInBytes / (1024 * 1024) + " Mb, titled " + fileName);
                        InputStream uploadedStream = fi.getInputStream();
                        String folder = ".";
                        if (ds != null) {
                            folder = LdcConfig.get("datasetsfolder","D:\\data\\ldc")  + "\\"+ds.name;
                        }
                        File flogo = new File(folder + namedest);
                        OutputStream os = new FileOutputStream(flogo);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = uploadedStream.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        IOUtils.closeQuietly(os);
                        uploadedStream.close();
                        
                        if (namedest.endsWith("data.nq") && !fileName.endsWith(".nq"))
                        {                
                            Transcoder t = new Transcoder();
                            String s = t.toNQuads(folder + namedest, "http://localhost/datasets/default");
                            if (s.startsWith("Transco"))
                            {
                                return false;
                            }
                        }
                    }
                }
                return true;
            } catch (Exception ex) {
                System.out.println("Error " + ex.getLocalizedMessage());
                return false;
            }        
    }    
    
    public static void serveError(HttpServletRequest req, HttpServletResponse resp)
    {
        try {
            resp.getWriter().println("Not found or parameters missing");
        } catch (IOException ex) {
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    
    /**
     * Obtiene recursos de un dataset
     */
    public static String getSomeEntities(String ds, int current, int ilimit, String searchFrase)
    {
        logger.info("Getting " +ilimit+" resources from " +ds+ " from "+current + " with searchPhrase " + searchFrase);
        ConditionalDataset cd = Ldc.getDataset(ds);
        if (cd==null)
            return "error";
        List<String> ls = cd.getDatasetIndex().getSujetos(current, ilimit, searchFrase, false);
        int total = cd.getDatasetIndex().getIndexedSujetos().size();
        
//        cd.getDatasetDump().getResources();
//            int total = RDFStoreFuseki.countEntities("http://www.w3.org/2004/02/skos/core#Concept");
            if (total==-1)
            {
                //WE HAVE A PROBLEM, WE PROBABLY LACK CONNECTION TO FUSEKI OR ANY OTHER STORE
                return "";
            }
            int init = (current - 1) * ilimit;
//            List<String> ls = RDFStoreFuseki.listConcepts(init, ilimit, searchFrase);
//            List<String> ls = Ldc.store.listResources(ds, init, ilimit, searchFrase);
            String s = "{\n"
                    + "  \"current\": " + current + ",\n"
                    + "  \"rowCount\": " + ilimit + ",\n"
                    + "  \"rows\": [\n";
            int conta = 0;
            for (String cp : ls) {
                int lasti = cp.lastIndexOf("/");
                String nombre = cp.substring(lasti + 1, cp.length());
                try {
                    nombre = URLDecoder.decode(nombre, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                }
                cp = cp.replace(" ", "+");
                if (conta != 0) {
                    s += ",\n";
                }
                s += "    {\n"
                        + "      \"resource\": \"" + nombre + "\",\n"
                        + "      \"resourceurl\": \"" + cp + "\"\n"
                        + "    } ";
                conta++;
            }

            s += "  ],\n"
                    + "  \"total\": " + total + "\n"
                    + "}    ";        
            return s;
    }    
    
    static boolean datasetIndex(ConditionalDataset ds) {
        DatasetIndex dsi=ds.getDatasetIndex();
        dsi.indexar();
        File f = new File(ds.getDumpPath());
        if (f.length()<1000000000) //1000Mb
        {
            Map<String, List<Integer>> map=dsi.createIndexGrafos();
            dsi.writeIndexGrafos(map);
        }
        else
        {
            logger.warn("Archivo gigantesco, no se ha creado grafo");
        }
        
        List<String> grafos = dsi.getIndexedGrafos();        
        for(String grafo : grafos)
        {
            if (grafo.isEmpty())
                continue;
            ds.getDatasetVoid().ensureGrafo(grafo);
//            List<Integer> indices = dsi.indexGrafos.get(grafo);
        }
        ds.getDatasetVoid().setProperty("http://rdfs.org/ns/void#triples",""+ds.getDatasetDump().ntriples);
        ds.getDatasetVoid().write();
        Ldc.loadDatasets();
        return true;
    }    
    
    /**
     * Service to manage the fake payments
     * @api {get} /{dataset}/service/showPayment showPayment
     * @apiName /showPayement
     * @apiGroup ConditionalLinkedData
     * @apiVersion 1.0.0
     * @apiDescription Redirects the user to the external gateway to make a payment. This is only simulated. </br>
     * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
     *
     * @apiParam {String} policy Policy that has been payed for
     * @apiParam {String} target Resource that has been payed for
     * @apiSuccess {String} void Nothing is returned
     */
        public static String getHTMLPayment(String policyName, String targetName) {
           
            String body = "";
            try {
                InputStream input = Api.class.getResourceAsStream("template_empty.html");
                BufferedInputStream bis = new BufferedInputStream(input);
                body = IOUtils.toString(bis, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String contenido = Api.getHTMLPasarela(policyName, targetName);
            body = body.replace("<!--TEMPLATE_CONTAINER-->", contenido);
            return body;
        }    
        
        /**
         * Shows the simulation of a payment
         */
        public static String getHTMLPasarela(String policyName, String targetName)
        {
            String html = "<center><img src=\"/ldc/img/pagos.png\">\n" +
            "<h3>Should we imagine you have paid? (this is just a demo)</h3>\n" +
            "<form action=\"/ldc/api/fakePayment\">\n" +
            "  <input type=\"submit\" value=\"Yes\" name=\"payment\"/>\n" +
            "  <input type=\"submit\" value=\"No\" name=\"payment\" onclick=\"window.close()\" />\n" +
            "  <input type=\"hidden\" id=\"policy\" name=\"policy\" value=\"<!--TEMPLATEHERE1-->\" />\n" +
            "  <input type=\"hidden\" id=\"target\" name=\"target\" value=\"<!--TEMPLATEHERE2-->\" />\n" +
            "</form>\n" +
            "<small>You are purchasing <!--TEMPLATEHERE2--></small>\n" +
            "</center>";
            html = html.replaceAll("<!--TEMPLATEHERE1-->", policyName);
            html = html.replaceAll("<!--TEMPLATEHERE2-->", targetName);
            
            return html;
        }
        
    public static Portfolio fakePayment(String email, String paramPago, String paramPolicy, String paramName) {
        if (email==null)
            email="noname@tmp.com";
        Portfolio p = Portfolio.getPortfolio(email);
        Policy policy = PolicyManagerOld.getPolicy(paramPolicy);
        if (paramPago!=null && paramPago.equals("Yes") && policy != null) {
            policy.setTargetInAllRules(paramName);
            policy.setAssigneeInAllRules(email);
            p.policies.add(policy);
            Evento.addEvento("Payment made for " + paramName, policy.getFirstPrice());
            Portfolio.setPortfolio(email, p);
            return p;
        }
        return null;
    }        

    public static String servePolicy(Policy policy, String target, Portfolio portfolio) {
        String body = "";
        try {
            InputStream input = Api.class.getResourceAsStream("template_empty.html");
            BufferedInputStream bis = new BufferedInputStream(input);
            body = IOUtils.toString(bis, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String html = HTMLODRLManager.htmlPolicy(policy, "en");
        String urilicense = policy.getURI();
        String uritarget = target;
        try {
            urilicense = URLEncoder.encode(policy.getURI(), "UTF-8");
            uritarget = URLEncoder.encode(target, "UTF-8");
            
        } catch (UnsupportedEncodingException ex) {
        }
        if (target != null && !target.isEmpty()) {
            String urioferta = "/ldc/api/showPayment?policy=" + urilicense + "&target=" + uritarget;
            String oferta = "<center><img width=\"64\" src=\"/ldc/img/carrito.png\"/><a href=\"" + urioferta + "\">purchase</a></center>";
            String price=policy.getPriceString();
            if (!price.isEmpty())
                html = html.replaceAll("<!--TEMPLATEHERE1-->", oferta);
        }
        body = body.replace("<!--TEMPLATE_CONTAINER-->", html);
        /*for (Policy policyx : portfolio.policies) {
            if (policyx.getURI().equals(urlcompleta)) {
                policy = policyx;
                break;
            }
        }*/


        return body;
    }
    
    
    /**
     * Generates HTML for the Account management
     */
    private String generateAccountHTML(HttpServletRequest request, HttpServletResponse response) {
        String body = "";
        String usuario = (String) request.getSession().getAttribute("usuario");
        if (usuario != null && !usuario.isEmpty()) {
            body += usuario;
        } else {
            String login = "";
            try {

                String isLogout = request.getParameter("action");
                if (isLogout != null) {
                    System.out.println("logging out");
                    request.getSession().removeAttribute("state");
                    request.getSession().removeAttribute("google");
                    request.getSession().invalidate();
                }
                String reqState = request.getParameter("state");
                String reqCode = (String) request.getParameter("code");
                String sesState = (String) request.getSession().getAttribute("state");
                String sesGoogle = (String) request.getSession().getAttribute("google");
//        if (reqCode == null || reqState == null) {
                if (reqCode != null && reqState != null && reqState.equals(sesState)) // se acaba de logear
                {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String json = helper.getUserInfoJson(request.getParameter("code"));
                    Object obj = JSONValue.parse(json);
                    org.json.simple.JSONObject jobj = (org.json.simple.JSONObject) obj;
                    String email = (String) jobj.get("email");
                    String name = (String) jobj.get("name");
                    String accountInfo="";
                    accountInfo+="<div class=\"well\">";
                    accountInfo += "Logged in as <strong>" + email + "</strong></br>User: <strong>" + name + "</strong>";
//                    accountInfo += " <a href=\"account.html?action=logout\">logout</a>";
                    accountInfo += "<br><a href=\"account.html?action=logout\" class=\"btn btn-default\" role=\"button\" >Logout</a>";
                    accountInfo +="</div>";
                    request.getSession().setAttribute("google", accountInfo);
                    login += accountInfo;
                    Portfolio portfolio = Portfolio.ReadPortfolio(GoogleAuthHelper.getMail(request));
                    Portfolio.setPortfolio(GoogleAuthHelper.getMail(request), portfolio);

                } else if (sesState == null || sesGoogle == null) {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String neochurro = "";
                    neochurro += "Logged in as <strong>guest</strong></br>";
                    neochurro += "<a href='" + helper.buildLoginUrl() + "'><img src=\"/img/googleplus.png\" height=40/> </a>";
                    //       request.getSession().setAttribute("state", helper.getStateToken());
                    login += neochurro;
                } else if (sesState != null) {
                    String accountInfo = (String) request.getSession().getAttribute("google");
                    login += sesGoogle;
                }

                String str = "";
                str += "<hr/>";

                String email = GoogleAuthHelper.getMail(request);
                Portfolio portfolio = Portfolio.getPortfolio(email);
                if (portfolio == null || portfolio.policies == null || portfolio.policies.isEmpty()) {
                    str += "You have purchased no permissions.<br/>";
                    //       str+=StringUtils.readTestFile();
                    login += str;
                } else {
                    str += "<p>You have purchased the following permissions:</p><div class=\"well\">";

//                    str += "<table border=\"1\" style=\"width:300px;\" ><tr style=\"font-weight:bold;\"><td >Price</td><td>Asset</td><td>License</td></tr>";
                    str += "<table class=\"table\"><thead><tr><th>Price</th><th>Asset</th><th>License</th></tr></thead>";

                    for (Policy policy : portfolio.policies) {
                        String starget = policy.getFirstTarget();
                        Asset a = AssetManager.getAsset(starget);
                        if (a != null) {
                            starget = a.getLabel("en");
                        }
//                        String llic = " <a href=\"" + policy.getURI() + "\">(view)</a>";

                        String llic = HandlerOffers.getPolicyHTMLTag(policy, null);

                        String precio = String.format("%.02f %s", policy.getFirstPrice(), policy.getFirstCurrency());
                        str += "<tr><td>" + precio + "</td><td>" + starget + "</td><td>" + llic + "</td></tr>";
                    }
                    str += "</table></div>";
                    login += str;
                    login += "<a href=\"service/resetPortfolio\" class=\"btn btn-default\" role=\"button\" >Reset portfolio</a>";
                    login += "<a href=\"service/exportPortfolio?signed=false\" class=\"btn btn-default\" role=\"button\" >View portfolio</a>";
                    login += "<a href=\"service/exportPortfolio?signed=true\" class=\"btn btn-default\" role=\"button\" >View signed portfolio</a>";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            login += "";
            body += login;
        }
        return body;
    }    
}
