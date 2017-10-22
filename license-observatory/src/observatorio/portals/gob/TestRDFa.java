package observatorio.portals.gob;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.hp.hpl.jena.rdf.model.Model;
import java.util.List;
import org.jsoup.Jsoup;

/**
 *
 * @author vroddon
 */
public class TestRDFa {

    public static void main(String[] args) {
        System.out.println("Análisis de licencias en datos.gob.es");
        String url = "http://datos.gob.es/catalogo/a12002994-buques-pesqueros-de-la-comunidad";

        TestRDFa test = new TestRDFa();
        test.findLicenseFromHTML(url);
        
/*        final WebClient webClient = new WebClient();
        try {
            final HtmlPage page = webClient.getPage(url);
            String s = page.getHead().toString();
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();

        }*/
    }
    
    public void findLicenseFromHTML(String url)
    {
        try{
            String html = Jsoup.connect(url).get().html();
            String[] lines = html.split("\n");            
            System.out.print(url+"\t");
            for(String line:lines)
            {
                if (line.contains("url\": \""))
                {
                    if (!line.contains("catalogo"))
                        continue;
                    line = line.replace("http://datos.gob.es/es/catalogo/","");
                    line = line.replace("url\": \"", "");
                    line =  line.replace("\"", "");
                    line =  line.replace(",", "");
                    line =  line.trim();
                    System.out.print(line+"\t");
                }
                
                
                if (line.contains("\"license\": \""))
                {
                    line =  line.replace("\"license\": \"", "");
                    line =  line.replace("\"", "");
                    line =  line.replace(",", "");
                    line =  line.trim();
                    System.out.print(line+"\t");
                }
            } 
            System.out.print("\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
