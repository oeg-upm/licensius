package rascaweb;

//HTMLUNIT "http://htmlunit.sourceforge.net";
import vroddon.web.utils.Downloader;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//JAVA
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vroddon
 */
public class RascaWeb {

    /**
     * Rasca los datatesets de una página web
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       final String PAGINA="http://www.w3.org/wiki/DataSetRDFDumps";

       final WebClient webClient = new WebClient();
        try {
            final HtmlPage page = webClient.getPage(PAGINA);
            List<HtmlAnchor> enlaces=page.getAnchors();
            for(HtmlAnchor a : enlaces)
            {
                URL url=RascaWeb.isRDF(a);
                if (url!=null)
                {
                    File f=null;
                    System.out.print("Descargando " + url);
                    try{
                        f=new File(url.getFile());
                        String sf=f.getName();
                        System.out.println(" como " + sf);
                        boolean ok=Downloader.Descargar(url.toString(), sf);
//                    FileUtils.copyURLToFile(url, new File(url.getFile()));
                    if (ok==true)
                        System.out.println("Descargado");
                    }catch(Exception e)
                    {
                        if (f!=null)
                            f.delete();
                        System.out.println("Error");
                    }

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RascaWeb.class.getName()).log(Level.SEVERE, null, ex);
        };
        System.out.println("Test ejecutado");
    }


    public static String getExtension(String fileName)
    {
    String extension = "";
    int i = fileName.lastIndexOf('.');
    if (i > 0) {
        extension = fileName.substring(i+1);
        return extension;
    }
    return "";
    }

    private static URL isRDF(HtmlAnchor a) {
        if (!a.hasAttribute("href"))
            return null;
        String s=a.getHrefAttribute();
        if (s.startsWith("#"))
            return null;

        URL url=null;
        try {
            url = new URL(s);
        } catch (MalformedURLException ex) {
            return null;
//            System.out.println(s);
//            Logger.getLogger(RascaWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        String sf=url.getFile();
        if (RascaWeb.getExtension(s).equals("tgz"))
            return url;
        if (RascaWeb.getExtension(s).equals("rdf"))
            return url;
         if (RascaWeb.getExtension(s).equals("gz"))
            return url;
         if (RascaWeb.getExtension(s).equals("owl"))
            return url;
        return null;
    }



}
