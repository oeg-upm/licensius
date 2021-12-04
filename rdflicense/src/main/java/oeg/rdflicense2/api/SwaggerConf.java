package oeg.rdflicense2.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger conf
 * @author vroddon
 */
@Configuration
@EnableSwagger2
public class SwaggerConf {
    	@Bean
	    public Docket api() {
	 		return new Docket(DocumentationType.SWAGGER_2)
	          .select()
	          .apis(RequestHandlerSelectors.basePackage("oeg.rdflicense2.api"))         
	          .paths(PathSelectors.any())
	          .build()
                  .useDefaultResponseMessages(false)
	          .apiInfo(apiEndPointsInfo());
	    }
	 	private ApiInfo apiEndPointsInfo() {
	        return new ApiInfoBuilder().title("RDFLicense REST API")
	            .description("This is the documentation for the HTTP REST API for the RDFLicense.")
	            .license("Apache 2.0")
	            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
	            .version("1.0.0")
	            .build();
	    }
    
}
