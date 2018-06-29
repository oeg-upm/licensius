package odrlmodel.rdf;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
//import com.hp.hpl.jena.sparql.util.StringUtils;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;

//JAVA
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import ldreditor.EjecutadorOperacionLenta;
//import ldreditor.LDREditorApp;
//import ldreditor.LDREditorView;
//import ldreditor.OperacionLenta;

//APACHE COMMONS
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
//import org.apache.commons.exec.util.StringUtils;
import org.apache.commons.io.IOUtils;

//LOG4J
import org.apache.log4j.Logger;


/**
 * Clase que gestiona un modelo de ontology o dataset
 * @author Victor
 */
public class Modelo {

    public static Model model = null;
    public static String fileNameOrUri = null;
    static String ayuda;
    static boolean isOperationOK;

    
    public static String getFirstSubjectOf(String isa)
    {
        String uri = "";
//        String sontology = isa;
        String stipo = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
//        Resource rontology = Modelo.model.getResource(sontology);
        Property ptipo = Modelo.model.getProperty(stipo);
        ResIterator it = Modelo.model.listSubjectsWithProperty(ptipo);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(ptipo);
            String objeto = s.getObject().asResource().getURI();
            String sujeto = s.getSubject().getURI();
            String propiedad = s.getPredicate().getURI();
            if (objeto.equals(isa)) {
                uri = sujeto;
                return uri;
            }
        }        
        return "";
    }
    
    
    /**
     * Obtiene la URI de una ontología - si es que es una ontología
     * ñapa
     */
    public static String getOntologyUri() {
        String uri = getFirstSubjectOf("http://www.w3.org/2002/07/owl#Ontology");
        if (uri.isEmpty())
            uri=fileNameOrUri;
        return uri;
    }
    public static String getDatasetUri() {
        String uri = getFirstSubjectOf("http://rdfs.org/ns/void#Dataset");
        if (uri.isEmpty())
        {
            uri=getFirstSubjectOf("http://vocab.deri.ie/dcat#Dataset");
        }
        return uri;
    }
    
    /**
     * Carga el modelo a partir de una cadena.
     * 
     * @param source Cadena
     */
    public static boolean parseFromString(String source, final boolean ttl) {
        ayuda = source;
  //      EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(5, new OperacionLenta() {
  //          public Object metodolento() {
                try {
                    InputStream in = IOUtils.toInputStream(ayuda);
                    model = ModelFactory.createDefaultModel();
                    if (ttl==false)
                        model.read(in, null);
                    else
                        model.read(in,null,"TTL");
                    if (model.isEmpty()) {
                        Logger.getLogger("licenser").info("Model is empty");
                        isOperationOK = false;
                    } else {
                        isOperationOK = true;
                        Logger.getLogger("licenser").info("Model loaded ");
                    }
                } catch (Exception e) {
                    Logger.getLogger("licenser").warn("Error parseando modelo en " + e.getMessage());
                    isOperationOK = false;
                }
                return isOperationOK;
//            }

   //         public void done() {
   //             LDREditorView view = (LDREditorView) LDREditorApp.getApplication().getMainView();
   //             view.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     //           view.flashStatus("Modelo leído " + isOperationOK);
//                view.setSemaforo(isOperationOK ? 1 : -1);
//                progressBar.setValue(0);
    //        }  ;        });
//        try {
//            task.runWithTimeout();
//        } catch (Exception e) {
 //           Logger.getLogger("licenser").warn("Operation timed out");
  //      }
  //      return isOperationOK;
    }

    //ñapa
    public static boolean parserWithoutThread(String filename) {
        try{
        model = ModelFactory.createDefaultModel();
        Modelo.fileNameOrUri=filename;
        model=FileManager.get().loadModel( filename,"TTL");
        boolean ok=!model.isEmpty();
        return ok;
        }catch(Exception e)
        {
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Parsea un archivo RDF y lo carga en memoria
     */
    public static boolean parser(String fileoruri) {
        fileNameOrUri = fileoruri;
        Logger.getLogger("licenser").info("Parsing " + fileoruri);
        try {
            File f = new File(fileoruri);
            String nameext = f.getName();
            model = ModelFactory.createDefaultModel();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(new Task());
            try {
                System.out.println(future.get(3, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                System.out.println("Terminated!");
                executor.shutdownNow();
                return false;
            }
            executor.shutdownNow();
        } catch (Exception e) {
            System.out.println("error");
            return false;
        }
        return true;
    }

    /**
     * Busca el primer objeto para un recurso dado
     * Presupone que el modelo está precargado
     */
    public static String findFirstObjectForSubjectAndResource(String uri, String predicado) {
        Logger.getLogger("licenser").debug("Buscando para " + uri + " con " + predicado);
        Resource res = Modelo.model.getResource(uri);
        if (res == null) {
            return "";
        }
        StmtIterator itx = res.listProperties();
        String salidalic = "";
        while (itx.hasNext()) {
            Statement s = itx.next();
            String objeto = s.getObject().toString();
            String sujeto = s.getSubject().getURI();
            String propiedad = s.getPredicate().getURI();
            if (propiedad.equals(predicado)) {
                salidalic = sujeto + "\t" + propiedad + "\t" + objeto + "\t";
                objeto = objeto.replace("\n", "").replace("\r", "").replace("\t", "");
                return objeto;
                //System.out.println(salidalic);
            }
//            System.out.println(sujeto + "\t" + propiedad + "\t" + objeto);
        }
        return "";
    }
    
    static String removeIllegal(String s)
    {
        String out = s;
        out=out.replace("*", "");
        out=out.replace("?", "");
        out=out.replace("=", "");
        out=out.replace("&", "");
        out=out.replace("+", "");
        out=out.replace("%", "");
        out=out.replace("/", "");
        out=out.replace("\\", "");
        out=out.replace(":", "");
        if (out.length()>64)
            out=out.substring(1, 64);
        return out;
    }
    
    /**
     * Almacena una cadena en un archivo
     */
    public static boolean save(String modelo, String filename)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(modelo);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }

    
    
        
   /**
     * Carga una cadena de un archivo (con mecanismo de cache)
     */
    public static String loadTurtle(String folder, String uri, boolean bCache) {
        String modelo = "";
        String filename=folder+"\\"+removeIllegal(uri);
        File f = new File(filename);
        if (bCache==false || !f.exists()) {
            try {
                Logger.getLogger("licenser").info("Downloading file from " + uri);
                String line = "curl -s -o salida.txt -L -H \"Accept: application/x-turtle\" " + uri;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);
                modelo = Modelo.readFileAsString("salida.txt");
                Logger.getLogger("licenser").info("Downloaded file from " + uri);
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Error downloading RDF: " + e.getMessage());
                return "";
            }
            save(modelo,filename );
            
        }else
        {
            try {
                modelo=FileUtils.readWholeFileAsUTF8(f.getAbsolutePath());
//                modelo=FileUtils.readFileToString(f);
            } catch (IOException ex) {
                Logger.getLogger("licenser").info("Error reading file from cache: " + ex.getMessage());
            }
        }
        return modelo;
    }        
        
        /**
     * This method reads a text file and loads it into a String
     * @param file File name
     * @return String with the text contents of the file
     */
    public static String readFileAsString(String file) {
        String s = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            Logger.getLogger("licenser").warn(ex.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger("licenser").warn(ex.getMessage());
            }
        }
        return s;
    }


    /**
     * Carga una cadena de un archivo (con mecanismo de cache)
     */
    public static String loadXMLRDF(String folder, String uri) {
//        LDREditorView view = (LDREditorView) LDREditorApp.getApplication().getMainView();

        String modelo = "";
        String filename=folder+"\\"+removeIllegal(uri);
        File f = new File(filename);
        if (!f.exists()) {
            try {
                Logger.getLogger("licenser").info("Downloading file from " + uri);
                String line = "curl -s -o salida.txt -L -H \"Accept: application/rdf+xml\" " + uri;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);
                modelo = Modelo.readFileAsString("salida.txt");
                Logger.getLogger("licenser").info("Downloaded file from " + uri);
 ///               view.flashStatus("Descripción cargada de " + uri);
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Error downloading RDF: " + e.getMessage());
  //              view.flashStatus("Descripción NO se pudo cargar de " + uri);
                return "";
            }
            save(modelo,filename );
            
        }else
        {
            try {
   //             view.flashStatus("Descripción encontrada en la cache para " + uri);
                //modelo=FileUtils.readFileToString(f);
                modelo=FileUtils.readWholeFileAsUTF8(f.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger("licenser").info("Error reading file from cache: " + ex.getMessage());
            }
        }
        return modelo;
    }
}

class Task implements Callable<String> {

    @Override
    public String call() throws Exception {
        try {
            InputStream is = FileManager.get().open(Modelo.fileNameOrUri);
            int mid = Modelo.fileNameOrUri.lastIndexOf(".");
            String ext = Modelo.fileNameOrUri.substring(mid + 1, Modelo.fileNameOrUri.length());
            if (is != null) {
                if (ext.equals("nt")) {
                    Modelo.model.read(is, "TTL");
                } else {
                    Modelo.model.read(is, null);
                }
            } else {
                Logger.getLogger("licenser").error("Cannot read or understand " + Modelo.fileNameOrUri);
                return null;
            }
            is.close();
        } catch (Exception ex) {
            return null;
            //java.util.logging.Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ok";
    }
}