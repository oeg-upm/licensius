package ckan;

//JAVA
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//JSON Simpler
import observatory.ObservatoryApp;
import observatory.ObservatoryCommands;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import vroddon.sw.Dataset;
import vroddon.sw.Modelo;
import vroddon.sw.RecursoDescrito;
import vroddon.web.utils.Downloader;

/**
 * Clase para obtener automáticamente datos del CKAN.
 * - Utiliza la API del CKAN (que puede dejar de funcionar en cualquier momento).
 *   La API del CKAN está descrita aquí: http://docs.ckan.org/en/ckan-1.5/api.html
 * - Utiliza la API de SimpleJSON.
 *   Descrita aquí: https://code.google.com/p/json-simple/wiki/
 * OEG - Diciembre 2012
 * 
 * @author Victor
 */
public class CKANExplorer {

    private static String CKANSITE="http://datahub.io";
 //   private static String CKANSITE = "http://linkeddatacatalog.dws.informatik.uni-mannheim.de";


    public static void downloadJSONFromCKAN(String filename, int number) {
        if (number<=1000)
        {
            String urltmp = CKANSITE + "/api/search/dataset?all_fields=1&rows="+number+"&start=0";
            getMiniJSONRapido(urltmp, filename);
            return;
        }
    }

    
    public static List<Dataset> getDatasetsFromCKAN(int number, boolean local) {
        String filename = "local/datasetsckan.json";
        List<Dataset> datasets = new ArrayList();
        
        if (number<=1000)
        {
            String urltmp = CKANSITE + "/api/search/dataset?all_fields=1&rows="+number+"&start=0";
            getMiniJSONRapido(urltmp, filename);
            datasets.addAll(CKANExplorer.getDatasetsFromFile("local/datasets.json"));
            return datasets;
        }
        int nqueries = 1+number/1000;
        for (int i=0;i<nqueries;i++)
        {
            int istart=1000*i;
            String fname="local/datasetsckan"+i+".json";
            String urltmp = CKANSITE + "/api/search/dataset?all_fields=1&rows=1000&start="+istart;
            if (!local)
                getMiniJSONRapido(urltmp, fname);
            datasets.addAll(CKANExplorer.getDatasetsFromFile("local/datasetsckan"+i+".json"));
        }
        return datasets;
    }

    /**
     * Método para testear la API. 
     */
    public static void othermain(String[] args) {
        List<String> ldatasets = CKANDatasets.getPreloadedDatasets("loddatasets.txt");
        for (String dataset : ldatasets) {
            String extension = "";
            String url = CKANExplorerold.getVoidURI(dataset);
            System.out.println(dataset + "\t" + url);
            int i = url.lastIndexOf('/');
            if (i > 0) {
                extension = url.substring(i + 1);
            }
            if (!url.isEmpty()) {
                Downloader.Descargar(url, "void/" + dataset + ".rdf");
            }
        }
        if (true) {
            return;
        }

        String urltmp = CKANSITE + "/api/3/search/dataset?all_fields=1&rows=1000&start=0&res_format=example%2Frdf%2Bxml";
        getMiniJSONRapido(urltmp, "d1.json");
        parseLocalJSON("d1.json");
//        http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=0&res_format=application%2Frdf%2Bxml

        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=0", "ckan1.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=1000", "ckan1.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=2000", "ckan2.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=3000", "ckan3.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=4000", "ckan4.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=5000", "ckan5.json");
        getMiniJSONRapido("http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=6000", "ckan6.json");
        parseLocalJSON("ckan1.json");
        parseLocalJSON("ckan2.json");
        parseLocalJSON("ckan3.json");
        parseLocalJSON("ckan4.json");
        parseLocalJSON("ckan5.json");
        parseLocalJSON("ckan6.json");
    }

    /**
     * Obtiene una lista de datasets y sus respectivas licencias.
     * @deprecated HACE QUERIES UNA A UNA Y ES UNA PESADILLA
     */
    public static void TESTgetResources() {
        List<String> ldatasets = CKANExplorer.getDatasetNames();
        for (String dataset : ldatasets) {
            List<String> lresources = CKANExplorer.getResourceTypes(dataset);
            String licencia = CKANExplorer.getLicenseFromCKAN(dataset);
            for (String resource : lresources) {
                System.out.println(dataset + "\t" + licencia + "\t" + resource);
            }
        }

    }

