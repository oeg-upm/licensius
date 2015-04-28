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
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
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
import ldconditional.model.ConditionalDataset;
import ldconditional.model.Grafo;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.ODRLModel;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.rdf.RDFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author Victor
 */
public class ServiceHandlerImpl {


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

    private static void addPrefixes(Model model) {
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("ldp", "http://www.w3.org/ns/ldp#");
        model.setNsPrefix("foaf", "http://xmlns.com/foaf/spec/");
        model.setNsPrefix("schema", "http://schema.org/");
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
            
            List<Statement> lst = cd.getDatasetDump().getTriplesInGrafo(grafo.getURI());
            Model model = ModelFactory.createDefaultModel();
            for (Statement st : lst) {
                model.add(st);
            }
            addPrefixes(model);

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

}
