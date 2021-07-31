package oeg.rdflicense2.api;

import io.swagger.annotations.ApiParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Makes a transparent mirror of the queries to the Triplestore SPARQL endpoint
 * @author vroddon
 */
@Controller
public class Sparql {

    @RequestMapping("/sparql")
    public ResponseEntity mirror(@ApiParam(required = false, hidden = true) @RequestBody(required = false) String body,
            @ApiParam(required = false, hidden = true) HttpMethod method,
            @ApiParam(required = false, hidden = true) HttpServletRequest request,
            @ApiParam(required = false, hidden = true) HttpServletResponse response) {

        System.out.println("Redirigiendo");
        try {
            return mirrorRest(body, method, request, response);
        } catch (Exception e) {
            return new ResponseEntity<>("error", HttpStatus.OK);
        }
    }

    public @ResponseBody
    ResponseEntity mirrorRest(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        String requestUrl = request.getRequestURI();
        int port = 3330;
        String server = "localhost";
        requestUrl = requestUrl.replace("/sparql", "/ds");
        URI uri = new URI("http", null, server, port, null, null, null);
        uri = UriComponentsBuilder.fromUri(uri)
                .path(requestUrl)
                .query(request.getQueryString())
                .build(true).toUri();
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(uri, method, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

}
