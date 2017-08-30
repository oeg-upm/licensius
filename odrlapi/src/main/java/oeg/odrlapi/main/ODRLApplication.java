package oeg.odrlapi.main;

import io.swagger.jaxrs.config.BeanConfig;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * Defines the components of a JAX-RS application and supplies additional meta-data.
 * This class is necessary for the generation of swagger.json
 * See https://github.com/swagger-api/swagger-core/wiki/swagger-core-resteasy-2.x-project-setup-1.5
 */

public class ODRLApplication extends Application {

    public ODRLApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setTitle("odrl");
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("oeg.odrlapi.rest.server.resources");
        beanConfig.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(oeg.odrlapi.rest.server.resources.Evaluator.class);
        set.add(oeg.odrlapi.rest.server.resources.Validator.class);
        set.add(oeg.odrlapi.rest.server.resources.Test.class);
        set.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        set.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return set;
    }
}
