package licenser;

//JENA
import ckan.CKANExplorer;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

//JAVA
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//LOG4J
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

//SW
import vroddon.sw.LODCloud;
import vroddon.sw.Modelo;

/**
 *
 * @author Victor
 */
public class LicenseFinder {

    //Mapa de datasets a la licencia que tienen.
    public static HashMap<String, String> mapLicenses = new HashMap<String, String>();

    //URIs Conocidas
    String URIdcrights = "http://purl.org/dc/terms/rights";
    String URIdclicense ="http://purl.org/dc/terms/license";
    String URIcclicense = "http://creativecommons.org/ns#license";
    String URIxhtmllicense = "www.w3.org/1999/xhtml/vocab/‎license";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ME QUITO LOS COMENTARIOS DE LOG4J
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        
        String s ="land-matrix";
        CKANExplorer.getLicenseFromCKAN(s);

        
//        LicenseFinder lf = new LicenseFinder();
//        lf.checkAllRDFInFolder();
    }

    /**
     * Looks for a license in each of the RDF files in a folder called RDF
     * @return Nothing, but it populates the "licensemap" variable
     */
    public void checkAllRDFInFolder() {
        List<String> archivos = LicenseFinder.getLODCloudFileNames();
        //OBTENGO LOS ARCHIVOS EN LA CARPETA
        for (String s : archivos) {
            boolean bOK = Modelo.parser("rdf/" + s);
            if (!bOK)
                continue;
            String lic = findLicense();
            String lds = s.substring(0, s.length() - 4);
            mapLicenses.put(lds, lic);
        }

    }

    /**
     * Returns the last chunk in a URL
     */
    private static String getLastBitFromUrl(final String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    /**
     * Retrieves all the file names in a given folder
     */
    public static List<String> getFileNamesInFolder(String sfolder) {
        List<String> lista = new ArrayList<String>();
        File folder = new File(sfolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String archivo = listOfFiles[i].getName();
                lista.add(archivo);
            } else if (listOfFiles[i].isDirectory()) {
            }
        }
        return lista;
    }

    /**
     * Gets the names of the dataset in the LOD
     */
    public static List<String> getLODCloudFileNames() {
        List<String> lista = new ArrayList<String>();
        for (String s : LODCloud.datasets) {
            s = s + ".rdf";
            lista.add(s);
        }
        return lista;
        /*
        File folder = new File("rdf");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
        String archivo=listOfFiles[i].getName();
        lista.add(archivo);
        //                System.out.println("File " + listOfFiles[i].getName());
        } else if (listOfFiles[i].isDirectory()) {
        // System.out.println("Directory " + listOfFiles[i].getName());
        }
        }
        return lista;*/
    }
