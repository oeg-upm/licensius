package ldconditional.handlers;

import ldconditional.auth.Evento;
import ldconditional.auth.AuthorizationResponse;
import ldconditional.auth.Authorizer;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.LDRConfig;
import ldconditional.auth.GoogleAuthHelper;

import ldconditional.auth.Portfolio;
import static ldconditional.handlers.ServiceHandler.logger;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.model.DatasetIndex;
import ldconditional.model.Grafo;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.ODRLModel;
import odrlmodel.rdf.ODRLRDF;
import oeg.rdf.commons.RDFUtils;
import oeg.rdftools.Transcoder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author Victor
 */
public class ServiceHandlerImpl {
    static final Logger logger = Logger.getLogger(ServiceHandlerImpl.class);


 /**
 * Service to manage the fake payments
 * @api {get} /{dataset}/service/fakePayment fakePayment
 * @apiName /fakePayement
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Simulates a payment has been made. This would be the called to be invoked upon payment in an external gateway.<br/>
 * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
 *
 * @apiParam {String} policy Policy that has been payed for
 * @apiParam {String} target Resource that has been payed for
 * @apiParam {String} payment "Yes" If payment has been made
 * @apiSuccess {String} void Nothing is returned
 */
    public static void fakePayment(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {

        String state = (String) request.getSession().getAttribute("state");
        String email = GoogleAuthHelper.getMail(request);
        Portfolio p = Portfolio.getPortfolio(email);
//        Portfolio p = (Portfolio) request.getSession().getAttribute("portfolio");

        String paramPago = request.getParameter("payment");
        String paramPolicy = request.getParameter("policy");
        String paramName = request.getParameter("target");
        try {
            paramPolicy = URLDecoder.decode(paramPolicy, "UTF-8");
            paramName = URLDecoder.decode(paramName, "UTF-8");
            paramPago = URLDecoder.decode(paramPago, "UTF-8"); //YES
        } catch (Exception e) {
            e.printStackTrace();
        }

        Policy policy = PolicyManagerOld.getPolicy(paramPolicy);

        if (paramPago!=null && paramPago.equals("Yes") && policy != null) {
            policy.setTargetInAllRules(paramName);
            if (state != null) {
                /*String google=(String)request.getSession().getAttribute("google");
                String email = "mailto:"+state+"@tmp.com";
                Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(google);
                if (m.find()) {
                email = "mailto:"+m.group();
                }        */
                policy.setAssigneeInAllRules(email);
                p.policies.add(policy);
                Evento.addEvento("Payment made for " + paramName, policy.getFirstPrice());
                Portfolio.setPortfolio(email, p);
                request.getSession().setAttribute("portfolio", p);
            }
        }

        try {
            String ruta = request.getRequestURI();
            int index = ruta.indexOf("service/");
            if (index!=-1)
                ruta = ruta.substring(0,index);
            response.sendRedirect(ruta+"account.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_FOUND);
        baseRequest.setHandled(true);
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
    public static void showPayment(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        String policyName = request.getParameter("policy");
        try {
            policyName = URLDecoder.decode(policyName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String targetName = request.getParameter("target");
        try {
            targetName = URLDecoder.decode(targetName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String body = "";
        try {
            body = FileUtils.readFileToString(new File("htdocs/payment.html"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        body = body.replaceAll("<!--TEMPLATEHERE1-->", policyName);
        body = body.replaceAll("<!--TEMPLATEHERE2-->", targetName);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=utf-8");
        try {
            response.getWriter().print(body);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        baseRequest.setHandled(true);
        return;
    }

    /**
     * Gets the dataset
     */
    public static void getDataset(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        Portfolio portfolio = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        if (portfolio == null) {
            portfolio = new Portfolio();
        }

        String datasetName = request.getParameter("dataset");
        try {
            datasetName = URLDecoder.decode(datasetName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        Asset asset = AssetManager.findAsset(datasetName);
        if (asset == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);
        }


        AuthorizationResponse ar = Authorizer.AuthorizeResource(datasetName, portfolio.policies);
        boolean ok = ar.ok;
        List<Policy> policiesForAsset = asset.getPolicies();

        if (!ok) {            //The authorization has failed, we need now to offer the payment
            if (policiesForAsset == null || policiesForAsset.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                baseRequest.setHandled(true);
                return;
            }
            String uripolicy = policiesForAsset.get(0).getURI();

            for (Policy policyX : policiesForAsset) {
                Policy policytmp = PolicyManagerOld.getPolicy(policyX.getURI());
                if (policytmp == null) {
                    Logger.getLogger("ldr").warn("No se ha encontrado la política " + policyX.getURI());
                    try {
                        response.sendRedirect("/ldr/en/account.html");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    response.setStatus(HttpServletResponse.SC_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                if (!policytmp.isPerTriple()) {
                    uripolicy = policyX.getURI();
                }
            }


            try {
                uripolicy = URLEncoder.encode(uripolicy, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

            String uritarget = datasetName;
            try {
                uritarget = URLEncoder.encode(uritarget, "UTF-8");
                response.sendRedirect("/ldr/service/showPayment?policy=" + uripolicy + "&target=" + uritarget);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_FOUND);
            baseRequest.setHandled(true);
            Evento.addEvento("Dataset " + datasetName + " denied", 0.0);
            return;
        } else { //the authorization was fine!
            List<Statement> lst = AssetManager.getTriples(datasetName);
            Model model = ModelFactory.createDefaultModel();
            for (Statement st : lst) {
                model.add(st);
            }

            //ADD LICENSE INFORMATION
            Resource rdataset = model.createResource(datasetName);
            rdataset.addProperty(RDF.type, RDFUtils.RDATASET);
            for (Policy p : ar.policies) {
                Resource rpolicy = ODRLRDF.getResourceFromPolicy(p);
                model.add(rpolicy.getModel());
                rdataset.addProperty(RDFUtils.PLICENSE, rpolicy);
            }
            //ADD PROVENANCE INFORMATION
            Resource activity = model.createResource();
            rdataset.addProperty(ODRLModel.PWASGENERATEDBY, activity);
            activity.addProperty(ODRLModel.PWASASSOCIATEDWITH, "http://conditional.linkeddata.es");
            Literal l = model.createTypedLiteral(new Date(), XSDDatatype.XSDdate);
            activity.addProperty(ODRLModel.PENDEDATTIME, l);


            try {
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, model, Lang.TTL);
                String ttl = sw.toString();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/turtle;charset=utf-8");
                response.getWriter().print(ttl);
                baseRequest.setHandled(true);
                Evento.addEvento("Dataset " + datasetName + " served", 0.0);
            } catch (Exception e) {
                e.printStackTrace();
                //          view.flashStatus("Error saving file");
            }
        }
    }

    public static void getDataset2(ConditionalDataset cd, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        Portfolio portfolio = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        if (portfolio == null) {
            portfolio = new Portfolio();
        }
        AuthorizationResponse ar = new AuthorizationResponse();
        String datasetName = request.getParameter("dataset");
        try {
            datasetName = URLDecoder.decode(datasetName, "UTF-8");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);
            e.printStackTrace();
            return;
        }
        Grafo grafo = cd.getDatasetVoid().getGrafo(datasetName);
        
        boolean ok = grafo.isOpen();
        if (ok)
        {
            ar.ok=ok;
            ar.policies=grafo.getOpenPolicies();
        }

        if (!ok)
        {
            ar = Authorizer.AuthorizeResource(datasetName, portfolio.policies);
            ok = ar.ok;
        }
        if (!ok) {            //The authorization has failed, we need now to offer the payment
            List<Policy> policiesForAsset = grafo.getPolicies();
            if (policiesForAsset == null || policiesForAsset.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                baseRequest.setHandled(true);
                return;
            }
            String uripolicy = policiesForAsset.get(0).getURI();

            for (Policy policyX : policiesForAsset) {
                Policy policytmp = PolicyManagerOld.getPolicy(policyX.getURI());
                if (policytmp == null) {
                    Logger.getLogger("ldr").warn("No se ha encontrado la política " + policyX.getURI());
                    try {
                        response.sendRedirect("account.html");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    response.setStatus(HttpServletResponse.SC_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                if (!policytmp.isPerTriple()) {
                    uripolicy = policyX.getURI();
                }
            }
            try {
                uripolicy = URLEncoder.encode(uripolicy, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            String uritarget = datasetName;
            try {
                uritarget = URLEncoder.encode(uritarget, "UTF-8");
                response.sendRedirect("service/showPayment?policy=" + uripolicy + "&target=" + uritarget);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_FOUND);
            baseRequest.setHandled(true);
            Evento.addEvento("Dataset " + datasetName + " denied", 0.0);
            return;
        } else { //the authorization was fine!
            
            List<Statement> lst = cd.getDatasetDump().getTriplesInGrafoSinIndexar(grafo.getURI());
            Model model = ModelFactory.createDefaultModel();
            for (Statement st : lst) {
                model.add(st);
            }
            RDFUtils.addPrefixesToModel(model);

            //ADD LICENSE INFORMATION
            Resource rdataset = model.createResource(datasetName);
            rdataset.addProperty(RDF.type, RDFUtils.RDATASET);
            for (Policy p : ar.policies) {
                Resource rpolicy = ODRLRDF.getResourceFromPolicy(p);
                model.add(rpolicy.getModel());
                rdataset.addProperty(RDFUtils.PLICENSE, rpolicy);
            }
            //ADD PROVENANCE INFORMATION
            Resource activity = model.createResource();
            rdataset.addProperty(ODRLModel.PWASGENERATEDBY, activity);
            activity.addProperty(ODRLModel.PWASASSOCIATEDWITH, LDRConfig.getServer());
            Literal l = model.createTypedLiteral(new Date(), XSDDatatype.XSDdate);
            activity.addProperty(ODRLModel.PENDEDATTIME, l);


            try {
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, model, Lang.TTL);
                String ttl = sw.toString();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/turtle;charset=utf-8");
                response.getWriter().print(ttl);
                baseRequest.setHandled(true);
                Evento.addEvento("Dataset " + datasetName + " served", 0.0);
            } catch (Exception e) {
                e.printStackTrace();
                //          view.flashStatus("Error saving file");
            }
        }
    }

 /**
 * Export portfolio
 * @api {get} /{dataset}/service/resetPorfolio resetPortfolio
 * @apiName /resetPortfolio
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Resets the  portfolio of the currently logged in user, deleteing any offer he might have purchased.<br/>
 * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
 *
 * @apiParam {String} dataset One word name of the document to be exported
 * @apiParam {String} signed True if the exported RDF document is to be signed
 *
 * @apiSuccess {String} void Nothing is returned
 */
    public static void resetPortfolio(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        Portfolio p = new Portfolio();
        request.getSession().setAttribute("portfolio", p);
        String mail = GoogleAuthHelper.getMail(request);
        Portfolio.setPortfolio(mail, new Portfolio());
        try {
            response.sendRedirect("../account.html");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ServiceHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setStatus(HttpServletResponse.SC_FOUND);
        baseRequest.setHandled(true);
    }


/**
 * Export portfolio
 * @api {get} /{dataset}/service/exportPorfolio exportPortfolio
 * @apiName /exportPortfolio
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Exports a RDF document, signed, where the purchased policies are shown.<br/> 
 * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
 *
 * @apiParam {String} dataset One word name of the document to be exported
 * @apiParam {String} signed True if the exported RDF document is to be signed
 *
 * @apiSuccess {String} portfolio RDF document with the policies in hand of a the user<br/>
 */
    public static void exportPortfolio(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        String state = (String) request.getSession().getAttribute("state");
        String signed = (String) request.getParameter("signed");

        String email = GoogleAuthHelper.getMail(request);
        Portfolio p = Portfolio.getPortfolio(email);
        String ttl = p.toTurtle();
        if (signed != null && signed.equals("true")) {
            ttl = p.toSignedRDF();
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/turtle;charset=utf-8");
        try {
            response.getWriter().print(ttl);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        baseRequest.setHandled(true);

    }

    static void editDataset(String rdataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static boolean uploadFile(HttpServletRequest request, String namedest) {
            try {
                List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                logger.info("Se ha subido " + items.size());
                ConditionalDataset ds = null;
                for (FileItem fi : items) {
                    if (fi.isFormField()) {
                        String name = fi.getFieldName();
                        String value = fi.getString();
                        logger.info(name + " " + value);
                        if (name.equals("dataset")) {
                            ds = ConditionalDatasets.getDataset(value);
                            if (ds==null)
                            {
                                ds = ConditionalDatasets.addDataset(value);
                            }
                        }
                    } else {
                        String fieldName = fi.getFieldName();
                        String fileName = fi.getName();
                        String contentType = fi.getContentType();
                        boolean isInMemory = fi.isInMemory();
                        long sizeInBytes = fi.getSize();
                        logger.info("Uploading file of " + sizeInBytes / (1024 * 1024) + " Mb, titled " + fileName);
                        InputStream uploadedStream = fi.getInputStream();
                        String folder = ".";
                        if (ds != null) {
                            folder = ds.getFolder();
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
                java.util.logging.Logger.getLogger(ServiceHandler.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }        
    }

    static boolean datasetIndex(ConditionalDataset ds) {
        DatasetIndex dsi=ds.getDatasetIndex();
        dsi.indexar();
        File f = new File(ds.getDumpPath());
        if (f.length()<100000000) //100Mb
        {
            Map<String, List<Integer>> map=dsi.createIndexGrafos();
            dsi.writeIndexGrafos(map);
        }
        List<String> grafos = dsi.getIndexedGrafos();        
        for(String grafo : grafos)
        {
            ds.getDatasetVoid().ensureGrafo(grafo);
        }
        ds.getDatasetVoid().setMetadataLiteral("http://rdfs.org/ns/void#triples",""+ds.getDatasetDump().ntriples);
        ds.getDatasetVoid().write();
        ConditionalDatasets.loadDatasets();
        return true;
    }

    /**
     * Devuelve un fragmento de json con el elemento i-esimo
     * http://salonica.dia.fi.upm.es/iate/resource/Elektroprothese-de
     */
    static String getResourceJSON(ConditionalDataset ds, int i) {
        String json = "";
        DatasetIndex dsi = ds.getDatasetIndex();
  //      List<String> sujetos = dsi.getIndexedSujetos();
        String sujeto = dsi.getIndexedSujetos().get(i);
        int triples = dsi.getNQuadsForSujeto(sujeto).size();
        
        try{sujeto=URLDecoder.decode(sujeto, "UTF-8");}catch(Exception e){e.printStackTrace();}
        sujeto = "<a href='"+sujeto+ "'>"+sujeto+"</a>";
        
        sujeto = quote(sujeto);
        
        json+=  "    {\n" +
                "      \"id\":" +i+",\n" +
//                "      \"uno\": \"  "+ sujeto +"\",\n" + //sender
                "      \"uno\": "+ sujeto +",\n" + //sender
                "      \"dos\": \""+ triples +"\"\n" + //receiver
                "    }";
        
        return json;
    }
    
    static List<String> getResourcesJSON(ConditionalDataset ds, int i, int num) {
        List<String> ls = new ArrayList();
        String json = "";
        DatasetIndex dsi = ds.getDatasetIndex();
        
        for(int k=i;k<i+num;k++)
        {
            String sujeto = dsi.getIndexedSujetos().get(k);
            int triples = dsi.getNQuadsForSujeto(sujeto).size();
            try{sujeto=URLDecoder.decode(sujeto, "UTF-8");}catch(Exception e){e.printStackTrace();}
            sujeto = "<a href='"+sujeto+ "'>"+sujeto+"</a>";
            sujeto = quote(sujeto);

            json+=  "    {\n" +
                    "      \"id\":" +k+",\n" +
    //                "      \"uno\": \"  "+ sujeto +"\",\n" + //sender
                    "      \"uno\": "+ sujeto +",\n" + //sender
                    "      \"dos\": \""+ triples +"\"\n" + //receiver
                    "    }";
            ls.add(json);
            logger.info("cargando... " + k);
        }
        
        return ls;
    }

    
public static String quote(String string) {
         if (string == null || string.length() == 0) {
             return "\"\"";
         }

         char         c = 0;
         int          i;
         int          len = string.length();
         StringBuilder sb = new StringBuilder(len + 4);
         String       t;

         sb.append('"');
         for (i = 0; i < len; i += 1) {
             c = string.charAt(i);
             switch (c) {
             case '\\':
             case '"':
                 sb.append('\\');
                 sb.append(c);
                 break;
             case '/':
 //                if (b == '<') {
                     sb.append('\\');
 //                }
                 sb.append(c);
                 break;
             case '\b':
                 sb.append("\\b");
                 break;
             case '\t':
                 sb.append("\\t");
                 break;
             case '\n':
                 sb.append("\\n");
                 break;
             case '\f':
                 sb.append("\\f");
                 break;
             case '\r':
                sb.append("\\r");
                break;
             default:
                 if (c < ' ') {
                     t = "000" + Integer.toHexString(c);
                     sb.append("\\u" + t.substring(t.length() - 4));
                 } else {
                     sb.append(c);
                 }
             }
         }
         sb.append('"');
         return sb.toString();
     }    
}
