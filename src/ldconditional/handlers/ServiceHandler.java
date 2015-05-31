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

    static final Logger logger = Logger.getLogger(Main.class);

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.contains("/service")) {
            return;
        }

        String slog = "Service handler processing this URI ";
        slog += request.getRequestURI();
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            slog += e.nextElement() + " ";
        }
        logger.info(slog);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        String uri = request.getRequestURI();
        int index = uri.indexOf('/', 1);
        String sdataset = uri.substring(1, index);

        if (string.contains("/service/logoUpload")) {
            logger.info("logoUpload");
            try {
                List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                System.out.println("Se ha subido " + items.size());
                ConditionalDataset ds = null;
                for (FileItem fi : items) {
                    if (fi.isFormField()) {
                        String name = fi.getFieldName();
                        String value = fi.getString();
                        logger.info(name + " " + value);
                        if (name.equals("dataset")) {
                            ds = ConditionalDatasets.getDataset(value);
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
                        File flogo = new File(folder + "/logo.png");
                        OutputStream os = new FileOutputStream(flogo);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = uploadedStream.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        IOUtils.closeQuietly(os);
                        uploadedStream.close();
                    }
                }
            } catch (FileUploadException ex) {
                System.out.println("Error " + ex.getLocalizedMessage());
                java.util.logging.Logger.getLogger(ServiceHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (string.contains("/service/describeDataset")) {
            logger.info("describeDataset");
            String datas = request.getParameter("dataset");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(ds.getDatasetVoid().getJSON());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (string.contains("/service/setDatasetMetadata")) {
            logger.info("setDatasetMetadata");
            String datas = request.getParameter("dataset");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            ConditionalDataset ds = ConditionalDatasets.setSelectedDataset(datas);
            response.setContentType("application/json;charset=utf-8");
            if (ds != null) {
                ds.getDatasetVoid().setDescription(description);
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
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }
}