//    private Model model = null;
    String baseuri=""; //this should be improved.

    public List<String> findLicenseStatementsForResource(String uri)
    {
        List<String> triples = new ArrayList();
        String licencia="";
        System.out.println("se ha encontrado que la ontología tiene esta uri " + uri);
        Resource res=Modelo.model.getResource(uri);
        StmtIterator itx=res.listProperties();
        String salidalic="";
        while(itx.hasNext())
        {
            Statement s=itx.next();
            String objeto=s.getObject().toString();
            String sujeto = s.getSubject().getURI();
            String propiedad = s.getPredicate().getURI();
            objeto = objeto.replace("\n", "").replace("\r", "").replace("\t","");
            if ( propiedad.equals(URIdcrights) || propiedad.equals(URIdclicense) || propiedad.equals(URIcclicense) || propiedad.equals(URIxhtmllicense))
            {
                salidalic=sujeto+"\t" + propiedad+"\t"+objeto+"\t";
                triples.add(salidalic);
            }
            System.out.println(sujeto + "\t" + propiedad + "\t" + objeto);
        }
        return triples;
    }      


    

    /**
     * Busca una licencia en una ontología / vocabulario etc.
     * Presupone que el modelo está precargado
     */
    public String findLicenseInOntology()
    {
        String licencia="";
        String uri="";
                String ontology = "http://www.w3.org/2002/07/owl#Ontology";
                String tipo = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
                Resource rontology = Modelo.model.getResource(ontology);
                Property ptipo = Modelo.model.getProperty(tipo);
                ResIterator it=Modelo.model.listSubjectsWithProperty(ptipo);
                while (it.hasNext()) {
                    Resource res = it.next();
                    Statement s=res.getProperty(ptipo);
                    String objeto=s.getObject().asResource().getURI();
                    String sujeto = s.getSubject().getURI();
                    String propiedad = s.getPredicate().getURI();
//                    System.out.println(sujeto + " " + propiedad + " " + objeto);
                    if (objeto.equals("http://www.w3.org/2002/07/owl#Ontology"))
                    {
                        uri=sujeto;
//                        System.out.println("Ontology URI es " + sujeto);
                    }
                }
        Resource res=Modelo.model.getResource(uri);
        StmtIterator itx=res.listProperties();
        String URIdcrights = "http://purl.org/dc/terms/rights";
        String URIdclicense ="http://purl.org/dc/terms/license";
        String URIcclicense = "http://creativecommons.org/ns#license";
        String URIxhtmllicense = "www.w3.org/1999/xhtml/vocab/‎license";
        
        
        String salidalic="";
        while(itx.hasNext())
        {
            Statement s=itx.next();
            String objeto=s.getObject().toString();
            String sujeto = s.getSubject().getURI();
            String propiedad = s.getPredicate().getURI();
            objeto = objeto.replace("\n", "").replace("\r", "").replace("\t","");
            if ( propiedad.equals(URIdcrights) || propiedad.equals(URIdclicense) || propiedad.equals(URIcclicense) || propiedad.equals(URIxhtmllicense)) 
            {
                salidalic+=propiedad+"\t"+objeto+"\t";
//                        System.out.println(sujeto + "\t" + propiedad + "\t" + objeto);
            }
            
        }
        System.out.println(Modelo.fileNameOrUri+"\t"+uri+"\t"+salidalic);
        
        return licencia;
    }
    

    public void printModel()
    {
        Modelo.model.write(System.out, "TURTLE");
    }

    /**
     * Busca una licencia
     */
    public String findLicense() {
        String salida="";

        String URIdataset = "http://www.w3.org/ns/dcat#DataSet";
        String URIdcrights = "http://purl.org/dc/terms/rights";
        String URIdclicense ="http://purl.org/dc/terms/license";
        String URIcclicense = "http://creativecommons.org/ns#license";
        String URIxhtmllicense = "www.w3.org/1999/xhtml/vocab/‎license";
        
/*        Resource Rdataset = model.getResource(URIdataset);
        Property Prights = model.getProperty(URIdcrights);

        Statement st2 = model.getProperty(Rdataset, Prights);

        ResIterator it = model.listSubjects();
        NodeIterator nodit = model.listObjects();
        StmtIterator propIt = null;
        Resource res = null;
*/
        salida += look(URIdcrights);
        salida += look(URIcclicense);
        salida += look(URIdclicense);
  //      salida += look(URIxhtmllicense);

        return salida;
    }
    
    /**
     * Busca por la aparición de un objeto
     */
    public String look(String URIrights)
    {
        Property Prights = Modelo.model.getProperty(URIrights);
        ResIterator it = Modelo.model.listSubjectsWithProperty(Prights);
        while (it.hasNext()) {
            Resource res = it.next();
            String nombre = getLastBitFromUrl(res.toString());
            if (nombre.equals(baseuri)) 
               return res.getPropertyResourceValue(Prights).toString();
            else
            {
                Resource recurso=res.getPropertyResourceValue(Prights);
                return "maybe " + recurso.toString();
            }
        }        
        return "";
    }
        
}

 