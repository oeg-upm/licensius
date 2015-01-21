package ldrauthorizer.ws;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

///JETTY
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Escuchador de los servicios
 * @author vroddon
 */
public class CLDHandlerService extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
  //      LDRAuthorizerView view = (LDRAuthorizerView) LDRAuthorizerApp.getApplication().getMainView();
 //       LDRAuthorizerView.globalSessionManager.getSessionIdManager().
    //   baseRequest.setSessionManager(LDRAuthorizerView.globalSessionManager);
    //   HttpSession xexion = baseRequest.getSession();
                
        
        if (!string.startsWith("/ldr/service")) {
            return;
        }
        
        //****************** RESET PORTFOLIO
        if (string.startsWith("/ldr/service/resetPortfolio")) {
            LDRServices.resetPortfolio(baseRequest, request, response);
        }
        
        //****************** FAKE PAYMENT
        if (string.startsWith("/ldr/service/fakePayment")) {
            LDRServices.fakePayment(baseRequest, request, response);
        }
        
        //****************** SHOW PAYMENT 
        if (string.startsWith("/ldr/service/showPayment")) {
            LDRServices.showPayment(baseRequest, request, response);
            return;
        }
        
        //****************** GET DATASET2 (NO IMPLEMENTADO)
        if (string.startsWith("/ldr/dataset/")) {
            LDRServices.getDataset2(baseRequest, request, response);
        }
        
        
        //****************** GET DATASET
        if (string.startsWith("/ldr/service/getDataset")) {
            LDRServices.getDataset(baseRequest, request, response);
        }
        
        //****************** EXPORT PORTFOLIO
        if (string.startsWith("/ldr/service/exportPortfolio")) {
            LDRServices.exportPortfolio(baseRequest, request, response);
        }
        
    }
}
