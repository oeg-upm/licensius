package ldconditional.handlers;

import ldconditional.ldserver.ws.GetOffers;
import ldconditional.ldserver.ws.GetResources;
import ldconditional.ldserver.ws.GetOpenResource;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.apache.log4j.Logger;

import ldconditional.Main;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;


/**
 * Handler that processes commands.
 * 
 * @author vroddon
 */
public class ServiceHandler extends AbstractHandler {
    
    static final Logger logger = Logger.getLogger(Main.class);
    
        public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.contains("/service")) {
            return;
        }
        
        String slog ="Service handler processing this URI "; 
        slog+=request.getRequestURI();
        Enumeration e = request.getParameterNames();
        while(e.hasMoreElements())
            slog+=e.nextElement()+" ";
        logger.info(slog);
        
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        String uri = request.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
        
        
        if (string.contains("/service/describeDataset"))
        {
            logger.info("describeDataset");
            String datas = request.getParameter("dataset");
            ConditionalDataset ds=ConditionalDatasets.setSelectedDataset(datas);
            String json = ds.getDatasetVoid().getJSON();
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(json);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }        

        if (string.contains("/service/chooseDataset"))
        {
            logger.info("chooseDataset");
            String datas = request.getParameter("dataset");
            ConditionalDatasets.setSelectedDataset(datas);
            response.setStatus(HttpServletResponse.SC_OK);
            HandlerPortal hp = new HandlerPortal();
            hp.serve(request, response, datas);
            return;
        }


        if (string.contains("/service/getOffers"))
        {
            logger.info("getOffers");
            GetOffers go = new GetOffers();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }
        if (string.contains("/service/getResources"))
        {
            logger.info("getResources");
            GetResources go = new GetResources();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }

        //****************** GET RESOURCE
        if (string.contains("/service/getResource"))
        {
            logger.info("getResource");
            GetOpenResource go = new GetOpenResource();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }

        //****************** RESET PORTFOLIO
        if (string.contains("/service/resetPortfolio")) {
            logger.info("resetPortfolio");
            ServiceHandlerImpl.resetPortfolio(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        //****************** FAKE PAYMENT
        if (string.contains("/service/fakePayment")) {
            logger.info("fakePayment");
            ServiceHandlerImpl.fakePayment(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** SHOW PAYMENT
        if (string.contains("/service/showPayment")) {
            logger.info("showPayment");
            ServiceHandlerImpl.showPayment(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        //****************** GET DATASET
        if (string.contains("/service/getDataset")) {
            logger.info("getDataset");
            ConditionalDataset cd = ConditionalDatasets.getDataset(sdataset);
            if (cd==null)
                return;
            ServiceHandlerImpl.getDataset2(cd, baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** EXPORT PORTFOLIO
        if (string.contains("/service/exportPortfolio")) {
            logger.info("exportPortfolio");
            ServiceHandlerImpl.exportPortfolio(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** 
        if (string.contains("/service/portalAction")) {
            logger.info("portal");
            String rdataset = request.getParameter("dataset");
            String raction = request.getParameter("action");
            logger.info(rdataset+" "+raction);
            if (raction.equals("edit"))
                ServiceHandlerImpl.editDataset(rdataset);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }   
}
