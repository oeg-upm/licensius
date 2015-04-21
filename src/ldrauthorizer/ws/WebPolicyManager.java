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
     * Obtiene el HTML que presenta una lista de grafos (como una lista de botones redondeados donde hay uno seleccionado)
     * @param selectedGrafo Idioma: en, es
     * @param lan Idioma: en, es
     */
    public static String getHTMLforGrafos(String lan) {
        List<String> labelGrafos = AssetManager.getGrafosLabels();
        List<String> uriGrafos = AssetManager.getGrafosURIs();
        int total = uriGrafos.size();
        String churro = "";
        for (int i = 0; i < total; i++) {

            try {
                String grafo = uriGrafos.get(i);
                String label = labelGrafos.get(i);
                String urigrafo = URLEncoder.encode(grafo, "UTF-8");
                String enlace = "";
                if (lan.equals("en")) {
                    enlace = "/ldr/manageren/selectGrafo?grafo=" + urigrafo;
                    enlace = "<a href=\"" + enlace + "\">" + label + "</a>";
                } else {
                    enlace = "/ldr/manager/selectGrafo?grafo=" + urigrafo;
                    enlace = "<a href=\"" + enlace + "\">" + label + "</a>";
                }
                if (grafo.equals(CLDHandlerManager.selectedGrafo)) {
                    churro += "<div style=\"border:2px solid; border-radius:25px; margin:10px; padding: 8px 8px 8px 8px; background:#0A0;\"><center>" + enlace + "</center></div>\n";
                } else {
                    churro += "<div style=\"border:2px solid; border-radius:25px; margin:10px; padding: 8px 8px 8px 8px; \"><center>" + enlace + "</center></div>\n";
                }
            } catch (Exception e) {
                Logger.getLogger("ldr").debug("Problema formateando un grafo");
            }
        }
        return churro;
    }

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
            form = "<form name=\"input\" action=\"/ldr/manageren/changeLicense\" method=\"get\">";
        } else {
            form = "<form name=\"input\" action=\"/ldr/manager/changeLicense\" method=\"get\">";
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