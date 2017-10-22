package vroddon.web.googlesearch;

//JAVA
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;


//GOOGLE GSON: http://code.google.com/p/google-gson/
import com.google.gson.Gson;

import vroddon.web.googlesearch.GoogleResults.Result;

/**
 * Ejemplo de uso de la API de Google.
 * @author vroddon
 */
public class GoogleSearchAPI {
public static void main(String[] arg)  {
    try{
    String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
    String search = "filetype:rdf"; //stackoverflow
    String charset = "UTF-8";

    URL url = new URL(google + URLEncoder.encode(search, charset));
    Reader reader = new InputStreamReader(url.openStream(), charset);
    GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);

    // Show title and URL of 1st result.
    for(Result result : results.getResponseData().getResults())
    {
        System.out.println(result.getUrl());
    }
    }catch(Exception e){e.printStackTrace();}
}
}
