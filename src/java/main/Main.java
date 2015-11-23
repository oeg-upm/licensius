package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Manera de desplegar este archivo:
 * curl -T "dist/licensius.war" "http://vroddon:PASSWORD@vroddon.dev.mirubee.com:80/manager/text/deploy?path=/licensius&update=true"
 * @author vrodriguez
 */
public class Main {
 
public static void main(String[] args) {
        String title = "Not found";
        String uri = "http://purl.org/net/p-plan";
        
        //WE OBTAIN THE MOST PROBABLE LICENSE FOUND IN THE RDF DOCUMENT POINTED BY A URI
	String license=getFirstLicenseFound(uri);
        if (!license.isEmpty())
        {
            //IF WE HAPPEN TO HAVE FOUND ANYTHING, WE WANT TO KNOW THE LABEL
            title = getTitle(license);
        }
	System.out.println(title);
}
public static String getTitle(String uriToScan) {
	String output="unknown";
	try {
		String uri="http://www.licensius.com/api/license/getlicenseinfo?uri=";
		String encodedData = URLEncoder.encode(uriToScan);
		uri+=encodedData;
		System.out.println(uri);
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");            
		conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
		if (conn.getResponseCode() != 200) {
		 throw new RuntimeException("HTTP error code : "+ conn.getResponseCode());
		}

                String r="";
                String linea="";
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		while ((linea = br.readLine()) != null) {
                    r+=linea+"\n";
		}
                
                JSONParser parser=new JSONParser();
                Object obj=parser.parse(r);
                JSONObject array=(JSONObject)obj;                
                output = (String) array.get("label");
		conn.disconnect();
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return output;
}


public static String getFirstLicenseFound(String uriToScan) {
	String output="unknown";
	try {
		String uri="http://www.licensius.com/api/license/findlicenseinrdf?uri=";
		String encodedData = URLEncoder.encode(uriToScan);
		uri+=encodedData;
		System.out.println(uri);
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");            
		conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
		if (conn.getResponseCode() != 200) {
		 throw new RuntimeException("HTTP error code : "+ conn.getResponseCode());
		}

                String r="";
                String linea="";
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		while ((linea = br.readLine()) != null) {
                    r+=linea+"\n";
		}
                
                JSONParser parser=new JSONParser();
                Object obj=parser.parse(r);
                JSONArray array=(JSONArray)obj;                
                String encontrada = "";
                for(int i =0;i<array.size();i++)
                {
                    Object o = array.get(i);
                    String ll=(String)((HashMap)o).get("license");
                    if (!ll.isEmpty())
                    {
                        return ll;
                    }
                }
		conn.disconnect();
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return output;
}

/**
 * Invokes the getLicense method of the Licensius service, so that
 * the license in a RDF resource is found.
 * @param uriToScan URI to scan, for example http://purl.org/goodrelations/v1.owl
 */
public static String getLicense(String uriToScan) {
	String output="unknown";
	try {
		String uri="http://licensius.appspot.com/getLicense?content=";
		String encodedData = URLEncoder.encode(uriToScan);
		uri+=encodedData;
		System.out.println(uri);
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");            
		conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
		if (conn.getResponseCode() != 200) {
		 throw new RuntimeException("HTTP error code : "+ conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String linea = "";
		while ((linea = br.readLine()) != null) {
			output=linea;
		}
		conn.disconnect();
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return output;
}    
}
