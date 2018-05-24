package upm;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.apache.jena.rdf.model.ModelFactory;
import upm.model.Constraint;

import java.io.*;
import java.util.Map;


@RestController
public class AjaxController {

    @JsonView(Views.Public.class)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public AjaxResponseBody  test(@ModelAttribute("input") PolicyWrapper input,
                                  BindingResult result) throws JsonProcessingException {

        input.getLicense().processRules();

        ObjectMapper mapper = new ObjectMapper();

        System.out.println(result.hasErrors());

        System.out.println(result.getAllErrors().toString());

        System.out.println(input.toString());

        AjaxResponseBody res = new AjaxResponseBody();

        String jsonInString  = "["+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(input.getLicense());

        if(input.getConstraints()!=null) {
            for (Constraint con :
                    input.getConstraints()) {
                jsonInString +=","+ "\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(con);
            }
        }
        jsonInString+="]";





        try {
            String url = "http://odrlapi.appspot.com/validator";

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);


            StringBuffer resultstring = new StringBuffer();

            post.setHeader("Content-Type","application/ld+json");
            post.setHeader("Accept","application/json");
            HttpEntity entity = new ByteArrayEntity(jsonInString.getBytes("UTF-8"));
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            ObjectMapper mapperReq = new ObjectMapper();
            Map<String, Object> jsonMap = mapperReq.readValue(response.getEntity().getContent(), Map.class);
            System.out.println(jsonMap.toString());

            if(jsonMap.containsKey("valid")){
                res.valid= (jsonMap.get("valid") instanceof Boolean) && ((Boolean)jsonMap.get("valid")).booleanValue();
            }
            if(jsonMap.containsKey("text") && (jsonMap.get("text") instanceof String)){
                res.txt= ((String)jsonMap.get("text"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }





        if(input.getFormat()!=null && !input.getFormat().equals("JSON-LD")) {

            Model model = ModelFactory.createDefaultModel();

            InputStream in = null;
            try {
                in = new ByteArrayInputStream(jsonInString.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            model.read(in, null,"JSON-LD");

            StringWriter sw = new StringWriter();
            model.write(sw,input.getFormat());
            jsonInString=sw.toString();
        }



        res.msg= jsonInString;



        return res;
    }
}