    /**
     * @deprecated HACE QUERIES UNA A UNA Y ES UNA PESADILLA
     */
    public static void TESTgetEndpoints() {
        List<String> ldatasets = CKANExplorer.getDatasetNames();
        for (String s : ldatasets) {
            String licencia = CKANExplorer.getLicenseFromCKAN(s);
            String endpoint = CKANExplorer.getEndpointFromCKAN(s);
            System.out.println(s + "\t" + licencia + "\t" + endpoint);
        }

    }

    /**
     * Almacena todos los JSONs en un único archivo llamado "totaljson.txt"
     * ESTE METODO TARDA VARIAS HORAS EN EJECUTARSE
     */
    public static void TESTstoreAllJSON() {
        List<String> ldatasets = CKANExplorer.getDatasetNames();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("infocompleta.json"));
            int conta = 0;
            for (String dataset : ldatasets) {
                String json = getFullJSON(dataset);
                out.write(json + "\n");
                System.out.println("Escritos " + ++conta);
            }
            out.close();
        } catch (IOException e) {
        }
    }

    public static List<Dataset> getDatasetsFromFile(String filename) {
        List datasets = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String output = br.readLine();
            if (output == null) {
                return datasets;
            }
            JSONObject obj = (JSONObject) JSONValue.parse(output);
            JSONArray array = (JSONArray) obj.get("results");
            int tam = array.size();
            for (int i = 0; i < tam; i++) {
                JSONObject jobj = (JSONObject) array.get(i);
                Dataset ds = parseDataset(jobj);
                if (ds != null) {
                    datasets.add(ds);
                }
            }
        } catch (Exception e) {
            System.err.println("HA HABIDO UN ERROR");

        }
        return datasets;
    }

    private static Dataset parseDataset(JSONObject jobj) {
        Dataset ds = new Dataset();
        String name = (String) jobj.get("name");
        String title = (String) jobj.get("title");
        ds.ckanjson = jobj.toJSONString();
        ds.title = name;
        ds.description = title;
        ds.license_id = (String) jobj.get("license_id");
        ds.license = (String) jobj.get("license");
        ds.license_title = (String) jobj.get("license_title");
        ds.uri = CKANSITE + "/dataset/" + name;

        JSONArray list = (JSONArray) jobj.get("tags");
        if (list != null) {
            int tam = list.size();
            for (int i = 0; i < tam; i++) {
                String recurso = (String) list.get(i);
                ds.tags.add(recurso.toString());
            }
        }

        list = (JSONArray) jobj.get("resources");
        if (list != null) {

            int tam = list.size();
            for (int i = 0; i < tam; i++) {
                JSONObject recurso = (JSONObject) list.get(i);
                RecursoDescrito r = new RecursoDescrito();
                r.title = (String) recurso.get("name");
                r.description = (String) recurso.get("description");
                r.url = (String) recurso.get("url");
                r.mimetype = (String) recurso.get("mimetype");
                if (r.mimetype.isEmpty()) {
                    r.mimetype = (String) recurso.get("format");
                }
                r.ckanid = (String) recurso.get("id");
                ds.recursos.add(r);
            }
            ds.alive = (tam > 0);

        }
        return ds;
    }

    /**
     * Parsea la información de datasets de un archivo local obtenido con el método getMiniJSONRapido
     * @param filename Nombre del archivo
     */
    public static void parseLocalJSON(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String output = br.readLine();
            if (output == null) {
                return;
            }
            JSONObject obj = (JSONObject) JSONValue.parse(output);
            JSONArray array = (JSONArray) obj.get("results");
//            String count = (String) obj.get("count");
//            JSONArray array = (JSONArray) obj;
            int tam = array.size();
            for (int i = 0; i < tam; i++) {
                JSONObject jobj = (JSONObject) array.get(i);
                String id = (String) jobj.get("id");
                id = (id == null ? "null" : id);
                String author = (String) jobj.get("author");
                author = (author == null ? "null" : author);
                String title = (String) jobj.get("title");
                title = (title == null ? "null" : title);
                String license_id = (String) jobj.get("license_id");
                license_id = (license_id == null ? "null" : license_id);
                JSONArray res_formats = (JSONArray) jobj.get("res_format");
                JSONArray res_urls = (JSONArray) jobj.get("res_url");
                if (res_formats == null) {
                    System.out.println(id + "\t" + title + "\t" + author + "\t" + license_id + "\t" + "null" + "\t" + "null");
                    continue;
                }
                for (int j = 0; j < res_formats.size(); ++j) {
                    String format = (String) res_formats.get(j);
                    String resurl = (String) res_urls.get(j);
                    System.out.println(id + "\t" + title + "\t" + author + "\t" + license_id + "\t" + format + "\t" + resurl);
//                    urls.add(tipo+"\t"+endpointurl);
                }
            }
        } catch (Exception e) {
            System.err.println("HA HABIDO UN ERROR");
        }
    }

    /**
     * Obtiene un archivo totaljson.txt con todos los datos de todos los datasets.
     * Obsérvese que los datos que se obtienen son menos que los que se obtendrían preguntando dataset a dataset
     * 
     * @param uri Query para obtener datos de los datasets. Ej: http://datahub.io/api/search/dataset?all_fields=1&rows=1000&start=0
     * @param filename Nombre del archivo donde se guardarán los datos Ej: "ckan1.json"
     */
    private static void getMiniJSONRapido(String uri, String filename) {
        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                return;
            }
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            if ((output = br.readLine()) != null) {
            }
            out.write(output + "\n");
            out.close();
            br.close();
        } catch (Exception e) {
        }
        return;
    }

    /**
     * Obtiene la descripción completa de un dataset 
     * 
     * @param dataset Nombre del dataset para el cual se quieren detalles
     * @return Una cadena con el JSON completo
     */
    public static String getFullJSON(String dataset) {
        String licencia = "";
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                return "error in dataset " + conn.getResponseCode();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
//                obj = JSONValue.parse(output);
            }
