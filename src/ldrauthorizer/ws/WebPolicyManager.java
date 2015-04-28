package ldrauthorizer.ws;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import languages.Multilingual;
import odrlmodel.Asset;
import odrlmodel.managers.AssetManager;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Manager of the control panel of resources and licenses
 */
public class WebPolicyManager {


 




    /**
     * Obtiene el HTML que presenta los detalles de un grafo (qué políticas aplican etc.)
     * @param selectedGrafo Idioma: en, es
     * @param lan Idioma: en, es
     */
    public static String getHTMLforGrafo(String selectedGrafo, String lan) {
        String churro = "";

        String labelGrafo = selectedGrafo;
        
        Asset asset = AssetManager.getAsset(selectedGrafo);
        if (asset!=null)
            labelGrafo = asset.getLabel("en");
        
        churro = "<big><strong>" + labelGrafo + "</big></strong><br/><hr/>";
        String comentario = "<strong>" + Multilingual.get(6, lan) + "</strong>: " + AssetManager.getGrafoComment(selectedGrafo);
        churro += comentario + "<br/>\n";
        String estadistica = "<strong>" + Multilingual.get(2, lan) + "</strong>: " + AssetManager.getGrafoNumTriples(selectedGrafo);
        churro += estadistica + "<br/>\n";

        //Dataset license
        String slicencia1 = "<strong>" + Multilingual.get(3, lan) + "</strong>: ";   //datasetlicense
        slicencia1 += AssetManager.getPolicyLabelsForAssetURI(selectedGrafo);
        churro += slicencia1 + "<br/>\n";
        String slicencia = "";
        churro += slicencia;
        String form = "";
        if (lan.equals("en")) {
            form = "<form name=\"input\" action=\"/ldr/manageren/managePolicy\" method=\"get\">";
        } else {
            form = "<form name=\"input\" action=\"/ldr/manager/managePolicy\" method=\"get\">";
        }

        String opciones = form + "<select name=\"licencia\" id=\"licencia\">";
        String defecto = AssetManager.getPolicyLabelForAssetURI(selectedGrafo);
        List<Policy> policies = PolicyManagerOld.getPolicies();
        for (Policy policy : policies) {
            String opcion = policy.getLabel("en");
            if (opcion.equals(defecto)) {
                opciones += "<option selected value=\"" + opcion + "\">" + opcion + "</option>";
            } else {
                opciones += "<option value=\"" + opcion + "\">" + opcion + "</option>";
            }
        }
        opciones += "</select>\n";
        churro += opciones;

        churro += "<input type=\"hidden\" id=\"selectedGrafo\" name=\"selectedGrafo\" value=\"<!--TEMPLATEHERE3-->\">";
        churro += "<input name=\"action\" type=\"submit\" value=\"" + Multilingual.get(12, lan) + "\"/>";                   //Añadir
        churro += "<input name=\"action\" type=\"submit\" value=\"" + Multilingual.get(13, lan) + "\"/>";                   //Quitar
        churro += "<input name=\"action\" type=\"submit\" value=\"" + "View" + "\"/>";                   //Ver
        
//        churro+="<a href=\"/ldr/service/resetAssignment\"><button>Restore default policies</button></a>";
        churro += "<input name=\"action\" type=\"submit\" value=\"" + "Restore default" + "\"/>"; 

        
        return churro;
    }
}