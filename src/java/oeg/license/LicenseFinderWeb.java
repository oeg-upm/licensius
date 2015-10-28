package oeg.license;

//JAVA
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

//HTMLUNIT "http://htmlunit.sourceforge.net";
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//VRODDON
import java.lang.String;
import java.util.ArrayList;
//import org.apache.log4j.Logger;
//import vroddon.web.utils.Downloader;

/**
 * This class implements some methods to analyze the license of dumped RDFs etc.
 * @author vroddon
 */
public class LicenseFinderWeb {

    //Lista con las palabras clave para reconocer licencias en el texto
    public static List<String> key;

    public static List<String> licencias;

    public static void init(){
        key=new ArrayList();
        key.add("http://creativecommons.org/licenses/by/3.0/");
        key.add("http://creativecommons.org/licenses/by-nc/3.0/");
        key.add("http://creativecommons.org/licenses/by-sa/3.0/");
        key.add("licensing");
        key.add("license");
        key.add("terms of use");
        key.add("policy");
        
        licencias = new ArrayList();

        licencias.add("http://creativecommons.org/licenses/by/3.0/");
        licencias.add("http://creativecommons.org/licenses/by-nc/3.0/");
        licencias.add("http://creativecommons.org/licenses/by-sa/3.0/");
        licencias.add("http://creativecommons.org/licenses/by-nc-sa/3.0/");

        licencias.add("http://creativecommons.org/licenses/by/");
        licencias.add("http://creativecommons.org/licenses/by-nc/");
        licencias.add("http://creativecommons.org/licenses/by-sa/");
        licencias.add("http://creativecommons.org/licenses/by-nc-sa/");
        /*
        licencias.add("http://creativecommons.org/licenses/by/2.0/deed.en");
        licencias.add("http://creativecommons.org/licenses/by-nc-sa/3.0/es/");
        licencias.add("http://creativecommons.org/licenses/by-nc-sa/3.0/us/");
        licencias.add("http://creativecommons.org/licenses/by-nc-sa/2.5/es/");*/
    }

    
    
    
    /**
     * Determines whether there is or not a hint to have a licenses clause
     */
    public static String checkForLicenseHint(String PAGINA)
    {
        init();
        String salida="No license hint found";
        final WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnScriptError(false);
        try {
            final HtmlPage page = webClient.getPage(PAGINA);
            System.out.println("First, looking for anchors...");
            HtmlAnchor ancla=null;
            List<HtmlAnchor> anclas=page.getAnchors();
            for(HtmlAnchor mancla : anclas)
            {
                for(String licencia : licencias)
                {
                    String s = mancla.getHrefAttribute();
                    if (s.contains(licencia))
                    {
                        System.out.println("Hooorray!");
                        return "Found a link to this license: " + licencia;
                    }
                }
            }

            ancla=null;
            for(String licencia : licencias)
            {
                System.out.println("scanning " + licencia);
                try{
                    ancla=page.getAnchorByHref(licencia);
                }catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
                if(ancla!=null) 
                {
                    System.out.println("EUROEKA CON " + licencia);
                    return "Found a link to this license: " + licencia;
                }
            }
            System.out.println("Not found a link to a license");
            String s=page.asText();
            System.out.println(s);
            for(String ss : LicenseFinderWeb.key)
            {
               int indice=s.toLowerCase().lastIndexOf(ss.toLowerCase());
               if (indice!=-1)
                   return "No link to license found. But hints of having a license because of " + ss;
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "Error parsing webpage. " + e.getMessage();
        }
        return salida;
    }

    /**
     * Gets the URIs in a web page with RDF files
     */
    public static List<String> getLinkedRDF(String PAGINA) {
        List<String> ls = new ArrayList();
        final WebClient webClient = new WebClient();
        try {
            final HtmlPage page = webClient.getPage(PAGINA);
            List<HtmlAnchor> enlaces = page.getAnchors();
            for (HtmlAnchor a : enlaces) {
                URL url = LicenseFinderWeb.isRDF(a);
                if (url!=null)
                    ls.add(url.toString());
            }
        } catch (Exception ex) {
         //   Logger.getLogger("licenser").error("Error " + ex);
        };
        return ls;
    }

    /**
     * Downloads all the RDF in a web page into the data folder
     *//*
    public static String analyzeWeb(String PAGINA) {
        String s = "";

        final WebClient webClient = new WebClient();
        try {
            final HtmlPage page = webClient.getPage(PAGINA);
            List<HtmlAnchor> enlaces = page.getAnchors();
            for (HtmlAnchor a : enlaces) {
                URL url = WebLicenseAnalyzer.isRDF(a);
                if (url != null) {
                    File f = null;
                    Logger.getLogger("licenser").info("Downloading " + url);
                    try {
                        f = new File(url.getFile());
                        String sf = "data/" + f.getName();
                        boolean ok = Downloader.Descargar(url.toString(), sf);
                        if (ok == true) {
                            Logger.getLogger("licenser").info("Downloaded as " + sf);
                        }
                    } catch (Exception e) {
                        Logger.getLogger("licenser").error("Error");
                        if (f != null) {
                            f.delete();
                        }
                        Logger.getLogger("licenser").error("Error");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger("licenser").error("Error " + ex);
        };
        return s;
    }
*/
    /**
     * Gets the file extension of a given file name
     */
    private static String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
            return extension;
        }
        return "";
    }

    /**
     * Decides 
     */
    private static URL isRDF(HtmlAnchor a) {
        if (!a.hasAttribute("href")) {
            return null;
        }
        String s = a.getHrefAttribute();
        if (s.startsWith("#")) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException ex) {
            return null;
//            System.out.println(s);
//            Logger.getLogger(RascaWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        String sf = url.getFile();
        if (LicenseFinderWeb.getExtension(s).equals("tgz")) {
            return url;
        }
        if (LicenseFinderWeb.getExtension(s).equals("rdf")) {
            return url;
        }
        if (LicenseFinderWeb.getExtension(s).equals("gz")) {
            return url;
        }
        if (LicenseFinderWeb.getExtension(s).equals("owl")) {
            return url;
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final String PAGINA = "http://wiki.dbpedia.org/data-set-20";
        System.out.println(checkForLicenseHint(PAGINA));
//        WebLicenseAnalyzer.analyzeWeb(PAGINA);
    }
    
}