//            JSONObject jobj = (JSONObject) obj;
//            String sjson=(String)obj;
            conn.disconnect();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return licencia;
    }

    /**
     * Obtiene la licencia de un dataset, según CKAN (y no por inspección de los datos).
     * 
     * @param dataset Nombre del Dataset segun CKAN. Por ejemplo "yago"
     * @return Un texto describiendo la licencia
     */
    public static String getLicenseFromCKAN(String dataset) {
        String licencia = "";
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                return "error in dataset " + conn.getResponseCode();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }

            JSONObject jobj = (JSONObject) obj;
            licencia = (String) jobj.get("license");
            String formato = (String) jobj.get("author");
            String urlx = (String) jobj.get("download_url");
            String licenseurlx = (String) jobj.get("license_url");
            String licensetitle = (String) jobj.get("license_title");
            String licenseid = (String) jobj.get("license_id");
            //System.out.println(dataset + "\t" + licencia + "\t" + licenseurlx + "\t" + formato + "\t" + urlx);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return licencia;

    }

    /**
     * Gets a Dataset Object from a JSON text given as the input param
     * @param output Input JSON
     * @return A Dataset
     */
    public static Dataset getDatasetFromJSON(String output) {
        Dataset ds = new Dataset();
        Object obj = JSONValue.parse(output);
        JSONObject jobj = (JSONObject) JSONValue.parse(output);
        String name = (String) jobj.get("name");
        String title = (String) jobj.get("title");
        ds.ckanjson = output;
        ds.title = name;
        ds.description = title;
        ds.license_id = (String) jobj.get("license_id");
        ds.license = (String) jobj.get("license");
        ds.license_title = (String) jobj.get("license_title");
        ds.uri = CKANSITE + "/dataset/" + name;

        JSONArray list = (JSONArray) jobj.get("tags");
        int tam = list.size();
        for (int i = 0; i < tam; i++) {
            String recurso = (String) list.get(i);
            ds.tags.add(recurso.toString());
        }

        list = (JSONArray) jobj.get("resources");
        tam = list.size();
        for (int i = 0; i < tam; i++) {
            JSONObject recurso = (JSONObject) list.get(i);
            RecursoDescrito r = new RecursoDescrito();
            r.title = (String) recurso.get("name");
            r.description = (String) recurso.get("description");
            r.url = (String) recurso.get("url");
            r.mimetype = (String) recurso.get("mimetype");
            if (r.mimetype==null || r.mimetype.isEmpty()) {
                r.mimetype = (String) recurso.get("format");
            }
            r.ckanid = (String) recurso.get("id");
            ds.recursos.add(r);
        }
        ds.alive = (tam > 0);
        return ds;
    }

    // ej: 81f850c1-501d-499c-8f6c-b6a06e2805fa
    public static Dataset getDatasetFromCKAN(String id) {
        ObservatoryApp.getReportador().flashStatus("Querying CKAN about " + id + " ...");

        try {
            String uri = CKANSITE + "/api/rest/package/" + id;
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                Logger.getLogger("licenser").warn("error from the api");
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = br.readLine();
            if (output == null) {
                return null;
            }

            Dataset ds = getDatasetFromJSON(output);
            return ds;


        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger("licenser").info("error in " + e.getLocalizedMessage());
        }


        return null;
    }

    public static void main(String[] args) {
        List<String> ls = getDatasetsFromAGroup("linguistics");
        for (String id : ls) {
            Dataset ds = getDatasetFromCKAN(id);
            if (ds != null) {
                System.out.println(ds.toSummaryString());
            }
        }
        System.out.println(ls);
    }

    /**
     * Obtiene una lista con los ids de los datsaets en un grupo
     */
    public static List<String> getDatasetsFromAGroup(String grupo) {
        List<String> ls = new ArrayList();
        try {
            String uri = CKANSITE + "/api/3/action/member_list?id=" + grupo;
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                Logger.getLogger("licenser").warn("error from the api");
                return ls;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray list = (JSONArray) jobj.get("result");

            int tam = list.size();
            for (int i = 0; i < tam; i++) {
                JSONArray listlist = (JSONArray) list.get(i);
                String list1 = (String) listlist.get(0);
                ls.add(list1);
            }

        } catch (Exception e) {
            Logger.getLogger("licenser").info("error in " + e.getLocalizedMessage());
        }
        return ls;
    }

    /**
     * Obtiene una lista en bruto de todos los datasets
     * @return Lista de nombres de los datasets segun el CKAN
     */
    public static List<String> getDatasetNames() {
        List<String> datasets = new ArrayList();
        String uri = CKANSITE + "/api/rest/dataset/search/dataset";
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                System.err.println("error calling the api " + conn.getResponseCode());
                return datasets;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONArray list = (JSONArray) obj;
            int tam = list.size();
            for (int i = 0; i < tam; i++) {
                String s = (String) list.get(i);
                datasets.add(s);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datasets;

    }

    /**
     * Obtiene el endpoint de un dataset, si lo hay. 
     * Ojo, que si hay dos, devuelve solo el primero.
     * 
     * @param dataset Nombre del dataset segun CKAN
     * @return El endpoint (URL) o un texto "no endpoint"
     */
    public static String getEndpointFromCKAN(String dataset) {
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                System.err.println("error calling the api " + conn.getResponseCode());
                return "no endpoint";
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray recursos = (JSONArray) jobj.get("resources");
            for (int i = 0; i < recursos.size(); ++i) {
                JSONObject recurso = (JSONObject) recursos.get(i);
                String tipo = (String) recurso.get("format");
                if (tipo.equals("api/sparql")) {
                    String endpointurl = (String) recurso.get("url");
                    return endpointurl;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "no endpoint";
    }

    /**
     * Obtiene los tipos de recursos de un dataset del CKAN dado, junto con su URL (separado por tabs)
     * 
     * @param dataset Nombre del dataset segun CKAN
     * @return El endpoint (URL) o un texto "no endpoint"
     */
    public static List<String> getResourceTypes(String dataset) {
        List<String> urls = new ArrayList();
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {

                Logger.getLogger("licenser").info("error in dataset " + conn.getResponseCode());

                return urls;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray recursos = (JSONArray) jobj.get("resources");
            for (int i = 0; i < recursos.size(); ++i) {
                JSONObject recurso = (JSONObject) recursos.get(i);
                String tipo = (String) recurso.get("format");
                String endpointurl = (String) recurso.get("url");
                urls.add(tipo + "\t" + endpointurl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static List<RecursoDescrito> getResourceTypes2(String dataset) {
        List<RecursoDescrito> urls = new ArrayList();
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {

                Logger.getLogger("licenser").info("error in dataset " + conn.getResponseCode());

                return urls;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray recursos = (JSONArray) jobj.get("resources");
            for (int i = 0; i < recursos.size(); ++i) {
                JSONObject recurso = (JSONObject) recursos.get(i);
                String tipo = (String) recurso.get("format");
                String endpointurl = (String) recurso.get("url");
                RecursoDescrito r = new RecursoDescrito();
                r.mimetype = tipo;
                r.uri = endpointurl;
                urls.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Obtiene los archivos RDF en un dataset del CKAN dado
     * 
     * @param dataset Nombre del dataset segun CKAN
     * @return El endpoint (URL) o un texto "no endpoint"
     */
    public static List<String> getRDFFilesFromCKAN(String dataset) {
        List<String> urls = new ArrayList();
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                System.err.println("error calling the api " + conn.getResponseCode());
                return urls;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray recursos = (JSONArray) jobj.get("resources");
            for (int i = 0; i < recursos.size(); ++i) {
                JSONObject recurso = (JSONObject) recursos.get(i);
                String tipo = (String) recurso.get("format");
                if (CKANExplorer.isRDF(tipo)) {
                    String endpointurl = (String) recurso.get("url");
                    urls.add(endpointurl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Decide si un mimetype es un formato RDF o no
     * @param tipo Mimetype que se quiere consultar. Por ejemplo "text/ttl"
     * @todo ESTA PENDIENTE DE COMPLETA. ¿CUAL ES LA LISTA DE MIMETYPES QUE HEMOS DE ENTENDER?
     */
    public static boolean isRDF(String tipo) {
        if (tipo.equals("text/turtle")) {
            return true;
        }
        if (tipo.equals("7z:turtle")) {
            return true;
        }
        if (tipo.equals("application/rdf+xml")) {
            return true;
        }

        return false;
    }

    public static void listLicenses(int startin) {
        /*
        List<String> ls = CKANDatasets.getCKANDataset();
        int contador = 0;
        for (String dataset : ls) {
        contador++;
        if (contador < startin) {
        continue;
        }
        String licencia = CKANExplorer.getLicenseFromCKAN(dataset);
        System.err.println("" + contador + "/" + ls.size() + " verificando " + dataset + " " + licencia);
        }
         */
    }

    /**
     * Para el dataset del CKAN que se pase, devuelve
     */
    public static List<String> getVoidURIsFromCKANDataset(String dataset) {
        //    dataset="world-bank-linked-data";
        List<String> uris = new ArrayList();
        String licencia = "";
        String uri = CKANSITE + "/api/rest/package/" + dataset;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                Logger.getLogger("licenser").info("error in dataset " + conn.getResponseCode());
                return uris;
            }
            Logger.getLogger("licenser").info("Obtenido info del dataset " + dataset);
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String cadena = "";
            Object obj = null;
            if ((output = br.readLine()) != null) {
                obj = JSONValue.parse(output);
            }
            JSONObject jobj = (JSONObject) obj;
            JSONArray recursos = (JSONArray) jobj.get("resources");
            for (int i = 0; i < recursos.size(); ++i) {
                JSONObject recurso = (JSONObject) recursos.get(i);
                String tipo = (String) recurso.get("format");
                if (tipo.equals("meta/void")) {
                    String endpointurl = (String) recurso.get("url");
                    uris.add(endpointurl);
//                    return endpointurl;
                }
            }
            Logger.getLogger("licenser").info("Encontradas " + uris.size() + " uris de recursos void para el dataset " + dataset);
            if (true) {
                return uris;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return uris;
    }

    /**
     * Obtiene del CKAN información sobre este dataset
     */
    public static void updateDataFromCKAN(Dataset d) {
        List<String> ls = CKANExplorer.getVoidURIsFromCKANDataset(d.title);
        d.sparql = getEndpointFromCKAN(d.title);
        if (ls.isEmpty()) {
            d.alive = false;
        } else {
            d.alive = true;
            d.voiduri = ls.get(0);
        }
        boolean ok = ObservatoryCommands.loadVoid(d);
        if (ok) {
            d.uri = Modelo.getDatasetUri();
        }


    }
}
