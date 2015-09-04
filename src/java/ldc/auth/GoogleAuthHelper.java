package ldc.auth;

//JAVA
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

//GOOGLE
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import ldc.LdcConfig;

/**
 * Helper class to aid in the Google+ authorization site
 * @author Víctor Rodríguez
 * @seeAlso http://ocpsoft.org/java/setting-up-google-oauth2-with-java/#section-7
 */
public final class GoogleAuthHelper {

    /**
     * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_ID = "470073812293-o8gemslohiaem98dcksgh3tlvo4fh0kq.apps.googleusercontent.com";
    /**
     * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_SECRET = "Cy4aDbK-h5HSJ_KZ-eS1qVBe";
    /**
     * Callback URI that google will redirect to after successful authentication
     */
    private static final String CALLBACK_URI = "http://salonica.dia.fi.upm.es/oauth2callback";
    private static final String CALLBACK_URI3 = "http://conditional.linkeddata.es/oauth2callback";
    private static final String CALLBACK_URI2 = "oauth2callback";
    // start google authentication constants
    private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    // end google authentication constants


    private String stateToken;
    private final GoogleAuthorizationCodeFlow flow;
    /**
     * Gets the mail associated to this session.
     * The email is as URI: mailto:user@gmail.com
     * @param request Request with the session 
     * @returns mailto:noname@tamp.com if not known
     */
    public static String getMail(HttpServletRequest request) {
        String google = (String) request.getSession().getAttribute("google");
        String state = (String) request.getSession().getAttribute("state");
        if (google == null) {
            google = "noname@tmp.com";
        }
        String email = "mailto:" + state + "@tmp.com";
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(google);
        if (m.find()) {
            email = "mailto:" + m.group();
        }
        return email;
    }

    /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
     * Creates a new state token. Should be done only once per session.
     */
    public GoogleAuthHelper() {

        List<String> scope = new ArrayList();
        for (String s : SCOPE) {
            scope.add(s);
        }
        flow = new GoogleAuthorizationCodeFlow(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, scope);
        generateStateToken();
    }
    
   /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
     * Creates a new state token. Should be done only once per session.
     */
    public GoogleAuthHelper(String mytoken) {

        List<String> scope = new ArrayList();
        for (String s : SCOPE) {
            scope.add(s);
        }
        flow = new GoogleAuthorizationCodeFlow(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, scope);
        if (mytoken==null || mytoken.isEmpty())
            generateStateToken();
        else
            stateToken=mytoken;
    }    

    /**
     * Builds a login URL based on client ID, secret, callback URI, and scope 
     */
    public String buildLoginUrl() {

        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();

        String cu = LdcConfig.get("server","")+CALLBACK_URI2;
        //String cu = CALLBACK_URI;

        return url.setRedirectUri(cu).setState(stateToken).build();
    }

    /**
     * Generates a secure state token 
     */
    private void generateStateToken() {

        SecureRandom sr1 = new SecureRandom();

        stateToken = "google;" + sr1.nextInt();

    }

    /**
     * Accessor for state token
     */
    public String getStateToken() {
        return stateToken;
    }

    /**
     * Expects an Authentication Code, and makes an authenticated request for the user's profile information
     * @return JSON formatted user profile information
     * @param authCode authentication code provided by google
     */
    public String getUserInfoJson(final String authCode) throws IOException {

        String cu = LdcConfig.get("server","")+CALLBACK_URI2;
//        String cu = CALLBACK_URI;


        final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(cu).execute();
        final Credential credential = flow.createAndStoreCredential(response, null);
        final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();
        return jsonIdentity;
    }
}
