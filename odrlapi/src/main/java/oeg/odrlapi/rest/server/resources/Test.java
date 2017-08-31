package oeg.odrlapi.rest.server.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/test")
@Api(value = "/test", description = "Internal testing")
@Produces({"application/json"})
public class Test {
	@GET
	@Path("/")
	@Produces("application/json")
        @ApiOperation(value = "test", notes = "Checks if the service is up and running")
        @ApiResponses(value = { @ApiResponse(code = 200, message = "Service up"),
                                @ApiResponse(code = 200, message = "Service down") })        
	public Response test() {
           String ok = "ok";
           return Response.status(200).entity(ok).build();
            /*Product product = new Product();
            product.setName(turtle);
            product.setQty(12);
            return product; */
	}
	
}