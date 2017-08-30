package oeg.odrlapi.rest.server.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/evaluator")
@Api(value = "/evaluator", description = "Evaluates an ODRL policy")
@Produces({"application/json"})
public class Evaluator {

    /*
	@GET
	@Path("/test")
	@Produces("application/json")
        @ApiOperation(value = "test", notes = "Checks if the service is up and running")
        @ApiResponses(value = { @ApiResponse(code = 200, message = "Service up"),
                                @ApiResponse(code = 404, message = "Service down") })        
	public Response test() {
           String ok = "ok";
           return Response.status(200).entity(ok).build();
//            Product product = new Product();
//            product.setName(turtle);
//            product.setQty(12);
//            return product; 
	} */

	@POST
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
        @ApiOperation(value = "evaluator", notes = "Returns if a policy is valid or not")
        @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid policy ID supplied") })
        public Response evaluator(@ApiParam(name="policy", value = "ODRL policy serialized as RDF Turtle", required = true) String turtle) {
//	public Response createProductInJSON(Product product) {

		String result = "Product created : " + turtle;
		return Response.status(201).entity(result).build();
		
	}
	
}