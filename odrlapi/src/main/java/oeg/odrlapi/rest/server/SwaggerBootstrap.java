package oeg.odrlapi.rest.server;

import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/*
COMO GENERAR LA PAGINA WEB CON LOS CLIENTES. 
IR A LA WEB DE https://editor.swagger.io/
PEGAR EL SWAGGER NUESTRO
ELEGIR html2 
DESCARGAR
COPIAR A LA CARPETA DESEADA
a) ir a https://editor.swagger.io/
b) copypaster el ultimo swagger.yaml
c) decir "descargar html2 client"
d) copiar la carpeta a webapp/clients

// Option 2 https://www.npmjs.com/package/bootprint-swagger
ir a d:/svn/adoc
mirar el run.bat

PENDIENTE DE MIRAR https://github.com/sourcey/spectacle

dESCARTADAS:

// Option 3 Following the steps here: https://stackoverflow.com/questions/40407112/how-to-generate-a-static-html-file-from-a-swagger-documentation
// Option 4 https://github.com/BigstickCarpet/swagger-server
// Option 5 https://stackoverflow.com/questions/34188440/swagger-ui-editor-like-page-add-to-my-web-site
a) Descargar localhost:8080/odrlapi/swagger.json a D:\svn\adoc\swagger-editor\spec-files\default.yaml
//ejemplos en svn/adoc
*/
/**
 * Necessary class to generate swagger.json
 * .termsOfService("http://swagger.io/terms/")
 */
public class SwaggerBootstrap extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
    Info info = new Info()
            .title("odrlapi")
            .description("This is an implementation of the ODRL Evaluator. For more info, read " +
                    "at [W3C Permissions and Obligations Working Group](https://www.w3.org/2016/poe/) ")
            .contact(new Contact()
                    .email("vrodriguezDELETETHIS@fi.upm.es"))
            .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
    info.setTitle("odrlapi");
    ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);
/*    swagger.securityDefinition("petstore_auth",
            new OAuth2Definition()
                    .implicit("http://localhost:8002/oauth/dialog")
                    .scope("email", "Access to your email address")
                    .scope("pets", "Access to your pets"));
    swagger.tag(new Tag()
            .name("pet")
            .description("Everything about your Pets")
            .externalDocs(new ExternalDocs("Find out more", "http://swagger.io")));
    swagger.tag(new Tag()
            .name("store")
            .description("Access to Petstore orders"));
    swagger.tag(new Tag()
            .name("user")
            .description("Operations about user")
            .externalDocs(new ExternalDocs("Find out more about our store", "http://swagger.io")));        
        */
   //     super.init(config);
 
      //  BeanConfig beanConfig = new BeanConfig();
        /*beanConfig.setVersion("1.0");
        beanConfig.setTitle("odrl");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("odrlapi/");
        beanConfig.setResourcePackage("oeg.odrlapi.rest.server.resources");
        beanConfig.setScan(true);*/
        
        new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
    }
}