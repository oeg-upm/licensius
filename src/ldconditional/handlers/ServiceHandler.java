package ldconditional.handlers;

import java.io.File;
import java.io.FileOutputStream;
import ldconditional.ldserver.ws.GetOffers;
import ldconditional.ldserver.ws.GetResources;
import ldconditional.ldserver.ws.GetOpenResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.apache.log4j.Logger;

import ldconditional.Main;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Handler that processes commands.
 *
 * @author vroddon
 */
public class ServiceHandler extends AbstractHandler {

    static final Logger logger = Logger.getLogger(ServiceHandler.class);

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.contains("/service")) {
            return;
        }

        String slog = "Service handler processing this URI ";
        slog += request.getRequestURI();
        Enumeration e = request.getParameterNames();
        slog+=" with parameters:\n";
        while (e.hasMoreElements()) {
            String param = (String)e.nextElement();
            String value = request.getParameterValues(param).length==1 ? request.getParameterValues(param)[0] : "";
            slog += param + " " + value + "\n";
        }
        logger.info(slog);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        String uri = request.getRequestURI();
        int index = uri.indexOf('/', 1);
        String sdataset = uri.substring(1, index);
        
        if (string.contains("/service/datasetGetResources"))
        {
            logger.info("datasetGetResources");
            String datas = request.getParameter("dataset");
            String rowCount = request.getParameter("rowCount");
            String current = request.getParameter("current");
            int icurrent=Integer.valueOf(current);
            int irowcount=Integer.valueOf(rowCount);
            int total =1000;
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset("geo");
            total = ds.getDatasetIndex().getIndexedSujetos().size();
            response.setContentType("application/json;charset=utf-8");
            if (ds!=null)
            {

                String s = "{\n" +
                "  \"current\": "+icurrent+ ",\n" +
                "  \"rowCount\": "+  irowcount  +",\n" +
                "  \"rows\": [\n";
                for(int i=0;i<irowcount;i++)
                {
                    int indice=(icurrent-1)*irowcount+i;
                    if (indice>=total)
                        continue;
                    s+= ServiceHandlerImpl.getResourceJSON(ds, indice);
                    if (i==(irowcount-1) || indice==(total-1))
                        s+="\n";
                    else
                        s+=",\n";
                }
                s+= "  ],\n" +
                "  \"total\": "+total+"\n" +
                "}";
                
                
                //System.out.println(s);
                response.getWriter().print(s);
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            return;                
            }
            
        }
        
        
        if (string.contains("/service/datasetUpload")) {
            logger.info("logoUpload");
            boolean ok = ServiceHandlerImpl.uploadFile(request, "/data.nq");
            if (!ok)
            {
                 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                 baseRequest.setHandled(true);
            }
            return;
        }
        
        if (string.contains("/service/datasetIndex")) {
            logger.info("datasetIndex");
            String datas = request.getParameter("dataset");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            boolean ok = ServiceHandlerImpl.datasetIndex(ds);
            return;
        }
        
        if (string.contains("/service/logoUpload")) {
            logger.info("logoUpload");
            boolean ok = ServiceHandlerImpl.uploadFile(request, "/logo.png");
            return;
        }

        if (string.contains("/service/describeDataset")) {
            logger.info("describeDataset");
            String datas = request.getParameter("dataset");
            response.setContentType("application/json;charset=utf-8");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            if (ds!=null)
            {
                response.getWriter().print(ds.getDatasetVoid().getJSON());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);
            return;
        }
        
        if (string.contains("/service/datasetRemove")) {
            logger.info("datasetRemove");
            String datas = request.getParameter("dataset");
            ConditionalDatasets.deleteDataset(datas);
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        if (string.contains("/service/datasetRebase")) {
            logger.info("datasetRebase");
            String datas = request.getParameter("dataset");
            String datasuri = request.getParameter("uri");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            if (ds==null)
                return;
            boolean ok = ds.rebase(datasuri);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        

        if (string.contains("/service/setDatasetMetadata")) {
            logger.info("setDatasetMetadata");
            String datas = request.getParameter("dataset");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String newlabel = request.getParameter("newlabel");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            if(ds==null)
            {
                ds = ConditionalDatasets.addDataset(newlabel);
                ds.getDatasetVoid().init();
            }
            
            
            response.setContentType("application/json;charset=utf-8");
            if (ds != null) {
                ds.getDatasetVoid().setDescription(description);
                ds.getDatasetVoid().setTitle(title);
                
                String json = ds.getDatasetVoid().getJSON();
                response.getWriter().print(json);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (string.contains("/service/chooseDataset")) {
            logger.info("chooseDataset");
            String datas = request.getParameter("dataset");
            ConditionalDatasets.setSelectedDataset(datas);
            response.setStatus(HttpServletResponse.SC_OK);
            HandlerPortal hp = new HandlerPortal();
            hp.serve(request, response, datas);
            return;
        }

        if (string.contains("/service/getOffers")) {
            logger.info("getOffers");
            GetOffers go = new GetOffers();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }
        if (string.contains("/service/getResources")) {
            logger.info("getResources");
            GetResources go = new GetResources();
            go.doGet(request, response);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            return;
        }

        //****************** GET RESOURCE
        if (string.contains("/service/getResource")) {
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
            if (cd == null) {
                return;
            }
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
            logger.info(rdataset + " " + raction);
            if (raction.equals("edit")) {
                ServiceHandlerImpl.editDataset(rdataset);
            }
            if (raction.equals("browse")) {
                System.out.println("xxxxx\n\n\n");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }
}
