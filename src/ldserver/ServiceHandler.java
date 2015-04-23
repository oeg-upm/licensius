package ldserver;

import ldserver.ws.GetOffers;
import ldserver.ws.GetResources;
import ldserver.ws.GetOpenResource;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.apache.log4j.Logger;

import ldconditional.Main;
import ldrauthorizer.ws.LDRServices;


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
        logger.info("Service handler");
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        String uri = request.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);


        if (string.contains("/service/getOffers"))
        {
            GetOffers go = new GetOffers();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }
        if (string.contains("/service/getResources"))
        {
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
            GetOpenResource go = new GetOpenResource();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }

        //****************** RESET PORTFOLIO
        if (string.contains("/service/resetPortfolio")) {
            LDRServices.resetPortfolio(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        //****************** FAKE PAYMENT
        if (string.contains("/service/fakePayment")) {
            LDRServices.fakePayment(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** SHOW PAYMENT
        if (string.contains("/service/showPayment")) {
            LDRServices.showPayment(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** GET DATASET2 (NO IMPLEMENTADO)
        /*if (string.contains("dataset/")) {
            LDRServices.getDataset2(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }*/


        //****************** GET DATASET
        if (string.contains("/service/getDataset")) {
            LDRServices.getDataset(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //****************** EXPORT PORTFOLIO
        if (string.contains("/service/exportPortfolio")) {
            LDRServices.exportPortfolio(baseRequest, request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }



        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }   
}
